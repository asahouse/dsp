<style>

</style>

<template>
    <cont-header
            title="广告主"
            sub-title="Advertisers" ></cont-header>

    <section class="content">
        <data-table
                :id="'admin-advertisers'"
                :box-height="systemContentHeight-100"
                :columns="columns"
                :options="tableOpt"
                v-on:call-action="adminAdvertiserAction"
                v-ref:advertiser-table
        >
            <div class="pull-left btn-group" slot="toolbar">
                <a type="button" v-link="{ path:'/admin/advertisers/add' }" class="btn btn-sm btn-primary" ><i class="fa fa-plus"></i> 新增</a>
                <button class="btn btn-default btn-sm" @click="$refs.advertiserTable.refresh">
                    <i class="fa fa-refresh"></i> 刷新
                </button>
            </div>
        </data-table>
    </section>

</template>
<script lang="babel">
    import contHeader from '../../public/contHeader.vue';
    import dataTable from '../../widgets/dataTable.vue'
    import resource from '../../../api/resource'
    import { advertiserMap } from "../../../config/maps"

    export default{
        data(){
            var self = this;
            var columns = [
                {
                    title:'操作',
                    field:"operation",
                    style:{ "width":"60px", "text-align": "center" },
                    template:function () {
                        return `<div class="operation_group">
                                  <button title="操作" type="button" class="btn btn-default btn-xs" data-toggle="dropdown">
                                    <i class="fa fa-gear"></i>
                                  </button>
                                  <div class="btn-list">
                                    <button class="btn btn-default btn-xs" type="button" v-action="editAdvertiser"><i class="fa fa-edit"></i> 修改</button>
                                  </div>
                                </div>`;
                    }
                },
                {
                    title:'序号', field:"xh", style:{ "width":"60px", "text-align": "center" },
                    template:function (index) {
                        var _this = this;
                        return _this.$parent.defaultOptions.page*_this.$parent.defaultOptions.pageSize + (index +1);
                    }
                },
                { title:'用户名', field:"userName", style:{ "width":"120px" } },
                { title:'邮箱', field:"email", style:{ "width":"240px" } },
                { title:'账户余额', field:"balance", style:{ "text-align": "left" }, template:(i,{balance})=>balance?balance:'0' },
                { title:'审核状态', field:"review", sortable:true, style:{ textAlign:"center" }, template:(index, {review})=>advertiserMap.review[review] },
                { title:'是否管理员', field:"isAdmin", style:{ textAlign:"center" }, sortable:true, template:(index, data)=>(data.isAdmin?"是":"否") },
                { title:'状态', field:"status", sortable:true, style:{ width:'70px' }, template:(index, {status})=>advertiserMap.status[status] }
            ];

            return{
                columns,
                openDownCont:false,
                advResource:resource.admin.advertiser(),
                tableOpt:{
                    showCheckBox:false,
                    filterKeys:['userName'],
                    sort:'createDate',
                    mutiSelect:false,
                    filterCtrl:{
                        keyMap:[
                            {
                                name:'status',
                                title:'状态',
                                options: { 1:'启用', 0:'禁止' }
                            },
                            {
                                name:'isAdmin',
                                title:'是否管理员',
                                options: { "true":'是', "false":'否' }
                            },
                            {
                                name:'review',
                                title:'审核状态',
                                options: {
                                    0:'等待处理',1:'通过',2:'审核中',3:'退回'
                                }
                            }
                        ]
                    },
                    // 获取后端数据
                    server: function ({ page, size, order, filter, success }) {
                        var _this = this,
                            params = { page, size, sort:order };
                        if( _this.filterText && _this.filterText != "" ){
                            params['userName'] = _this.filterText;
                        }
                        for( var k in filter){
                            if(filter[k]) params[k] = filter[k];
                        }

                        self.advResource.get(params).done(function ({ statusCode, list, message, total }) {
                            if( statusCode == 200 ){
                                success(list, total);
                            }
                        });
                    }
                },
            }
        },
        methods:{
            /**
             * 由 table 派发的event
             * @param action
             * @param data
             */
            adminAdvertiserAction(action, data) {
                var _this = this;
                _this[action]&&_this[action](data);
            },
            editAdvertiser(data) {
                this.$router.go({ path:'/admin/advertisers/edit/'+ data.id });
            }
        },
        components:{
            contHeader,
            dataTable,
        }
    }
</script>