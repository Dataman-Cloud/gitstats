package com.dataman.gitstats.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.GitlabAccount;
import com.dataman.gitstats.repository.GitlabAccountRepository;

@Component
public class GitlabUtil {

	
	@Autowired
	GitlabAccountRepository gitlabAccountRepository;
	
	Map<String,GitLabApi> map=new HashMap<String,GitLabApi>(2);
	
	public GitLabApi getGitLabApi(String id) throws GitLabApiException{
		if(map.containsKey(id)){
			return map.get(id);
		}
		GitlabAccount ga= gitlabAccountRepository.findOne(id);
		GitLabApi gitLabApi = login(ga);
		map.put(id, gitLabApi);
		return gitLabApi;
	}
	
	public GitLabApi login(GitlabAccount ga) throws GitLabApiException{
		Calendar cal=Calendar.getInstance();
		GitLabApi gitlabApi =null;
		if(ga.getType()==1){
			gitlabApi=new GitLabApi(ApiVersion.V4,ga.getUrl(),ga.getToken());
			ga.setSession(gitlabApi.getSession());
		}else{
			gitlabApi =GitLabApi.login(ApiVersion.V4, ga.getUrl(), ga.getUser(), ga.getPwd());
			ga.setSession(gitlabApi.getSession());
		}
		ga.setLastupdate(cal.getTime());
		gitlabAccountRepository.save(ga);
		return gitlabApi;
	}
}
