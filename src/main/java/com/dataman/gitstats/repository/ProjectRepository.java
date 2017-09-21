package com.dataman.gitstats.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.ProjectStats;
/**
 * @ClassName: ProjectRepository
 * @Description: 数据处理 
 * @author liuqing 
 * @date 2017年9月19日 上午11:33:13 
 * @Copyright © 2017北京数人科技有限公司
 */
@Component
public interface ProjectRepository extends MongoRepository<ProjectStats,String> {
	
	ProjectStats findByNameAndAccountId(String name,String accountId);
}
