package com.dataman.gitstats.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectHook;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.param.AddProjectParam;
import com.dataman.gitstats.po.CommitStatsPo;
import com.dataman.gitstats.po.ProjectBranchStats;
import com.dataman.gitstats.repository.CommitStatsRepository;
import com.dataman.gitstats.repository.ProjectBranchStatsRepository;
import com.dataman.gitstats.util.ClassUitl;
import com.dataman.gitstats.util.Commnt;
import com.dataman.gitstats.util.GitlabUtil;
import com.dataman.gitstats.vo.CommitStatsVo;
import com.dataman.gitstats.vo.ProjectBranchStatsVo;

@Service
public class ProjectBranchService {
	
	@Autowired
	ProjectBranchStatsRepository projectBranchStatsRepository;
	@Autowired
	CommitStatsRepository commitStatsRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	AsyncTask asyncTask;

	@Autowired
	GitlabUtil gitlabUtil;

	 @Autowired
	 private CommonService commonService;

	@Autowired
	private WebHookService webHookService;
	
	
	SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
	
	/**
	 * @method addProject(添加需要统计的项目)
	 * @return int
	 * @author liuqing
	 * @throws Exception 
	 * @date 2017年9月19日 下午3:12:39
	 */
	public int addProject(AddProjectParam param,HttpServletRequest request) throws Exception{
		int SUCCESS=0,EXISTED=1,NOTEXIST=2;
		Calendar cal=Calendar.getInstance();
		//验证是否 存在于 gitlab
		GitLabApi gitLabApi= gitlabUtil.getGitLabApi(param.getAccountid());
		Project project= gitLabApi.getProjectApi().getProject(param.getProid());
		if(project==null){
			return NOTEXIST;
		}
		Branch branch=gitLabApi.getRepositoryApi().getBranch(param.getProid(),param.getBranch());
		if(branch==null){
			return NOTEXIST;
		}
		ProjectBranchStats pbs=projectBranchStatsRepository.findOne(project.getWebUrl()+"_"+param.getProid()+"_"+param.getBranch());
		if(pbs==null){
			pbs=new ProjectBranchStats();
			ClassUitl.copyProperties(param,pbs);
			pbs.setProjectNameWithNamespace(project.getNameWithNamespace());
			pbs.setWeburl(project.getWebUrl());
			pbs.setStatus(0);
			pbs.setTotalAddRow(0);
			pbs.setTotalDelRow(0);
			pbs.setTotalRow(0);
			pbs.setCreatedate(cal.getTime());
			pbs.setLastupdate(cal.getTime());
			projectBranchStatsRepository.insert(pbs);
			asyncTask.initProjectStats(pbs);
			String webHookUrl=commonService.getHookListenerPath(request);
			webHookService.addGitlabPushEventWebHook(pbs,webHookUrl);
		}else{
			return EXISTED;
		}
		return SUCCESS;
	}
	
	
	boolean checkWebhookStats(String aid,int pid) throws GitLabApiException{
		boolean flag=false;
		GitLabApi gitLabApi= gitlabUtil.getGitLabApi(aid);
		List<ProjectHook> hooks= gitLabApi.getProjectApi().getHooks(pid);
		if(!hooks.isEmpty()){
			flag= hooks.stream().filter(hook -> hook.getUrl().indexOf("/webHook/receive")>0).findFirst().isPresent();	
		}
		return flag;
	}
	
	public ProjectBranchStats findProjectBranchStatsById(String id){
		return projectBranchStatsRepository.findOne(id);
	}

	public ProjectBranchStats findProjectBranchStatsByParams(String weburl,Integer proid,String branch){
		return projectBranchStatsRepository.findOne(weburl+"_"+proid+"_"+branch);
	}

	public List<ProjectBranchStats> getAllProjectBranchStats(){
		return projectBranchStatsRepository.findAll();
	}

	public void deleteProjectBranchStats(String id){
		projectBranchStatsRepository.delete(id);
		commitStatsRepository.deleteByBranchId(id);
	}

	public void modifyProjectBranchStats(AddProjectParam param) throws Exception{
		ProjectBranchStats projectBranchStats=projectBranchStatsRepository.findOne(param.getId());
		if(param.getId()==null || projectBranchStats==null){
			throw new Exception("参数错误");
		}
		ClassUitl.copyProperties(param,projectBranchStats);
		projectBranchStatsRepository.save(projectBranchStats);
	}

//	public ProjectBranchStats findProjectBranchStatsByProjectIdAndBranch(String projectId,String branch){
//		return projectBranchStatsRepository.findByProjectidAndBranch(projectId,branch);
//	}

//	public int delProject(String id){
//		int SUCCESS=0;
//		projectRepository.delete(id);
//		projectBranchStatsRepository.deleteByProjectid(id);
//		commitStatsRepository.deleteByProid(id);
//		return SUCCESS;
//	}
	
