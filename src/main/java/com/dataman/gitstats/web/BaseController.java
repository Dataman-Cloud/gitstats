package com.dataman.gitstats.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins="*")
public class BaseController {

	protected static String SUCCESS_CODE="00";
	protected static String FAIL_CODE="01";
	protected static String PARAMERR_CODE="02";
	
	public Map<String, Object> json = new HashMap<String, Object>(4);
	
	public void setJson(String resultCode, Object object) {
		this.json.put("resultCode", resultCode);
		this.json.put("data", object);
	}


	public void setJson(String resultCode) {
		this.json.put("resultCode", resultCode);
	}
	
	
	public void setJson(Object object) {
		this.json.put("data", object);
	}
}