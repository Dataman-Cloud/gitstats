package com.dataman.gitstats.param;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AddGroupParam {
    private String id;        //mongodb 主键
    @NotNull
    String accountid;

    @NotNull
    Integer groupId;
    String viewName;//页面展示项目别名
    private String dateformat;//日期格式    YMD统计年月日   YM 统计年月  YW  统计年星期
    private Integer lastDate;//统计最近日期数量，与dateformat联合使用     如dateformat为YMD，lastdate为30，则展示最近30天以天为单位统计
    private List<ProjectWithBranches> include;
    private List<ProjectWithBranches> exclude;

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<ProjectWithBranches> getInclude() {
        return include;
    }

    public void setInclude(List<ProjectWithBranches> include) {
        this.include = include;
    }

    public List<ProjectWithBranches> getExclude() {
        return exclude;
    }

    public void setExclude(List<ProjectWithBranches> exclude) {
        this.exclude = exclude;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
