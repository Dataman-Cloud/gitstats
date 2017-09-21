package com.dataman.gitstats.po;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName: ProjectStats
 * @Description: 项目  需要统计的项目
 * @author liuqing 
 * @date 2017年9月19日 上午10:23:59 
 * @Copyright © 2017北京数人科技有限公司
 */
@Document
public class ProjectStats {
	
	@Id
	String id;
	int proId; // gitlab project Id
	String name; 
	Date lastupdate;
	Date createdate;
	String accountId; //帐号id
	String weburl;
	int webhookstatus; // 0 未配置　１已配置
	
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
	public Date getLastupdate() {
		return lastupdate;
	}
	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public int getWebhookstatus() {
		return webhookstatus;
	}
	public void setWebhookstatus(int webhookstatus) {
		this.webhookstatus = webhookstatus;
	}
	public String getWeburl() {
		return weburl;
	}
	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}
	
}
