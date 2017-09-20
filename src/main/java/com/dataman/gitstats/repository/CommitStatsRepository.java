package com.dataman.gitstats.repository;

import java.util.List;

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
	
	/**
	 * @method delByProjectName(根据项目名称删除)
	 * @return Long
	 * @author liuqing
	 * @date 2017年9月19日 下午5:34:47
	 */
	public Long deleteByProjectName(String projectName);
	
	

}
