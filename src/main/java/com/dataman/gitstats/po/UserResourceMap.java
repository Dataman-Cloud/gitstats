package com.dataman.gitstats.po;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Description: 用户与资源映射表
 * @author biancl
 * @date 2017-10-11 18:54
 * @Copyright © 2017北京数人科技有限公司
 */
public class UserResourceMap {

    @Id
    private String id;
    private String userId;
    private String resourceId;
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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
