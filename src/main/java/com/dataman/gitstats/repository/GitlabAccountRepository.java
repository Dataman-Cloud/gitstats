package com.dataman.gitstats.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.GitlabAccount;

@Component
public interface GitlabAccountRepository extends MongoRepository<GitlabAccount, String>{

	public GitlabAccount findByTypeAndUrlAndUserAndToken(int type,String url,String user,String token);

	
}
