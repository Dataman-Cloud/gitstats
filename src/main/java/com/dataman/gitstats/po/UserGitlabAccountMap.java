package com.dataman.gitstats.po;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Description: 用户与gitlab账户映射表
 * @author biancl
 * @date 2017-10-11 19:00
 * @Copyright © 2017北京数人科技有限公司
 */
public class UserGitlabAccountMap {
    @Id
    private String id;
    private String userId;
    private String gitlabAccountId;
    private Date createAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGitlabAccountId() {
        return gitlabAccountId;
    }

    public void setGitlabAccountId(String gitlabAccountId) {
        this.gitlabAccountId = gitlabAccountId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
