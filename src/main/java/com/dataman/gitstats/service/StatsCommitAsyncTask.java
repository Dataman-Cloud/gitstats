package com.dataman.gitstats.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CommitStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.dataman.gitstats.po.CommitStatsPo;
import com.dataman.gitstats.repository.CommitStatsRepository;
import com.dataman.gitstats.service.AsyncTask.V3StatsCallback;
import com.dataman.gitstats.util.ClassUitl;
import com.dataman.gitstats.vo.CommitStatsVo;

@Component
public class StatsCommitAsyncTask {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommitStatsRepository commitStatsRepository;
	
	@Async
	public Future<CommitStatsVo> commitstats(List<Commit> list,GitLabApi api,int pid,
			String branchId,int pageNum,CountDownLatch cdl,V3StatsCallback back) throws GitLabApiException{
		logger.info("加载第"+pageNum+"页数据");
		long begin = System.currentTimeMillis();
		int addRow=0;
		int removeRow=0;
		CommitStatsVo vo = new CommitStatsVo();
		
		try {
			for (Commit commit : list) {
				CommitStatsPo csp=new CommitStatsPo();
				try {
					csp=ClassUitl.copyProperties(commit, csp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.info(e.getMessage());
				}
				csp.setBranchId(branchId);
				Commit sigleCommit= api.getCommitsApi().getCommit(pid, commit.getId());
				CommitStats stats= sigleCommit.getStats();
				csp.set_id();
				csp.setAddRow(stats.getAdditions());
				csp.setRemoveRow(stats.getDeletions());
				csp.setCrateDate(new Date());
				try {
					commitStatsRepository.insert(csp);
				} catch (Exception e) {
					logger.info(e.getMessage());
				}
				addRow+=stats.getAdditions();
				removeRow+=stats.getDeletions();
			}
			vo.setAddrow(addRow);
			vo.setRemoverow(removeRow);
			long usetime = begin-System.currentTimeMillis();
			logger.info("加载第"+pageNum+"页数据\taddrow:"+addRow+"\tremoveRow"+removeRow);
			logger.info("加载第"+pageNum+"页数据完成,耗时:"+usetime+"ms");
		} catch (Exception e) {
			logger.info("加载第"+pageNum+"页数据失败");
			e.printStackTrace();
		}finally {
			if(null!=cdl){
				cdl.countDown();
			}
			if(null != back ){
				back.call(addRow, removeRow, pageNum, list.size());
			}
		}
		return new AsyncResult<CommitStatsVo>(vo);
	}
}
