
<template>

    <div class="box grid-scroll-box">
        <spinner id="box-spinner" :size="lg" :fixed="(true,false)" text="加载中" v-ref:spinner></spinner>
        <div class="box-header with-border clearfix" v-if="defaultOptions.showHeader">
            <slot name="toolbar">
                <div class="btn-group pull-left">
                    <button class="btn btn-default btn-sm" v-if="defaultOptions.showRefresh" @click="refresh">
                        <i class="ion-ios-loop-strong"></i>
                    </button>
                </div>
            </slot>
            <div class="box-tools clearfix  pull-right">
                <div class="input-group input-group-sm pull-right" v-if="defaultOptions.search">
                    <input class="form-control"
                           type="text"
                           :placeholder="defaultOptions.searchPlaceholder"
                           @keyup.enter="searchByKey"
                           v-model="searchText">
                    <span class="reset" v-if="searchText != ''" @click="refresh" title="清空搜索"><span aria-hidden="true">×</span></span>
                    <div class="input-group-btn">
                        <button class="btn btn-primary btn-get-result" @click="searchByKey"><i class="fa fa-search"></i></button>
                    </div>

                    <ul class="search_preview list-group animated fadeIn" v-if="previewKey != '' && !defaultOptions.server">
                        <li
                                class="list-group-item"
                                v-for='data in orderByGridData'
                                @click="searchByKey($event, data[defaultOptions.filterKeys[0]])" >
                            {{data[defaultOptions.filterKeys[0]]}}
                        </li>
                    </ul>
                </div>

                <div class="pull-right" style="margin-right: 5px;" v-if="defaultOptions.filterCtrl">
                    <dropdown v-ref:filter-dropdown >
                        <button type="button" title="条件过滤" class="btn btn-sm btn-default" data-toggle="dropdown">
                            <i class="fa fa-filter"></i>
                        </button>
                        <ul slot="dropdown-menu" class="dropdown-menu edit-chk-cont">
                            <li class="header"> 条件过滤 </li>
                            <li class="form-horizontal">
                                <div class="form-group form-group-sm" v-for="item in defaultOptions.filterCtrl.keyMap">
                                    <label class="col-sm-3 control-label">{{ item.title }}</label>
                                    <div class="col-sm-9">
                                        <select
                                                :id="'filter_select_' + item.name"
                                                class="form-control"
                                                style="width:100%;"
                                                data-allow-clear="true"
                                                data-placeholder="请选择"
                                                v-model="filterParams[item.name]"
                                                v-select2="filterParams[item.name]"
                                        >
                                            <option value="">请选择</option>
                                            <option v-for="(key,option) in item.options" :value="key||option">{{option}}</option>
                                        </select>
                                    </div>
                                </div>
                            </li>
                            <li class="footer clearfix">
                                <button class="btn btn-sm btn-primary pull-right" @click.prevent="filterHandle">确定</button>
                                <button class="btn btn-sm btn-default pull-right" @click="$refs.filterDropdown.close">取消</button>
                            </li>
                        </ul>
                    </dropdown>
                </div>

            </div>
            <div class="filter-items clearfix pull-right" v-if="defaultOptions.filterCtrl&&filterParamsPlain.length>0">
                <span class="title">过滤条件:</span>
                <div class="filter-ctrls">
                    <span @click="emptyFilters" class="close text-danger"> <i class="fa fa-trash"></i> 清空条件</span>
                </div>
                <template v-for="item in filterParamsPlain">
                    <span class="label label-default pull-left" @click.prevent="resetFilter(item.name)">
                        {{item.title}}：{{item.option}} <i class="fa fa-times"></i>
                    </span>
                </template>
            </div>
        </div>
        <div class="box-body">
            <div class="grid-scroll-x">
                <div id="grid-header-scroll">
                    <data-table-head-column
                            v-on:resize-column="resizeColumnHandle"
                            :header-style="{ minWidth:tableWidth+'px' }"
                            :columns="tableColumns"
                            :check-all-items="checkAllItems"></data-table-head-column>
                </div>
                <div class="grid-scroll-y" :style="{ height: computedBoxHeight }">
                    <table :id="table_id" :class="defaultOptions.classes" data-table-body :style="{ minWidth:tableWidth+'px' }">
                        <tbody>

                        <template v-if="defaultOptions.clientPagination">
                            <tr v-for="(dataIdx,data) in viewData"
                                track-by="id"
                                @click="checkItem(data)"
                                :class="{ 'active':(checkItems.indexOf(data)!==-1), 'disabled':(defaultOptions.disabledItems.indexOf(data) !== -1) }">
                                <template v-for="(cidx,column) in tableColumns">
                                    <data-table-field
                                            v-if="column.visible!==false"
                                            :column="column"
                                            :data="data"
                                            :data-idx="dataIdx"
                                            :grid-data="viewData"
                                    ></data-table-field>
                                </template>
                            </tr>
                        </template>
                        <template v-else>
                            <tr v-for="(dataIdx,data) in orderByGridData"
                                @click="checkItem(data)"
                                :class="{ 'active':(checkItems.indexOf(data)!==-1), 'disabled':(defaultOptions.disabledItems.indexOf(data) !== -1) }">
                                <template v-for="(cidx,column) in tableColumns">
                                    <data-table-field
                                            v-if="column.visible!==false"
                                            :column="column"
                                            :data="data"
                                            :data-idx="dataIdx"
                                            :grid-data="gridData"
                                    ></data-table-field>
                                </template>
                            </tr>
                        </template>

                        <tr v-if="loadingData">
                            <td style="text-align: center;" :colspan="tableColumns.length">
                                <div class="td-inner">
                                    加载中...
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>

                    <div class="no-data-placeholder" v-if="gridData.length<=0">
                        <span>{{defaultOptions.noDataPlaceholder}}</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="box-footer" v-if="defaultOptions.pagination">
            <pagination :total="defaultOptions.clientPagination?viewData.length:total"
                        :page-size="defaultOptions.pageSize"
                        :page="defaultOptions.page"
                        v-on:change-page="changPageHandle"
                        v-ref:pagination
            ></pagination>
        </div>
    </div>
