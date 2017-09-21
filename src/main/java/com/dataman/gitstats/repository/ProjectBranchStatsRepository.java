package com.dataman.gitstats.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.ProjectBranchStats;

@Component
public interface ProjectBranchStatsRepository extends MongoRepository<ProjectBranchStats, String>{

	
	public List<ProjectBranchStats> findByProjectid(String projectid);
	
}
