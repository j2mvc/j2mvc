package com.j2mvc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * 
 * 通用工具类
 * 
 * 2014-3-29 创建@杨朔
 */
public class Utils {
	final static Logger log = Logger.getLogger(Utils.class);
	public final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat  DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	/**
	 * 创建ID
	 */
	public static String createId(){	
		return getRandomUUID(null).toString();
	}
    /**
     * 驗證UUID
     * @param str
     * @return
     */
	public static boolean isValidUUID(String uuid) {
		// UUID校验
		if (uuid == null) {
			return false;
		}
		String regex = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
		if (uuid.matches(regex)) {
			return true;
		}
		return false;
	}
    /**
     * 創建UUID
     * @param str
     * @return
     */
	public static UUID getRandomUUID(String str) {
		// 产生UUID
		if (str == null) {
			return UUID.randomUUID();
		} else {
			return UUID.nameUUIDFromBytes(str.getBytes());
		}
	}
	/**
     * 获取当前日期是星期几<br>
     * 
     * @param date
     * 期几
     */
    public static int getWeekOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }
	/**
     * 获取当前日期是星期几<br>
     * 
     * @param date
     *  当前日期是星  */
    public static String getWeekName(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        String name = null;
        switch(w){
        case 0:
        	name = "周日";
        	break;
        case 1:
        	name = "周一";
        	break;
        case 2:
        	name = "周二";
        	break;
        case 3:
        	name = "周三";
        	break;
        case 4:
        	name = "周四";
        	break;
        case 5:
        	name = "周五";
        	break;
        case 6:
        	name = "周六";
        	break;
        	default:
            	break;
        }
        return name;
    }
	/**
	 * 创建token
	 * @param username
	 * @param domain
	 * @param password
	 */
	public static String createToken(String username,String domain,String password){
		String sessionid = MD5.md5(username+domain+System.currentTimeMillis()+new Random().nextInt(10000));	
		if(password.length()>32){
			log.error("密码不能超过32位");
			return "";
		}
		return encode(sessionid,password);
	}
	/**
	 * 创建Sessionid
	 * @param username
	 * @param domain
	 * @param password
	 */
	public static String createSessionid(String username,String domain,String password){
		String sessionid = MD5.md5(username+domain+System.currentTimeMillis()+new Random().nextInt(10000));	
		if(password.length()>32){
			log.error("密码不能超过32位");
			return "";
		}
		return encode(sessionid,password);
	}
	/**
	 * 解密
	 * @param sessionid
	 */
	public static String getPassword(String sessionid){
		return decode(sessionid);
	}
	/**
	 * 编码
	 * @param large 大的数
	 * @param small 小的数，小的数每个字符将被分隔
	 */
	private static String encode(String large,String small ){	
		long lLength = large.length();
		long sLength = small.length();
		long diff = lLength - sLength; 
		int step = (int) (diff/sLength)+1;
		StringBuffer buffer = new StringBuffer();
		int lEnd = 0;
		int lStart = 0;
		for(int i=0;i<sLength;i++){
			lStart = lEnd;
			lEnd = lEnd+step;
			buffer.append(small.substring(i, i+1));
			buffer.append(large.substring(lStart,lEnd));
		}
		buffer.append(large.substring(lEnd));
		return buffer.toString();
	}
	
	/**
	 * 解码
	 * @param source
	 */
	private static String decode(String source){
		// md5长度为32
		int length = source.length();
		if(length>64){
			log.error("字符不能超过64位");
			return "";
		}
		int sLength = length - 32;
		int diff = 32 - sLength;
		// md5小于密码长度
		int step = (int) (diff/sLength)+2;
		int end = sLength * step;
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<end;i=i+step){
			buffer.append(source.substring(i, i+1));
		}
		return buffer.toString();
	}
	/**
	 * 获取当前时间
	 */
	static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String now(){
		return format.format(new Date());
	}

	 /**
	  * 获取文件大小单位
	  * @param size
	  */
	public static String format(float size){
		String unit = "B";
	 	if (size > 1000) {
	 		size = size > 1000 ? size / 1024 : size;
	 		unit = "KB";
	 	}
	 	if (size > 1000) {
	 		size = size > 1000 ? size / 1024 : size;
	 		unit = "MB";
	 	}
	 	if (size > 1000) {
	 		size = size > 1000 ? size / 1024 : size;
	 		unit = "GB";
	 	}
	 	if (size > 1000) {
	 		size = size > 1000 ? size / 1024 : size;
	 		unit = "TB";
	 	}
	 	return String.format("%.2f", size) + unit;
	}

	 /**
	  * 获取时间大小单位
	  * @param time
	  */
	public static String formatTime(float time){
		int sStep = 1000; // 秒步长
		int mStep = sStep*60;	// 分步长
		int hStep = mStep*60; // 时步长
		int dStep = hStep*24;	// 天步长
		if(time < sStep){
			return (int)time+"毫秒";
		}else if(time < mStep){
			return (int)(time/sStep)+"秒";
		}else if(time < hStep){
			// 分
			float m = time/mStep;
			float s = (time-((int)m)*mStep)/1000;// 剩余秒
			return (int)m+"分"+(int)s+"秒";
		}else if(time < dStep){
			// 小于天,小时
			float h = time/hStep;
			float m = (time - (int)h * hStep) / mStep;
			float s = (time - (int)h * hStep  - (int)m*mStep)/sStep;
			return (int)h+"时"+(int)m+"分"+(int)s+"秒";
		}else {
			float d = time/dStep;
			float h = (time - (int)d * dStep) / hStep;
			float m = (time - (int)d * dStep - (int)h * hStep) / mStep;
			float s = (time -  (int)d * dStep - (int)h * hStep - (int)m*mStep)/sStep;
			return (int)d+"天"+(int)h+"时"+(int)m+"分"+(int)s+"秒";
		}
	}
	/**
	 * 转换HTML标签
	 * @param str
	 */
	public static String htmlspecialchars(String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\"", "&quot;");
		return str;
	}

	
	/**
	 * 转换日期时间输出
	 * @param source
	 * @param format
	 */
	public static String formatDate(String source,String format){
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(DATE_TIME_FORMAT.parse(source));
			Calendar todayCal = Calendar.getInstance();
			todayCal.get(Calendar.YEAR);
			if(todayCal.get(Calendar.YEAR) == c.get(Calendar.YEAR)
					&& todayCal.get(Calendar.MONTH) == c.get(Calendar.MONTH) 
					&& todayCal.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH)){
				// 今天
				if(todayCal.get(Calendar.HOUR_OF_DAY) == c.get(Calendar.HOUR_OF_DAY)
						&& todayCal.get(Calendar.MINUTE) == c.get(Calendar.MINUTE)){
					return (todayCal.get(Calendar.SECOND) - c.get(Calendar.SECOND))+"秒前";
				}else if(todayCal.get(Calendar.HOUR_OF_DAY) == c.get(Calendar.HOUR_OF_DAY)){
					return (todayCal.get(Calendar.MINUTE) - c.get(Calendar.MINUTE))+"分钟前";
				}else{
					return (todayCal.get(Calendar.HOUR_OF_DAY) - c.get(Calendar.HOUR_OF_DAY))+"小时前";
				}
			}else{
				SimpleDateFormat dateformat = DATE_TIME_FORMAT;
				if(format!=null && !format.trim().equals("")) 
					dateformat = new SimpleDateFormat(format);
				return dateformat.format(DATE_TIME_FORMAT.parse(source));
			}
		} catch (ParseException e) {
			System.err.println(Utils.class.getSimpleName()+":formatDate:格式化时间错误，source="+source+";信息:"+e.getMessage());
			return "";
		}
	}

	/**
	 * 转换日期时间输出
	 * @param source
	 * @param format
	 * @param force
	 */
	public static String formatDate(String source,String format,boolean force){
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(DATE_TIME_FORMAT.parse(source));
			Calendar todayCal = Calendar.getInstance();
			todayCal.get(Calendar.YEAR);
			if(!force && todayCal.get(Calendar.YEAR) == c.get(Calendar.YEAR)
					&& todayCal.get(Calendar.MONTH) == c.get(Calendar.MONTH) 
					&& todayCal.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH)){
				// 今天
				if(todayCal.get(Calendar.HOUR_OF_DAY) == c.get(Calendar.HOUR_OF_DAY)
						&& todayCal.get(Calendar.MINUTE) == c.get(Calendar.MINUTE)){
					return (todayCal.get(Calendar.SECOND) - c.get(Calendar.SECOND))+"秒前";
				}else if(todayCal.get(Calendar.HOUR_OF_DAY) == c.get(Calendar.HOUR_OF_DAY)){
					return (todayCal.get(Calendar.MINUTE) - c.get(Calendar.MINUTE))+"分钟前";
				}else{
					return (todayCal.get(Calendar.HOUR_OF_DAY) - c.get(Calendar.HOUR_OF_DAY))+"小时前";
				}
			}else{
				SimpleDateFormat dateformat = DATE_TIME_FORMAT;
				if(format!=null && !format.trim().equals(""))
					dateformat = new SimpleDateFormat(format);
				return dateformat.format(DATE_TIME_FORMAT.parse(source));
			}
		} catch (ParseException e) {
			System.err.println(Utils.class.getSimpleName()+":formatDate:格式化时间错误，source="+source+";信息:"+e.getMessage());
			return "";
		}
	}
	// 随机生成数字
	public static String getRandom() {
		return getRandom(14);
	}

	
	// 随机生成数字
	public static String getRandom(int num) {
		/*
		 * 容器
		 */
		String[] chars = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
				"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
				"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
				"Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
				"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
				"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
				"y", "z" };

		// 创建一个随机数生成器类。
		Random random = new Random();
		StringBuffer randomCode = new StringBuffer();
		// 随机产生
		for (int i = 0; i < num; i++) {
			// 得到随机产生的字符。
			String strRand = chars[random.nextInt(16)];
			// 将产生的四个随机数组合在一起。
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}
	/**
	 * 对象转换为字节
	 * @param obj
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
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 将byte数组转换为字符串
	 * 
	 * @param bytes
	 * date 2015-3-3
	 */
	public static String byteToString(byte[] bytes) {
		String result = null;
		if (bytes == null) {
			return null;
		}
		if(bytes.length <= 0){
			log.warn(" bytes length is " + bytes.length );
			return null;
		}
		// bytearray to object
		try {
			result = new String(bytes, "UTF-8");
		} catch (IOException e) {
			log.warn("IOException bytes length is " + bytes.length );
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将字符串转换为byte数组
	 * 
	 * @param string
	 * date 2015-3-3
	 */
	public static byte[] stringToBytes(String string) {
		if (string == null) {
			return new byte[0];
		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(byteOut);
		try {
			dos.writeUTF(string);
			dos.close();
			byteOut.close();
		} catch (IOException e) {
			return new byte[0];
		}
		return byteOut.toByteArray();
	}
	/**
	 * 字节转换为对象
	 * @param bytes
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
			e.printStackTrace();
		}
		return obj;
    }
	
	/** 
     * 功能 读取流 
     * @param is 输入流
     * return 字节数组 
	 * @throws IOException 
     */  
    public static String readInputStream(InputStream is) throws IOException {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len;
		while ((len = is.read(b)) != -1 ) {
			baos.write(b, 0, len);
        }  
		baos.flush();
		baos.close();
		String result = new String(baos.toByteArray());
        return result;  
    }  
    /**
     * 
     * @param is
     * @param end
     * @throws IOException
     */
    public static String readInputStream(InputStream is,String end) throws IOException {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len;
		boolean isEnd = false;
		while (!isEnd && (len = is.read(b)) != -1 ) {
			String line = new String(b);
			isEnd = line.lastIndexOf(end)!=-1;
			baos.write(b, 0, len);
        }  
		baos.flush();
		baos.close();
		String result = new String(baos.toByteArray());
        return result!=null && result.lastIndexOf(end)!=-1?result.substring(0,result.lastIndexOf(end)):result;  
    }  
    /**
     * 写输出流
     * @param arg
     * @param out
     * @throws IOException 
     */
	public static void writeBytes(String arg,OutputStream out) throws IOException{
		out.write(arg.getBytes());
	}
	
	public static void main(String...args){
		String s = " 输入流";
		byte [] bytes = stringToBytes(s);
		System.out.println("string to bytes "+stringToBytes(s));
		System.out.println("bytes to string "+byteToString(bytes));
//		System.out.println(formatTime((1000*60*60)*24+(1000*60*60)*2+(1000*60*40)+(1000*30)));
//		String domain = "aojia.org";
//		String username = "sessionid";
//		String password = "AOS112131Jsessi@@@onidIswwe23134";
//		String sessionid = createSessionid(username, domain,password);
//		System.out.println("password="+password+" length="+password.length());
//		System.out.println(sessionid+" length="+sessionid.length());
//		String decodeString = getPassword(sessionid);
//		System.out.println("password="+decodeString+" length="+decodeString.length());
//		String rgx = "(20)([1-9])([0-9])([-])([0-1])?([0-9])([-])([0-3])?([0-9])";
//		String time = "2011-5-2";
//		System.out.println(time.matches(rgx));
		
//		String DATETIME_REGEXT = "(20)([1-9])([0-9])([-])([0-1])?([0-9])([-])([0-3])?([0-9])([\\s]+)(([0-1][0-9])|([2][0-3]))([:][0-5][0-9])([:][0-5][0-9])?";
//		String dtime = "2011-5-2 09:08:45";
//		System.out.println(dtime.matches(DATETIME_REGEXT));
		System.out.println(Utils.createId().length());

	}
}
