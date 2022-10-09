<template>
    <div>
        <cont-header
                title="投放活动监测"
                sub-title="Activity Monitoring">
            <select v-select2="currentSiteId"
                    class="form-control"
                    data-class="site-switch"
                    v-model="currentSiteId"
            >
                <template v-for="item in siteList">
                    <option :value="item.site_id">{{item.domain}}</option>
                </template>
            </select>
        </cont-header>

        <section class="content">

            <div class="box">
                <div class="box-header">
                    <h3 class="box-title">今日流量</h3>
                </div>
                <div class="box-body">
                    <table v-if="!previewLoading" cellspacing="0" cellpadding="0" class="table list table-bordered text-center">
                        <tbody>
                        <tr class="title">
                            <th></th>
                            <th>浏览量(PV)</th>
                            <th>访客数(UV)</th>
                            <th>IP数</th>
                            <th>跳出率</th>
                            <th>平均访问时长</th>
                            <th>转化次数</th>
                        </tr>
                        <tr class="highlight">
                            <td class="normal">今日</td>
                            <td>{{timeTrendRpt.pv_count.items[1][1]}}</td>
                            <td>{{timeTrendRpt.visitor_count.items[1][1]}}</td>
                            <td>{{timeTrendRpt.ip_count.items[1][1]}}</td>
                            <td>{{timeTrendRpt.bounce_ratio.items[1][1]}}%</td>
                            <td>{{vt_2}}</td>
                            <td>{{timeTrendRpt.trans_count.items[1][1]}}</td>
                        </tr>
                        <tr>
                            <td class="normal">昨日</td>
                            <td>{{timeTrendRpt.pv_count.items[1][0]}}</td>
                            <td>{{timeTrendRpt.visitor_count.items[1][0]}}</td>
                            <td>{{timeTrendRpt.ip_count.items[1][0]}}</td>
                            <td>{{timeTrendRpt.bounce_ratio.items[1][0]}}%</td>
                            <td>{{vt_1}}</td>
                            <td>{{timeTrendRpt.trans_count.items[1][0]}}</td>
                        </tr>
                    </table>
                    <p class="text-center" v-if="previewLoading" >加载中...</p>
                </div>
            </div>

            <div class="row">

                <div class="col-sm-12">
                    <div class="date-time-select clearfix">
                        <a @click.prevent="tsIndex=0" :class="{'active': tsIndex == 0 }" class="item pull-left">今天</a>
                        <a @click.prevent="tsIndex=1" :class="{'active': tsIndex == 1 }" class="item pull-left">昨天</a>
                        <a @click.prevent="tsIndex=2" :class="{'active': tsIndex == 2 }" class="item pull-left">最近7天</a>
                        <a @click.prevent="tsIndex=3" :class="{'active': tsIndex == 3 }" class="item pull-left">最近30天</a>
                    </div>
                </div>

                <div class="col-sm-6">

                    <div class="box mid-box">
                        <div class="box-header">
                            <h3 class="box-title">趋势图</h3>
                            <div class="box-tools pull-right qst-tools">
                                对比：
                                <span class="db-cbx">
                                    <input type="radio" class="magic-checkbox" v-model="qst_db" value="1">
                                    <label @click="toggleDb(1)">昨天</label>
                                </span>
                                <span class="db-cbx">
                                    <input type="radio" class="magic-checkbox" v-model="qst_db" value="2">
                                    <label @click="toggleDb(2)">上周同期</label>
                                </span>
                            </div>
                        </div>
                        <select v-model="qst_metric" class="form-control qst-metric">
                            <option v-for="item in qst_metrics" :value="$key">{{item}}</option>
                        </select>
                        <div class="box-body">
                            <div id="ec-qst" class="ec-cont">
                                <p class="text-center ec-loading">
                                    加载中
                                </p>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="col-sm-6">

                    <div class="box visit-box mid-box">
                        <div class="box-header">
                            <h3 class="box-title">新老访客</h3>
                        </div>
                        <div class="box-body">
                            <div class="row" style="padding: 20px 0;">
                                <div class="col-xs-4">
                                    <img class="center-block" style="margin-top: 15px;" src="http://tongji.baidu.com/web/css/decorator/visit-type-icon.png" alt="">
                                </div>
                                <div class="col-xs-4">
                                    <h4>新访客</h4>
                                    <p class="text-success">{{visitData.visitType.newVisitor.ratio}}%</p>
                                </div>
                                <div class="col-xs-4">
                                    <h4>老访客</h4>
                                    <p class="text-primary">{{visitData.visitType.oldVisitor.ratio}}%</p>
                                </div>
                            </div>

                            <table class="table table-bordered">
                                <tbody>
                                <tr>
                                    <td class="visit-type-detail-name">浏览量</td>
                                    <td class="visit-type-detail-new">{{visitData.visitType.newVisitor.pv_count}}</td>
                                    <td class="visit-type-detail-old">{{visitData.visitType.oldVisitor.pv_count}}</td>
                                </tr>
                                <tr>
                                    <td class="visit-type-detail-name">访客数</td>
                                    <td class="visit-type-detail-new">{{visitData.visitType.newVisitor.visitor_count}}</td>
                                    <td class="visit-type-detail-old">{{visitData.visitType.oldVisitor.visitor_count}}</td>
                                </tr>
                                <tr>
                                    <td class="visit-type-detail-name">跳出率</td>
                                    <td class="visit-type-detail-new">{{visitData.visitType.newVisitor.bounce_ratio}}%</td>
                                    <td class="visit-type-detail-old">{{visitData.visitType.oldVisitor.bounce_ratio}}%</td>
                                </tr>
                                <tr>
                                    <td class="visit-type-detail-name">平均访问时长</td>
                                    <td class="visit-type-detail-new">{{visitData.visitType.newVisitor.avg_visit_time}}</td>
                                    <td class="visit-type-detail-old">{{visitData.visitType.oldVisitor.avg_visit_time}}</td>
                                </tr>
                                <tr>
                                    <td class="visit-type-detail-name">平均访问页数</td>
                                    <td class="visit-type-detail-new">{{$toHourTime(visitData.visitType.newVisitor.avg_visit_pages)}}</td>
                                    <td class="visit-type-detail-old">{{$toHourTime(visitData.visitType.oldVisitor.avg_visit_pages)}}</td>
                                </tr>
                                </tbody>
                            </table>

                        </div>
                    </div>

                </div>
                <div class="col-sm-6">

                    <div class="box mid-box">
                        <div class="box-header">
                            <h3 class="box-title">访客属性-年龄分布 </h3>
                        </div>
                        <div class="box-body">
                            <div id="ec-fksx" class="ec-cont">
                                <p class="text-center ec-loading">
                                    加载中
                                </p>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="col-sm-6">

                    <div class="box mid-box">
                        <div class="box-header">
                            <h3 class="box-title">地域分布</h3>
                        </div>
                        <div class="box-body">
                            <div class="ec-cont">
                                <div id="ec-dyfb" style="position: absolute; top: 0;right: 0;left: 0;bottom: 0;">
                                    <p class="text-center ec-loading">
                                        加载中
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>


        </section>
    </div>
