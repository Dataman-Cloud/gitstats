package com.dataman.gitstats.po;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.gitlab4j.api.models.Session;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * @ClassName: GitlabAccount
 * @Description: gitlab帐号记录表
 * @author liuqing
 * @date 2017年9月21日 上午9:55:59
 * @Copyright © 2017北京数人科技有限公司
 */
@Document
public class GitlabAccount {

	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Id
	String id;
	String url;
	String user;
	String pwd;
	Session session;
	Date cratedate;
	Date Lastupdate;
	private  String showCreatedate;
	private String showLastupdate;
	int stats; // 状态   0 未通过 1 正常
	int type; //类型 0 pwd 验证 1 token 验证
	String token;
	String name;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Date getCratedate() {
		return cratedate;
	}
	public void setCratedate(Date cratedate) {
		this.cratedate = cratedate;
	}
	public Date getLastupdate() {
		return Lastupdate;
	}
	public void setLastupdate(Date lastupdate) {
		Lastupdate = lastupdate;
	}
	public int getStats() {
		return stats;
	}
	public void setStats(int stats) {
		this.stats = stats;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getShowCreatedate() {
		return sdf.format(this.cratedate);
	}

	public String getShowLastupdate() {
		return sdf.format(this.Lastupdate);
	}
}
