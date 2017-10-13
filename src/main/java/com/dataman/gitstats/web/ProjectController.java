package com.dataman.gitstats.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dataman.gitstats.annotation.AuthRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import com.dataman.gitstats.param.AddProjectParam;
import com.dataman.gitstats.service.ProjectBranchService;
import com.dataman.gitstats.util.MongoDateUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "/project")
@RestController
@RequestMapping(value = "/project", produces = "application/json;charset=utf-8")
public class ProjectController extends BaseController {
	
	@Autowired
	ProjectBranchService projectBranchService;

	@RequestMapping(value = "/",method = RequestMethod.GET)
	@ApiOperation(value = "获取所有统计项目")
	public Object getAll(){
		json.clear();
		try {
			setJson(SUCCESS_CODE,projectBranchService.getAllProjectBranchStats());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	@AuthRequired
	@RequestMapping(value = "/{branchId}",method = RequestMethod.DELETE)
	@ApiOperation(value = "删除统计项目")
	public Object delete(
			@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
			@ApiParam(required = true, name = "branchId", value = "分支id") @PathVariable String branchId){
		json.clear();
		projectBranchService.deleteProjectBranchStats(branchId);
		setJson(SUCCESS_CODE);
		return json;
	}
	@AuthRequired
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ApiOperation(value = "添加需要统计的项目")
	public Object add(
			@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
			@Valid @RequestBody AddProjectParam param,BindingResult bingingresult,HttpServletRequest request){
		json.clear();
		if(bingingresult.hasErrors()){
			setJson(PARAMERR_CODE,bingingresult.getAllErrors());
			return json;
		}
		try {
			if(param.getId()==null){
				setJson(SUCCESS_CODE, projectBranchService.addProject(param,request));
			}else{
				setJson(SUCCESS_CODE);
				projectBranchService.modifyProjectBranchStats(param);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}

	@RequestMapping(value="/projectBranchStats/{id}/byUser",method=RequestMethod.GET)
	@ApiOperation(value = "根据User显示统计数据")
	public Object showProjectBranchStatsByUser(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id){
		json.clear();
		try {
			setJson(SUCCESS_CODE, projectBranchService.showStatsByUser(id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}

	@RequestMapping(value="/projectBranchStats/{id}/byDay",method=RequestMethod.GET)
	@ApiOperation(value = "根据时间显示统计数据")
	public Object showProjectBranchStats(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id,
			@RequestParam(required = false)  @ApiParam(name = "dateformat", value = "时间格式") String dateformat,
			@RequestParam(required = false) @ApiParam(name = "lastDate", value = "时间范围") Integer lastDate){
		json.clear();
		try {
			setJson(SUCCESS_CODE, projectBranchService.showStatsByDay(id,MongoDateUtil.getFormat(dateformat)
					,MongoDateUtil.getOperation(id, dateformat, lastDate)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="/projectBranchStats/{id}/byUserAndDay",method=RequestMethod.GET)
	@ApiOperation(value = "根据时间和用户显示统计数据")
	public Object showProjectBranchByUserAndDay(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id,
			@RequestParam(required = false) @ApiParam(name = "dateformat", value = "时间格式") String dateformat,
			@RequestParam(required = false) @ApiParam(name = "lastDate", value = "时间范围") Integer lastDate){
		json.clear();
		try {
			setJson(SUCCESS_CODE, projectBranchService.showStatsByUserAndDay(id,MongoDateUtil.getFormat(dateformat)
					,MongoDateUtil.getOperation(id, dateformat, lastDate)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="/projectBranchStats/{id}/byDayAndUser",method=RequestMethod.GET)
	@ApiOperation(value = "根据时间和用户显示统计数据")
	public Object showProjectBranchByDayAndUser(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id,
			@RequestParam(required = false) @ApiParam(name = "dateformat", value = "时间格式") String dateformat,
			@RequestParam(required = false) @ApiParam(name = "lastDate", value = "时间范围") Integer lastDate){
		json.clear();
		try {
			setJson(SUCCESS_CODE, projectBranchService.showStatsByDayAndUser(id,MongoDateUtil.getFormat(dateformat)
					,MongoDateUtil.getOperation(id, dateformat, lastDate)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="/projectBranchStats/byDay",method=RequestMethod.GET)
	@ApiOperation(value = "根据时间显示统计所有项目数据")
	public Object showProjectBranchStats(@RequestParam(required = false) @ApiParam(name = "dateformat", value = "时间格式") String dateformat,
			@RequestParam(required = false) @ApiParam(name = "lastDate", value = "时间范围") Integer lastDate){
		json.clear();
		try {
			setJson(SUCCESS_CODE, projectBranchService.statsByDay(MongoDateUtil.getFormat(dateformat),MongoDateUtil.getOperation(null, dateformat, lastDate)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
}
