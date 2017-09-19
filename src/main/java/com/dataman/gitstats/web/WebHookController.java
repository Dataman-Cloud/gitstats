package com.dataman.gitstats.web;

import org.gitlab4j.api.webhook.AbstractPushEvent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "webHook API")
@RestController
@RequestMapping(value = "/webHook", produces = "application/json;charset=utf-8")
public class WebHookController extends BaseController {

	@RequestMapping(value="/receive",method=RequestMethod.POST)
	@ApiOperation(value = "gitlab的webHook接收方法", notes = "gitlab的webHook接收方法")
	public Object receiveWebHook(AbstractPushEvent  event){
		json.clear();
		return json;
	}
}
