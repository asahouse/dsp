package com.baidu.statistics.config;

public interface Config {
	
	//preLogin,doLogin URL
	public static final String K_LOGIN_URL = "baidutongji.login_url";
	//DataApi URL
	public static final String K_API_URL = "baidutongji.api_ul";
	//USERNAME
	public static final String K_USERNAME = "baidutongji.username";
	//PASSWORD
	public static final String K_PASSWORD = "baidutongji.password";
	//TOKEN
	public static final String K_TOKEN = "baidutongji.token";
	//UUID, used to identify your device, for instance: MAC address 
	public static final String K_UUID = "baidutongji.uuid";//已使用获取本机MAC替代
	//ACCOUNT_TYPE
	public static final String K_ACCOUNT_TYPE = "baidutongji.account_type";

	
	public void loadConfig();
	public String getString(String key);
	public Integer getInteger(String key);
}
