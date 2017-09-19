package com.dataman.gitstats.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.User;

import com.alibaba.fastjson.JSON;
import com.dataman.gitstats.properties.GitlabProperties;

@Configuration
@EnableConfigurationProperties(GitlabProperties.class)
public class GitlabConfiguration {
	@Autowired
	GitlabProperties properties;
	
	@Bean
	public GitLabApi GitLabApi() throws GitLabApiException{
		GitLabApi api= GitLabApi.login(ApiVersion.V3, properties.getUrl(), properties.getUser(), properties.getPwd());
		return api;
	}
}
