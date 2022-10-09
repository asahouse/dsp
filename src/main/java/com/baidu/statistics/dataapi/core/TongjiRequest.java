package com.baidu.statistics.dataapi.core;

public class TongjiRequest<T extends ApiRequest> {
	/**
	 * 认证信息（必填）
	 */
	private AuthHeader header;
	/**
	 * api 请求（必填）
	 */
	private T body;

	private String api;
	
	public TongjiRequest() {
		super();
	}

	public TongjiRequest(AuthHeader header, T body, String api) {
		super();
		this.header = header;
		this.body = body;
		this.api = api;
	}
	
	public AuthHeader getHeader() {
		return header;
	}
	public void setHeader(AuthHeader header) {
		this.header = header;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
}
