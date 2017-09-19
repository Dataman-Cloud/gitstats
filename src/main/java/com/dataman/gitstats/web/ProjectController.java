package com.dataman.gitstats.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dataman.gitstats.service.ProjectService;

import io.swagger.annotations.Api;

@Api(value = "Project API")
@RestController
@RequestMapping(value = "/project", produces = "application/json;charset=utf-8")
public class ProjectController extends BaseController {
	
	@Autowired
	ProjectService projectService;
}
