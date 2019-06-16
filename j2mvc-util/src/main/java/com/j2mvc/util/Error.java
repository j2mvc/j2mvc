package com.j2mvc.util;

import java.util.List;

import com.j2mvc.util.json.JSONField;
import com.j2mvc.util.json.JSONObjectStr;

/** 
 * 
 * 错误
 * 2014-3-29 创建@杨朔
 *
 */
@JSONObjectStr("error")
public class Error{
	/** 字符串太长 */
	public static final int ERROR_NULL = 301;
	/** 空值 */
	public static final int ERROR_TOOLONG = 302;
	/** 参数不正确或不全 */
	public static final int ERROR_PARAM = 503;
	/** 数据 */
	public static final int ERROR_DATA = 403;
	/** 存在 */
	public static final int ERROR_EXISTS = 404;
	/** 不存在 */
	public static final int ERROR_NOT_EXISTS = 404;
	/** IO */
	public static final int ERROR_IO = 504;
	/** 权限不足 */
	public static final int ERROR_AUTH = 601;

	/** 错误码 */
	@JSONField("code")
	private int code;
	
	/** 错误信息 */
	@JSONField("message")
	private String message;

	/** 错误信息 */
	@JSONField("errors")
	private List<Error> errors;
	
	public Error() {
		super();
	}

	public Error(String message) {
		super();
		this.message = message;
	}

	public Error(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Error(int code, String message, List<Error> errors) {
		super();
		this.code = code;
		this.message = message;
		this.errors = errors;
	}

	public Error(String message, List<Error> errors) {
		super();
		this.message = message;
		this.errors = errors;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}
}
