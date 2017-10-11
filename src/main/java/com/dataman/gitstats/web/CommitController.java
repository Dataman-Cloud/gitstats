package com.dataman.gitstats.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dataman.gitstats.service.CommitStatsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "Commit API")
@RestController
@RequestMapping(value = "/commit", produces = "application/json;charset=utf-8")
public class CommitController extends BaseController {

	@Autowired
	CommitStatsService commitStatsService;
	
	@RequestMapping(value="/{pbid}",method=RequestMethod.GET)
	@ApiOperation(value = "显示统计数据")
	public Object showProjecBranchCommits(@ApiParam(required = true, name = "pbid", value = "分支id") @PathVariable  String pbid){
		json.clear();
		
		return json;
	}
}
