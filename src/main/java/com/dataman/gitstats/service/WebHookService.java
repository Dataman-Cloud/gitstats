package com.dataman.gitstats.service;

import com.dataman.gitstats.po.PushEventRecord;
import com.dataman.gitstats.repository.CommitStatsRepository;
import com.dataman.gitstats.repository.ProjectBranchStatsRepository;
import com.dataman.gitstats.repository.ProjectRepository;
import com.dataman.gitstats.repository.PushEventRecordRepository;
import com.dataman.gitstats.util.ClassUitl;
import org.gitlab4j.api.webhook.PushEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by biancl on 2017-09-21.
 */
@Service
public class WebHookService {

    private  Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PushEventRecordRepository recordRepository;

    @Autowired
    private CommitStatsRepository commitStatsRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectBranchStatsRepository branchStatsRepository;

    public void add(PushEvent event) throws Exception {
        PushEventRecord record=new PushEventRecord();
        ClassUitl.copyProperties(event,record);
        record.setStatus(record.DEFAULT);
        recordRepository.insert(record);

    }

    private void handlePushEvent(PushEventRecord record){
        Integer projectId=record.getProjectId();
        record.getProject();
    }

}
