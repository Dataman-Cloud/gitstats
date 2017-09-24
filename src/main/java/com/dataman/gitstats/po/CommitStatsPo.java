package com.dataman.gitstats.po;

import java.util.Date;
import java.util.Set;

import org.gitlab4j.api.models.Commit;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dataman.gitstats.util.Commnt;

/**
 * @ClassName: Commit 补充统计
 * @Description: commit 补充统计 
 * @author liuqing 
 * @date 2017年9月19日 上午10:37:50 
 * @Copyright © 2017北京数人科技有限公司
 */
@Document
public class CommitStatsPo extends Commit{
	
	@Id
	String _id; // uuid
	int addRow; //添加行数
	int removeRow; //删除行数
	String projectName;
	String proid;
	String branch;
	Date crateDate;

	public String get_id() {
		return proid+branch+getId();
	}

	public void set_id(String _id) {
		this._id=proid+branch+getId();
	}

	public int getAddRow() {
		return addRow;
	}

	public void setAddRow(int addRow) {
		this.addRow = addRow;
	}

	public int getRemoveRow() {
		return removeRow;
	}

	public void setRemoveRow(int removeRow) {
		this.removeRow = removeRow;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProid() {
		return proid;
	}

	public void setProid(String proid) {
		this.proid = proid;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Date getCrateDate() {
		return crateDate;
	}

	public void setCrateDate(Date crateDate) {
		this.crateDate = crateDate;
	}
	
	public CommitStatsPo(){

	}
}
