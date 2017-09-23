package com.dataman.gitstats.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Commit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.ProjectBranchStats;
import com.dataman.gitstats.repository.CommitStatsRepository;
import com.dataman.gitstats.repository.ProjectBranchStatsRepository;
import com.dataman.gitstats.util.GitlabUtil;
import com.dataman.gitstats.vo.CommitStatsVo;


@Component
public class AsyncTask {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	GitlabUtil gitlabUtil;
	
	@Autowired
	ProjectBranchStatsRepository projectBranchStatsRepository;
	
	@Autowired
	CommitStatsRepository commitStatsRepository;
	
	@Autowired
	StatsCommitAsyncTask statsCommitAsyncTask;
	/**
	 * @method initProjectStats(初始化数据)
	 * @return String
	 * @author liuqing
	 * @throws GitLabApiException 
	 * @throws Exception 
	 * @date 2017年9月19日 下午4:31:20
	 */
	@Async
	public Future<String> initProjectStats(ProjectBranchStats pbs) throws GitLabApiException{
		logger.info("初始化开始:"+pbs.getProjectname()+"."+pbs.getBranch());
		Calendar cal =Calendar.getInstance();
		long begin = System.currentTimeMillis();
		int addRow=0,removeRow=0;
		int projectId= pbs.getProid();
		String branch=pbs.getBranch();
		String pid=pbs.getProjectid();
		// 清理数据 
		commitStatsRepository.deleteByProidAndBranch(pid, branch);
		GitLabApi gitLabApi=  gitlabUtil.getGitLabApi(pbs.getAccountid());
		//获取当前项目当前分支的所有commit
		Pager<Commit> page= gitLabApi.getCommitsApi().getCommits(projectId, branch, null, cal.getTime(),100);
		logger.info(pbs.getProjectname()+"."+pbs.getBranch()+":TotalPages:"+page.getTotalPages());
		CountDownLatch cdl=new CountDownLatch(page.getTotalPages());
		List<Future<CommitStatsVo>> stats=new ArrayList<>();
		while (page.hasNext()) {
			List<Commit> list=  page.next();
			Future<CommitStatsVo> f= statsCommitAsyncTask.commitstats(list, gitLabApi, projectId, pid, branch, page.getCurrentPage(), cdl);
			stats.add(f);
		}
		try {
			cdl.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (Future<CommitStatsVo> future : stats) {
			try {
				CommitStatsVo vo= future.get();
				addRow+=vo.getAddrow();
				removeRow+=vo.getRemoverow();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		pbs.setStatus(1);
		pbs.setTotalAddRow(addRow);
		pbs.setTotalDelRow(removeRow);
		pbs.setTotalRow(addRow-removeRow);
		pbs.setLastupdate(cal.getTime());
		try {
			projectBranchStatsRepository.save(pbs);  //保存跟新记录
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		logger.info("update success");
		long usetime = begin-System.currentTimeMillis();
		logger.info("初始化"+pbs.getProjectname()+"."+pbs.getBranch()+"完成耗时:"+usetime+"ms");
		return new AsyncResult<String>("初始化完成");  
	}
	
	
}
