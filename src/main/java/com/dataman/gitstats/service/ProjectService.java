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
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectHook;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.param.AddProjectParam;
import com.dataman.gitstats.po.CommitStatsPo;
import com.dataman.gitstats.po.ProjectBranchStats;
import com.dataman.gitstats.po.ProjectStats;
import com.dataman.gitstats.repository.CommitStatsRepository;
import com.dataman.gitstats.repository.ProjectBranchStatsRepository;
import com.dataman.gitstats.repository.ProjectRepository;
import com.dataman.gitstats.util.ClassUitl;
import com.dataman.gitstats.util.Commnt;
import com.dataman.gitstats.util.GitlabUtil;
import com.dataman.gitstats.vo.CommitStatsVo;
import com.dataman.gitstats.vo.ProjectBranchStatsVo;
import com.dataman.gitstats.vo.StatsByUserByDayVo;

@Service
public class ProjectService {
	
	@Autowired
	ProjectRepository projectRepository;
	
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

	// @Autowired
	// private CommonService commonService;

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
		//验证是否 存在于 mongodb
		ProjectStats ps = projectRepository.findByNameAndAccountId(param.getName(), param.getAid());
		if(ps != null){
			return EXISTED;
		}
		//验证是否 存在于 gitlab
		GitLabApi gitLabApi= gitlabUtil.getGitLabApi(param.getAid());
		List<Project> projects= gitLabApi.getProjectApi().getProjects(param.getName());
		if(projects.isEmpty()){
			return NOTEXIST;
		}
		Project project= projects.get(0);
		//存库
		ps =new ProjectStats();
		ps.setId(Commnt.createUUID());
		ps.setAccountId(param.getAid());
		ps.setName(param.getName());
		ps.setProId(project.getId());
		ps.setCreatedate(cal.getTime());
		ps.setLastupdate(cal.getTime());
		ps.setWeburl(project.getWebUrl());
		ps.setDsc(project.getDescription());
		boolean check=checkWebhookStats(param.getAid(),project.getId());
		ps.setWebhookstatus(check?1:0);
		projectRepository.insert(ps);
		
		List<ProjectBranchStats> branchs=new ArrayList<ProjectBranchStats>(); 
		for (String branch : param.getBranchs()) {
			ProjectBranchStats pbs= new ProjectBranchStats();
			pbs.setId(Commnt.createUUID());
			pbs.setAccountid(param.getAid());
			pbs.setProjectid(ps.getId());
			pbs.setBranch(branch);
			pbs.setProjectname(ps.getName());
			pbs.setStatus(0);
			pbs.setTotalAddRow(0);
			pbs.setTotalDelRow(0);
			pbs.setTotalRow(0);
			pbs.setCreatedate(cal.getTime());
			pbs.setLastupdate(cal.getTime());
			pbs.setProid(ps.getProId());
			branchs.add(pbs);
		}
		projectBranchStatsRepository.insert(branchs);
		for (ProjectBranchStats projectBranchStats : branchs) {
			asyncTask.initProjectStats(projectBranchStats);
		}
		// String webHookUrl=commonService.getHookListenerPath(request);
		// webHookService.addGitlabPushEventWebHook(ps,webHookUrl);
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
	
	
	/**
	 * @method getAll(获取所有项目)
	 * @return List<ProjectStats>
	 * @author liuqing
	 * @date 2017年9月25日 下午2:55:22
	 */
	public List<ProjectStats> getAll(){
		List<ProjectStats> list=projectRepository.findAll();
		List<String> ids=new ArrayList<String>();
		list.forEach(ps -> ids.add(ps.getId()));
		List<ProjectBranchStats> chlids= projectBranchStatsRepository.findByProjectidIn(ids);
		Map<String, List<ProjectBranchStats>> map= chlids.stream().collect(Collectors.groupingBy(ProjectBranchStats::getProjectid));
		list.forEach(ps ->ps.setBranchs(map.get(ps.getId())));
		ids.clear();
		chlids.clear();
		map.clear();
		return list;
	}
	
	/**
	 * @method getAllBranchs(获取所有分支)
	 * @return List<ProjectBranchStats>
	 * @author liuqing
	 * @date 2017年9月25日 下午2:56:19
	 */
	public List<ProjectBranchStats> getAllBranchs(){
		return projectBranchStatsRepository.findAll();
	}

	public ProjectStats findProjectStatsByPushEvent(PushEvent event){
		Integer projectId=event.getProjectId();
		String repository=event.getProject().getWebUrl();
		return projectRepository.findByWeburlAndProId(repository,projectId);
	}

	public ProjectBranchStats findProjectBranchStatsByProjectIdAndBranch(String projectId,String branch){
		return projectBranchStatsRepository.findByProjectidAndBranch(projectId,branch);
	}
	
	public int delProject(String id){
		int SUCCESS=0;
		projectRepository.delete(id);
		projectBranchStatsRepository.deleteByProjectid(id);
		commitStatsRepository.deleteByProid(id);
		return SUCCESS;
	}
	
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
			Aggregation.match(new Criteria("proid").is(pbs.getProjectid()).andOperator(new Criteria("branch").is(pbs.getBranch()))),
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
			Aggregation.match(new Criteria("proid").is(pbs.getProjectid()).andOperator(new Criteria("branch").is(pbs.getBranch()))),
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
	
//	db.getCollection('commitStatsPo').aggregate([  
//	{$match:{"proid" : "0670ed0736be4101aab4d679a997977e","branch" : "develop"}}
//	,{$project : { day : {$substr: ["$createdAt", 0, 10] },addRow : 1,removeRow:1 ,authorName:1}}
//	,{$group   : { _id : {authorName: "$authorName",day : "$day"},  addRow : { $sum : "$addRow" } ,removeRow : { $sum : "$removeRow" },commit:{$sum:1}}}   
//	,{$project : {_id :0, authorName : '$_id.authorName', dayinfo : {day : '$_id.day', addRow:'$addRow',removeRow:'$removeRow',commit:'$commit'}}}
//	,{$group:{_id:"$authorName", days:{$push:"$dayinfo"}, addrow: {$sum: "$dayinfo.addRow"},removeRow: {$sum: "$dayinfo.removeRow"},commit: {$sum: "$dayinfo.commit"}}}
//	,{$sort : { addrow : -1 }}
//	,{ $limit : 8 }
//	])
	public List<StatsByUserByDayVo> statsByUserByDay(ProjectBranchStats pbs){
		List<StatsByUserByDayVo> list=null;
		Aggregation agg= Aggregation.newAggregation(
				Aggregation.match(new Criteria("proid").is(pbs.getProjectid()).andOperator(new Criteria("branch").is(pbs.getBranch()))),
				Aggregation.project().and("createdAt").substring(0, 10).as("day").and("$addRow").as("addRow")
					.and("$removeRow").as("removeRow"),
				Aggregation.group(Fields.fields("authorName","day")).sum("addRow").as("addRow").sum("removeRow").as("removeRow")
					.count().as("commit"),
				Aggregation.project().and("_id.authorName").as("authorName"),
				Aggregation.group(Fields.fields("authorName")).sum("addRow").as("dayinfo.addRow")
					.sum("dayinfo.removeRow").as("removeRow").sum("commit").as("dayinfo.commit")
					.push("dayinfo").as("days"),
				Aggregation.sort(Direction.ASC, "_id"),
				Aggregation.limit(8l)
			);
		System.out.println(agg.toString());
			
		AggregationResults<StatsByUserByDayVo> ret=  mongoTemplate.aggregate(agg, StatsByUserByDayVo.class, StatsByUserByDayVo.class);
		list =ret.getMappedResults();
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
