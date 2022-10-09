<template>
    <div class="audit-box">
        <cont-header
                title="素材审核"
                sub-title="audit" ></cont-header>

        <section class="content">
            <div class="nav-tabs-custom no-margin">
                <ul class="nav nav-tabs">
                    <li><a v-link="'/admin/review'">审核列表</a></li>
                    <li class="active"><a href="javascript:;">预览审核</a></li>
                </ul>
                <div class="tab-content no-padding" style="position: relative;">
                    <div class="noDataPlaceholder" v-if="noDataPlaceholder">暂无数据</div>
                    <div id="audit_preview" class="audit_preview" :style="{height:(systemContentHeight-380)+'px'}">
                        <h4 style="padding-left:15px; line-height: 2;">图片预览：</h4>
                        <div class="audit_preview_inner">
                            <span v-if="!!preview_img_txt">{{preview_img_txt}}</span>
                            <img :src="material.url" alt="" :style="previewImgStyle">
                        </div>
                        <div class="audit_page text-center">
                            {{auditIndex+1}}/{{total}}
                        </div>
                        <div class="audit_page" style="bottom:-12px; padding: 0 15px; z-index: 100000;">
                            <a v-link="prevLink" class="btn btn-default pull-left" v-show="!!prevLink"><i class="fa fa-angle-left"></i> 上一个</a>
                            <a v-link="nextLink" class="btn btn-default pull-right" v-show="!!nextLink">下一个 <i class="fa fa-angle-right"></i></a>
                        </div>
                    </div>
                    <div class="audit_base_info" v-if="material">
                        <!--<span class="title">物料信息</span>-->
                        <div class="row">
                            <div class="col-sm-3">
                                <strong>名称：</strong>{{material.name}}
                            </div>
                            <div class="col-sm-3">
                                <strong>行业：</strong>{{$tradeName(material.tradeId)}}
                            </div>
                            <div class="col-sm-3">
                                <strong>物料类型：</strong>{{creativeMap.type[material.type]}}
                            </div>
                            <div class="col-sm-3">
                                <strong>物料状态：</strong>{{creativeMap.status[material.status]}}
                            </div>
                            <div class="col-sm-3">
                                <strong>审核状态：</strong>{{creativeMap.review[material.review]}}
                            </div>
                            <div class="col-sm-3">
                                <strong>尺寸：</strong>{{material.width}}x{{material.height}}
                            </div>
                            <div class="col-sm-3">
                                <strong>广告主：</strong>{{material.advertiser.userName}}
                            </div>
                            <div class="col-sm-6">
                                <strong>物料URL：</strong>{{material.url}}
                            </div>
                            <div class="col-sm-6">
                                <strong>行业：</strong>
                                <select v-select2="material.tradeId"
                                        class="form-control"
                                        data-placeholder="行业"
                                        style="width:200px"
                                >
                                    <option value="">行业</option>
                                    <template v-for="trade in tradeAdvert">
                                        <optgroup :label="trade.category">
                                            <template v-for="td in trade.subs">
                                                <option :value="td.categoryId">{{td.category}}</option>
                                            </template>
                                        </optgroup>
                                    </template>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="campaign-footer clearfix">
                        <div class="btn-group pull-right">
                            <a class="btn btn-default" v-link="'/admin/review'"><i class="fa fa-angle-left"></i> 返回列表</a>
                            <button class="btn btn-danger" @click.prevent="doAudit(1);"><i class="fa fa-hand-stop-o"></i> 驳回</button>
                            <button class="btn btn-primary" @click.prevent="doAudit(0);"><i class="fa fa-thumbs-o-up"></i> 通过</button>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</template>
<style>
    .noDataPlaceholder{
        position: absolute;
        top: 0;
        right: 0;
        left: 0;
        bottom: 0;
        background: #fff;
        z-index: 2;
        text-align: center;
        padding-top: 38%;
        font-size: 32px;
        color: #ccc;
    }
    .audit_base_info{
        margin-top: 20px;
        padding: 20px 15px 0 15px;
        border-top:1px solid #eee;
        position: relative;
        color: #999;
        line-height: 2;
        height: 130px;
    }
    .audit_base_info span.title{
        position: absolute;
        right: 15px;
        top: 0;
        font-size: 32px;
        color: #ccc;
    }
    .audit_base_info strong{
        color: #000;
    }
    .audit_base_info [class*=col-]{
        padding-top: 3px;
        padding-bottom: 3px;
        white-space: nowrap;
        overflow: hidden;
        -ms-text-overflow: ellipsis;
        text-overflow: ellipsis;
    }
    .audit_preview{
        position: relative;
    }
    .audit_preview_inner{
        position: absolute;
        left: 15px;
        right: 15px;
        top: 35px;
        bottom: 0;
        background:center no-repeat;
        background-size: contain;
        text-align: center;
    }
    .audit_preview_inner span{
        position: absolute;
        top: 30%;
        left: 0;
        right: 0;
    }
    .audit_page{
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        color: #888;
    }
