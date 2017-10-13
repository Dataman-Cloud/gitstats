package com.dataman.gitstats.param;

import javax.validation.constraints.NotNull;

public class AddProjectParam {
    String id;        //mongodb 主键
    @NotNull
    String accountid;
    @NotNull
    String branch;
    @NotNull
    Integer proid;
    String viewName;//页面展示项目别名
    private String dateformat;//日期格式    YMD统计年月日   YM 统计年月  YW  统计年星期
    private Integer lastDate;//统计最近日期数量，与dateformat联合使用     如dateformat为YMD，lastdate为30，则展示最近30天以天为单位统计

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

    public String getDateformat() {
        return dateformat;
    }

    public void setDateformat(String dateformat) {
        this.dateformat = dateformat;
    }

    public Integer getLastDate() {
        return lastDate;
    }

    public void setLastDate(Integer lastDate) {
        this.lastDate = lastDate;
    }
}
