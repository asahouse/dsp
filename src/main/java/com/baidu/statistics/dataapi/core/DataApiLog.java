package com.baidu.statistics.dataapi.core;

import com.baidu.statistics.log.StatisticLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DataApiLog implements StatisticLog {
	private static final transient Log log = LogFactory.getLog(DataApiLog.class);
	
	private String request;
	private String response;
	
	private long startTime;
	private long endTime;
	
	public void print() {
		String logStr = "\n" + "请求数据：" + request + "\n"
				+ "返回数据：" + response + "\n"
				+ "耗时：" + String.valueOf(this.endTime - this.startTime) + "ms";
		log.debug(logStr);
	}
	
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public static Log getLog() {
		return log;
	}
}
