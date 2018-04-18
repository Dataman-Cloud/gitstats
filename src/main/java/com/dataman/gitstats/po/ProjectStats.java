package com.dataman.gitstats.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName:ProjectStats
 * @Description 项目统计对象，包含项目所有统计分支
 * @author biancl
 * @date 2018-03-04 14:34
 */
@Document
public class ProjectStats {

	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Id
	String id;		//自动生成
	String accountid;
	String projectNameWithNamespace;//项目名称+命名空间  只有项目名称可能会重复
	int proid;
	int status; // 0  未初始化  1 初始化
	private String showStatus;
	private String groupId;
	String weburl;
	int totalRow;
	int totalAddRow;
	int totalDelRow;
	int totalCommits;
	Date createdAt; //项目创建时间
	Date lastupdate;
	private String showLastupdate;
	Date createdate;
	private String showCreatedate;

	
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

	public int getTotalCommits() {
		return totalCommits;
	}

	public void setTotalCommits(int totalCommits) {
		this.totalCommits = totalCommits;
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
	public String getWeburl() {
		return weburl;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	public String getShowStatus(){
		return this.status==0?"正在初始化":this.status==-1?"初始化出错":"初始化完成";
	}
}
