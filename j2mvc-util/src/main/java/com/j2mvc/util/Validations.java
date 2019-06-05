package com.j2mvc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 验证类
 * 2014-4-4@杨朔
 */
public class Validations {

	/**
	 * 是否不为空
	 * 
	 * @param s
	 */
	public static boolean empty(String s) {
		s = s != null ? s.trim() : "";
		if (!s.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 是否不含有特殊符号
	 * 
	 * @param s
	 */
	public static boolean notHasSpecial(String s) {
		s = s != null ? s.trim() : "";
		String[] words = { "~", "!", "@", "#", "$", "%", "^", "&", "*", "(",
				")", "_", "+", "|", "\\", "?", ">", "<", "*", "‘", ",", ",",
				"“", "~", "”", "\"" };
		for (int i = 0; i < words.length; i++) {
			if (s.indexOf(words[i]) != -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否是数值
	 * 
	 * @param s
	 */
	public static boolean isNumeric(String s) {
		s = s != null ? s.trim() : "";
		if (s.matches("\\d+")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是DOUBLE或INT类型
	 * 
	 * @param s
	 */
	public static boolean isDoubleOrInt(String s) {
		s = s != null ? s.trim() : "";
		String re = "\\d+[.]?[\\d+]?";
		if (s.matches(re)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是DOUBLE类型
	 * 
	 * @param s
	 */
	public static boolean isDouble(String s) {
		s = s != null ? s.trim() : "";
		String re = "\\d+.\\d+";
		if (s.matches(re)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是正负DOUBLE类型
	 * 
	 * @param s
	 */
	public static boolean isNegativeDouble(String s) {
		s = s != null ? s.trim() : "";
		String re = "([-])?\\d+[.]?\\d+";
		if (s.matches(re)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是正负DOUBLE类型 或INT
	 * 
	 * @param s
	 */
	public static boolean isNegativeDoubleOrInt(String s) {
		s = s != null ? s.trim() : "";
		String re = "([-])?\\d+.\\d+";
		if (s.matches(re)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是网址
	 * 
	 * @param s
	 */
	public static boolean isUrl(String s) {
		s = s != null ? s.trim() : "";
		if (s.indexOf("http://", 0) == 0 || s.indexOf("https://", 0) == 0) {
			s = s.replace("http://", "");
			s = s.replace("https://", "");
			if (s == "") {
				// 为空
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 是否是域名
	 * 
	 * @param s
	 */
	public static boolean isDomain(String s) {
		s = s != null ? s.trim() : "";
		if (s.indexOf(".")!=-1 && s.indexOf(".")< s.length()-1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是邮箱地址
	 * 
	 * @param s
	 */
	public static boolean isEmail(String s) {
		s = s != null ? s.trim() : "";
		String re = "\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*.\\w+([-.]\\w+)*";
		if (s.matches(re)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证电话号码
	 * 
	 * @param mobilephone
	 */
	public static boolean isMobilephoneOrPhone(String mobilephone) {
		boolean flag = true;
		String reg1 = "(^\\d{3,4}-?\\d{7,8})$|(1[0-9]{10})";
		String reg2 = "(^([(]\\d{3,4}[)])?\\d{7,8})$|(1[0-9]{10})";
		if (mobilephone == null || "".equals(mobilephone)) {
			return false;
		} else {
			try {
				Pattern p = Pattern.compile(reg1);
				Matcher m = p.matcher(mobilephone);
				flag = m.matches();
				if (!flag) {
					p = Pattern.compile(reg2);
					m = p.matcher(mobilephone);
					flag = m.matches();
				}
			} catch (Exception e) {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 验证电话号码
	 * 
	 * @param mobilephone
	 */
	public static boolean isMobilephone(String mobilephone) {
		boolean flag = false;
		if (mobilephone == null || "".equals(mobilephone)) {
			return false;
		} else {
			try {
				Pattern p = Pattern.compile("^[1][34578][0-9]{9}$");
				Matcher m = p.matcher(mobilephone);
				flag = m.matches();
			} catch (Exception e) {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 是否为身份证号
	 * 
	 * @param idCard
	 */
	public static boolean isIDCard(String idCard) {
		boolean flag = false;
		if (idCard == null || "".equals(idCard)) {
			return false;
		} else {
			try {
				Pattern p = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
				Matcher m = p.matcher(idCard);
				flag = m.matches();
				if (!flag) {
					p = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
					m = p.matcher(idCard);
					flag = m.matches();
				}
			} catch (Exception e) {
				flag = false;
			}
		}
		return flag;
	}


}
