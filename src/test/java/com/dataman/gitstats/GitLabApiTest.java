package com.dataman.gitstats;


import java.util.Calendar;
import java.util.Date;
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
	
	static String LINETAG = "[?line?]";
	
	@Test
	public void getUsers() throws GitLabApiException{
		User user= gitLabApi.getUserApi().getCurrentUser();
		System.out.println(JSON.toJSONString(user)); 
	}
	@Test
	public void getCommit() throws GitLabApiException{

		List<Commit> list= gitLabApi.getCommitsApi().getCommits(1);
		System.out.println(JSON.toJSONString(list)); 
//		
//		Calendar cal=Calendar.getInstance();
//		Date currdate=cal.getTime();
//		cal.set(2000, 1, 1);
//		Date begindate=cal.getTime();
//		List<Commit> list3= gitLabApi.getCommitsApi().getCommits(1, "dev", begindate, currdate);
//		System.out.println(JSON.toJSONString(list3)); 
//		
//		Commit commit= gitLabApi.getCommitsApi().getCommit(1, "1d1742bfddda9fe43d6818f42c28292db9983610");
//		System.out.println(JSON.toJSONString(commit)); 
//		
//		
//		List<Diff> list2= gitLabApi.getCommitsApi().getDiff(1, "bd157a3e0c6c05ea90b78148db6ac0bc6662ab05");
//		System.out.println(JSON.toJSONString(list2)); 
		
		List<Diff> list4= gitLabApi.getCommitsApi().getDiff(1, "efd36965037a21013bfa650a8d7f86521506922c");
		System.out.println(JSON.toJSONString(list4)); 
		
		
		for (Diff diff : list4) {
			String diffstr=diff.getDiff();
			String[] lines= diffstr.split("\n");
			for (String line : lines) {
				System.out.println(line);
			}
		}
		Commit commit= gitLabApi.getCommitsApi().getCommit(1, "efd36965037a21013bfa650a8d7f86521506922c");
		System.out.println(JSON.toJSONString(commit)); 
		
		// System.out.println("\n /n+\n \\n+\n++\n");
		
		
	}
	
	@Test
	public void projectTest() throws GitLabApiException{
		List<Project> list = gitLabApi.getProjectApi().getProjects("simple-maven-project-with-tests");
		System.out.println(JSON.toJSONString(list)); 
	}
}
