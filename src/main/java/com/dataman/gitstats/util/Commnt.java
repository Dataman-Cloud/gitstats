package com.dataman.gitstats.util;

import java.util.UUID;

public class Commnt {

	public static String PWDSALT ="&*^%%"; //密码盐值
	
	/**
	 * @method createUUID(返回一个uuid)
	 * @return String
	 * @author liuqing
	 * @date 2017年9月21日 上午11:51:13
	 */
	public static String createUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	
}
