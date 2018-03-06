package com.dataman.gitstats.po;

import com.dataman.gitstats.param.ProjectWithBranches;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @className: GroupStats
 * @Description 应用组统计对象
 * @author biancl
 * @date 2018-03-04 14:27
 */
@Document
public class GroupStats {

	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//将ProjectStats和BranchStats合并为一张表
	@Id
	String id;		//自动生成
	String accountid;
	String branch;
	Integer groupId;
	int status; // 0  未初始化  1 初始化
	private String showStatus;
	String viewName;//页面展示项目别名
	private String webUrl;
	private String name;
	private String path;
	private String fullPath;
	private List<ProjectWithBranches> include;
	private List<ProjectWithBranches> exclude;
	int totalRow;
	int totalAddRow;
	int totalDelRow;
	int totalCommits;
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
	public String getViewName() {
		return viewName;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
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

	public String getShowLastupdate() {
		return sdf.format(this.lastupdate);
	}

	public String getShowCreatedate() {
		return sdf.format(this.createdate);
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
