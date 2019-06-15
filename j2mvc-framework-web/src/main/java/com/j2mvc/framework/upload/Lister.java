package com.j2mvc.framework.upload;

import org.apache.commons.fileupload.ProgressListener;

import com.j2mvc.framework.upload.entity.FileInfo;

/**
 * 上传文件进度兼听器
 * @author 杨朔
 *	2014年1月14日
 */
public class Lister implements ProgressListener{
	// 文件超限
	public static final int ERROR_LIMIT = 601;
	// IO错误
	public static final int ERROR_IO = 602;
	
	// 等待中
	public static final int STATUS_WAIT = 0;
	// 上传中
	public static final int STATUS_STARTING = 1;
	// 完成
	public static final int STATUS_DONE = 3;
	// 取消
	public static final int STATUS_CANCEL = 4;
	// 暂停
	public static final int STATUS_PAUSE = 5;
	// 错误
	public static final int STATUS_ERROR = -1;

	private FileInfo fileInfo = null;
		
	public Lister(FileInfo fileInfo){
		this.fileInfo = fileInfo;
	}

	/**
	 * @totalSize 总大小
	 * @bytesRead 已读取字节数
	 * @index 上传序号
	 */

	public void update(long bytesRead, long totalSize, int index) {
		fileInfo.setBytesRead(bytesRead);
		fileInfo.setTotalSize(totalSize);
		fileInfo.setIndex(index);
		long startTime = fileInfo.getStartTime();
		// 计算使用时间，单位：秒
		long elapsedTime = (long) ((float)(System.currentTimeMillis() - startTime)/(float)1000);
		elapsedTime = elapsedTime == 0? 1:elapsedTime;
		// 计算速度:已上传，单位：字节/秒
		long spead = 0;
		if(bytesRead>0 && elapsedTime>0)
			spead = (long) ((float)bytesRead / (float)elapsedTime);
		// 计算百分比:完成字节除以总大小
		long percent = (long) (((float)bytesRead / (float)totalSize) * (float)100);
		// 估计剩余时间:总大小减去已传除以速度，单位：秒
		long restTime = 0;
		if(spead>0)
			restTime = (long) (((float)totalSize - (float)bytesRead)/ (float)spead);
		fileInfo.setElapsedTime(elapsedTime);
		fileInfo.setSpead(spead);
		fileInfo.setPercent(percent);
		fileInfo.setRestTime(restTime);
	}
}