</template>
<style lang="less" scoped>
    .qst-metric{
        position: absolute;
        z-index: 99999;
        padding: 2px 5px;
        font-size: 12px;
        text-align: center;
        right: 30px;
        top: 44px;
        width: 100px;
        height: 28px;
    }
    .visit-box p {
        font-size: 30px;
    }
    .visit-type-detail-name{
        width: 30%;
        text-align: center;
        color: #787a7d;
    }
    .ec-loading{
        height: 320px;
        line-height: 320px;
    }
    .ec-cont{
        height: 320px;
        position: relative;
    }
    .mid-box{
        min-height: 400px;
    }
    .date-time-select{
        margin-bottom: 15px;
        border:1px solid #dfe0e0;
        background: #fff;
        line-height: 30px;
        color:#323437;
        .item{
            cursor: pointer;
            border-right: 1px solid #f2f4f4;
            padding: 0 20px;
            &.active{
                 background-color: #4da7fd;
                 color: #fff;
             }
        }
    }
    .qst-tools{
        position: relative;
        top: 5px;
        right: 5px;
        transform: scale(.9);
        .db-cbx{
            display: inline-block;
            padding-right: 5px;
            label{
                font-weight: normal;
            }
        }
    }
</style>
<script type="text/ecmascript-6" lang="babel">

    import mixin from './mixin'
    import moment from 'moment'
    import echarts from 'echarts'
    import chinaMap from '../../../data/china.json'
    import _ from 'lodash'

    echarts.registerMap('china', chinaMap);

    export default{
        mixins: [mixin],
        data(){
            return {
                timeTrendRpt: {},
                visitData: {},
                tsIndex:0, // 时间维度
                qst_db:0, //趋势图对比
                start_date: moment().format('YYYYMMDD'),
                end_date: moment().format('YYYYMMDD'),
                start_date2: null,
                end_date2: null,
                echarts:{
                    qst:{},
                    fksx:{},
                    xlfk:{}
                },
                previewLoading: false,
                qst_metric:"pv_count",
                qst_metrics:{
                    pv_count: "浏览量(PV)",
                    visitor_count: "访客数(UV)",
                    ip_count: "IP 数",
                    bounce_ratio: "跳出率，%",
                    avg_visit_time: "平均访问时长，秒",
                    trans_count: "转化次数",
                    contri_pv: "百度推荐贡献浏览量",
                },
                lineStyle:[
                    {
                        areaStyle: {normal: {}},
                        itemStyle:{
                            normal:{
                                color: "#4FA8F9"
                            }
                        },
                        lineStyle:{
                            normal:{
                                color: "#4FA8F9"
                            }
                        },
                    },
                    {
                        areaStyle: {normal: {}},
                        itemStyle:{
                            normal:{
                                color: "#B9DCFD"
                            }
                        },
                        lineStyle:{
                            normal:{
                                color: "#B9DCFD"
                            }
                        },
                    }
                ]
            }
        },
        ready(){
            let self = this;

            self.initEcharts();

        },
        methods: {

            loadChartsData(){
                let self = this;
                self.setQst();
                self.setDyfb();
                self.setFksx();
                self.setXlfk();
            },

            toggleDb(val){
                this.qst_db = this.qst_db == val?0:val;
            },

            initEcharts(){
                let self = this;
                self.echarts.sqt = echarts.init(self.$el.querySelector('#ec-qst'));
                self.echarts.dyfb = echarts.init(self.$el.querySelector('#ec-dyfb'));
                self.echarts.fksx = echarts.init(self.$el.querySelector('#ec-fksx'));

                $(window).resize(()=>{
                    self.echarts.sqt.resize();
                    self.echarts.dyfb.resize();
                    self.echarts.fksx.resize();
                });

            },

            // 趋势图
            setQst(){
                let self = this, series = [], request;

                request = {
                    site_id: self.currentSiteId,
                    start_date: self.start_date,
                    end_date: self.end_date,
                    method: "overview/getTimeTrendRpt",
                    metrics: self.qst_metric
                };

                // 昨天对比
                if(self.qst_db == 1){
                    let last_day = moment(self.start_date).subtract(1, 'day').format('YYYYMMDD');
                    request = Object.assign(request, {
                        start_date: last_day,
                        end_date: last_day,
                        start_date2: self.start_date,
                        end_date2: self.start_date
                    });
                }else if(self.qst_db == 2){ // 上周同期
                    let last_week_day = moment(self.start_date).subtract(7, 'day').format('YYYYMMDD');
                    request = Object.assign(request, {
                        start_date: last_week_day,
                        end_date: last_week_day,
                        start_date2: self.start_date,
                        end_date2: self.start_date
                    });
                }

                self.echarts.sqt.showLoading();
                self.tjApi.data( request )
                    .then((data)=>{
                        self.echarts.sqt.hideLoading();

                        let rspData = {}, fields = data.data.fields, xAxis = [], singleData = [];
                        fields.splice(0, 1);

                        data.data.items.forEach((items, index)=>{
                            for(let item of items){
                                if( index == 0 ){
                                    xAxis.push(item[0]);
                                }else{

                                    if( fields.length > 1){
                                        if( !rspData[item[0]] ) rspData[item[0]] = [];
                                        rspData[item[0]].push(item[1]=="--"?0:item[1]);
                                    }else{
                                        singleData.push(item[0]=="--"?0:item[0]);
                                    }
                                }
                            }
                        });

                        if( data.data.timeSpan.length > 1){
                            data.data.timeSpan.forEach((ts, i)=>{
                                series.push(
                                    Object.assign({},{
                                        name: ts,
                                        type:'line',
                                        data: rspData[ts]
                                    }, self.lineStyle[i])
                                );
                            });
                        }else{
                            series.push(
                                Object.assign({},{
                                    name: data.data.timeSpan[0],
                                    type:'line',
                                    data: singleData
                                }, self.lineStyle[0])
                            );
                        }

                        self.echarts.sqt.clear();
                        self.echarts.sqt.setOption({
                            title: false,
                            tooltip: {
                                trigger: 'axis',
                                formatter: function (params) {
                                    let str = "";
                                    params.forEach((item)=>{
                                        str += item.seriesName
                                            + "<p style='font-size: 12px; margin-bottom: 0;'>"
                                            + self.qst_metrics[self.qst_metric] + "： " + (item.value||'-') + "</p>"
                                    });

                                    return  str;
                                }
                            },
                            grid:{
                                top: 30
                            },
                            legend:{
                                data: data.data.timeSpan
                            },
                            xAxis:  {
                                type: 'category',
                                boundaryGap: false,
                                data: xAxis,
                                splitNumber: 3,
                                splitLine: {
                                    show: false
                                }
                            },
                            yAxis: {
                                type: 'value'
                            },
                            series: series
                        });




                    });


            },
            // 地域分布
            setDyfb(){
                let self = this, request = {
                    method: "overview/getDistrictRpt",
                    site_id: self.currentSiteId,
                    start_date: self.start_date,
                    end_date: self.start_date,
                    start_date2: self.end_date,
                    end_date2: self.end_date,
                    metrics: "pv_count"
                };

                self.echarts.dyfb.showLoading()

                self.tjApi.data( request )
                    .then(({data})=>{

                        self.echarts.dyfb.hideLoading()

                        let series_data = [];
                        if( data.items ){
                            data.items[0].forEach((item, index)=>{
                                series_data.push({
                                    name: item[0],
                                    value: data.items[1][index][0],
                                    persent: data.items[1][index][1]
                                });
                            });
                        }

                        self.echarts.dyfb.clear();
                        self.echarts.dyfb.setOption({
                            title: false,
                            tooltip: {
                                trigger: 'item',
                                formatter: function ({ data }) {
                                    return  data.name
                                        + "<p style='font-size: 12px; margin-bottom: 0;'>"
                                        + "浏览量： " + (data.value||'-') + "<br>"
                                        + "占比： " + (data.persent?data.persent:"-") + "%<br></p>";
                                }
                            },
                            legend: false,
                            visualMap: {
                                left: 'left',
                                top: 'bottom',
                                orient: 'horizontal',
                                text: ['高','低'],           // 文本，默认为数值文本
                                calculable: true,
                                inRange: {
                                    color: ['#EAF2FB','#C6DAF5', '#92B7ED', '#6395E5', '#457DDE']
                                }
                            },
                            series: [
                                {
                                    name: '浏览量',
                                    type: 'map',
                                    mapType: 'china',
                                    roam: false,
                                    label: {
                                        normal: {
                                            show: false
                                        },
                                        emphasis: {
                                            show: false
                                        }
                                    },
                                    top: 0,
                                    data: series_data
                                }
                            ]
                        });

                    });

            },
            // 访客属性-年龄分布
            setFksx(){
                let self = this, request = {
                    method: "overview/getCommonTrackRpt",
                    site_id: self.currentSiteId,
                    start_date: self.start_date,
                    end_date: self.start_date,
                    start_date2: self.end_date,
                    end_date2: self.end_date,
                    metrics: "pv_count"
                };

//                self.tjApi.data( request )
//                    .then(({ data })=>{
//                        console.log( data )
//                    })

                self.echarts.fksx.clear();
                self.echarts.fksx.setOption({
                    color: ['#4FA8F9'],
                    tooltip : {
                        trigger: 'axis',
                        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        }
                    },
                    grid: {
                        top: '3%',
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis : [
                        {
                            type : 'category',
                            data : [
                                '10-19岁',
                                '20-29岁',
                                '30-39岁',
                                '40-49岁',
                                '50-59岁'
                            ],
                            axisTick: {
                                alignWithLabel: true
                            }
                        }
                    ],
                    yAxis : {
                        axisLabel: {
                            formatter: function (val) {
                                return val + '%';
                            }
                        }
                    },
                    series : [
                        {
                            name:'年龄分布',
                            type:'bar',
                            barWidth: '60%',
                            data:[10, 52, 40, 34, 60]
                        }
                    ]
                });


            },
            // 新老访客
            setXlfk(){

                let self = this, request = {
                    method: "overview/getCommonTrackRpt",
                    site_id: self.currentSiteId,
                    start_date: self.start_date,
                    end_date: self.start_date,
                    start_date2: self.end_date,
                    end_date2: self.end_date,
                    metrics: "pv_count"
                };

                self.tjApi.data( request )
                    .then(({ data })=>{
                        self.$set('visitData', data);
                    })


            },
            // 今日流量
            getPreviewData(){
                const start_date = moment().subtract(1, 'day').format('YYYYMMDD'),
                    end_date = moment().format('YYYYMMDD');
                let self = this;


                let request = {
                    requisite:{ site_id: self.currentSiteId, start_date, end_date},
                    requests: []
                }, metrics = Object.keys( self.qst_metrics );
                metrics.forEach((k)=>{
                    request.requests.push({
                        "method":"overview/getTimeTrendRpt",
                        "metrics": k
                    })
                });

                self.previewLoading = true;
                self.tjApi
                    .overview(request)
                    .then((data)=>{
                        self.previewLoading = false;

                        let timeTrenRpt = {};
                        metrics.forEach((k, index)=>{
                            timeTrenRpt[k] = data.data[index];
                        });

                        self.$set('timeTrendRpt', timeTrenRpt);
                    });


            },

            getAllData: _.debounce(function(){
                let self = this;
                self.getPreviewData();
                self.loadChartsData();
            }, 100)
        },
        computed: {
            vt_1(){
                return this.$toHourTime(this.timeTrendRpt.avg_visit_time.items[1][0]);
            },
            vt_2(){
                return this.$toHourTime(this.timeTrendRpt.avg_visit_time.items[1][1]);
            }
        },
        watch:{
            tsIndex(val){
                let self = this;
                switch (self.tsIndex){
                    case 0:
                        self.start_date = moment().format('YYYYMMDD');
                        self.end_date = moment().format('YYYYMMDD');
                        break;
                    case 1:
                        self.start_date = moment().subtract(1, 'day').format('YYYYMMDD');
                        self.end_date = moment().subtract(1, 'day').format('YYYYMMDD');
                        break;
                    case 2:
                        self.start_date = moment().subtract(7, 'day').format('YYYYMMDD');
                        self.end_date = moment().format('YYYYMMDD');
                        break;
                    case 3:
                        self.start_date = moment().subtract(30, 'day').format('YYYYMMDD');
                        self.end_date = moment().format('YYYYMMDD');
                        break;
                }

                self.loadChartsData();

            },
            qst_db(){
                this.setQst();
            },
            qst_metric(){
                this.setQst();
            },
            "$route"(val){
                let self = this;
                if( !val.params.siteId && self.currentSiteId){
                    self.$router.go('/admin/bdtj/' + self.currentSiteId);
                }
            },
            currentSiteId(currentSiteId){
                let self = this;
                if( currentSiteId ){
                    self.$router.go('/admin/bdtj/' + self.currentSiteId);
                    self.getAllData();
                }
            },
        }

    }
</script>
