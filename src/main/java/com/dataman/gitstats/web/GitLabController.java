package com.dataman.gitstats.web;

import org.gitlab4j.api.models.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dataman.gitstats.util.GitlabUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

@Api(value = "gitlab API")
@RestController
@RequestMapping(value = "/git", produces = "application/json;charset=utf-8")
public class GitLabController extends BaseController{

	@Autowired
	GitlabUtil gitlabUtil;
	
	@ApiOperation(value = "获取帐号下面所有项目")
	@RequestMapping(value="/{aid}/projects",method=RequestMethod.GET)
	public Object getAllProject(@ApiParam(required = true, name = "aid", value = "帐号id") @PathVariable String aid){

		try {
			// 只返回成员项目
			return  setJson(SUCCESS_CODE,gitlabUtil.getGitLabApi(aid).getProjectApi().getMemberProjects());
		} catch (Exception e) {
			logger.error("获取帐号下面所有项目异常：", e);
			return  setJson(FAIL_CODE, e.getMessage());
		}

	}

	@ApiOperation(value = "获取帐号下面所有项目组")
	@RequestMapping(value="/{aid}/groups",method=RequestMethod.GET)
	public Object getAllGroups(@ApiParam(required = true, name = "aid", value = "帐号id") @PathVariable String aid){

		try {
			// 只返回成员项目
			return  setJson(SUCCESS_CODE,gitlabUtil.getGitLabApi(aid).getGroupApi().getGroups());
		} catch (Exception e) {
			logger.error("获取帐号下面所有项目组异常：", e);
			return  setJson(FAIL_CODE, e.getMessage());
		}

	}
	
	@ApiOperation(value = "获取项目下面所有分支")
	@RequestMapping(value="/{aid}/{pid}/branchs",method=RequestMethod.GET)
	public Object getAllBranch(@ApiParam(required = true, name = "aid", value = "帐号id") @PathVariable String aid,
			@ApiParam(required = true, name = "pid", value = "项目id") @PathVariable int pid){

		try {
			List<Branch> branchList=gitlabUtil.getGitLabApi(aid).getRepositoryApi().getBranches(pid);
//			if(branchList.size()==0){
//				Branch branch=new Branch();
//				branch.setName("master");
//				branchList.add(branch);
//			}
			return  setJson(SUCCESS_CODE,branchList);
		} catch (Exception e) {
			logger.error("获取项目下面所有分支异常：", e);
			return  setJson(FAIL_CODE, e.getMessage());
		}

	}
	
	@ApiOperation(value = "获取项目下面所有用户")
	@RequestMapping(value="/{aid}/{pid}/users",method=RequestMethod.GET)
	public Object getAllUser(@ApiParam(required = true, name = "aid", value = "帐号id") @PathVariable String aid,
			@ApiParam(required = true, name = "pid", value = "项目id") @PathVariable int pid){

		try {
			return  setJson(SUCCESS_CODE,gitlabUtil.getGitLabApi(aid).getProjectApi().getMembers(pid));
		} catch (Exception e) {
			logger.error("获取项目下面所有分支异常：", e);
			return  setJson(FAIL_CODE, e.getMessage());
		}

	}
	
}
