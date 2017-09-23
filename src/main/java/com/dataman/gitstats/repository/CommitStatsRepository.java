package com.dataman.gitstats.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.CommitStatsPo;
/**
 * @ClassName: CommitStatsRepository
 * @Description: 数据处理 
 * @author liuqing 
 * @date 2017年9月19日 上午11:32:49 
 * @Copyright © 2017北京数人科技有限公司
 */
@Component
public interface CommitStatsRepository  extends MongoRepository<CommitStatsPo,String>{
	
	public Long deleteByProidAndBranch(String proid,String branch);
	
	public Long deleteByProid(String proid);

}
