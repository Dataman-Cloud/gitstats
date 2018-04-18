package com.dataman.gitstats.repository;

import com.dataman.gitstats.po.GroupStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GroupStatsRepository extends MongoRepository<GroupStats, String>{

	
	public List<GroupStats> findByStatus(int status);
	
}
