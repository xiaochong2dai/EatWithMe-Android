package com.way.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {
	/**
	 * static encoding method
	 * 
	 * @param codeType
	 *           input encoding method
	 * @param content
	 *           input contents need to be encoded
	 * @return return encoded results
	 */
	public static String getEncode(String codeType, String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance(codeType);
			digest.reset();
			digest.update(content.getBytes());
			StringBuilder builder = new StringBuilder();
			for (byte b : digest.digest()) {
				builder.append(Integer.toHexString((b >> 4) & 0xf));
				builder.append(Integer.toHexString(b & 0xf));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
//
//	public static void main(String[] args) {
//		String str = Encode.getEncode("MD5", "hello world!");
//		System.out.println(str);
//		// fc3ff98e8c6a0d3087d515c0473f8677
//		String str1 = Encode.getEncode("SHA", "hello world!");
//		System.out.println(str1);
//		// 430ce34d020724ed75a196dfc2ad67c77772d169
//	}
}
