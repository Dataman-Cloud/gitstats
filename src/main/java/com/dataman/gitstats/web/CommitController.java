package com.dataman.gitstats.web;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	@ApiOperation(value = "查询指定项目的提交记录")
	public Object showProjecBranchCommits(@ApiParam(required = true, name = "pbid", value = "分支id") @PathVariable  String pbid,
			@RequestParam(required = false) @ApiParam(name = "authorName", value = "提交人") String authorName,
			@RequestParam(required = false) @ApiParam(name = "begin", value = "时间范围",defaultValue = "2010/01/01") Date begin,
			@RequestParam(required = false) @ApiParam(name = "end", value = "时间范围",defaultValue = "2099/01/01") Date end,
			@RequestParam(required = false, defaultValue = "0") @ApiParam(name = "pageNum", value = "页码") Integer pageNum,
			@RequestParam(required = false, defaultValue = "20") @ApiParam(name = "pageSize", value = "页大小") Integer pageSize){
		json.clear();
		try {
			Calendar cal= Calendar.getInstance();
			if(null == begin){
				cal.set(2010, 1, 1);
				begin=cal.getTime();
			}
			if(null == end){
				cal.set(2099, 1, 1);
				end=cal.getTime();
			}
			setJson(SUCCESS_CODE,commitStatsService.find(pbid, authorName,begin, end, pageSize, pageNum));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJson(FAIL_CODE, e.getMessage());
		}
		return json;
	}
}