	public ProjectBranchStatsVo showStatsByUser(String pbsId) throws Exception{
		ProjectBranchStats pbs=projectBranchStatsRepository.findOne(pbsId);
		ProjectBranchStatsVo pbsv=ClassUitl.copyProperties(pbs, new ProjectBranchStatsVo());
		pbsv.setData(statsByUser(pbs));
		return pbsv;
	}
	
	public ProjectBranchStatsVo showStatsByDay(String pbsId) throws Exception{
		ProjectBranchStats pbs=projectBranchStatsRepository.findOne(pbsId);
		ProjectBranchStatsVo pbsv=ClassUitl.copyProperties(pbs, new ProjectBranchStatsVo());
		pbsv.setData(statsByDay(pbs));
		return pbsv;
	}
	
	//	db.getCollection('commitStatsPo').aggregate([
	//	 {$match:{"proid" : "3ec34ce6b6f64d90afba8009a31a504e","branch" : "dev"}},
	//	 {$group:{_id:"$authorName",addrow:{$sum:"$addRow"},removerow:{$sum:"$removeRow"},commit:{$sum:1}}}
	//	 ,{$sort:{addrow:-1}}
	//	 ])
	public List<CommitStatsVo> statsByUser(ProjectBranchStats pbs){
		List<CommitStatsVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
			Aggregation.match(new Criteria("branchId").is(pbs.getId())),
			Aggregation.group(Fields.fields("$authorName")).sum("$addRow").as("addrow").sum("$removeRow").as("removerow").count().as("commit"),
			Aggregation.sort(Direction.DESC, "addrow")
		);
		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
		return list;
	}
	
//	db.getCollection('commitStatsPo').aggregate([  
// 	    {$match:{"proid" : "3ec34ce6b6f64d90afba8009a31a504e","branch" : "dev"}}
//	    ,{$project : { day : {$substr: ["$createdAt", 0, 10] },addRow : 1,removeRow:1 }}
//	    ,{$group   : { _id : "$day",  addRow : { $sum : "$addRow" } ,removeRow : { $sum : "$removeRow" },commit:{$sum:1}}}   
//	    ,{$sort :{_id : -1}}
//	])
	public List<CommitStatsVo> statsByDay(ProjectBranchStats pbs){
		List<CommitStatsVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
			Aggregation.match(new Criteria("branchId").is(pbs.getId())),
			Aggregation.project().and("createdAt").substring(0, 10).as("day").and("$addRow").as("addRow").and("$removeRow").as("removeRow"),
			Aggregation.group(Fields.fields("day")).sum("addRow").as("addrow").sum("removeRow").as("removerow").count().as("commit"),
			Aggregation.sort(Direction.ASC, "_id")
		);
		System.out.println(agg.toString());
		
		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
		//补充 缺省日期数据  补充 到 当前
		list =complementDay(list,null);
		return list;
	}
	/**
	 * @method complementDay(缺省日期数据)
	 * @param beginDate 开始日期  null -> list第一个时间
	 * @return List<CommitStatsVo>
	 * @author liuqing
	 * @throws ParseException 
	 * @date 2017年9月24日 上午10:37:09
	 */
	public List<CommitStatsVo> complementDay(List<CommitStatsVo> list,Date beginDate){
		LocalDate today = LocalDate.now();
		LocalDate loopday=null;
		// mongo查询出来的UnmodifiableRandomAccessList是一个 不可以修改
		System.out.println(list.getClass());
		
		if(beginDate==null){
			loopday = LocalDate.parse(list.get(0).get_id());
		}else{
			 Instant instant = beginDate.toInstant();
		     ZoneId zoneId = ZoneId.systemDefault();
		     loopday = instant.atZone(zoneId).toLocalDate();
		}
		List<CommitStatsVo> tmp= new ArrayList<CommitStatsVo>();
		int index=0;
		while (loopday.toEpochDay()<=today.toEpochDay()) {
			if(list.size()-1<index){
				CommitStatsVo csv=new CommitStatsVo();
				csv.set_id(loopday.format(DateTimeFormatter.ISO_LOCAL_DATE));
				tmp.add(csv);
			}else{
				LocalDate cld=LocalDate.parse(list.get(index).get_id());
				if(cld.toEpochDay()!=loopday.toEpochDay()){
					CommitStatsVo csv=new CommitStatsVo();
					csv.set_id(loopday.format(DateTimeFormatter.ISO_LOCAL_DATE));
					tmp.add(csv);
				}else{
					tmp.add(list.get(index));
					index++;
				}
			}
			long nextday=loopday.toEpochDay()+1;
			loopday=LocalDate.ofEpochDay(nextday);
		}
		return tmp;
	}
	
	
	
}
