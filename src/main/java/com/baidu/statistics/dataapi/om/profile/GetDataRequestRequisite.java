package com.baidu.statistics.dataapi.om.profile;

import com.baidu.statistics.dataapi.core.ApiRequest;

/**
 * Created by benjaminkc on 16/12/9.
 */
public class GetDataRequestRequisite extends ApiRequest{

    //requisite
    private int site_id;
    private int start_date;
    private int end_date;
    private int start_date2;
    private int end_date2;
    private String gran;                //时间粒度(只支持有该参数的报告): day/hour/week/month
    private String order;               //指标排序,示例:visitor_count,desc
    private int start_index;            //获取数据偏移,用于分页;默认是 0
    private int max_results;            //单次获取数据条数,用于分页;默认是 20; 0 表示获取所有数据.

    private String method;              //通常对应要查询的报告

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getStart_date() {
        return start_date;
    }

    public void setStart_date(int start_date) {
        this.start_date = start_date;
    }

    public int getEnd_date() {
        return end_date;
    }

    public void setEnd_date(int end_date) {
        this.end_date = end_date;
    }

    public int getStart_date2() {
        return start_date2;
    }

    public void setStart_date2(int start_date2) {
        this.start_date2 = start_date2;
    }

    public int getEnd_date2() {
        return end_date2;
    }

    public void setEnd_date2(int end_date2) {
        this.end_date2 = end_date2;
    }

    public String getGran() {
        return gran;
    }

    public void setGran(String gran) {
        this.gran = gran;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getStart_index() {
        return start_index;
    }

    public void setStart_index(int start_index) {
        this.start_index = start_index;
    }

    public int getMax_results() {
        return max_results;
    }

    public void setMax_results(int max_results) {
        this.max_results = max_results;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
