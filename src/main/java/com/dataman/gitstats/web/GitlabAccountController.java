package com.dataman.gitstats.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
	
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ApiOperation(value = "添加gitlab帐号")
	public Object addAccount(@Valid @RequestBody AddAccountParam param,BindingResult bingingresult){
		json.clear();
		if(bingingresult.hasErrors()){
			setJson(PARAMERR_CODE,bingingresult.getAllErrors());
			return json;
		}
		try {
			int ret= gitlabAccountService.addGitlabAccount(param);
			if(ret==0){
				setJson(SUCCESS_CODE, "保存成功");
			}else if(ret==1){
				setJson(FAIL_CODE, "已存在");
			}else if(ret==2){
				setJson(FAIL_CODE, "验证不通过");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="/",method=RequestMethod.GET)
	@ApiOperation(value = "获取所有的帐号")
	public Object allAccount(){
		json.clear();
		try {
			setJson(SUCCESS_CODE, gitlabAccountService.getAll());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	@ApiOperation(value = "删除帐号")
	public Object delAccount(@ApiParam(required = true, name = "id", value = "帐号id") @PathVariable  String id){
		json.clear();
		try {
			setJson(SUCCESS_CODE, gitlabAccountService.delAccount(id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
	
}
