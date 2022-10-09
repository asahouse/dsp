<template>
    <cont-header
            title="活动列表"
            sub-title="campaign list" ></cont-header>

    <section class="content">
        <data-table
                :id="'advertisers-campaign-table'"
                :box-height="systemContentHeight-100"
                :columns="campaignTableColumn"
                :options="campaignTableOption"
                v-ref:advertisers-campaign-table
                v-on:call-action="campaignTableAction">
            <div class="pull-left btn-group" slot="toolbar">
                <a class="btn btn-sm btn-primary" v-link="'/advertisers/campaigns/add'"><i class="fa fa-plus"></i> 新增</a>
                <button class="btn btn-default btn-sm" @click="$refs.advertisersCampaignTable.refresh">
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
    import { campaignMap } from '../../../config/maps'

    export default {
        data(){
            const self = this;
            const campaignTableOption = {
                pageSize: 30,
                filterKeys: ['name'],
                sort: 'id',
                server: function ({page, size, order, filter, success}) {
                    var _this = this,
                            params = {page, size, sort: order};
                    if (_this.filterText && _this.filterText != "") {
                        params['name'] = _this.filterText;
                    }
                    for (var k in filter) {
                        if (filter[k]) params[k] = filter[k];
                    }
                    self.campaign_res.get(params).then(({statusCode, list, total})=> {
                        if (statusCode == 200) {
                            success(list, total);
                        }
                    });
                }
            };
            const campaignTableColumn = [
                {
                    title: '操作',
                    field: "operation",
                    style: {"width": "120px", "text-align": "center", "vertical-align": "middle"},
                    template: ()=>`<div class="btn-group btn-group-xs"><button class="btn btn-default" type="button" v-action="editCampaign"><i class="fa fa-edit"></i> 编辑</button>
                                    <button class="btn btn-success" type="button" v-action="campaignCreative"><i class="fa fa-lightbulb-o"></i> 创意</button></div>`
                },
                {
                    title: '名称',
                    field: "name",
                    style: {"width": "240px", "text-align": "center", "vertical-align": "middle"}
                },
                {
                    title: '广告主状态',
                    field: "status",
                    sortable: true,
                    style: {textAlign: "center"},
                    template: (i, {status})=>campaignMap.status[status]
                },
                {
                    title: '审核状态',
                    field: "review",
                    sortable: true,
                    style: {textAlign: "center"},
                    template: (i, {review})=>campaignMap.review[review]
                },
                {
                    title: '活动预算',
                    field: "budget.budget",
                    style: {"text-align": "center"},
                    template: (i, {budget}) =>('￥' + budget)
                },
                {
                    title: '预算类型',
                    field: "budget.budgetType",
                    style: {"text-align": "center"},
                    template: (i, {budgetType})=>campaignMap.budgetType[budgetType]
                },
                {title: '出价', field: "budget.bid", style: {"text-align": "center"}, template: (i, {bid})=>('￥' + bid)},
                {
                    title: '开始时间',
                    field: "budget.startDate",
                    style: {"text-align": "center"},
                    template: (i, {startDate})=>startDate
                },
                {
                    title: '结束时间',
                    field: "budget.startDate",
                    style: {"text-align": "center"},
                    template: (i, {endDate})=>endDate
                },
            ];
            return {
                campaignTableOption,
                campaignTableColumn,
                campaign_res: resource.advertisers.campaign(self.user.id),
            }
        },
        methods: {
            editCampaign({id}){
                this.$router.go('/advertisers/campaigns/' + id);
            },
            campaignCreative({id}){
                this.$router.go('/advertisers/campaigns/' + id + '/creative');
            },
            campaignTableAction(action, data){
                var self = this;
                self[action] && self[action](data);
            }
        },
        components: {
            contHeader, dataTable
        }
    };
</script>