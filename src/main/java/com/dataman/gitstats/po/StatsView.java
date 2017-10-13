package com.dataman.gitstats.po;

import org.springframework.data.annotation.Id;

/**
 * @Description: 统计报表页面 主要记录user与ProjectBranchStats的对应关系以及定制化页面元素
 * @author biancl
 * @date 2017-10-11 19:06
 * @Copyright © 2017北京数人科技有限公司
 */
public class StatsView {
    @Id
    private String id;//主键
    private String viewName;//视图名称  总标题
    private String userId;//所属用户
    private String branchId;//对应分支
    //以下属性为定制页面展示元素，暂时只用到部分
    private String projectStatsTitle;//项目统计折线图标题
    private String projectStatsStartDate;//项目统计折线图  起始统计时间（结束统计时间默认为当前时间）
    private String projectStatsUnitY;//项目统计折线图Y轴单位
    private String projectStatsTimeUnit;//项目统计折线图  统计时间单位 如   day（日）、week（周）、month（月）、year（年）
    private String pieStatsTitle;//饼状图（开发者代码贡献比）标题
    private String pieStatsTimeUnit;//饼状图 统计时间单位
    private String pieStatsStartDate;//饼状图 起始统计时间
    private String barStatsTitle;//柱状图（开发者贡献代码排行榜）标题
    private String barStatsStartDate;//柱状图 起始统计时间（结束统计时间默认为当前时间）
    private String barStatsUnitY;//柱状图 Y轴单位
    private String barStatsTimeUnit;//柱状图  统计时间单位 如   day（日）、week（周）、month（月）、year（年）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getProjectStatsTitle() {
        return projectStatsTitle;
    }

    public void setProjectStatsTitle(String projectStatsTitle) {
        this.projectStatsTitle = projectStatsTitle;
    }

    public String getProjectStatsStartDate() {
        return projectStatsStartDate;
    }

    public void setProjectStatsStartDate(String projectStatsStartDate) {
        this.projectStatsStartDate = projectStatsStartDate;
    }

    public String getProjectStatsUnitY() {
        return projectStatsUnitY;
    }

    public void setProjectStatsUnitY(String projectStatsUnitY) {
        this.projectStatsUnitY = projectStatsUnitY;
    }

    public String getProjectStatsTimeUnit() {
        return projectStatsTimeUnit;
    }

    public void setProjectStatsTimeUnit(String projectStatsTimeUnit) {
        this.projectStatsTimeUnit = projectStatsTimeUnit;
    }

    public String getPieStatsTitle() {
        return pieStatsTitle;
    }

    public void setPieStatsTitle(String pieStatsTitle) {
        this.pieStatsTitle = pieStatsTitle;
    }

    public String getPieStatsTimeUnit() {
        return pieStatsTimeUnit;
    }

    public void setPieStatsTimeUnit(String pieStatsTimeUnit) {
        this.pieStatsTimeUnit = pieStatsTimeUnit;
    }

    public String getPieStatsStartDate() {
        return pieStatsStartDate;
    }

    public void setPieStatsStartDate(String pieStatsStartDate) {
        this.pieStatsStartDate = pieStatsStartDate;
    }

    public String getBarStatsTitle() {
        return barStatsTitle;
    }

    public void setBarStatsTitle(String barStatsTitle) {
        this.barStatsTitle = barStatsTitle;
    }

    public String getBarStatsStartDate() {
        return barStatsStartDate;
    }

    public void setBarStatsStartDate(String barStatsStartDate) {
        this.barStatsStartDate = barStatsStartDate;
    }

    public String getBarStatsUnitY() {
        return barStatsUnitY;
    }

    public void setBarStatsUnitY(String barStatsUnitY) {
        this.barStatsUnitY = barStatsUnitY;
    }

    public String getBarStatsTimeUnit() {
        return barStatsTimeUnit;
    }

    public void setBarStatsTimeUnit(String barStatsTimeUnit) {
        this.barStatsTimeUnit = barStatsTimeUnit;
    }
}
