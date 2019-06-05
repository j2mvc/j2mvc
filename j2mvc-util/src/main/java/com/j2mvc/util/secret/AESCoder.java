package com.j2mvc.util.secret;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密
 * @author Wanjia
 *
 */
public class AESCoder {  
	
	private final static String TYPE = "AES/CBC/PKCS5Padding";
	private final static String IV_SPEC = "32k14d41yh130014";
	
//	private final static byte[] IV_SPEC_BYTES= { 0xA, 1, 0xB, 5, 4, 0xF, 7, 9, 0x17, 3, 1, 6, 8, 0xC,0xD, 91 };
	/**
	 * 加密
	 * @param src 需要加密的内容 
	 * @param key 加密密码
	 */
    public static String encrypt(String src,String key){
		try {
			SecretKeySpec skeySpec = getKey(key);
	        Cipher cipher = Cipher.getInstance(TYPE);
	        IvParameterSpec iv = new IvParameterSpec(IV_SPEC.getBytes());
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	        byte[] encrypted = cipher.doFinal(src.getBytes());
	        return Base64Encoder.encode(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
    }
	/**
	 * 解密
	 * @param src 需要解密的内容 
	 * @param key 加密密码
	 */
    public static String decrypt(String src,String key) {
		try {
	        SecretKeySpec skeySpec = getKey(key);
	        Cipher cipher = Cipher.getInstance(TYPE);
	        IvParameterSpec iv = new IvParameterSpec(IV_SPEC.getBytes());
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	        byte[] encrypted = Base64Decoder.decodeToBytes(src);
	
	        byte[] original = cipher.doFinal(encrypted);
	        return new String(original);
        } catch (Exception e) {
			e.printStackTrace();
		}
		return "";
    }

    
    /**
     * 获取16进制密钥
     * @param key
     * @throws Exception
     */
    private static SecretKeySpec getKey(String key) throws Exception {
        byte[] arrBTmp = key.getBytes();
        byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）

        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");

        return skeySpec;
    }


	public static void main(String[] args) throws Throwable {
		
		String src = "1234567890abcdefghijklmnopqrstuvwxyz";
		String key = "wanjia@123";
		System.out.println("加密："+src+"；长度"+src.length()+"；加密字符串："+key);
		long start = System.currentTimeMillis();

		String encryptString = encrypt(src,key);
		
		long end = System.currentTimeMillis();
		System.out.println("加密后："+encryptString+"；长度"+encryptString.length()+"；耗时："+(end-start)+"毫秒。");

		
		// luLuNFtEKgJMxUM43tGFbFOgVdVQ]tQZC[PWB56iKad02iXo4HQdBJg3IwnJb1RS
		// iRDx3Kew[3YYEx8Q9CZ6DvG[sfjTKaFdxXBD[UHSARQvoYHTgd3fBxlaAqYMQmKy
		String desencryptString = decrypt("JzASiZHirUqYtZmCv[R1FrOtHHyyo4jWDxZ9rtRtdxypj1Xo3[TYJnhDNlokL3cm","GzkbApp2015");

		System.out.println("解密后："+desencryptString+"；长度"+desencryptString.length()+"；耗时："+(System.currentTimeMillis()-end)+"毫秒。");
	}
}  