</style>
<script lang="babel">
    import contHeader from '../../public/contHeader.vue';
    import dataTable from '../../widgets/dataTable.vue'
    import resource from '../../../api/resource'
    import { modal } from 'vue-strap'
    import { creativeMap } from '../../../config/maps'

    export default{
        data(){
            const self = this;
            return {
                preview_img_txt:"",
                previewScroll:[],
                auditList:[],
                total:0,
                creativeMap,
                auditIndex:-1,
                audit_res: resource.admin.review.audit()
            }
        },
        ready(){
            var self = this;
            self.refreshList();
            /*self.previewScroll = new IScroll(self.$el.querySelector('#audit_preview'), {
                interactiveScrollbars: true,
                click: true,
                scrollX: true,
                scrollY: true,
                mouseWheel: true,
                scrollbars: true,
                probeType: 3
            });*/
        },
        computed:{
            prevLink() {
                var self = this, link = "";
                if(self.auditIndex>0 && self.auditList[self.auditIndex-1]){
                    link = "/admin/review_item/" + self.auditList[self.auditIndex-1].id;
                }
                return link;
            },
            nextLink() {
                var self = this, link = "";
                if(self.auditList[self.auditIndex+1]){
                    link = "/admin/review_item/" + self.auditList[self.auditIndex+1].id;
                }
                return link;
            },
            noDataPlaceholder(){
                return this.auditList<=0;
            },
            material(){
                var self = this, material;
                if(self.$route.params.id){
                    let ms = self.auditList.filter((m)=>(m.id == self.$route.params.id));
                    if(ms.length > 0){
                        material = ms[0];
                    }else{
                        material = self.auditList[0];
                    }
                }else{
                    material = self.auditList[0];
                }
                self.auditIndex = self.auditList.indexOf(material);
                return self.$deepClone(material);
            },
            previewImgStyle(){
                let self = this,
                    $ele = $(self.$el.querySelector('#audit_preview')),
                    cw = $ele.width() * 0.8,
                    ch = (self.systemContentHeight-380) * 0.8,
                    _width=self.material.width,
                    _height=self.material.height;

                if( self.material.width/self.material.height >1){
                    if(self.material.width>cw){
                        _width = cw;
                        _height = self.material.height * (_width/self.material.width);
                    }
                }else{
                    if( self.material.height > ch){
                        _height = ch;
                        _width = self.material.width * (_height/self.material.height);
                    }
                }

                return self.material.url?{
                    'position': "absolute",
                    'width': _width + 'px',
                    'height': _height + 'px',
                    'left': '50%',
                    'top': '50%',
                    'margin-left': -_width/2 + "px",
                    'margin-top': -_height/2 + "px",
                }:{};
            }
        },
        methods: {
            refreshList(){
                const self = this;
                self.audit_res.get({size:10000, review: 3}).then(({statusCode, list, total})=> {
                    if (statusCode == 200) {
                        if(total > 0){
                            self.auditList = list;
                            self.total = total;
                        }
                    }
                });
            },
            doAudit(review){
                let self = this, {id, tradeId} = self.material;

                layer.confirm("确定要操作吗？", function () {
                    self.audit_res
                            .post([{id, review, tradeId}])
                            .then(({statusCode})=> {
                                if (statusCode == 200) {
                                    layer.toast("操作成功");
                                    self.$nextTick(()=>{
                                        self.$router.go(self.nextLink?self.nextLink:'/admin/review_item');
                                    });
                                }
                            });
                });

            },
            previewImg(data){
                layer.open({
                    type: 1,
                    title: false,
                    closeBtn: 0,
                    effect: 1,
                    area: [data.width + "px", data.height + "px"],
                    skin: 'layui-layer-nobg',
                    shadeClose: true,
                    content: `<img src='${data.url}' />`
                });
            }
        },
        components: {
            contHeader,
            dataTable,
            modal
        }
    };
</script>