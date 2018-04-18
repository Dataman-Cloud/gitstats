package com.dataman.gitstats.web;

import com.dataman.gitstats.service.WebHookService;
import io.swagger.annotations.ApiParam;
import org.gitlab4j.api.models.ProjectHook;
import org.gitlab4j.api.webhook.PushEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "webHook API")
@RestController
@RequestMapping(value = "/webHook", produces = "application/json;charset=utf-8")
public class WebHookController extends BaseController {

	private  Logger logger= LoggerFactory.getLogger(this.getClass());
	@Autowired
	private WebHookService hookService;

	@RequestMapping(value = "get/{projectId}",method = RequestMethod.GET)
	@ApiOperation(value = "获取某个分支所属项目的webhook", notes = "获取某个分支所属项目的webhook")
	public Object getProjectHooks(HttpServletRequest request,@ApiParam(required = true, name = "branchId", value = "项目分支id")@PathVariable String branchId) throws Exception {

		List<ProjectHook> hooks= hookService.getProjectHook(branchId);
		return  setJson(hooks);

	}

	@RequestMapping(value="/listener",method=RequestMethod.POST)
	@ApiOperation(value = "gitlab的webHook接收方法", notes = "gitlab的webHook接收方法")
	public Object receiveWebHook(HttpServletRequest request) throws Exception {
		hookService.handleWebHookEvent(request);
		return null;
	}
}
