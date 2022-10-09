package org.codeworks.dsp.adx.bes.dto.commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * bes api错误
 * Created by Luis on 2016/9/13.
 */
public class APIError implements Serializable {

    // 错误所在list中的索引位置
    private int index = -1;

    // 错误所在类中的变量名称
    private String field;

    // 错误码
    private int code;

    // 错误信息,英文
    private String message;

    public APIError(int index, String field, int code, String message) {
        super();
        this.index = index;
        this.field = field;
        this.code = code;
        this.message = message;
    }

    public APIError(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public APIError(){}

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).append("index", index).append("field", field).append("code", code).append("message", message).toString();
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
