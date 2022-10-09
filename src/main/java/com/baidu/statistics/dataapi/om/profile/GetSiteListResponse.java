package com.baidu.statistics.dataapi.om.profile;

import com.baidu.statistics.dataapi.core.ApiResponse;

import java.util.List;

/**
 * Created by benjaminkc on 16/12/9.
 */
public class GetSiteListResponse extends ApiResponse {
    private List<Site> list;

    public List<Site> getList() {
        return list;
    }

    public void setList(List<Site> list) {
        this.list = list;
    }
}
