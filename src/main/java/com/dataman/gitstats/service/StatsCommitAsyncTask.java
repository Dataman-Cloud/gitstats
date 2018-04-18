package com.dataman.gitstats.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CommitStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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

	private  Map<String,Integer> allPageNum=new HashMap<String,Integer>();
	
	public Future<CommitStatsVo> commitstats(List<Commit> list,GitLabApi api,int pid,
			String branchId,int pageNum,CountDownLatch cdl,V3StatsCallback back) throws Exception{
		addPageNum(branchId);
		logger.info("加载第"+allPageNum.get(branchId)+"页数据");
		long begin = System.currentTimeMillis();
		int addRow=0;
		int removeRow=0;
		CommitStatsVo vo = new CommitStatsVo();

			for (Commit commit : list) {
				CommitStatsPo csp=new CommitStatsPo();
				csp=ClassUitl.copyProperties(commit, csp);
				csp.setBranchId(branchId);
				Commit sigleCommit= api.getCommitsApi().getCommit(pid, commit.getId());
				CommitStats stats= sigleCommit.getStats();
				csp.set_id(branchId+"_"+commit.getId());
				csp.setAddRow(stats.getAdditions());
				csp.setRemoveRow(stats.getDeletions());
				csp.setCrateDate(new Date());
				try{//TODO(此处需捕捉唯一索引异常)
					commitStatsRepository.insert(csp);
				}catch(DuplicateKeyException e){
					logger.error("插入数据库异常：",e);
					continue;
				}
				addRow+=stats.getAdditions();
				removeRow+=stats.getDeletions();
			}
			vo.setAddrow(addRow);
			vo.setRemoverow(removeRow);
			long usetime = begin-System.currentTimeMillis();
			logger.info("加载第"+allPageNum.get(branchId)+"页数据\taddrow:"+addRow+"\tremoveRow"+removeRow);
			logger.info("加载第"+allPageNum.get(branchId)+"页数据完成,耗时:"+usetime+"ms");

			if (null != cdl) {
				cdl.countDown();
			}
			if (null != back) {
				back.call(addRow, removeRow, allPageNum.get(branchId), list.size());
			}
		return new AsyncResult<CommitStatsVo>(vo);
	}

	public CommitStatsVo commitstats2(List<Commit> list,GitLabApi api,int pid,
											 String branchId,String groupId,int pageNum) throws Exception{
		logger.info("加载第"+pageNum+"页数据");
		long begin = System.currentTimeMillis();
		int addRow=0;
		int removeRow=0;
		int commits=0;
		CommitStatsVo vo = new CommitStatsVo();

			for (Commit commit : list) {
				CommitStatsPo csp=new CommitStatsPo();
				csp=ClassUitl.copyProperties(commit, csp);
				csp.setBranchId(branchId);
				Commit sigleCommit= null;
				sigleCommit=api.getCommitsApi().getCommit(pid, commit.getId());
				CommitStats stats= sigleCommit.getStats();
				csp.set_id(groupId+"_"+commit.getId());
				csp.setGroupId(groupId);
				csp.setAddRow(stats.getAdditions());
				csp.setRemoveRow(stats.getDeletions());
				csp.setCrateDate(new Date());
				try{//TODO(此处需捕捉唯一索引异常)
					commitStatsRepository.insert(csp);
				}catch(DuplicateKeyException e){
					// logger.error("插入数据库异常：",e);
					continue;
				}


				addRow+=stats.getAdditions();
				removeRow+=stats.getDeletions();
				commits+=1;
			}
			vo.setAddrow(addRow);
			vo.setRemoverow(removeRow);
			vo.setCommit(commits);
			long usetime = begin-System.currentTimeMillis();
			logger.info("加载第"+pageNum+"页数据\taddrow:"+addRow+"\tremoveRow"+removeRow);
			logger.info("加载第"+pageNum+"页数据完成,耗时:"+usetime+"ms");
		return vo;
	}

	private  synchronized void addPageNum(String pbsid){
		if(allPageNum.get(pbsid)==null){
			allPageNum.put(pbsid,1);
		}else{
			allPageNum.put(pbsid,allPageNum.get(pbsid)+1);
		}
	}

	public   void clearPageNum(String pbsid){
		allPageNum.remove(pbsid);
	}
}
