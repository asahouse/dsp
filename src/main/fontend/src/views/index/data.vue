
<template>
    <div class="content">
        <div class="box">
            <div class="box-body">
                <div class="box-header">
                    <h3 class="box-title">生成数据</h3>
                </div>
                <div class="well">
                    <div class="form">
                        <div class="row">
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>广告主数量</label>
                                    <input v-model="advertiser_count" number type="text" class="form-control">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>活动数</label>
                                    <input v-model="campaign_count" number type="text" class="form-control">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>创意数</label>
                                    <input v-model="creative_count" number type="text" class="form-control">
                                </div>
                            </div>
                            <div class="col-md-9 text-right">
                                <div class="btn-group">
                                    <button class="btn btn-primary" :disabled="rendering" @click.prevent="renderData">开始生成数据</button>
                                    <dropdown type="success" text="生成文件">
                                        <li><a href="javascript:;" @click.prevent="saveFile(1)">广告主</a></li>
                                        <li><a href="javascript:;" @click.prevent="saveFile(2)">活动</a></li>
                                        <li><a href="javascript:;" @click.prevent="saveFile(3)">创意</a></li>
                                    </dropdown>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>
    .code{
        padding: 5px;
        font-size: 12px;
        height: 300px;
    }
</style>
<script lang="babel">

    import {dropdown} from 'vue-strap'
    import _ from 'lodash'
    import imageSize from '../../data/imageSizeData'
    import fileSaver from 'file-saver'
    import moment from 'moment'
    import categoryData from '../../data/category'

    import { getAll } from '../../vuex/actions/category'

    let creativeData = [],
        advertisersData = [],
        campaignsData = [];

    export default{
        data(){
            return {
                advertiser_count: 1,
                campaign_count: 1,
                creative_count: 1,
                carrier: [0, 1, 2],
                networkType: [0, 1, 2, 3, 4],
                os: [0, 1, 2, 3],

                gender: [],
                age: [],
                education: [],
                marriage: [],
                career: [],

                advertisers: [],
                campaigns: [],
                creative: [],

                rendering:false,

                fileId:0
            };
        },
        computed:{
            categoryData() {
                let masses_tag = categoryData['MASSES_TAG'].filter((cate)=> {
                    return cate.category == '固有属性'
                })[0]['subs'];
                return masses_tag;
            },
            tradeAdvert2(){
                return categoryData['TRADE_ADVERT']
            }
        },
        ready(){
            var self = this;

            self.gender = self.getIds('性别');
            self.age = self.getIds('年龄');
            self.education = self.getIds('教育程度');
            self.marriage = self.getIds('婚姻状态');
            self.career = self.getIds('所在行业');
        },
        methods:{
            getIds(tag){
                var self = this, ids = [];
                var subs = self.categoryData.filter((item)=>{
                    return item.category == tag
                })[0]['subs'];
                subs.forEach(({ categoryId })=>{
                    ids.push(categoryId);
                });
                ids.push(null);
                return ids;
            },
            getRanIds(key){
                var ids = null, self = this;
                var r1 = parseInt(Math.random()*100)%self[key].length;
                if( self[key][r1] ){
                    ids = [];
                    for( var i=0; i<r1; i++){
                        ids.push(self[key][i]);
                    }
                }
                return ids;
            },
            renderCreative(id, advId){
                let self = this, count = self.creative_count||_.random(0, 100), _creative = [];
                for( var i = 0; i< count; i++){
                    var _id = _.uniqueId();
                    var s = imageSize[parseInt(Math.random()*1000)%imageSize.length].split("*");
                    _creative.push({
                        "id" : +_id,
                        "campaignId" : +id,
                        "advId" : +advId,
                        "width" : +s[0],
                        "height" : +s[1],
                        "tradeId" : +self.getTradeId()
                    });
                }
                return _creative;
            },
            renderCampaign(id){
                let self = this, count = self.campaign_count||_.random(0, 100), campaigns = [];
                for(let i = 1; i<=count; i++){

                    var gender = self.getRanIds('gender');
                    var education = self.getRanIds('education');
                    var age = self.getRanIds('age');
                    var career = self.getRanIds('career');
                    var _id = _.uniqueId();
                    
                    var chunk = function (array) {
                        var re = (function () {
                            var index = parseInt(Math.random()*100)%array.length;
                            if( index == array.length -1){
                                return (new Date().getTime())%2==0?null:array;
                            }else{
                                return _.reverse(array).slice(index);
                            }
                        })();
                        console.log( re )
                        return re;
                    }
                    
                    campaigns.push({
                        "id" : +_id,
                        "advId" : +id,
                        "bid" : +(Math.random()*Math.pow(100, i%3+1)).toFixed(2),
                        "userCategory" : {
                            gender, age, education, career
                        },
                        "budget" : +(Math.random()*Math.pow(1000, i%5+1)).toFixed(2),
                        "mobileDevice" : {
                            "carrier" : chunk(self.carrier),
                            "networkType" : chunk(self.networkType),
                            "os" : chunk(self.os)
                        }
                    });

                    creativeData = creativeData.concat(self.renderCreative(_id, id));
                }
                return campaigns;
            },
            renderData(){
                var self = this;
                advertisersData = [];
                campaignsData = [];
                creativeData = [];
                self.rendering = true;

                layer.msg('处理中...', { time:500000 });

                setTimeout(function () {
                    var start = moment();
                    for( var i = 1; i<=self.advertiser_count; i++){
                        advertisersData.push({
                            "id" : i,
                            "balance" : +(Math.random()*Math.pow(100, i%5+1)).toFixed(2)
                        });
                        campaignsData = campaignsData.concat(self.renderCampaign(i));
                    }

                    /*self.advertisers = advertisers;
                     self.campaigns = campaigns;*/

//                    console.log( advertisersData )
//                    console.log( campaignsData )
//                    console.log( creativeData )

                    self.rendering = false;
                    layer.msg('生成成功：' + moment().diff(start, 's'));

                }, 500);

            },
            getTradeId(){
                var self = this, aaa = [];
                self.tradeAdvert2.forEach(({subs})=>{
                    aaa = aaa.concat(subs);
                });
                return aaa[parseInt(Math.random()*1000)%aaa.length]['categoryId']
            },
            saveFile(type){
                this.fileId = this.fileId?this.fileId:_.uniqueId();
                var id = this.fileId;

                if( advertisersData.length == 0 || creativeData.length == 0 || campaignsData.length == 0){
                    layer.msg("别急，先点击隔壁的按钮");
                    return;
                }

                if( type == 1){
                    fileSaver.saveAs(new Blob([JSON.stringify(advertisersData)], {type: "application/json;charset=utf-8"}), `advertisersData_${id}.json`);
                }

                if( type == 2){
                    fileSaver.saveAs(new Blob([JSON.stringify(campaignsData)], {type: "application/json;charset=utf-8"}), `campaignsData_${id}.json`);
                }

                if( type == 3){
                    fileSaver.saveAs(new Blob([JSON.stringify(creativeData)], {type: "application/json;charset=utf-8"}), `creativeData_${id}.json`);
                }
            }
        },
        components:{
            dropdown
        }
    }
</script>
