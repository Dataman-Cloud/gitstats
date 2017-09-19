package com.dataman.gitstats.service;

import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	GitLabApi gitLabApi;
	
	@Autowired
	ProjectRepository projectRepository;
}
