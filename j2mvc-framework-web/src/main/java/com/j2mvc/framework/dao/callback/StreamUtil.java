package com.j2mvc.framework.dao.callback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

public class StreamUtil {
	static final Logger log = Logger.getLogger(StreamUtil.class);

	/**
	 * 对象转换为字节
	 * @param obj
	 * 
	 */
	public static byte[] objectToBytes(java.lang.Object obj) {
		if(obj == null)
			return null;
		byte[] bytes = null;
		try {
			// object to bytearray
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);

			bytes = bo.toByteArray();

			bo.close();
			oo.close();
		} catch (Exception e) {
		}
		return bytes;
	}
	/**
	 * 字节转换为对象
	 * @param bytes
	 * 
	 */
	public static Object bytesToObject(byte[] bytes){
		if(bytes == null)
			return null;
		Object obj = null;
		try {
			// bytearray to object
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);

			obj = oi.readObject();

			bi.close();
			oi.close();
		} catch (Exception e) {
		}
		return obj;
    }
}
