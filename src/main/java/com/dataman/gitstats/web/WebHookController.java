package com.dataman.gitstats.web;

import com.dataman.gitstats.service.WebHookService;
import org.gitlab4j.api.webhook.PushEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "webHook API")
@RestController
@RequestMapping(value = "/webHook", produces = "application/json;charset=utf-8")
public class WebHookController extends BaseController {

	private  Logger logger= LoggerFactory.getLogger(this.getClass());
	@Autowired
	private WebHookService hookService;

	@RequestMapping(value="/listener",method=RequestMethod.POST)
	@ApiOperation(value = "gitlab的webHook接收方法", notes = "gitlab的webHook接收方法")
	public Object receiveWebHook(PushEvent  event) throws Exception {
		hookService.handlePushEvent(event);
		json.clear();
		return json;
	}
}
