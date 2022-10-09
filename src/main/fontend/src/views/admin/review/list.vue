<template>
    <cont-header
            title="素材审核"
            sub-title="audit" ></cont-header>

    <section class="content">
        <div class="nav-tabs-custom no-margin">
            <ul class="nav nav-tabs">
                <li class="active"><a href="javascript:;">审核列表</a></li>
                <li><a v-link="'/admin/review_item'">预览审核</a></li>
            </ul>
            <div class="tab-content no-padding">
                <data-table
                        :id="'advertisers-review-audit'"
                        :box-height="systemContentHeight-145"
                        :columns="auditColumns"
                        :options="auditTableOpt"
                        v-ref:advertisers-review-audit
                        v-on:call-action="auditTableAction"
                >

                    <div class="pull-left btn-group" slot="toolbar">
                        <button type="button" class="btn btn-sm btn-primary" @click="auditMultiple">
                            <i class="fa fa-gavel"></i> 批量审核
                        </button>
                        <button class="btn btn-default btn-sm" @click="$refs.advertisersReviewAudit.refresh">
                            <i class="fa fa-refresh"></i> 刷新
                        </button>
                    </div>
                </data-table>
            </div>
        </div>
    </section>

    <modal title="审核操作" :show.sync="isShowReviewModal" effect="fade" :width="300">
        <div slot="modal-body" class="modal-body">
            <div class="form-group form-group-sm">
                <div class="radio-inline">
                    <input id="rr_1" class="magic-radio" type="radio" v-model="review.review" :value="0">
                    <label for="rr_1">通过</label>
                </div>
                <div class="radio-inline">
                    <input  id="rr_2" class="magic-radio" type="radio" v-model="review.review" :value="1">
                    <label for="rr_2">不通过</label>
                </div>
            </div>
            <div class="form-group form-group-sm no-margin">
                <select v-select2="review.tradeId"
                        class="form-control"
                        data-placeholder="行业"
                        v-model="review.tradeId"
                        style="width:100%;"
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
        <div slot="modal-footer" class="modal-footer">
            <button type="button" class="btn btn-default" @click="isShowReviewModal = false">退出</button>
            <button type="button" class="btn btn-primary" @click="doAudit">确定</button>
        </div>
    </modal>
</template>

<script lang="babel">
    import contHeader from '../../public/contHeader.vue';
    import dataTable from '../../widgets/dataTable.vue'
    import resource from '../../../api/resource'
    import { modal } from 'vue-strap'
    import { creativeMap } from '../../../config/maps'
    import { getTradeName } from '../../../utils/filters'

    export default{
        data(){
            const self = this;
            const auditTableOpt = {
                pageSize: 30,
                filterKeys: ['userName'],
                filterCtrl: {
                    keyMap: [
                        {name: 'status', title: '物料状态', options: creativeMap.status},
                        {name: 'type', title: '物料类型', options: creativeMap.type},
                        {name: 'review', title: '审核状态', options: creativeMap.reveiw}
                    ]
                },
                sort: 'id',
                server: function ({page, size, order, filter, success}) {
                    var _this = this,
                            params = {page, size, sort: order, review: 3};
                    if (_this.filterText && _this.filterText != "") {
                        params['userName'] = _this.filterText;
                    }
                    for (var k in filter) {
                        if (filter[k]) params[k] = filter[k];
                    }
                    self.audit_res.get(params).then(({statusCode, list, total})=> {
                        if (statusCode == 200) {
                            success(list, total);
                        }
                    });
                }
            };
            const auditColumns = [
                {
                    title: '序号', field: "xh", style: {"width": "50px", "text-align": "center"},
                    template (index) {
                        var _this = this;
                        return _this.$parent.defaultOptions.page * _this.$parent.defaultOptions.pageSize + (index + 1);
                    }
                },
                {
                    title: '操作',
                    field: "operation",
                    style: {"width": "60px", "text-align": "center"},
                    template () {
                        return '<button class="btn btn-primary btn-xs" style="position: relative;top: -3px;" type="button" v-action="auditSingle">审核</button>';
                    }
                },
                {
                    title: '名称',
                    field: "name",
                    style: {"width": "240px"},
                    template: (i,d)=>`<a v-action="linkPreview" href="#!/admin/review_item/${d.id}">${d.name}</a>`
                },
                {
                    title: '行业',
                    field: "tradeId",
                    style: {"width": "80px", "text-align": "center"},
                    template: (i, {tradeId})=>getTradeName(tradeId)
                },
                {
                    title: '物料类型',
                    sortable: true,
                    field: "type",
                    style: {"text-align": "center"},
                    template: (index, {type})=>creativeMap.type[type]
                },
                {
                    title: '物料状态',
                    sortable: true,
                    field: "status",
                    style: {"text-align": "center"},
                    template: (i, {status})=>creativeMap.status[status]
                },
                {
                    title: '审核状态',
                    sortable: true,
                    field: "review",
                    style: {textAlign: "center"},
                    template: (i, d)=>creativeMap.review[d.review]
                },
                {
                    title: '尺寸',
                    field: "width",
                    sortable: true,
                    style: {"width": "120px", textAlign: "center"},
                    template: (i, d)=>(d.width + "*" + d.height)
                },
                {
                    title: '物料URL', field: "url", style: {"width": "80px", textAlign: "center"},
                    template: (i, d)=>`<button class="btn btn-default btn-xs" v-action="previewImg" target="_blank">预览</button>`
                },
                {title: '目标地址URL', field: "landingUrl", style: {"width": "240px"}},
                {
                    title: '广告主',
                    field: "advertiser",
                    style: {"width": "60px", textAlign: "center"},
                    template: (i, {advertiser})=>advertiser.userName
                },
                {title: '创建时间', field: "createDate", sortable: true, style: {"width": "140px", textAlign: "center"}}
            ];

            return {
                auditColumns,
                auditTableOpt,
                isShowReviewModal: false,
                reviewItems: [],
                review: {
                    tradeId: "",
                    review: 0
                },
                isShowToolDropdown: false,
                audit_res: resource.admin.review.audit()
            }
        },
        watch: {
            isShowReviewModal(val){
                var self = this;
                if (!val) {
                    self.review = {tradeId: "", review: 0};
                    self.reviewItems = [];
                }
            }
        },
        methods: {
            auditMultiple(){
                var self = this, checkItems = self.$refs.advertisersReviewAudit.checkItems;
                if (checkItems.length == 0) {
                    layer.toast('请选择行');
                    return;
                }
                self.reviewItems = [].concat(checkItems);
                self.isShowReviewModal = true;
            },
            auditSingle(data){
                var self = this;
                self.reviewItems = [data];
                self.isShowReviewModal = true;
            },
            doAudit(){
                let self = this,
                        {review, tradeId} = self.review,
                        reviewData = [];

                self.reviewItems.forEach(({id})=> {
                    let item = {id, review};
                    if (tradeId) item['tradeId'] = tradeId;
                    reviewData.push(item);
                });

                self.audit_res
                        .post(reviewData)
                        .then(({statusCode})=> {
                            if (statusCode == 200) {
                                layer.toast("操作成功");
                                self.isShowReviewModal = false;
                                self.$refs.advertisersReviewAudit.refresh();
                            }
                        });

            },
            auditTableAction(action, data){
                var self = this;
                self[action] && self[action](data);
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
            },
            linkPreview({id}){
                this.$router.go(`/admin/review/preview/${id}`);
            }
        },
        components: {
            contHeader,
            dataTable,
            modal
        }
    };
</script>