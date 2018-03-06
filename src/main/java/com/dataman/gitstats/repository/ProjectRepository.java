package com.dataman.gitstats.repository;

import com.dataman.gitstats.po.ProjectStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectRepository extends MongoRepository<ProjectStats, String>{

	
//	public List<ProjectBranchStats> findByProjectid(String projectid);

	public Long deleteByGroupId(String groupId);
//	public List<ProjectBranchStats> findByProjectidIn(List<String> ids);
	
	public List<ProjectStats> findByStatus(int status);
	
}
