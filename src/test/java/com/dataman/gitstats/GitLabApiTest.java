package com.dataman.gitstats;


import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.Project;
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

		Commit commit= gitLabApi.getCommitsApi().getCommit(1, "1d1742bfddda9fe43d6818f42c28292db9983610");
		System.out.println(JSON.toJSONString(commit)); 
		List<Diff> list2= gitLabApi.getCommitsApi().getDiff(1, "1d1742bfddda9fe43d6818f42c28292db9983610");
		System.out.println(JSON.toJSONString(list2)); 
		
	}
	
	@Test
	public void projectTest() throws GitLabApiException{
		List<Project> list = gitLabApi.getProjectApi().getProjects("simple-maven-project-with-tests");
		System.out.println(JSON.toJSONString(list)); 
	}
}
