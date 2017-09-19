package com.dataman.gitstats.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataman.gitstats.service.CommitStatsService;

import io.swagger.annotations.Api;

@Api(value = "Commit API")
@RestController
@RequestMapping(value = "/commit", produces = "application/json;charset=utf-8")
public class CommitController extends BaseController {

	@Autowired
	CommitStatsService commitStatsService;
	
}
