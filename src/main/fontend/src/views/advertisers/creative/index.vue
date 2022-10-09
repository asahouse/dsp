
<template>

    <cont-header
        :title="'活动创意：' + campaign.name"
        sub-title="creatives" ></cont-header>
    <section class="content">
        <div class="nav-tabs-custom no-margin">
            <ul class="nav nav-tabs">
                <li><a v-link="'../../campaigns/' + $route.params.campaignId">活动信息</a></li>
                <li class="active"><a href="javascript:;">活动创意</a></li>
                <li class="pull-right"><a v-link="'/advertisers/campaigns'" class="text-muted"><i class="fa fa-list"></i> 返回列表</a></li>
            </ul>
            <div class="tab-content no-padding">
                <data-table
                        :id="'advertisers-creative-material'"
                        :box-height="systemContentHeight-145"
                        :columns="columns"
                        :options="tableOpt"
                        v-on:call-action="advertiserCreativeAction"
                        v-ref:advertiser-creative-table >
                    <div class="pull-left btn-group" slot="toolbar">
                        <button type="button" class="btn btn-sm btn-primary" @click.prevent="addMaterials">
                            <i class="fa fa-plus"></i> 新增物料
                        </button>
                        <button type="button" class="btn btn-sm btn-success" @click.prevent="editMaterials">
                            <i class="fa fa-pencil"></i> 编辑所选
                        </button>
                        <button class="btn btn-default btn-sm" @click="$refs.advertiserCreativeTable.refresh">
                            <i class="fa fa-refresh"></i> 刷新
                        </button>
                    </div>
                </data-table>
            </div>
        </div>
    </section>

    <material-upload-modal
            v-on:close="showImageUploadBox = false"
            v-on:add-material-success="addMaterialSuccess"
            v-on:add-material-error="addMaterialError"
            :campaign-id="campaign.id"
            :material-list="selectedMaterials"
            :edit-mode="materialEditMode"
            :show="showImageUploadBox" ></material-upload-modal>
</template>

