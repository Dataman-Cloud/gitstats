package com.dataman.gitstats.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dataman.gitstats.po.CommitStatsPo;
import com.dataman.gitstats.repository.CommitStatsRepository;

@Service
public class CommitStatsService {

	@Autowired
	CommitStatsRepository commitStatsRepository;

	@Autowired
	MongoTemplate mongoTemplate;
	/**
	 * @method find(分页查询)
	 * @return Page<CommitStatsPo>
	 * @author liuqing
	 * @date 2017年10月19日 上午10:21:58
	 */
	public Page<CommitStatsPo> find(String pid,String authorName,Date begin,Date end,int pageSize,int pageNum){
		PageRequest pageReq=new PageRequest(pageNum, pageSize, new Sort(Direction.DESC,"createdAt"));
		if(StringUtils.isEmpty(authorName)){
			return commitStatsRepository.findByBranchIdAndCreatedAtBetween(pid, begin, end, pageReq);
		}
		return commitStatsRepository.findByBranchIdAndAuthorNameAndCreatedAtBetween(pid,authorName,begin, end, pageReq);
	}
	

}
