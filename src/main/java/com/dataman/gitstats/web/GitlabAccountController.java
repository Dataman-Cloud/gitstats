package com.dataman.gitstats.web;

import javax.validation.Valid;

import com.dataman.gitstats.annotation.AuthRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.dataman.gitstats.param.AddAccountParam;
import com.dataman.gitstats.service.GitlabAccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "Account API")
@RestController
@RequestMapping(value = "/account", produces = "application/json;charset=utf-8")
public class GitlabAccountController extends BaseController{

	@Autowired
	GitlabAccountService gitlabAccountService;

	@AuthRequired
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ApiOperation(value = "添加gitlab帐号")
	public Object addAccount(@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
			@Valid @RequestBody AddAccountParam param,BindingResult bingingresult){

		if(bingingresult.hasErrors()){
			return  setJson(PARAMERR_CODE,bingingresult.getAllErrors());
		}
		try {
			int ret= gitlabAccountService.addGitlabAccount(param);
			if(ret==0){
				return  setJson(SUCCESS_CODE, "保存成功");
			}else if(ret==1){
				return  setJson(FAIL_CODE, "已存在");
			}else if(ret==2){
				return  setJson(FAIL_CODE, "验证不通过");
			}else{
				return  setJson(FAIL_CODE, "未知错误");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  setJson(FAIL_CODE, e.getMessage());
		}

	}

	@RequestMapping(value="/",method=RequestMethod.GET)
	@ApiOperation(value = "获取所有的帐号")
	public Object allAccount(){

		try {
			return  setJson(SUCCESS_CODE, gitlabAccountService.getAll());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  setJson(FAIL_CODE, e.getMessage());
		}

	}
	@AuthRequired
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	@ApiOperation(value = "删除帐号")
	public Object delAccount(@ApiParam(required = true, name = "token", value = "请求头token权限认证") @RequestHeader String token,
			@ApiParam(required = true, name = "id", value = "帐号id") @PathVariable  String id){

		try {
			return  setJson(SUCCESS_CODE, gitlabAccountService.delAccount(id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  setJson(FAIL_CODE, e.getMessage());
		}

	}
	
}
