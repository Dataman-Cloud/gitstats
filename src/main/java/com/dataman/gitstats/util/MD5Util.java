package com.dataman.gitstats.util;

import java.security.MessageDigest;

/**
 * @ClassName: MD5Util
 * @Description: TODO(MD5加密工具类) 
 * @author 张海阳 
 * @date 2017年5月22日 下午6:46:41 
 * @Copyright © 2017北京数人科技有限公司
 */
public class MD5Util {

	public final static String MD5(String pwd) {

		// 用于加密的字符
		char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			// 使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
			byte[] btInput = pwd.getBytes();

			// 信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
			MessageDigest mdInst = MessageDigest.getInstance("MD5");

			// MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
			mdInst.update(btInput);

			// 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
			byte[] md = mdInst.digest();

			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) { // i = 0
				byte byte0 = md[i]; // 95
				str[k++] = md5String[byte0 >>> 4 & 0xf]; // 5
				str[k++] = md5String[byte0 & 0xf]; // F
			}

			// 返回经过加密后的字符串
			return new String(str);

		} catch (Exception e) {
			return null;
		}
	}

	public static String convert(String s) {
		char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] bytes = s.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);
			bytes = md.digest();
			int j = bytes.length;
			char[] chars = new char[j * 2];
			int k = 0;
			for (int i = 0; i < bytes.length; i++) {
				byte b = bytes[i];
				chars[k++] = hexChars[b >>> 4 & 0xf];
				chars[k++] = hexChars[b & 0xf];
			}
			return new String(chars);
		} catch (Exception e) {
			return null;
		}

	}

	// 可逆的加密算法
	public static String rencrypt(String inStr) {
		// String s = new String(inStr);
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String s = new String(a);
		return s;
	}

	// 加密后解密
	public static String decrypt(String inStr) {
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String k = new String(a);
		return k;
	}

	

	public static void main(String[] args) {

		System.out.println(MD5("Admin1234"));
		System.out.println(rencrypt(MD5("Dataman1")));
		System.out.println(decrypt(rencrypt(MD5("Dataman1"))));
	}

}
