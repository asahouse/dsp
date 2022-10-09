package com.baidu.statistics.dataapi.om.profile;

import java.io.Serializable;

/**
 * Created by benjaminkc on 16/12/9.
 */
public class SiteDir implements Serializable {
    private String name;
    private String create_time;
    private int sub_dir_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getSub_dir_id() {
        return sub_dir_id;
    }

    public void setSub_dir_id(int sub_dir_id) {
        this.sub_dir_id = sub_dir_id;
    }
}
