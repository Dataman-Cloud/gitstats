package com.dataman.gitstats.service;

import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.repository.CommitStatsRepository;

@Service
public class CommitStatsService {

	// @Autowired
	GitLabApi gitLabApi;
	@Autowired
	CommitStatsRepository commitStatsRepository;
	
	
}
