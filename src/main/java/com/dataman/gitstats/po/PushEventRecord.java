package com.dataman.gitstats.po;

import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName: PushEventRecord
 * @Description: 记录pushevent的信息 
 * @author liuqing 
 * @date 2017年9月20日 下午4:33:01 
 * @Copyright © 2017北京数人科技有限公司
 */
@Document
public class PushEventRecord extends PushEvent{

	@Id
	String id_;
	String status; // 0 未处理  1 已处理
	String domsg; // 处理标记
	
	static String DOMSG_NOTINIT ="DOMSG_NOTINIT"; // 未初始化
	static String DOMSG_WAIT ="DOMSG_WAIT"; // 等待
	
	public String getId_() {
		return id_;
	}
	public void setId_(String id_) {
		this.id_ = id_;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDomsg() {
		return domsg;
	}
	public void setDomsg(String domsg) {
		this.domsg = domsg;
	}
	
	
}
