package com.dataman.gitstats.service;

import java.util.Calendar;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.param.AddProjectParam;
import com.dataman.gitstats.po.ProjectStats;
import com.dataman.gitstats.repository.ProjectBranchStatsRepository;
import com.dataman.gitstats.repository.ProjectRepository;

@Service
public class ProjectService {

	// @Autowired
	GitLabApi gitLabApi;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	ProjectBranchStatsRepository projectBranchStatsRepository;
	
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
		// 验证 分支是
		
		
		
		
		return SUCCESS;
	}
	
	public List<ProjectStats> getAll(){
		return projectRepository.findAll();
	}
	
	public int delProject(String id){
		int SUCCESS=0;
		projectRepository.delete(id);
		return SUCCESS;
	}
	
}
