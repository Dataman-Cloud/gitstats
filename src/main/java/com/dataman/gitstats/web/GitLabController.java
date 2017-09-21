package com.dataman.gitstats.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dataman.gitstats.util.GitlabUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "gitlab API")
@RestController
@RequestMapping(value = "/git", produces = "application/json;charset=utf-8")
public class GitLabController extends BaseController{

	@Autowired
	GitlabUtil gitlabUtil;
	
	@ApiOperation(value = "获取帐号下面所有项目")
	@RequestMapping(value="/{aid}/projects",method=RequestMethod.GET)
	public Object getAllProject(@ApiParam(required = true, name = "aid", value = "帐号id") @PathVariable String aid){
		json.clear();
		try {
			setJson(SUCCESS_CODE,gitlabUtil.getGitLabApi(aid).getProjectApi().getProjects());
		} catch (Exception e) {
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	
	@ApiOperation(value = "获取项目下面所有分支")
	@RequestMapping(value="/{aid}/{pid}/projects",method=RequestMethod.GET)
	public Object getAllBranch(@ApiParam(required = true, name = "aid", value = "帐号id") @PathVariable String aid,
			@ApiParam(required = true, name = "pid", value = "项目id") @PathVariable int pid){
		json.clear();
		try {
			setJson(SUCCESS_CODE,gitlabUtil.getGitLabApi(aid).getRepositoryApi().getBranches(pid));
		} catch (Exception e) {
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	
}
