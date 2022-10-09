package com.baidu.statistics.dataapi.om.profile;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/12/9.
 */
public class ReportData implements Serializable{
    private String metrics;

    private Integer total;
    private Integer offset;
    private List<Object> sum;
    private List<Object> pageSum;
    private String analyticsResult;
    private List<Object> items;
    private List<String> timeSpan;
    private List<String> fields;

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<Object> getSum() {
        return sum;
    }

    public void setSum(List<Object> sum) {
        this.sum = sum;
    }

    public List<Object> getPageSum() {
        return pageSum;
    }

    public void setPageSum(List<Object> pageSum) {
        this.pageSum = pageSum;
    }

    public String getAnalyticsResult() {
        return analyticsResult;
    }

    public void setAnalyticsResult(String analyticsResult) {
        this.analyticsResult = analyticsResult;
    }

    public List<Object> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }

    public List<String> getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(List<String> timeSpan) {
        this.timeSpan = timeSpan;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
