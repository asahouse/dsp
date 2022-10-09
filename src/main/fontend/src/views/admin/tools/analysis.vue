<template>
    <div>
        <cont-header
                title="大数据画像分析"
                sub-title="Analysis" ></cont-header>
        <section class="content">

            <div v-if="showUploadInput">
                <button class="upload-input btn btn-success">
                    <i class="fa fa-file-excel-o"></i>
                    <span>
                    文件上传
                </span>
                    <input type="file" id="uploadInput" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
                </button>
                <p class="text-center margin">
                    请选择文件上传，支持文件类型：.xls、.xlsx
                </p>
                <p class="text-center text-primary" v-show="fileUploading">文件上传中，请稍后...</p>
            </div>


            <div class="ana-content" v-if="!showUploadInput">

                <div class="box box-solid bg-light-blue-gradient ana-about">
                    <div class="box-header ui-sortable-handle">
                        <h3 class="box-title">
                            概览
                        </h3>
                    </div>
                    <div class="box-body" style="padding-top: 38px; ">
                        <ul class="clearfix">
                            <li>
                                <h3>ID 数量</h3><p>23051</p>
                            </li>
                            <li>
                                <h3>ID 打通数量</h3><p>13600</p>
                            </li>
                            <li>
                                <h3>打通率</h3><p>59.00%</p>
                            </li>
                            <li>
                                <h3>有标签量</h3><p>10619</p>
                            </li>
                            <li>
                                <h3>画像覆盖率</h3><p>78.08%</p>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-6">
                        <div class="box box-solid ana-about level-1">
                            <div class="box-header ui-sortable-handle">
                                <h3 class="box-title">
                                    一级标签
                                </h3>
                            </div>
                            <div class="box-body">
                                <div class="chart-content" id="chart-level-1"></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="box box-solid ana-about">
                            <div class="box-header ui-sortable-handle">
                                <h3 class="box-title">
                                    年龄
                                </h3>
                            </div>
                            <div class="box-body">
                                <div class="chart-content" id="chart-age"></div>

                            </div>
                        </div>
                    </div>


                    <div class="col-sm-6">
                        <div class="box box-solid ana-about">
                            <div class="box-header ui-sortable-handle">
                                <h3 class="box-title">
                                    所在地
                                </h3>
                            </div>
                            <div class="box-body">
                                <div class="chart-content" id="chart-location"></div>
                            </div>
                        </div>
                    </div>

                    <div class="col-sm-6">
                        <!--bg-light-blue-gradient-->
                        <div class="box box-solid ana-about">
                            <div class="box-header ui-sortable-handle">
                                <h3 class="box-title">
                                    性别
                                </h3>
                            </div>
                            <div class="box-body">
                                <div class="chart-content" id="chart-sex"></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-12">

                        <div class="box box-solid bg-light-blue-gradient ana-about" style="background: #7ba8d6!important;">
                            <div class="box-header ui-sortable-handle">
                                <h3 class="box-title">
                                    教育水平、品类消费水平、消费水平
                                </h3>
                            </div>
                            <div class="box-body">
                                <div class="chart-content" id="chart-consumption-level"></div>
                            </div>
                        </div>

                    </div>
                    <div class="col-sm-12">

                        <div class="box box-solid ana-about">
                            <div class="box-header ui-sortable-handle">
                                <h3 class="box-title">
                                    人生阶段
                                </h3>
                            </div>
                            <div class="box-body">
                                <div class="chart-content" id="chart-live-stage"></div>
                            </div>
                        </div>

                    </div>
                    <div class="col-sm-6">

                        <div class="box box-solid ana-about">
                            <div class="box-header ui-sortable-handle">
                                <h3 class="box-title">
                                    所在行业
                                </h3>
                            </div>
                            <div class="box-body">
                                <div class="chart-content" id="chart-positions" style="height: 450px;"></div>
                            </div>
                        </div>

                    </div>
                    <div class="col-sm-6">

                        <div class="box box-solid ana-about">
                            <div class="box-header ui-sortable-handle">
                                <h3 class="box-title">
                                    资产状态
                                </h3>
                            </div>
                            <div class="box-body">
                                <div class="chart-content" id="chart-assets-condition" style="height: 450px;"></div>
                            </div>
                        </div>

                    </div>
                    
                </div>

            </div>

        </section>
    </div>