</template>
<style lang="less" src="../../less/dataTable.less" scoped></style>
<style lang="less">
    tr.disabled{
        td{
        .magic-checkbox+label{
            cursor: not-allowed;
            background-color: #f4f4f4;
                &:after{
                     display: block;
                     border-color: silver;
                }
            }
        }
    }
    .dataTable{
        .btn-group{
            position: relative;
        }
    }
</style>
<script lang="babel">
    import Vue from 'vue'
    import {spinner} from 'vue-strap';
    import pagination from './pagination.vue';
    import dataTableField from './dataTableField.vue'
    import dataTableHeadColumn from './dataTableHeadColumn.vue'
    import dropdown from '../widgets/dropdown.vue'
    import store from 'store'
    import _ from 'lodash'
    import IScroll from 'iscroll'

    var filterBy = Vue.filter('filterBy');

    export default{
        props: {
            // 组件ID
            "id": {
                type: String,
                required: true
            },
            // 栏目数据
            "columns": {
                type: Array,
                required: true,
            },
            // 其他配置
            "options": {
                type: Object,
                required: true
            },
            "boxHeight": {
                type: [String, Number],
                required: true
            }
        },
        data(){
            return {
                gridData: [],
                tableWidth: "",
                baseColumnWidth: 100,// 默认栏目宽度
                isLastPage: false,
                loadingData: false,
                total: 0,
                page: 0,
                yScroll: {},

                checkItems: [],// 选择的数值
                checkAllItems: false,

                filterText: '',
                filterParams: {},
                resetFilterKeyName: '',
                searchText: '',
                previewKey: '',

                // 过滤的条件
                filterParamsTmp: {},
                filterParamsPlain: [],

                storeColumns: {},
                hasSetCheckBox: false
            }
        },
        created(){
            var self = this, _filterParams = {};
            if (self.defaultOptions.filterCtrl.keyMap) {
                for (let item of self.defaultOptions.filterCtrl.keyMap) {
                    _filterParams[item.name] = "";
                }
                self.$set('filterParams', _filterParams);
            }
        },
        computed: {
            defaultOptions(){
                let self = this;
                let defaultOptions = {
                    pageSize: 50,
                    serverParmasName: {
                        pageSize: "size",
                        pageNum: "page",
                        sort: "sort"
                    },
                    autoLoad: true,
                    classes: "table table-bordered table-hover dataTable",
                    searchAuth: null, // 搜索验证
                    showRefresh: true, // 展示刷新
                    showCheckBox: true, // 展示checkbox
                    showHeader: true,
                    pagination: true, // 展示分页
                    clientPagination: false, // 客户端分页
                    selectable: true, // 是否可选
                    mutiSelect: true, // 是否多选
                    search: true, // 展示搜索
                    filterKeys: [], // 过滤字段
                    filterCtrl: false, // 过滤控件
                    searchPlaceholder: "搜索",
                    noDataPlaceholder: "没有数据",
                    undefinedText: '-',
                    sort: '', // 排序字段
                    page: 0, // 当前页码
                    filter: '', // 过滤关键词
                    sortOrder: 'desc',
                    disabledItems: []
                };
                return Object.assign({}, defaultOptions, self.options);
            },
            computedBoxHeight(){
                var self = this,
                        computedBoxHeight = self.boxHeight - 128;

                if (self.defaultOptions.pagination) {
                    computedBoxHeight = computedBoxHeight - 24;
                }

                if (self.filterParamsPlain.length > 0) {
                    computedBoxHeight = computedBoxHeight - $(self.$el.querySelector('.filter-items')).height() - 15;
                }

                _.debounce(()=> {
                    self.$nextTick(()=> {
                        self.yScroll.refresh();
                        self.xHeaderScroll.refresh();
                    });
                }, 20)();

                return computedBoxHeight + "px";
            },
            tableColumns(){
                var tableColumns = [], self = this, left = 0;

                if (self.defaultOptions.showCheckBox && !self.hasSetCheckBox) {
                    self.hasSetCheckBox = true;
                    self.columns = [{
                        style: {"width": "30px", "text-align": "center", "vertical-align": "middle"},
                        checkbox: true
                    }].concat(self.columns);
                }

                self.columns.forEach(function (c, index) {
                    if (c.visible !== false) {
                        var hasWidth = true;
                        if (!c.style) {
                            c.style = {width: self.baseColumnWidth + "px"};
                            hasWidth = false;
                        }
                        if (!c.style.width || (c.style.width && c.style.width == 'auto')) {
                            c.style.width = self.baseColumnWidth + "px";
                            hasWidth = false;
                        }

                        if (index === self.columns.length - 1) {
                            var w = parseInt(c.style.width.replace("px", "")) + 2;
                            c.style.width = w + 4 + "px";
                            // add offset 2 px
                            if (hasWidth) {
                                self.tableWidth = left + w;
                            } else {
                                self.tableWidth = left + self.baseColumnWidth;
                            }
                            c.style.width = "auto";
                        }
                        c.style.left = left + "px";
                        left = left + parseInt(c.style.width.replace("px", ""));
                    }
                    tableColumns.push(c);
                });
                return tableColumns;
            },
            orderByGridData(){
                var self = this;
                return _.orderBy(self.gridData, self.defaultOptions.sort, self.defaultOptions.sortOrder);
            },
            viewData(){
                var self = this, _viewData = [];
                if (self.defaultOptions.clientPagination) {
                    _viewData = filterBy(self.gridData, self.filterText, self.defaultOptions.filterKeys);

                    self.defaultOptions.filterKeys.forEach((key)=> {
                        self.gridData.filter(function (data) {
                            return data[key].indexOf(self.filterText) !== -1;
                        });
                    });

                    Object.keys(self.filterParamsTmp).forEach(function (k) {
                        _viewData = filterBy(_viewData, self.filterParamsTmp[k], [k]);
                    });
                }


                _viewData = _.orderBy(_viewData, self.defaultOptions.sort, self.defaultOptions.sortOrder);
                _viewData = _viewData.slice((self.defaultOptions.page * self.defaultOptions.pageSize), self.defaultOptions.pageSize);

                _.debounce(function () {
                    self.$nextTick(()=> {
                        self.resetScrollbar();
                    });
                }, 20)();

                return _viewData;
            },
            filterDisabledItems(){
                var self = this;
                return ((datas)=>datas.filter(data=>(self.defaultOptions.disabledItems.indexOf(data) == -1)))(self.defaultOptions.clientPagination ? self.viewData : self.orderByGridData)
            }
        },
        watch: {
            searchText(newVal) {
                this.previewKey = newVal;
            },
            storeColumns: {
                deep: true,
                handler: function (val) {
                    var self = this;
                    for (var k in val) {
                        var index = k.replace('index_', '');
                        self.columns[index].style.width = val[k] + "px";
                    }
                    // 将表格的宽度存储到local
                    store.set(self.id + '-column-size', val);
                    self.yScroll.refresh();
                    self.xHeaderScroll.refresh();
                }
            },
            checkItems(){
                var self = this, dataLen = self.filterDisabledItems.length;
                self.checkAllItems = (self.checkItems.length == dataLen && dataLen > 0);
            }
        },
        methods: {
            fillGridData(list, total, cbk) {
                var self = this;

                self.gridData = (list && list.length > 0) ? list : [];
                self.total = total || 0;
                self.loadingData = false;
                self.isLastPage = (list && list.length < self.defaultOptions.pageSize);
                self.$refs.spinner.active && self.$refs.spinner.hide();
                self.checkItems = [];
                cbk && cbk();
            },
            getGridData (cbk) {
                var self = this;
                self.$refs.spinner.show();
                self.loadingData = true;

                const success = (list, total)=>self.fillGridData(list, total, cbk);

                if (self.getGridDataTm) clearTimeout(self.getGridDataTm);

                self.getGridDataTm = setTimeout(()=> {

                    if (typeof self.defaultOptions.server === 'string') {
                        $.ajax({
                            url: self.defaultOptions.server,
                            data: {
                                [ self.defaultOptions.serverParmasName.pageNum ]: self.defaultOptions.page,
                                [ self.defaultOptions.serverParmasName.pageSize ]: self.defaultOptions.pageSize,
                                [ self.defaultOptions.serverParmasName.sort ]: self.defaultOptions.sort
                            },
                            success
                        });
                    } else if (typeof self.defaultOptions.server === 'function') {
                        (self.defaultOptions.server.bind(self))({
                            page: self.defaultOptions.page,
                            size: self.defaultOptions.pageSize,
                            order: self.defaultOptions.sort + "," + self.defaultOptions.sortOrder,
                            filter: self.filterParamsTmp,
                            success
                        });
                    }

                }, 100);
            },
            nextPage() {
                var self = this;
                if (!self.isLastPage && self.$refs.pagination.totalPage > self.defaultOptions.page) {
                    self.isLastPage = false;
                    self.defaultOptions.page++;
                    self.getGridData(function () {
                        self.$nextTick(function () {
                            self.yScroll.refresh();
                        });
                    });
                } else {
                    self.isLastPage = true;
                }
            },
            thClickHandle(e, column) {
                e.preventDefault();
                var self = this;
                if (column.sortable) {
                    self.defaultOptions.sortName = column.field;
                    for (let el of self.$el.querySelectorAll('.sorting')) {
                        el.classList.remove("sorting_asc");
                        el.classList.remove("sorting_desc");
                    }

                    let $target = self.$el.querySelector('.' + column.field);

                    if (self.defaultOptions.sortOrder == "asc") {
                        self.defaultOptions.sortOrder = "desc";
                        $target.className = column.field + " grid-header-column sorting sorting_desc";
                    } else {
                        self.defaultOptions.sortOrder = "asc";
                        $target.className = column.field + " grid-header-column sorting sorting_asc";
                    }

                    self.defaultOptions.sort = column.field;

                    // 后端排序
                    if (!self.defaultOptions.clientPagination) {
                        self.$nextTick(()=> {
                            self.refresh();
                        });
                    }
                }
                if (column.checkbox) {
                    self.checkAllItemHandle();
                }
            },
            callActions(action, data) {
                var self = this;
                self.$emit('call-action', action, data);
            },
            resetKeys() {
                var self = this;
                self.searchText = "";
                self.filterText = "";
                self.previewKey = "";
            },
            resetScrollbar() {
                var self = this;
                self.yScroll.refresh();
                self.xHeaderScroll.refresh();
                self.yScroll.scrollTo(0, 0);
                self.xHeaderScroll.scrollTo(0, 0);
            },
            searchByKey(e, k) {
                var self = this;
                var handle = function () {

                    if (k) self.searchText = k;

                    self.filterText = self.searchText;
                    if (self.defaultOptions.server && !self.defaultOptions.clientPagination) {
                        // 从server端拉取数据
                        self.gridData = [];
                        self.getGridData(function () {
                            self.$nextTick(()=> {
                                self.resetScrollbar();
                            });
                        });
                    }

                    setTimeout(function () {
                        self.previewKey = "";
                    }, 10);

                };

                if (!!self.defaultOptions.searchAuth) {
                    self.defaultOptions.searchAuth(handle);
                } else {
                    handle();
                }
            },
            refresh() {
                var self = this;
                self.resetKeys();
                self.defaultOptions.page = 0;
                self.getGridData(()=> {
                    self.$nextTick(()=> {
                        self.resetScrollbar();
                        // 刷新成功

                    });
                })
            },
            refreshCurrentPage() {
                var self = this;
                self.resetKeys();

                self.getGridData(()=> {
                    self.$nextTick(()=> {
                        self.resetScrollbar();
                    });
                })
            },
            checkItem: _.debounce(function (data) {
                var self = this;
                if (!self.defaultOptions.selectable) {
                    return;
                }

                if (self.defaultOptions.disabledItems.indexOf(data) !== -1) {
                    return;
                }

                if (self.defaultOptions.mutiSelect) {
                    var _index = self.checkItems.indexOf(data);
                    if (_index == -1) {
                        self.checkItems.push(data);
                    } else {
                        self.checkItems.splice(_index, 1);
                    }
                } else {
                    self.checkItems = [data];
                }

                self.$emit('check-item', data);
            }, 10),
            checkAllItemHandle(){
                var self = this;
                self.checkAllItems = !self.checkAllItems;

                if (self.checkAllItems) {
                    self.checkItems = [].concat(self.filterDisabledItems);
                } else {
                    self.checkItems = [];
                }
            },
            filterHandle() {
                var self = this;

                // 确定条件
                self.filterParamsTmp = Object.assign({}, self.filterParams);
                self.filterParamsPlain = [];
                for (var key in self.filterParamsTmp) {
                    self.defaultOptions.filterCtrl.keyMap.forEach(({name, title, options})=> {
                        if (self.filterParamsTmp[key] != '' && name == key) {
                            self.filterParamsPlain.push({
                                title: title,
                                name: name,
                                option: (options[self.filterParamsTmp[key]] || self.filterParamsTmp[key])
                            });
                        }
                    });
                }
                self.$refs.filterDropdown.close();

                // 服务端赛选
                if (!self.defaultOptions.clientPagination) {
                    self.refresh();
                }

            },
            resetFilter(key) {
                var self = this;
                if (key) {
                    self.filterParams[key] = "";
                    self.filterHandle();
                }
            },
            emptyFilters(){
                var self = this;
                for( var k in self.filterParams){
                    self.filterParams[k] = '';
                }
                self.filterParamsPlain = [];
                self.filterHandle();
            },
            setGridData(keyField, keyValue, targetField, targetValue) {
                var self = this;
                for (var i = 0, len = self.gridData.length; i < len; i++) {
                    if (self.gridData[i][keyField] && self.gridData[i][keyField] == keyValue) {
                        self.gridData[i][targetField] = targetValue;
                    }
                }
            },
            changPageHandle(p) {
                var self = this;
                if (self.defaultOptions.page != p) {

                    if (self.defaultOptions.clientPagination) {
                        self.$refs.spinner.show();
                        self.$nextTick(()=> {
                            self.defaultOptions.page = p;
                            self.$nextTick(()=> {
                                self.$refs.spinner.hide();
                                self.resetScrollbar();
                            });
                        });
                    } else {
                        self.defaultOptions.page = p;
                        // 如果不是客户端数据，重新拉数据
                        self.getGridData(()=> {
                            self.$nextTick(()=> {
                                self.resetScrollbar();
                            });
                        });
                    }
                }
            },
            resizeColumnHandle(index, offset, isMouseUp) {
                var self = this;
                if (!self.storeColumns['index_' + index]) {
                    self.$set('storeColumns.index_' + index, parseInt(self.columns[index].style.width.replace('px', '')));
                }
                if (self.storeColumns['index_' + index] + offset < 50) {
                    self.$set('storeColumns.index_' + index, 50);
                    return;
                }
                if (isMouseUp) {
                    self.$set('storeColumns.index_' + index, self.storeColumns['index_' + index] + offset);
                    return;
                }
                self.columns[index].style.width = ( self.storeColumns['index_' + index] + offset ) + "px";
            },
            checkFirstItem() {
                var self = this;

                if (self.gridData.length <= 0) {
                    return;
                }

                var list;
                if (self.defaultOptions.clientPagination) {
                    list = self.viewData;
                } else {
                    list = self.orderByGridData;
                }
                self.checkItem(list[0]);
            }
        },
        ready(){
            var self = this,
                    $gridHeaderScroll = self.$el.querySelector('#grid-header-scroll'),
                    $gridScrollY = self.$el.querySelector('.grid-scroll-y');

            self.yScroll = new IScroll($gridScrollY, {
                interactiveScrollbars: true,
                click: true,
                scrollX: true,
                scrollY: true,
                mouseWheel: true,
                scrollbars: true,
                probeType: 3
            });
            self.xHeaderScroll = new IScroll($gridHeaderScroll, {
                scrollX: true,
                disableMouse: true,
                disablePointer: true,
                disableTouch: true,
                mouseWheel: false
            });

            self.yScroll.on('scroll', function () {
                self.xHeaderScroll.scrollTo(this.x, 0);
            });

            self.$on('toggle-system-sidebar', _.debounce(()=> {
                self.yScroll.refresh();
                self.xHeaderScroll.refresh();
            }, 1000));

            // 拉取数据
            if (self.defaultOptions.autoLoad) {
                self.getGridData(function () {

                    self.$nextTick(function () {
                        self.yScroll.refresh();
                        self.xHeaderScroll.refresh();

                        self.$emit('autoload-complete');
                    });

                });
            }

            // 从local读取宽度并还原视图
            var storeColumns = store.get(self.id + '-column-size');
            if (storeColumns) {
                self.$set('storeColumns', storeColumns)
            }

        },
        components: {
            spinner,
            dropdown,
            pagination,
            dataTableHeadColumn,
            dataTableField
        }
    };
</script>