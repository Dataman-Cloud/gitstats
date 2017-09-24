package com.dataman.gitstats.service;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import java.util.*;

import java.util.concurrent.Future;

import com.dataman.gitstats.po.MergeRequestEventRecord;
import com.dataman.gitstats.po.PushEventRecord;
import com.dataman.gitstats.repository.MergeRequestEventRecordRepository;
import com.dataman.gitstats.repository.PushEventRecordRepository;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Commit;

import org.gitlab4j.api.models.CommitStats;
import org.gitlab4j.api.webhook.EventCommit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.CommitStatsPo;
import com.dataman.gitstats.po.ProjectBranchStats;
import com.dataman.gitstats.repository.CommitStatsRepository;
import com.dataman.gitstats.repository.ProjectBranchStatsRepository;
import com.dataman.gitstats.util.ClassUitl;
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
	PushEventRecordRepository pushEventRecordRepository;

	@Autowired
	private MergeRequestEventRecordRepository mergeRequestEventRecordRepository;
	
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
		//分页获取 (每页获取 100个数据)
		Pager<Commit> page= gitLabApi.getCommitsApi().getCommits(projectId, branch, null, cal.getTime(),100);
		logger.info(pbs.getProjectname()+"."+pbs.getBranch()+":TotalPages:"+page.getTotalPages());
		CountDownLatch cdl=new CountDownLatch(page.getTotalPages());
		List<Future<CommitStatsVo>> stats=new ArrayList<>();
		//异步读取分页信息
		while (page.hasNext()) {
			List<Commit> list=  page.next();
			Future<CommitStatsVo> f= statsCommitAsyncTask.commitstats(list, gitLabApi, projectId, pid, branch, page.getCurrentPage(), cdl);
			stats.add(f);
		}
		// 计数机阻塞 返回结果
		try {
			cdl.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 统计 每页 返回的结果
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

	@Async
	public void saveCommitStatsFromPushEventCommitsList(PushEventRecord record,ProjectBranchStats projectBranchStats,List<EventCommit> eventCommitList) throws Exception {
		GitLabApi gitLabApi=gitlabUtil.getGitLabApi(projectBranchStats.getAccountid());
			while (projectBranchStats.getStatus()==0){
				Thread.sleep(1000);
				projectBranchStats=projectBranchStatsRepository.findOne(projectBranchStats.getId());
			}
		projectBranchStats.setStatus(0);
		projectBranchStatsRepository.save(projectBranchStats);
		CommitStatsPo commitStats;
		for(EventCommit eventCommit:eventCommitList){
			Commit commit=gitLabApi.getCommitsApi().getCommit(projectBranchStats.getProid(),eventCommit.getId());
			commitStats=new CommitStatsPo();
			ClassUitl.copyPropertiesExclude(commit, commitStats, new String[]{"parentIds","stats"});
			commitStats.setProid(projectBranchStats.getProjectid());
			// Set<String> branch=new HashSet<>();
			// branch.add(projectBranchStats.getBranch());
			commitStats.setBranch(projectBranchStats.getBranch());
			commitStats.setAddRow(commit.getStats().getAdditions());
			commitStats.setRemoveRow(commit.getStats().getDeletions());
			commitStats.setCrateDate(new Date());

			projectBranchStats.setTotalAddRow(projectBranchStats.getTotalAddRow()+commit.getStats().getAdditions());
			projectBranchStats.setTotalDelRow(projectBranchStats.getTotalDelRow() + commit.getStats().getDeletions());
			projectBranchStats.setTotalRow(projectBranchStats.getTotalAddRow()-projectBranchStats.getTotalDelRow());
			projectBranchStatsRepository.save(projectBranchStats);
			commitStatsRepository.save(commitStats);
		}
		projectBranchStats.setStatus(1);
		projectBranchStatsRepository.save(projectBranchStats);
		record.setStatus(record.FINISHED);
		record.setUpdateAt(new Date());
		pushEventRecordRepository.save(record);
	}

	@Async
	public void saveCommitStatsFromMergeRequestEventCommitsList(MergeRequestEventRecord record,ProjectBranchStats projectBranchStats,List<Commit> eventCommitList) throws Exception {
		GitLabApi gitLabApi=gitlabUtil.getGitLabApi(projectBranchStats.getAccountid());
		while (projectBranchStats.getStatus()==0){
			Thread.sleep(1000);
			projectBranchStats=projectBranchStatsRepository.findOne(projectBranchStats.getId());
		}
		projectBranchStats.setStatus(0);
		projectBranchStatsRepository.save(projectBranchStats);
		CommitStatsPo commitStats;
		for(Commit eventCommit:eventCommitList){
			commitStats=commitStatsRepository.findOne(eventCommit.getId());
			Commit commit=gitLabApi.getCommitsApi().getCommit(projectBranchStats.getProid(),eventCommit.getId());
			commitStats=new CommitStatsPo();
			ClassUitl.copyPropertiesExclude(commit, commitStats, new String[]{"parentIds","stats"});
			commitStats.setProid(projectBranchStats.getProjectid());
			// Set<String> branch=new HashSet<>();
			// branch.add(projectBranchStats.getBranch());
			commitStats.setBranch(projectBranchStats.getBranch());
			commitStats.setAddRow(commit.getStats().getAdditions());
			commitStats.setRemoveRow(commit.getStats().getDeletions());
			commitStats.setCrateDate(new Date());

			projectBranchStats.setTotalAddRow(projectBranchStats.getTotalAddRow()+commit.getStats().getAdditions());
			projectBranchStats.setTotalDelRow(projectBranchStats.getTotalDelRow() + commit.getStats().getDeletions());
			projectBranchStats.setTotalRow(projectBranchStats.getTotalAddRow()-projectBranchStats.getTotalDelRow());
			projectBranchStatsRepository.save(projectBranchStats);
			commitStatsRepository.save(commitStats);
		}
		projectBranchStats.setStatus(1);
		projectBranchStatsRepository.save(projectBranchStats);
		record.setStatus(record.FINISHED);
		record.setUpdateAt(new Date());
		mergeRequestEventRecordRepository.save(record);
	}
	
}
