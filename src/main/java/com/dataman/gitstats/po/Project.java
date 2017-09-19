package com.dataman.gitstats.po;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName: Project
 * @Description: 项目  需要统计的项目
 * @author liuqing 
 * @date 2017年9月19日 上午10:23:59 
 * @Copyright © 2017北京数人科技有限公司
 */
@Document
public class Project {
	
	@Id
	String id;
	int proId; // gitlab project Id
	String name; 
	String sha; // branch 
	Date lastupdate;
	int totalRow; //总行数
	int totalCommit; //总提交
	int totalRemove; //总删除
	int status; // 0  未初始化  1 初始化
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getProId() {
		return proId;
	}
	public void setProId(int proId) {
		this.proId = proId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}
	public Date getLastupdate() {
		return lastupdate;
	}
	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}
	public int getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}
	public int getTotalCommit() {
		return totalCommit;
	}
	public void setTotalCommit(int totalCommit) {
		this.totalCommit = totalCommit;
	}
	public int getTotalRemove() {
		return totalRemove;
	}
	public void setTotalRemove(int totalRemove) {
		this.totalRemove = totalRemove;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
