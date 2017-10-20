package com.dataman.gitstats;


import java.util.Date;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON; 

@RunWith(SpringRunner.class) 
@SpringBootTest()
public class GitLabApiTest {
	
	@Test
	public void test(){
		
		
		GitLabApi api;
		Project p=null;
		Pager<Commit> page=null;
		try {
			api = GitLabApi.login(ApiVersion.V3, "http://192.168.199.32:8004", "root", "12345678");
			p= api.getProjectApi().getProject(4);
			System.out.println(JSON.toJSONString(p));
			page= api.getCommitsApi().getCommits(4, "master", null, new Date(),100);
			System.out.println(JSON.toJSONString(page));
			List<Commit> list= page.first();
			System.out.println(JSON.toJSONString(list));
			int i=0;
			while (page.hasNext()) {
				i++;
				list=page.next();
				System.out.println("pagesize:"+i);
				System.out.println(JSON.toJSONString(list));
			}
			
		} catch (GitLabApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
}
