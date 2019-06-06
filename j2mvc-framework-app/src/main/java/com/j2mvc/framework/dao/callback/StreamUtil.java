package com.j2mvc.framework.dao.callback;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;

public class StreamUtil {
	static Logger log = Logger.getLogger(StreamUtil.class.getName());
	//将对象转换为byte[]
	public static byte[] objectToBytes(Object obj){

		ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
		ObjectOutputStream outObj;
		try {
			outObj = new ObjectOutputStream(byteOut);
			outObj.writeObject(obj) ;
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return byteOut.toByteArray();
	}
	//将byte[]转换为对象
	public static Object bytesToObject(byte[] buff) {
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(buff)));  
			return in.readObject();
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}catch (ClassNotFoundException e) {
			log.error(e.getMessage());
			return null;
		}
	}
}
