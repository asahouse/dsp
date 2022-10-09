package com.baidu.statistics.dataapi.om.profile;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/12/9.
 */
public class Site implements Serializable{
    private int status;
    private String create_time;
    private String domain;
    private int site_id;

    private List<SiteDir> sub_dir_list;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public List<SiteDir> getSub_dir_list() {
        return sub_dir_list;
    }

    public void setSub_dir_list(List<SiteDir> sub_dir_list) {
        this.sub_dir_list = sub_dir_list;
    }
}
