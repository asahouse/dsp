package com.baidu.statistics.login.core;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public abstract class RealDataLoginRequest implements Serializable {
	
	@JSONField(serialize=false)
	protected String functionName;

	public String getFunctionName() {
		return functionName;
	}
}
