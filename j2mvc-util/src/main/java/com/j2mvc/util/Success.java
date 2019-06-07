package com.j2mvc.util;

import java.util.List;

import com.j2mvc.util.json.JSONField;
import com.j2mvc.util.json.JSONObjectStr;

/** 
 * 
 * 正确
 * 2014-3-29 创建@杨朔
 *
 */
@JSONObjectStr("success")
public class Success{
	/** 参数正确 */
	public static final int SUCCESS_PARAM = 201;
	/** 数据 */
	public static final int SUCCESS_DATA = 202;
	/** IO */
	public static final int SUCCESS_IO = 203;
	/** NET */
	public static final int SUCCESS_NET = 204;

	/** 成功码 */
	@JSONField("code")
	private int code;
	
	/** 成功信息 */
	@JSONField("message")
	private String message;

	/** 成功信息 */
	@JSONField("successes")
	private List<Success> successes;

	public Success() {
		super();
	}

	public Success(String message) {
		super();
		this.message = message;
	}

	public Success(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Success(int code, String message, List<Success> successes) {
		super();
		this.code = code;
		this.message = message;
		this.successes = successes;
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

	public List<Success> getSuccesses() {
		return successes;
	}

	public void setSuccesses(List<Success> successes) {
		this.successes = successes;
	}
	
}
