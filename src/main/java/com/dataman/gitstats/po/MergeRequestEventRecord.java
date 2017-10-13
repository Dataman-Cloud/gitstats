package com.dataman.gitstats.po;

import org.gitlab4j.api.webhook.MergeRequestEvent;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by biancl on 2017-09-24.
 */
public class MergeRequestEventRecord extends MergeRequestEvent{
    public static String DEFAULT="DEFAULT";
    public static String HANDLING="HANDLING";
    public static String NEED_NOT_HANDLE_NO_THIS_PROJECT="NEED_NOT_HANDLE_NO_THIS_PROJECT";
    public static String NEED_NOT_HANDLE_NO_THIS_BRANCH="NEED_NOT_HANDLE_NO_THIS_BRANCH";
    public static String FINISHED="FINISHED";
    public static String ERROR="ERROR";
    @Id
    String id_;
    String status=DEFAULT; // 0 未处理  1 已处理	2
    String domsg; // 处理标记
    Date createAt=new Date();
    Date updateAt;

    static String DOMSG_NOTINIT ="DOMSG_NOTINIT"; // 未初始化
    static String DOMSG_WAIT ="DOMSG_WAIT"; // 等待

    public String getId_() {
        return id_;
    }
    public void setId_(String id_) {
        this.id_ = id_;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDomsg() {
        return domsg;
    }
    public void setDomsg(String domsg) {
        this.domsg = domsg;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

}
