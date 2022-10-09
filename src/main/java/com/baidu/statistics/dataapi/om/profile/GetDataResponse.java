package com.baidu.statistics.dataapi.om.profile;

import com.baidu.statistics.dataapi.core.ApiResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by benjaminkc on 16/12/9.
 */
public class GetDataResponse extends ApiResponse {
    private Map result;

    public Map getResult() {
        return result;
    }

    public void setResult(Map result) {
        this.result = result;
    }
}
