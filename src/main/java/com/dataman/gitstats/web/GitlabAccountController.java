package com.dataman.gitstats.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dataman.gitstats.param.AddAccountParam;
import com.dataman.gitstats.service.GitlabAccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Account API")
@RestController
@RequestMapping(value = "/account", produces = "application/json;charset=utf-8")
public class GitlabAccountController extends BaseController{

	@Autowired
	GitlabAccountService gitlabAccountService;
	
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ApiOperation(value = "添加gitlab帐号")
	public Object addAccount(AddAccountParam param){
		json.clear();
		
		return json;
	}
	
}
