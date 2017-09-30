package com.dataman.gitstats.service;

import com.dataman.gitstats.po.MergeRequestEventRecord;
import com.dataman.gitstats.po.ProjectBranchStats;
import com.dataman.gitstats.po.PushEventRecord;
import com.dataman.gitstats.properties.HookProperties;
import com.dataman.gitstats.repository.*;
import com.dataman.gitstats.util.ClassUitl;
import com.dataman.gitstats.util.GitlabUtil;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.ProjectHook;
import org.gitlab4j.api.utils.JacksonJson;
import org.gitlab4j.api.webhook.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by biancl on 2017-09-21.
 */
@Service
@EnableConfigurationProperties(HookProperties.class)
public class WebHookService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JacksonJson jacksonJson = new JacksonJson();

    @Autowired
    private PushEventRecordRepository recordRepository;

    @Autowired
    private MergeRequestEventRecordRepository mergeRequestEventRecordRepository;

    @Autowired
    private ProjectBranchService projectBranchService;

    @Autowired
    private AsyncTask asyncTask;

    @Autowired
    private GitlabUtil gitlabUtil;

    @Autowired
    private HookProperties hookProperties;


    public PushEventRecord addPushEventRecord(PushEvent event) throws Exception {
        PushEventRecord record = new PushEventRecord();
        ClassUitl.copyProperties(event, record);
        record.setId_(record.getBefore() +"_"+ record.getAfter());
        return recordRepository.save(record);
    }

    public MergeRequestEventRecord addMergeRequestEventRecord(MergeRequestEvent event) throws Exception {
        MergeRequestEventRecord record = new MergeRequestEventRecord();
        ClassUitl.copyProperties(event, record);
        record.setId_(event.getObjectAttributes().getUrl());
        return mergeRequestEventRecordRepository.save(record);
    }

    public void handleWebHookEvent(HttpServletRequest request) throws Exception {
        if (!isValidSecretToken(request)) {
            String message = "X-Gitlab-Token mismatch!";
            throw new GitLabApiException(message);
        }

        String eventName = request.getHeader("X-Gitlab-Event");
        InputStreamReader reader = new InputStreamReader(request.getInputStream());
        Event event = jacksonJson.unmarshal(Event.class, reader);
        ;
        switch (eventName) {
            case BuildEvent.X_GITLAB_EVENT:
                break;
            case IssueEvent.X_GITLAB_EVENT:
                break;
            case MergeRequestEvent.X_GITLAB_EVENT:
                handleMergeRequestEvent((MergeRequestEvent) event);
            case NoteEvent.X_GITLAB_EVENT:
                break;
            case PipelineEvent.X_GITLAB_EVENT:
                break;
            case PushEvent.X_GITLAB_EVENT:
                handlePushEvent((PushEvent) event);
            case TagPushEvent.X_GITLAB_EVENT:
            case WikiPageEvent.X_GITLAB_EVENT:
                break;

            default:
                String message = "Unsupported X-Gitlab-Event, event Name=" + eventName;
                throw new GitLabApiException(message);
        }

    }

    public void handlePushEvent(PushEvent event) throws Exception {
        if (recordRepository.findOne(event.getBefore() + event.getAfter()) == null) {
            PushEventRecord record = this.addPushEventRecord(event);
                ProjectBranchStats branchStats = projectBranchService.findProjectBranchStatsByParams(event.getProject().getWebUrl(),event.getProjectId(),event.getBranch());
                if (branchStats != null) {
                    record.setStatus(record.HANDLING);
                    asyncTask.saveCommitStatsFromPushEventCommitsList(record, branchStats, record.getCommits());
                } else {
                    record.setStatus(record.NEED_NOT_HANDLE_NO_THIS_BRANCH);
                }
            recordRepository.save(record);
        }
    }

    public void handleMergeRequestEvent(MergeRequestEvent event) throws Exception {
        if (mergeRequestEventRecordRepository.findOne(event.getObjectAttributes().getUrl()) == null) {

            MergeRequestEventRecord record = this.addMergeRequestEventRecord(event);
//            ProjectStats projectStats = projectBranchService.findProjectStatsByIdAndUrl(event.getObjectAttributes().getProjectId(), event.getObjectAttributes().getUrl());
                ProjectBranchStats branchStats = projectBranchService.findProjectBranchStatsByParams(event.getObjectAttributes().getUrl(),event.getObjectAttributes().getProjectId(), event.getObjectAttributes().getTargetBranch());
                if (branchStats != null) {
                    record.setStatus(record.HANDLING);
                    List<Commit> commits = getMergetRequestCommits(branchStats, event);
                    asyncTask.saveCommitStatsFromMergeRequestEventCommitsList(record, branchStats, commits);
                } else {
                    record.setStatus(record.NEED_NOT_HANDLE_NO_THIS_BRANCH);
                }
            mergeRequestEventRecordRepository.save(record);
        }
    }

    public List<Commit> getMergetRequestCommits(ProjectBranchStats projectBranchStats, MergeRequestEvent event) throws Exception {
        GitLabApi gitLabApi = gitlabUtil.getGitLabApi(projectBranchStats.getAccountid());
        return gitLabApi.getMergeRequestApi().getCommits(event.getObjectAttributes().getProjectId(), event.getObjectAttributes().getIid());
    }

    public void addGitlabPushEventWebHook(ProjectBranchStats projectBranchStats, String applicationUrl) throws Exception {
        logger.info("*********************hook url:{}", applicationUrl);
        GitLabApi gitLabApi = gitlabUtil.getGitLabApi(projectBranchStats.getAccountid());
        ProjectHook projectHook = new ProjectHook();
        projectHook.setPushEvents(true);
        projectHook.setMergeRequestsEvents(true);
        projectHook.setUrl(applicationUrl);
        projectHook.setToken("123");
        gitLabApi.getProjectApi().addHook(projectBranchStats.getProid(), applicationUrl, projectHook, true, null);
        logger.info("**************************添加成功*****************");
    }

    public List<ProjectHook> getProjectHook(String branchId) throws Exception {
        ProjectBranchStats projectBranchStats = projectBranchService.findProjectBranchStatsById(branchId);
        GitLabApi gitLabApi = gitlabUtil.getGitLabApi(projectBranchStats.getAccountid());
        return gitLabApi.getProjectApi().getHooks(projectBranchStats.getProid());
    }

    public boolean isValidSecretToken(HttpServletRequest request) {

        if (hookProperties.getSecretToken() != null) {
            String secretToken = request.getHeader("X-Gitlab-Token");
            return (isValidSecretToken(secretToken));
        }

        return (true);
    }

    public boolean isValidSecretToken(String secretToken) {
        return (hookProperties.getSecretToken() == null || hookProperties.getSecretToken().equals(secretToken) ? true : false);
    }


}
