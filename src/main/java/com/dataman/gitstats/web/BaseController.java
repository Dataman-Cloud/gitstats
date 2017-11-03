package com.dataman.gitstats.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins="*")
public class BaseController {

	protected static String SUCCESS_CODE="00";
	protected static String FAIL_CODE="01";
	protected static String PARAMERR_CODE="02";
	
	public Map<String, Object> setJson(String resultCode, Object object) {
		Map<String, Object> json = new HashMap<String, Object>(4);
		json.put("resultCode", resultCode);
		json.put("data", object);
		return json;
	}


	public Map<String, Object> setJson(String resultCode) {
		Map<String, Object> json = new HashMap<String, Object>(4);
		json.put("resultCode", resultCode);
		return json;
	}
	
	
	public Map<String, Object> setJson(Object object) {
		Map<String, Object> json = new HashMap<String, Object>(4);
		json.put("data", object);
		return json;
	}
}