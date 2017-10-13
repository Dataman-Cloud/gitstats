package com.dataman.gitstats.service;

import java.util.Calendar;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApi.ApiVersion;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dataman.gitstats.param.AddAccountParam;
import com.dataman.gitstats.po.GitlabAccount;
import com.dataman.gitstats.repository.GitlabAccountRepository;
import com.dataman.gitstats.util.Commnt;

@Service
public class GitlabAccountService {

	@Autowired
	GitlabAccountRepository gitlabAccountRepository;
	
	
	public List<GitlabAccount> getAll(){
		return gitlabAccountRepository.findAll();
	}
	
	public int delAccount(String id){
		int SUCCESS =0;
		gitlabAccountRepository.delete(id);
		return SUCCESS;
	}
	
	/**
	 * @method addGitlabAccount(添加帐号)
	 * @return int
	 * @author liuqing
	 * @date 2017年9月21日 下午12:25:13
	 */
	public int addGitlabAccount(AddAccountParam param){
		int SUCCESS =0,EXISTACCOUNT=1,CHECKERR=2;
		Calendar cal=Calendar.getInstance();
		GitlabAccount account=null;
		if(param.getId()==null||param.getId().length()==0) {
			//TODO	去重不应该带type，可考虑与其他持久化对象一样，以URL+user作为id确保唯一
			account = gitlabAccountRepository.findByTypeAndUrlAndUserAndToken(param.getType(),
					param.getUrl(), param.getUser(), param.getToken());
			if (account != null) {
				return EXISTACCOUNT; //当前记录已存在
			}
			//添加记录
			account = new GitlabAccount();
			account.setId(Commnt.createUUID());
			account.setCratedate(cal.getTime());
		}else{
			account=gitlabAccountRepository.findOne(param.getId());
		}
			account.setLastupdate(cal.getTime());
			account.setUrl(param.getUrl());
			account.setType(param.getType());
			account.setPwd(param.getPwd());
			account.setUser(param.getUser());
			account.setToken(param.getToken());
			//分类处理登录验证
			GitLabApi gitlabApi = null;
			if(param.getType()==1){
				gitlabApi=new GitLabApi(ApiVersion.V3,param.getUrl(),param.getToken());
				account.setSession(gitlabApi.getSession());
				account.setStats(1);
				try {
					User user= gitlabApi.getUserApi().getCurrentUser();
					account.setName(user.getName());
				} catch (GitLabApiException e) {
					// TODO Auto-generated catch block
					return CHECKERR;
				}
			}else if(param.getType()==0){
				try {
					gitlabApi =GitLabApi.login(ApiVersion.V3, param.getUrl(), param.getUser(), param.getPwd());
					account.setSession(gitlabApi.getSession());
					account.setStats(1);
					User user= gitlabApi.getUserApi().getCurrentUser();
					account.setName(user.getName());
				} catch (GitLabApiException e) {
					account.setStats(0);
					return CHECKERR;
				}
			}
			gitlabAccountRepository.save(account);
		//判断是否存在
		return SUCCESS;
	}
	
}