<script lang="babel">

    import contHeader from '../../public/contHeader.vue';
    import dataTable from '../../widgets/dataTable.vue'
    import resource from '../../../api/resource'

    import materialUploadModal from './materialUploadModal.vue'
    import { dropdown, modal } from 'vue-strap'
    import { getCreativeGroupList } from '../../../vuex/actions/advertiser'

    import { creativeMap } from '../../../config/maps'
    import { getTradeName } from '../../../utils/filters'

    export default{
        data(){
            const self = this;
            const columns = [
                {
                    title: '序号', field: "xh", style: {"width": "60px", "text-align": "center"},
                    template: function (index) {
                        var _this = this;
                        return _this.$parent.defaultOptions.page * _this.$parent.defaultOptions.pageSize + (index + 1);
                    }
                },
                {
                    title: '操作',
                    field: "operation",
                    style: {"width": "60px", "text-align": "center"},
                    template: ()=>`<div class="operation_group">
                                  <button title="操作" type="button" class="btn btn-default btn-xs" data-toggle="dropdown">
                                    <i class="fa fa-gear"></i>
                                  </button>
                                  <div class="btn-list">
                                    <button class="btn btn-default btn-xs" type="button" v-action="editMaterial"><i class="fa fa-edit"></i> 修改</button>
                                  </div>
                                </div>`
                },
                {title: '名称', field: "name", style: {"width": "160px"}},
                {
                    title: '行业',
                    field: "tradeId",
                    style: {"width": "80px", "text-align": "center"},
                    template: (i, {tradeId})=> self.$tradeName(tradeId)
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
                    template: function (i, {status, review}) {
                        var colors = ["btn-primary", "btn-danger", "btn-success"];
                        return review==4?`<button title="操作" type="button" class="btn ${colors[status]} btn-xs" disabled="disabled">
                                ${creativeMap.status[status]} <i class="fa fa-angle-right"></i>
                                </button>`:`<div class="operation_group">
                                <button title="操作" type="button" class="btn ${colors[status]} btn-xs" data-toggle="dropdown">
                                ${creativeMap.status[status]} <i class="fa fa-angle-right"></i>
                                </button>
                                <div class="btn-list">
                                    <button class="btn btn-primary btn-xs ${(status == 0 ? "disabled" : "")}" v-action="changeMaterialStatus_0">${creativeMap.status[0]}</button>
                                    <button class="btn btn-danger btn-xs ${(status == 1 ? "disabled" : "")}" v-action="changeMaterialStatus_1">${creativeMap.status[1]}</button>
                                </div>
                                </div>`;
                    }
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
                    title: '物料URL',
                    field: "url",
                    style: {"width": "80px"},
                    template: (i, d)=>`<button class="btn btn-default btn-xs" v-action="previewImg" target="_blank">预览</button>`
                },
                {title: '目标地址URL', field: "landingUrl", style: {"width": "300px"}, template: (i, d)=>`<a target="_blank" href="${d.landingUrl}">${d.landingUrl}</a>`},
                {title: '创建时间', field: "createDate", sortable: true}
            ];


            return {
                materialEditMode: false,
                material_res: resource.advertisers.creatives.material(self.user.id, self.$route.params.campaignId),
                selectedMaterials: [],
                columns,
                hasCheckFirstItem: false,
                showImageUploadBox: false,
                materialStatus: 0,
                campaignRes: resource.advertisers.campaign(self.user.id),
                campaign:{},
                tableOpt: {
                    pageSize: 30,
                    clientPagination: true, // 客户端分页
                    filterKeys: ['name'],
                    disabledItems: [],
                    filterCtrl: {
                        keyMap: [
                            {
                                name: 'status',
                                title: '物料状态',
                                options: creativeMap.status
                            },
                            {
                                name: 'review',
                                title: '审核状态',
                                options: creativeMap.review
                            }
                        ]
                    },
                    sort: 'id',
                    server({success}) {
                        self.material_res.get().then(({statusCode, list, total})=> {
                            if (statusCode == 200) {
                                self.tableOpt.disabledItems = list.filter(({review})=> {
                                    return review == 4;
                                });
                                success(list, total);
                            }
                        });
                    }
                }
            };
        },
        ready(){
            var self = this;
            self.campaignRes.getOne(self.$route.params.campaignId)
                    .then(({statusCode, campaign})=>{
                        if(statusCode == 200){
                            let { id, name, status, budgetType, budget, bid, startDate, endDate } = campaign;
                            self.$set("campaign", { id, name, status, budgetType, budget, bid, startDate, endDate });
                        }else{
                            layer.toast("活动数据加载失败...", function () {
                                self.$router.go('/advertisers/campaigns')
                            });
                        }
                    });
        },
        methods: {
            // 编辑
            editMaterial(data) {
                var self = this;
                self.materialEditMode = true;
                self.selectedMaterials = [data];
                self.showImageUploadBox = true;
            },
            editMaterials(){
                let self = this, {checkItems} = self.$refs.advertiserCreativeTable;
                if (checkItems.length == 0) {
                    layer.toast("请选择");
                    return;
                }
                self.materialEditMode = true;
                self.selectedMaterials = [].concat(checkItems);
                self.showImageUploadBox = true;
            },
            addMaterials() {
                var self = this;
                if (self.user.review == 0) {
                    self.materialEditMode = false;
                    self.selectedMaterials = [];
                    self.showImageUploadBox = true;
                } else {
                    layer.warning("<h4>没有权限！</h4>广告主未审核通过");
                }
            },
            advertiserCreativeAction(action, data) {
                var _this = this;
                _this[action] && _this[action](data);
            },
            advertiserCreativeGroupAction(action, data) {
                var _this = this;
                _this[action] && _this[action](data);
            },
            // 物料添加成功响应
            addMaterialSuccess(){
                var self = this;
                console.log('addMaterialSuccess')
                self.showImageUploadBox = false
                self.$refs.advertiserCreativeTable.refresh();
            },
            // 物料添加失败响应
            addMaterialError(){
                var self = this;
                console.log('addMaterialError')
            },
            changeMaterialStatus(data, status){
                var self = this,
                        $res = resource.advertisers.creatives.material(self.user.id, self.$route.params.campaignId);

                $res.put({materials: [ _.extend({}, data, {status}) ]}).then(({statusCode})=> {
                            if (statusCode == 200) {
                                layer.toast('操作成功');
                                data.status = status;
                            }
                        });
            },
            changeMaterialStatus_0(data){
                var self = this;
                self.changeMaterialStatus(data, 0)
            },
            changeMaterialStatus_1(data){
                var self = this;
                self.changeMaterialStatus(data, 1)
            },
            previewImg(data){
                layer.open({
                    type: 1,
                    title: false,
                    closeBtn: 0,
                    effect: 1,
                    area: [ data.width+"px", data.height+"px" ],
                    skin: 'layui-layer-nobg',
                    shadeClose: true,
                    content: `<img src='${data.url}' />`
                });
            }
        },
        components: {
            contHeader, dataTable, dropdown,
            materialUploadModal,
            modal
        }
    };
</script>