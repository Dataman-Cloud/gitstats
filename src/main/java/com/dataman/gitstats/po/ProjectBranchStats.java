package com.dataman.gitstats.po;

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

	@Id
	String id;
	String projectid;
	String accountid;
	String branch;
	String projectname;
	int status; // 0  未初始化  1 初始化
	int webhookstatus; // 0 未配置　１已配置
	
	int totalRow;
	int totalAddRow;
	int totalDelRow;
	
	Date lastupdate;
	Date createdate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProjectid() {
		return projectid;
	}
	public void setProjectid(String projectid) {
		this.projectid = projectid;
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
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getWebhookstatus() {
		return webhookstatus;
	}
	public void setWebhookstatus(int webhookstatus) {
		this.webhookstatus = webhookstatus;
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
}
