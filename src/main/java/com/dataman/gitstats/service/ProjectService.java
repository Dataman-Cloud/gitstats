package com.dataman.gitstats.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.dataman.gitstats.po.PushEventRecord;
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

import javax.servlet.http.HttpServletRequest;

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

	 @Autowired
	 private CommonService commonService;

	@Autowired
	private WebHookService webHookService;
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

		 String webHookUrl=commonService.getHookListenerPath(request);
		 webHookService.addGitlabPushEventWebHook(ps,webHookUrl);
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
	
	public List<ProjectStats> getAll(){
		return projectRepository.findAll();
	}

	public ProjectStats findProjectStatsByIdAndUrl(Integer projectId,String projectWebUrl){
		return projectRepository.findByWeburlAndProId(projectWebUrl,projectId);
	}

	public ProjectStats findProjectStatsById(String projectId){
		return projectRepository.findOne(projectId);
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
			Aggregation.sort(Direction.DESC, "_id")
		);
		System.out.println(agg.toString());
		
		AggregationResults<CommitStatsVo> ret=  mongoTemplate.aggregate(agg, CommitStatsPo.class, CommitStatsVo.class);
		list =ret.getMappedResults();
		return list;
	}
	
}
