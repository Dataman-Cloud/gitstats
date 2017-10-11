package com.dataman.gitstats.param;

import javax.validation.constraints.NotNull;

public class AddProjectParam {
    String id;        //weburl+proid+branch确保分支唯一，不重复添加
    @NotNull
    String accountid;
    @NotNull
    String branch;
    @NotNull
    Integer proid;
    String viewName;//页面展示项目别名

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getProid() {
        return proid;
    }

    public void setProid(Integer proid) {
        this.proid = proid;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
