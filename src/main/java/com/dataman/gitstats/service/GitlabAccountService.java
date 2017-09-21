package com.dataman.gitstats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.param.AddAccountParam;
import com.dataman.gitstats.repository.GitlabAccountRepository;

@Service
public class GitlabAccountService {

	@Autowired
	GitlabAccountRepository gitlabAccountRepository;
	/**
	 * @method addGitlabAccount(添加帐号)
	 * @return int
	 * @author liuqing
	 * @date 2017年9月21日 上午10:43:58
	 */
	public int addGitlabAccount(AddAccountParam param){
		int SUCCESS =0;
		
		return SUCCESS;
	}
	
}
