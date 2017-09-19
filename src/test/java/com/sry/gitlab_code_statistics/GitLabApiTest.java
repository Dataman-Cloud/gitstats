package com.sry.gitlab_code_statistics;


import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON; 

@RunWith(SpringRunner.class) 
@SpringBootTest()
public class GitLabApiTest {
	
	@Autowired
	GitLabApi gitLabApi;
	
	@Test
	public void getUsers() throws GitLabApiException{
		User user= gitLabApi.getUserApi().getCurrentUser();
		System.out.println(JSON.toJSONString(user)); 
	}
	@Test
	public void getCommit() throws GitLabApiException{
		
		// List<Commit> list= gitLabApi.getCommitsApi().getCommits(1);
		// System.out.println(JSON.toJSONString(list)); 
		
		String commitid ="9c07cc7a544206bd02352d6cc7bba62dcbdc113a";
		
		
		
		List<Diff> list2= gitLabApi.getCommitsApi().getDiff(1, "efd36965037a21013bfa650a8d7f86521506922c");
		System.out.println(JSON.toJSONString(list2)); 
		
	}
	
}
