package com.j2mvc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.j2mvc.util.MD5;

/**
 * MD5
 * 变更履历：
 * 2013/8/2 创建@杨朔
 */
public class MD5 {
	
	/** 
	 * 转换为MD5
	 * @param s
	 */
	public static String md5(String s) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}
	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	private final static char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	/**
	 * test
	 * @param args
	 */
	public static void main(String[]args){
		String s = "111111";
		String mString = MD5.md5(s);
		System.out.println("原文:"+s);
		System.out.println("加密:"+mString);
	}
}
