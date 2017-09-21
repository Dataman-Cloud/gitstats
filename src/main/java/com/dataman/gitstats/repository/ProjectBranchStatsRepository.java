package com.dataman.gitstats.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.ProjectBranchStats;

@Component
public interface ProjectBranchStatsRepository extends MongoRepository<ProjectBranchStats, String>{

}
