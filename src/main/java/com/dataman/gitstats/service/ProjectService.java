package com.dataman.gitstats.service;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.param.AddProjectParam;
import com.dataman.gitstats.po.ProjectStats;
import com.dataman.gitstats.repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	GitLabApi gitLabApi;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	AsyncTask asyncTask;
	/**
	 * @method addProject(添加需要统计的项目)
	 * @return int
	 * @author liuqing
	 * @throws Exception 
	 * @date 2017年9月19日 下午3:12:39
	 */
	public int addProject(AddProjectParam param) throws Exception{
		int SUCCESS=0,NOTEXISTPROJECT=1,NOTEXISTBRANCH=2,SAMEPROJECTNAME=3;
		Calendar cal=Calendar.getInstance();
		//判断项目名称 和 版本 是不是 在gitlib中存在
		List<Project> list= gitLabApi.getProjectApi().getProjects(param.getName());
		if(list.isEmpty()){
			return NOTEXISTPROJECT; //不存在该项目名称
		}
		if(list.size()>1){
			return SAMEPROJECTNAME; //存在多个相同的查询名称
		}
		Project project= list.get(0);
		//获取所有的分支 判断是否需要统计的分支是否存在
		List<Branch> branchs= gitLabApi.getRepositoryApi().getBranches(project.getId());
		if (branchs.isEmpty() || branchs.stream().filter(b -> b.getName().equals(param.getBranch()))
				.findFirst().isPresent()){
			return NOTEXISTBRANCH; //不存需要的版本
		}
		//判断 webhook 是否注册好
		int webhookststus=1;
		List<ProjectHook> hooks = gitLabApi.getProjectApi().getHooks(project.getId());
		if(hooks.stream().filter(h -> h.getUrl().indexOf("/webHook/receive")>0)
				.findFirst().isPresent()){
			webhookststus=0;
		}
		//添加记录到mongodb
		ProjectStats prostats = new ProjectStats();
		prostats.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		prostats.setName(param.getName());
		prostats.setSha(param.getBranch());
		prostats.setProId(project.getId());
		prostats.setWebhookstatus(webhookststus);
		prostats.setStatus(0);
		prostats.setLastupdate(cal.getTime());
		prostats.setTotalCommit(0);
		prostats.setTotalRemove(0);
		prostats.setTotalAdd(0);
		prostats.setTotalRow(0);
		projectRepository.insert(prostats);
		//异步执行 初始化方法
		asyncTask.initProjectStats(prostats);
		return SUCCESS;
	}
	
	
	
}
