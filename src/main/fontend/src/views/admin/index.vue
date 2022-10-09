<template>
    <div>
        <cont-header
                title="控制台"
                sub-title="Dashboard" ></cont-header>



        <section class="content">

            <div class="row">
                <div class="col-lg-3 col-xs-6">
                    <div class="small-box bg-aqua">
                        <div class="inner">
                            <h3>{{dashboardResult.totalAdv}}</h3>

                            <p>广告主总量</p>
                        </div>
                        <a v-link="'/admin/advertisers'" class="small-box-footer">更多内容 <i class="fa fa-arrow-circle-right"></i></a>
                    </div>
                </div>
                <div class="col-lg-3 col-xs-6">
                    <div class="small-box bg-green">
                        <div class="inner">
                            <h3>{{dashboardResult.totalCampaign}}</h3>

                            <p>活动数总数</p>
                        </div>
                        <a v-link="'/admin/campaigns'" class="small-box-footer">更多内容 <i class="fa fa-arrow-circle-right"></i></a>
                    </div>
                </div>
                <div class="col-lg-3 col-xs-6">
                    <div class="small-box bg-red">
                        <div class="inner">
                            <h3>{{dashboardResult.totalCampaignNow}}</h3>

                            <p>当天活动数</p>
                        </div>
                        <a v-link="'/admin/campaigns'" class="small-box-footer">更多内容 <i class="fa fa-arrow-circle-right"></i></a>
                    </div>
                </div>
                <div class="col-lg-3 col-xs-6">
                    <div class="small-box bg-yellow">
                        <div class="inner">
                            <h3>{{dashboardResult.threeDaysConsume}}</h3>

                            <p>3天内的消费总数</p>
                        </div>
                        <a v-link="'/admin/tongji'" class="small-box-footer">更多内容 <i class="fa fa-arrow-circle-right"></i></a>
                    </div>
                </div>


                <div class="col-sm-6">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">站点曝光次数</h3>
                        </div>
                        <div class="box-body">
                            <div class="echart-cont" id="echart_1"></div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">站点点击次数</h3>
                        </div>
                        <div class="box-body">
                            <div class="echart-cont" id="echart_2"></div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">广告主消费趋势</h3>
                        </div>
                        <div class="box-body">
                            <div class="echart-cont" id="echart_3"></div>
                        </div>
                    </div>
                </div>



            </div>


        </section>



    </div>
</template>
<style lang="less" scoped>
    .echart-cont{
        height: 360px;
    }
    .small-box{
        background-image: url("../../images/overlay.png");
        text-align: center;
        h3{
            font-size: 50px;
        }
        p{
            font-size: 20px;
        }
    }
</style>
<script lang="babel">
    import contHeader from '../public/contHeader.vue';
    import resource from '../../api/resource'
    import echarts from 'echarts';

    export default{
        data(){
            return{
                dashboardResult:{
                    totalAdv: 0,
                    totalCampaign: 0,
                    totalCampaignNow: 0,
                    threeDaysConsume: 0
                },
                echarts:{
                    echart_1:{},
                    echart_2:{},
                    echart_3:{}
                }
            }
        },
        created(){
            let self = this;
            resource.admin
                .dashboard()
                .get()
                .then((data)=> {
                    self.dashboardResult = data.result;
                });
        },
        ready(){
            let self = this;
            self.initCharts();

            self.setChart_1();
        },
        methods:{
            initCharts(){
                let self = this;

                self.echarts.echart_1 = echarts.init(self.$el.querySelector('#echart_1'));
                self.echarts.echart_2 = echarts.init(self.$el.querySelector('#echart_2'));
                self.echarts.echart_3 = echarts.init(self.$el.querySelector('#echart_3'));

                $(window).resize(()=>{
                    for(let key in self.echarts){
                        self.echarts[key].resize();
                    }
                });
            },
            setChart_1(){
                let self = this;
                let option = {

                };
//                self.echarts.echart_1.setOption(option);
            },
            setChart_2(){
                let self = this;
                let option = {

                };
//                self.echarts.echart_1.setOption(option);
            },
            setChart_3(){
                let self = this;
                let option = {

                };
//                self.echarts.echart_1.setOption(option);
            },
            setChart_4(){
                let self = this;
                let option = {

                };
//                self.echarts.echart_1.setOption(option);
            }
        },
        components:{
            contHeader
        }
    }
</script>
