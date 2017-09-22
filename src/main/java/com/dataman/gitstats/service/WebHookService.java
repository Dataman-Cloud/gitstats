package com.dataman.gitstats.service;

import com.dataman.gitstats.po.ProjectBranchStats;
import com.dataman.gitstats.po.ProjectStats;
import com.dataman.gitstats.po.PushEventRecord;
import com.dataman.gitstats.repository.CommitStatsRepository;
import com.dataman.gitstats.repository.ProjectBranchStatsRepository;
import com.dataman.gitstats.repository.ProjectRepository;
import com.dataman.gitstats.repository.PushEventRecordRepository;
import com.dataman.gitstats.util.ClassUitl;
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.PushEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by biancl on 2017-09-21.
 */
@Service
public class WebHookService {

    private   Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PushEventRecordRepository recordRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AsyncTask asyncTask;


    public PushEventRecord add(PushEvent event) throws Exception {
        PushEventRecord record=new PushEventRecord();
        ClassUitl.copyProperties(event,record);
        return recordRepository.save(record);
    }

    public void handlePushEvent(PushEvent event) throws Exception {
        PushEventRecord record=this.add(event);
        ProjectStats projectStats=projectService.findProjectStatsByPushEvent(event);
        if(projectStats!=null){
            ProjectBranchStats branchStats=projectService.findProjectBranchStatsByProjectIdAndBranch(projectStats.getId(), event.getBranch());
            if(branchStats!=null){
                record.setStatus(record.HANDLING);
                asyncTask.saveCommitStatsFromEventCommitsList(record.getCommits());

            }else{
                record.setStatus(record.NEED_NOT_HANDLE_NO_THIS_BRANCH);
            }
        } else {
            record.setStatus(record.NEED_NOT_HANDLE_NO_THIS_PROJECT);
        }
        recordRepository.save(record);
    }





}
