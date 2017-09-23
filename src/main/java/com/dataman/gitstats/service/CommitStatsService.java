package com.dataman.gitstats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.repository.CommitStatsRepository;

@Service
public class CommitStatsService {

	@Autowired
	CommitStatsRepository commitStatsRepository;
	

	
	
}
