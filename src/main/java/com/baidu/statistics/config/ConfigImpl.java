package com.baidu.statistics.config;

import com.baidu.statistics.utils.FileUtil;

import java.util.Properties;

public class ConfigImpl implements Config {
	
	private Properties prop;
	
	private static final String CONFIG_NAME = "baidutongji.properties";
	
	
	public void loadConfig() {
		prop = FileUtil.loadProperties("/config/props/" + CONFIG_NAME);
	}
	
	public String getString(String key) {
		return prop.getProperty(key, "");
	}

	public Integer getInteger(String key) {
		return Integer.valueOf(getString(key));
	}
	
}
