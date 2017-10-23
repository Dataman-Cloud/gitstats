package com.dataman.gitstats.po;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName: ProjectBranchStats
 * @Description: 项目分支统计
 * @author liuqing 
 * @date 2017年9月21日 上午10:09:37 
 * @Copyright © 2017北京数人科技有限公司
 */
@Document
public class ProjectBranchStats {

	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//将ProjectStats和BranchStats合并为一张表
	@Id
	String id;		//自动生成
	String accountid;
	String branch;
	String projectNameWithNamespace;//项目名称+命名空间  只有项目名称可能会重复
	int proid;
	int status; // 0  未初始化  1 初始化
	private String showStatus;
	String viewName;//页面展示项目别名
	String weburl;
	int totalRow;
	int totalAddRow;
	int totalDelRow;
	Date createdAt; //项目创建时间
	Date lastupdate;
	private String showLastupdate;
	Date createdate;
	private String showCreatedate;
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

	public String getProjectNameWithNamespace() {
		return projectNameWithNamespace;
	}

	public void setProjectNameWithNamespace(String projectNameWithNamespace) {
		this.projectNameWithNamespace = projectNameWithNamespace;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}
	public int getTotalAddRow() {
		return totalAddRow;
	}
	public void setTotalAddRow(int totalAddRow) {
		this.totalAddRow = totalAddRow;
	}
	public int getTotalDelRow() {
		return totalDelRow;
	}
	public void setTotalDelRow(int totalDelRow) {
		this.totalDelRow = totalDelRow;
	}
	public Date getLastupdate() {
		return lastupdate;
	}
	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public int getProid() {
		return proid;
	}
	public void setProid(int proid) {
		this.proid = proid;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getWeburl() {
		return weburl;
	}

	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}

	public String getShowLastupdate() {
		return sdf.format(this.lastupdate);
	}

	public String getShowCreatedate() {
		return sdf.format(this.createdate);
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
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

	public String getShowStatus(){
		return this.status==0?"正在初始化":this.status==-1?"初始化出错":"初始化完成";
	}
}
