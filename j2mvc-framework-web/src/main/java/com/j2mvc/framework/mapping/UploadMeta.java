package com.j2mvc.framework.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * 上传信息
 * 
 * @author 杨朔
 * @version 1.0@date2014-5-26
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  UploadMeta {
	/** 多媒体目录 */
	public static final String DIR_MEDIA = "media";
	/** 视频目录 */
	public static final String DIR_VIDEO = "video";
	/** 音频目录 */
	public static final String DIR_AUDIO = "audio";
	/** 文件目录 */
	public static final String DIR_FILE = "file";
	/** 图片目录 */
	public static final String DIR_IMAGE = "image";
	/** flash目录 */
	public static final String DIR_FLASH = "flash";

	/** 文件保存根路径 */
	public String savePath() default "";

	/** 附件访问Url */
	public String saveUrl() default "";

	/** 附件扩展名限制 */
	public String exts() default "";
	
	/** 附件目录 */
	public String dirname() default "";

	/** 附件大小 */
	public long maxSize() default 0;
	
	/** 固定文件名称 */
	public String filename() default "";

	/** 保持原文件名称 */
	public boolean keepOriginName() default true;

	/** 仅接收输入流 */
	public boolean inputStreamOnly() default false;
}
