package com.dataman.gitstats.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter{
	
	@Value("${gitstats.indexpath}")
	private String indexpath;
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/static/"+indexpath); // 添加首页
		// registry.addViewController("/manager").setViewName("redirect:/static/account.html"); // 添加管理页面
	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/swagger/");
	}
}
