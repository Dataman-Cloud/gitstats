package com.dataman.gitstats.web;

import com.dataman.gitstats.annotation.AuthRequired;
import com.dataman.gitstats.param.AddGroupParam;
import com.dataman.gitstats.param.AddProjectParam;
import com.dataman.gitstats.service.CommonService;
import com.dataman.gitstats.service.GroupService;
import com.dataman.gitstats.service.ProjectBranchService;
import com.dataman.gitstats.util.MongoDateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(value = "/groups")
@RestController
@RequestMapping(value = "/groups", produces = "application/json;charset=utf-8")
public class GroupController extends BaseController {

	@Autowired
	private GroupService groupService;
	
	@Autowired
	ProjectBranchService projectBranchService;

	@Autowired
	private CommonService commonService;

	@RequestMapping(value = "/",method = RequestMethod.GET)
	@ApiOperation(value = "获取所有统计项目组")
	public Object getAll(@RequestParam(required = false) @ApiParam(name = "limit", value = "是否过滤未初始化") String limit){

		try {
			return setJson(SUCCESS_CODE,groupService.getAllGroupStats(limit));
		} catch (Exception e) {
			logger.error("获取所有统计项目异常：", e);
			return setJson(FAIL_CODE, e.getMessage());
		}

	}
	@AuthRequired
	@RequestMapping(value = "/{groupId}",method = RequestMethod.DELETE)
	@ApiOperation(value = "删除统计项目")
	public Object delete(
			@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
			@ApiParam(required = true, name = "branchId", value = "分支id") @PathVariable String groupId){

		groupService.deleteGroupStats(groupId);
		return setJson(SUCCESS_CODE);
	}
	@AuthRequired
	@RequestMapping(value = "/{groupId}",method = RequestMethod.PUT)
	@ApiOperation(value = "重新初始化项目")
	public Object reset(
			@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
			@ApiParam(required = true, name = "branchId", value = "分支id") @PathVariable String groupId,
			HttpServletRequest request) throws Exception {

		String webhookUrl=commonService.getHookListenerPath(request);
		groupService.resetGroupStats(groupId,webhookUrl);
		return setJson(SUCCESS_CODE);

	}
	@AuthRequired
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ApiOperation(value = "添加需要统计的项目")
	public Object add(
			@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
			@Valid @RequestBody AddGroupParam param,BindingResult bingingresult,HttpServletRequest request) throws Exception{

		if(bingingresult.hasErrors()){
			return setJson(PARAMERR_CODE,bingingresult.getAllErrors());

		}
		String webhookUrl=commonService.getHookListenerPath(request);
			if(param.getId()==null){
				return setJson(SUCCESS_CODE, groupService.addGroup(param, webhookUrl));
			}else{
				groupService.modifyGroupStats(param, webhookUrl);
				return setJson(SUCCESS_CODE);
			}


	}

	@RequestMapping(value="/groupStats/{id}/byUser",method=RequestMethod.GET)
	@ApiOperation(value = "根据User显示统计数据")
	public Object showProjectBranchStatsByUser(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id){

		try {
			return setJson(SUCCESS_CODE, groupService.showStatsByUser(id));
		} catch (Exception e) {
			logger.error("根据User显示统计数据异常：", e);
			return setJson(FAIL_CODE, e.getMessage());
		}

	}

	@RequestMapping(value="/groupStats/{id}/byDay",method=RequestMethod.GET)
	@ApiOperation(value = "根据时间显示统计数据")
	public Object showProjectBranchStats(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id,
			@RequestParam(required = false)  @ApiParam(name = "dateformat", value = "时间格式") String dateformat,
			@RequestParam(required = false) @ApiParam(name = "lastDate", value = "时间范围") Integer lastDate){

		try {
			return setJson(SUCCESS_CODE, groupService.showStatsByDay(id,MongoDateUtil.getFormat(dateformat)
					,MongoDateUtil.getGroupOperation(id, dateformat, lastDate)));
		} catch (Exception e) {
			logger.error("根据时间显示统计数据异常：", e);
			return setJson(FAIL_CODE, e.getMessage());
		}

	}
	
	@RequestMapping(value="/groupStats/{id}/byUserAndDay",method=RequestMethod.GET)
	@ApiOperation(value = "根据时间和用户显示统计数据")
	public Object showProjectBranchByUserAndDay(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id,
			@RequestParam(required = false) @ApiParam(name = "dateformat", value = "时间格式") String dateformat,
			@RequestParam(required = false) @ApiParam(name = "lastDate", value = "时间范围") Integer lastDate){

		try {
			return setJson(SUCCESS_CODE, groupService.showStatsByUserAndDay(id,MongoDateUtil.getFormat(dateformat)
					,MongoDateUtil.getOperation(id, dateformat, lastDate)));
		} catch (Exception e) {
			logger.error("根据时间和用户显示统计数据异常：", e);
			return setJson(FAIL_CODE, e.getMessage());
		}

	}
	
	@RequestMapping(value="/groupStats/{id}/byDayAndUser",method=RequestMethod.GET)
	@ApiOperation(value = "根据时间和用户显示统计数据")
	public Object showProjectBranchByDayAndUser(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id,
			@RequestParam(required = false) @ApiParam(name = "dateformat", value = "时间格式") String dateformat,
			@RequestParam(required = false) @ApiParam(name = "lastDate", value = "时间范围") Integer lastDate){

		try {
			return setJson(SUCCESS_CODE, groupService.showStatsByDayAndUser(id,MongoDateUtil.getFormat(dateformat)
					,MongoDateUtil.getGroupOperation(id, dateformat, lastDate)));
		} catch (Exception e) {
			logger.error("根据时间和用户显示统计数据异常：", e);
			return setJson(FAIL_CODE, e.getMessage());
		}

	}
	
	@RequestMapping(value="/groupStats/byDay",method=RequestMethod.GET)
	@ApiOperation(value = "根据时间显示统计所有项目数据")
	public Object showProjectBranchStats(@RequestParam(required = false) @ApiParam(name = "dateformat", value = "时间格式") String dateformat,
			@RequestParam(required = false) @ApiParam(name = "lastDate", value = "时间范围") Integer lastDate){

		try {
			return setJson(SUCCESS_CODE, groupService.statsByDay(MongoDateUtil.getFormat(dateformat),MongoDateUtil.getGroupOperation(null, dateformat, lastDate)));
		} catch (Exception e) {
			logger.error("根据时间显示统计所有项目数据异常：", e);
			return setJson(FAIL_CODE, e.getMessage());
		}

	}
	
	@RequestMapping(value="/groupStats/{id}/users",method=RequestMethod.GET)
	@ApiOperation(value = "获取项目所有提交者")
	public Object showProjectBranchByDayAndUser(@ApiParam(required = true, name = "id", value = "分支id") @PathVariable  String id){

		try {
			return setJson(SUCCESS_CODE, groupService.getProAllAuthorName(id));
		} catch (Exception e) {
			logger.error("获取项目所有提交者异常：", e);
			return setJson(FAIL_CODE, e.getMessage());
		}

	}
	
}
