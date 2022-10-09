package com.baidu.statistics.dataapi.om.profile;

import com.baidu.statistics.dataapi.core.ApiRequest;

/**
 * Created by benjaminkc on 16/12/9.
 *
 * 注:

 1. method 参数,通常对应要查询的报告,与网站页面所发送请求的 method 参数一致,如要获取
 趋势分析报告的数据,所用到的方法为“trend/time/a”。

 2. metrics 参数,是所要获取的指标,根据不同的报告填写相关指标,与网站页面所发送请求的
 indicators 参数一致,如要获取浏览量(PV)、访客数(UV)、新访客数,则所填指标为
 “pv_count,visitor_count,new_visitor_count”。
 详见下面“关键参数与报告的对应”的描述。

 */
public class GetDataRequest extends GetDataRequestRequisite{

    //requisite
    private String method;              //通常对应要查询的报告
    private String metrics;             //自定义指标选择,多个指标用逗号分隔

    //option
    private int target;                 //转化目标: -1:全部页面目标 -2:全部事件目标 -3:时长目标 -4:访问页数目标
    private String source;              //来源过滤: through:直接访问 search,0:搜索引擎全部 link:外部链接
    private String clientDevice;        //设备过滤 pc:计算机 mobile:移动设备
    private String area;                //地域过滤 china:全国 province,1:省市自治区北京 province,2:省市自治区上海 province,3:省市自治区天津 other:其他
    private String visitor;             //访客过滤 new:新访客 old:老访客


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClientDevice() {
        return clientDevice;
    }

    public void setClientDevice(String clientDevice) {
        this.clientDevice = clientDevice;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getVisitor() {
        return visitor;
    }

    public void setVisitor(String visitor) {
        this.visitor = visitor;
    }


}
