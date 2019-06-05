package com.j2mvc.util.secret;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加密解密
 * @author Wanjia
 *
 */
public class DESCoder {
	
	/**
	 * 执行加密解密
	 * @param src
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] doFinal(int mode,byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(mode, securekey, sr);

		// 正式执行加密/解密操作
		return cipher.doFinal(src);
	}

	/**
	 * 加密
	 * @param pass 密码
	 * @param key 加密字符串
	 * @return
	 */
	public final static String encrypt(String password, String key) {
		try {
			return byte2String(doFinal(Cipher.ENCRYPT_MODE,password.getBytes(), key.getBytes()));
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * 解密
	 * @param data 解密数据
	 * @param key 加密字符串
	 * @return
	 */
	public final static String decrypt(String data, String key) {
		try {
			return new String(doFinal(Cipher.DECRYPT_MODE,String2byte(data.getBytes()), key.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	private static String byte2String(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}


	public static byte[] String2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	public static void main(String[] args) {
		String src = "1234567890abcdefghijklmnopqrstuvwxyz";//加密源
		String key = "wanjia@123"; // 密钥
		
		System.out.println("加密："+src+"；长度"+src.length()+"；加密字符串："+key);
		
		long start = System.currentTimeMillis();
		
		String encryptString = encrypt(src, key);
		
		long end = System.currentTimeMillis();
		System.out.println("加密后："+encryptString+"；长度"+encryptString.length()+"；耗时："+(end-start)+"毫秒。");

		String desencryptString = decrypt(encryptString,key);

		System.out.println("解密后："+desencryptString+"；长度"+desencryptString.length()+"；耗时："+(System.currentTimeMillis()-end)+"毫秒。");
	}
}