</template>
<style lang="less" scoped>
    .content{
        position: relative;
    }
    .upload-input{
        position: relative;
        display: block;
        width: 120px;
        margin: 23% auto 0 auto;
        input{
            position: absolute;
            left: 0;
            top: 0;
            right: 0;
            bottom: 0;
            opacity: 0;
            cursor: pointer;
            z-index: 5;
        }
    }
    .ana-about{

        position: relative;
        .chart-content{
            height: 390px;
        }
        .box-header{
            position: absolute;
            left: 0;
            top: 0;
            .box-title{
                font-size: 20px;
                opacity: .95;
            }
        }


        ul{
            padding: 0;
            margin: 0;
            li{
                margin: 0;
                list-style:none;
                float: left;
                width: 20%;
                border-right: 1px solid rgba(236, 240, 245, 0.3);
                padding: 0 15px;
                &:last-child{
                    border-right: 0;
                }
                h3{
                    margin: 10px 0;
                    font-size: 16px;
                }
                p{
                    font-size: 28px;
                }
            }
        }
    }

</style>
<script lang="babel">
    import contHeader from '../../public/contHeader.vue';
    import analysisAmway from '../../../data/analysis_amway';
    import _ from 'lodash'
    import echarts from 'echarts';
    import 'echarts-wordcloud';
    import geoCoordMap from '../../../data/geoCoordMap'

    export default{
        data(){
            return{
                showUploadInput: true,
                fileUploading: false,
                echarts: {
                    level1:{},
                    age:{},
                    sex:{},
                    location:{},
                    consumptionLevel:{},
                    liveStage:{},
                    positions:{},
                    assetsCondition:{},
                }
            }
        },
        vuex:{
            getters:{
                user: ({user})=>user,
            }
        },
        ready(){
            let self = this;
            $(self.$el).find('#uploadInput').on('change', (e)=>{

                if( self.user.id != 1){
                    layer.msg("您的权限不足，无法使用！");
                    return;
                }

                self.fileUploading = true;

                setTimeout(()=>{
                    self.showDataResult();

                    self.fileUploading = false;
                }, 2000)
            });

        },
        methods:{
            // 展示数据图表
            showDataResult(){
                let self = this;
                self.showUploadInput = false;


                setTimeout(()=>{
                    self.initCharts();
                    self.setChartLevel1();
                    self.setChartAge();
                    self.setChartSex();
                    self.setChartEduLevel();
                    self.setChartLiveStage();
                    self.setChartPositions();
                    self.setAssetsCondition();
                    self.setChartLocations();
                }, 500);
            },
            initCharts(){
                let self = this;

                self.echarts.level1 = echarts.init(self.$el.querySelector('#chart-level-1'));
                self.echarts.age = echarts.init(self.$el.querySelector('#chart-age'));
                self.echarts.sex = echarts.init(self.$el.querySelector('#chart-sex'));
                self.echarts.location = echarts.init(self.$el.querySelector('#chart-location'));
                self.echarts.consumptionLevel = echarts.init(self.$el.querySelector('#chart-consumption-level'));
                self.echarts.liveStage = echarts.init(self.$el.querySelector('#chart-live-stage'));
                self.echarts.positions = echarts.init(self.$el.querySelector('#chart-positions'));
                self.echarts.assetsCondition = echarts.init(self.$el.querySelector('#chart-assets-condition'));

                $(window).resize(()=>{
                    for(let key in self.echarts){
                        self.echarts[key].resize();
                    }
                });
            },
            setChartLevel1(){
                let self = this;

                let option = {
                    title: false,
                    tooltip: {
                        show: true,
                        formatter: function ({ color, name, value}) {
                            return `<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:${color}"></span>${name}: ${value}%`
                        }
                    },
                    series: [{
                        name: '热点分析',
                        type: 'wordCloud',
                        left: 'center',
                        top: 'center',
                        width: '80%',
                        height: '80%',
                        right: null,
                        bottom: null,
                        sizeRange: [12, 60],
                        rotationRange: [-90, 90],
                        rotationStep: 45,
                        gridSize: 8,
                        autoSize: {
                            enable: true,
                            minSize: 1
                        },
                        textStyle: {
                            normal: {
                                color: function() {
                                    return 'rgb(' + [
                                            Math.round(Math.random() * 160),
                                            Math.round(Math.random() * 160),
                                            Math.round(Math.random() * 160)
                                        ].join(',') + ')';
                                }
                            },
                            emphasis: {
                                shadowBlur: 10,
                                shadowColor: '#333'
                            }
                        },
                        data: analysisAmway.level_1
                    }]
                };

                self.echarts.level1.setOption(option);

            },
            setChartAge(){
                let self = this, datas = analysisAmway.attributes['年龄'], markLineData = [];


                for (let i = 1; i < datas.length; i++) {
                    markLineData.push([{
                        xAxis: i - 1,
                        yAxis: datas[i - 1]['value'],
//                        value: (datas[i]['value'] / datas[i - 1]['value'] * 100).toFixed(2)
                    }, {
                        xAxis: i,
                        yAxis: datas[i]['value']
                    }]);
                }

                let option = {
                    "title": false,
                    "tooltip": {
                        "trigger": "axis",
                        "axisPointer": {
                            "type": "shadow",
                            textStyle: {
                                color: "#fff"
                            }

                        },
                        formatter: function ({color, name, value}) {
                            return `<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:${color}"></span>${name}: ${value}%`
                        }
                    },
                    "grid": {
                        "borderWidth": 0,
                        "top": "12%",
                        "left": "10%",
                        "right": "10%",
                        "bottom": "10%",
                        textStyle: {
                            color: "#fff"
                        }
                    },
                    "calculable": true,
                    "xAxis": [{
                        "type": "category",
                        "axisLine": {
                            lineStyle: {
                                color: '#90979c'
                            }
                        },
                        "data": _.map(datas, 'name'),
                    }],
                    "yAxis": [{
                        "type": "value",
                        splitLine: {
                            lineStyle: {
                                color: '#ddd'
                            }
                        }
                    }],
                    "series": [{
                        "name": "年龄占比",
                        "type": "bar",
                        "stack": "总量",
                        "barGap": "10%",
                        "itemStyle": {
                            "normal": {
                                "color": "#38A8E1",
                                "label": {
                                    "show": true,
                                    "textStyle": {
                                        "color": "#fff"
                                    },
                                    "position": "insideTop",
                                    formatter: function(p) {
                                        return p.value > 0 ? (p.value) + "%" : '';
                                    }
                                }
                            }
                        },
                        "data": datas,
                    }]
                };

                self.echarts.age.setOption(option);
            },
            setChartSex(){
                let self = this, boy = +analysisAmway.attributes['性别'][1]['value'], girl = 100-boy;
                let option = {
                    "series": [
                        {
                            "center": [
                                "25.0%",
                                "50%"
                            ],
                            "radius": [
                                "55%",
                                "56%"
                            ],
                            "clockWise": false,
                            "hoverAnimation": false,
                            "type": "pie",
                            "itemStyle": {
                                "normal": {
                                    "label": {
                                        "show": true,
                                        "textStyle": {
                                            "fontSize": 15,
                                            "fontWeight": "bold"
                                        },
                                        "position": "center"
                                    },
                                    "labelLine": {
                                        "show": false
                                    },
                                    "color": "#5886f0",
                                    "borderColor": "#5886f0",
                                    "borderWidth": 25
                                },
                                "emphasis": {
                                    "label": {
                                        "textStyle": {
                                            "fontSize": 15,
                                            "fontWeight": "bold"
                                        }
                                    },
                                    "color": "#5886f0",
                                    "borderColor": "#5886f0",
                                    "borderWidth": 25
                                }
                            },
                            "data": [
                                {
                                    "value": boy,
                                    "name": "男  覆盖率"+boy+"%"
                                },
                                {
                                    "name": " ",
                                    "value": girl,
                                    "itemStyle": {
                                        "normal": {
                                            "label": {
                                                "show": false
                                            },
                                            "labelLine": {
                                                "show": false
                                            },
                                            "color": "#5886f0",
                                            "borderColor": "#5886f0",
                                            "borderWidth": 0
                                        },
                                        "emphasis": {
                                            "color": "#5886f0",
                                            "borderColor": "#5886f0",
                                            "borderWidth": 0
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            "center": [
                                "75.0%",
                                "50%"
                            ],
                            "radius": [
                                "55%",
                                "56%"
                            ],
                            "clockWise": false,
                            "hoverAnimation": false,
                            "type": "pie",
                            "itemStyle": {
                                "normal": {
                                    "label": {
                                        "show": true,
                                        "textStyle": {
                                            "fontSize": 15,
                                            "fontWeight": "bold"
                                        },
                                        "position": "center"
                                    },
                                    "labelLine": {
                                        "show": false
                                    },
                                    "color": "#ee3a3a",
                                    "borderColor": "#ee3a3a",
                                    "borderWidth": 25
                                },
                                "emphasis": {
                                    "label": {
                                        "textStyle": {
                                            "fontSize": 15,
                                            "fontWeight": "bold"
                                        }
                                    },
                                    "color": "#ee3a3a",
                                    "borderColor": "#ee3a3a",
                                    "borderWidth": 25
                                }
                            },
                            "data": [
                                {
                                    "value": girl,
                                    "name": "女  覆盖率" +girl+ "%"
                                },
                                {
                                    "name": " ",
                                    "value": boy,
                                    "itemStyle": {
                                        "normal": {
                                            "label": {
                                                "show": false
                                            },
                                            "labelLine": {
                                                "show": false
                                            },
                                            "color": "#ee3a3a",
                                            "borderColor": "#ee3a3a",
                                            "borderWidth": 0
                                        },
                                        "emphasis": {
                                            "color": "#ee3a3a",
                                            "borderColor": "#ee3a3a",
                                            "borderWidth": 0
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                };

                self.echarts.sex.setOption(option)
            },
            setChartEduLevel(){
                let self = this, indicator = {}, datas = {
                    "教育水平": analysisAmway.attributes['教育水平'],
                    "品类消费水平": analysisAmway.attributes['品类消费水平'],
                    "消费水平": analysisAmway.attributes['消费水平'],
                };
                for(let k in datas){
                    datas[k].forEach(function(item){
                        if(!indicator[k]) indicator[k] = [];
                        if(!indicator[k+"_d"]) indicator[k+"_d"] = [];
                        indicator[k].push({
                            text: item.name,
                            max: 100
                        });
                        indicator[k+"_d"].push(item.value)
                    });
                }

                let option = {
                    title: false,
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: false,
                    radar: [
                        {
                            indicator: indicator['教育水平'],
                            center: ['18%','55%'],
                            radius: 120,
                            name: {
                                textStyle: {
                                    color: '#fff'
                                }
                            },
                        },
                        {
                            indicator: indicator['品类消费水平'],
                            radius: 120,
                            center: ['50%','75%'],
                            name: {
                                textStyle: {
                                    color: '#fff'
                                }
                            },
                        },
                        {
                            indicator: indicator['消费水平'],
                            center: ['83%','55%'],
                            radius: 120,
                            name: {
                                textStyle: {
                                    color: '#fff'
                                }
                            },
                        }
                    ],
                    series: [
                        {
                            type: 'radar',
                            tooltip: {
                                trigger: 'item'
                            },
                            itemStyle: {normal: {areaStyle: {type: 'default'}}},
                            data: [
                                {
                                    value: indicator['教育水平_d'],
                                    name: '教育水平'
                                }
                            ]
                        },
                        {
                            type: 'radar',
                            radarIndex: 1,
                            data: [
                                {
                                    value: indicator['品类消费水平_d'],
                                    name: '品类消费水平'
                                }
                            ]
                        },
                        {
                            type: 'radar',
                            radarIndex: 2,
                            itemStyle: {normal: {areaStyle: {type: 'default'}}},
                            data: [
                                {
                                    value: indicator['消费水平_d'],
                                    name: '消费水平'
                                }
                            ]
                        }
                    ]
                };

                self.echarts.consumptionLevel.setOption(option)
            },
            setChartLiveStage(){
                let self = this, data = analysisAmway.attributes['人生阶段'],
                    data_val = _.map(data, 'value').reverse(),
                    xAxis_val = _.map(data, 'name').reverse(),
                    data_val1=[0,0,0,0,0,0,0];
                let option = {
                    grid:{
                        top: '12%',
                        bottom: "5%",
                        left: "4%",
                        right: "5%",
                        containLabel:true
                    },
                    tooltip:{
                        show:true,
                        backgroundColor:'#fff',
                        borderColor:'#fff',
                        borderWidth:1,
                        textStyle:{
                            color:'#3c3c3c',
                            fontSize:13
                        },
                        formatter:'{b}:{c}',
                        extraCssText:'box-shadow: 0 0 5px rgba(0, 0, 0, 0.3)'
                    },
                    legend: false,
                    xAxis: {
                        data: xAxis_val,
                        boundaryGap:false,
                        axisLine:{
                            show:false
                        },
                        axisTick:{
                            show:false
                        }
                    },
                    yAxis: {
                        axisLabel:{
                            textStyle:{
                                color: "#000"
                            }
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#ccc',
                                opacity:0.49,
                            }
                        },
                        splitLine:{
                            show:true,
                            lineStyle:{
                                color:'#ccc',
                                opacity:0.49,
                            }
                        }
                    },
                    series: [
                        {
                            type: 'bar',
                            name:'rate',
                            tooltip:{
                                show:false
                            },
                            animation:false,
                            barWidth:1,
                            hoverAnimation:false,
                            data: data_val,
                            itemStyle:{
                                normal:{
                                    color:'#fff',
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        },
                        {
                            type: 'line',
                            name:'rate',
                            smooth:true,
                            symbolSize:8,
                            animation:false,
                            hoverAnimation:false,
                            data:data_val,
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:true,
                                        position:'top',
                                        textStyle:{
                                            color:'#3c3c3c'
                                        }
                                    }
                                }
                            },
                            label:{
                                normal:{
                                    formatter: "{c}%"
                                }
                            },
                            areaStyle:{
                                normal:{
                                    color:'#F46B5F',
                                }
                            }

                        }
                    ]
                };

                self.echarts.liveStage.setOption(option);

            },
            setChartPositions(){
                let self = this,
                    data = analysisAmway.attributes['所在行业'],
                    categories = _.map(data, 'name'),
                    datas = _.map(data, 'value'),
                    bar_category_gap = '35%',
                    category_font_size = 13;

                let option = {
                    title: false,
                    grid: {
                        left: 140,
                        bottom: 15
                    },
                    tooltip: {
                        trigger: 'item',
                    },
                    yAxis: {
                        type: 'category',
                        data: categories,
                        inverse: true,
                        axisTick: {
                            show: false,
                        },
                        axisLine: {
                            show: false
                        },
                        splitLine: {
                            show: true,
                            interval: 3,
                            lineStyle: {
                                width: 1.5
                            }
                        },
                        axisLabel: {
                            textStyle: {
                                fontSize: category_font_size,
                            },
                        },
                    },
                    xAxis: {
                        axisLine: {
                            show: true,
                            lineStyle: {
                                color: 'rgb(135,135,135)',
                                width: 1,
                            }
                        },
                        axisTick: {
                            length: 4
                        },
                        axisLabel: {
                            formatter: '{value}%',
                            textStyle: {
                                fontSize: 10,
                            },
                        },
                        splitLine: {
                            show: false
                        },
                        name: '好评率',
                        nameLocation: 'end',
                        position: 'top',


                    },
                    series: [{
                        type: 'bar',
                        data: datas,
                        // barWidth: 30,
                        barCategoryGap: bar_category_gap,
                        label: {
                            normal: {
                                show: true,
                                position: 'insideRight',
                                formatter: '{c}%',
                            }
                        },
                        itemStyle: {
                            normal: {
                                color: "#2E90C2"
                            }
                        },
                        markLine: {
                            lineStyle: {
                                normal: {
                                    // color: average_line_color,
                                }
                            },
                            data: [{
                                name: '平均线',
                                type: 'average'
                            }],
                        },
                    }]
                };

                self.echarts.positions.setOption(option)
            },
            setAssetsCondition(){
                let self = this;
                self.echarts.assetsCondition.setOption({
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        x: 'right',
                        data: ['有车', '无车']
                    },
                    series: [{
                        name: '资产状况',
                        type: 'pie',
                        radius: '65%',
                        center: ['50%', '50%'],
                        data: [{
                            value: 35.56,
                            name: '有车',
                            selected:true
                        }, {
                            value: 63.93,
                            name: '无车',
                        }],
                        itemStyle: {
                            normal: {
                                label: {
                                    show: true,
                                    formatter: '{b} :  {d}%'
                                }
                            },
                            labelLine: {
                                show: true
                            }
                        }
                    }]
                });
            },
            setChartLocations(){
                let self = this, datas = analysisAmway.attributes['所在地'], markLineData = [];

                for (let i = 1; i < datas.length; i++) {
                    markLineData.push([{
                        xAxis: i - 1,
                        yAxis: datas[i - 1]['value'],
                    }, {
                        xAxis: i,
                        yAxis: datas[i]['value']
                    }]);
                }

                let option = {
                    "title": false,
                    "tooltip": {
                        "trigger": "axis",
                        "axisPointer": {
                            "type": "shadow",
                            textStyle: {
                                color: "#fff"
                            }

                        },
                        formatter: function (data) {
                            return `<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:${color}"></span>${name}: ${value}%`
                        }
                    },
                    "grid": {
                        "borderWidth": 0,
                        "top": "12%",
                        "left": "10%",
                        "right": "10%",
                        "bottom": "10%",
                        textStyle: {
                            color: "#fff"
                        }
                    },
                    "calculable": true,
                    "xAxis": [{
                        "type": "category",
                        "axisLine": {
                            lineStyle: {
                                color: '#90979c'
                            }
                        },
                        axisLabel:{
                            textStyle:{
                                color:'#000'
                            }
                        },
                        "data": _.map(datas, 'name'),
                    }],
                    "yAxis": [{
                        "type": "value",
                        splitLine: {
                            lineStyle: {
                                color: '#ddd'
                            }
                        },
                        axisLabel:{
                            textStyle:{
                                color:'#000'
                            }
                        }
                    }],
                    "series": [{
                        "name": "所在地占比",
                        "type": "bar",
                        "stack": "总量",
                        "barGap": "10%",
                        "itemStyle": {
                            "normal": {
                                "color": "#02ACD0",
                                'opacity': .5,
                                "label": {
                                    "show": true,
                                    "textStyle": {
                                        "color": "#000"
                                    },
                                    "position": "insideTop",
                                    formatter: function(p) {
                                        return p.value > 0 ? (p.value) + "%" : '';
                                    }
                                }
                            }
                        },
                        "data": datas,
                    }]
                };

                self.echarts.location.setOption(option)

            }
        },
        components:{
            contHeader
        }
    }
</script>
