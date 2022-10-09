<template>

    <div class="clearfix">
        <div class="pull-left pagination-detail">
            <span class="pagination-info">共 {{total}} 条记录</span>
        </div>

        <div class="input-group input-group-sm pull-right" style="width:90px; margin-left: 5px"  v-if="totalPage>1">
            <input type="text" class="form-control text-center" v-el:page>
            <span class="input-group-btn">
              <button type="button" class="btn btn-default btn-flat" @click="goToPage">跳转</button>
            </span>
        </div>

        <ul class="pagination pull-right no-margin" v-if="totalPage>1">
            <li :disabled="__is_first_page" :class="{'disabled':__is_first_page}"><a href="javascript:;" @click="prevPage">上一页</a></li>

            <template v-if="totalPage>6">

                <li v-for="pn in numbers_start" :class="{ 'active': (page == pn) }">
                    <a href="javascript:;" @click="setPage(pn)">{{pn+1}}</a>
                </li>

                <template v-if="numbers_mid.length > 0">
                    <li><a href="javascript:;">...</a></li>
                    <li v-for="pn in numbers_mid" :class="{ 'active': (page == pn) }">
                        <a href="javascript:;" @click="setPage(pn)">{{pn+1}}</a>
                    </li>
                </template>

                <template v-if="numbers_end.length > 0">
                    <li><a href="javascript:;">...</a></li>
                    <li v-for="pn in numbers_end" :class="{ 'active': (page == pn) }">
                        <a href="javascript:;" @click="setPage(pn)">{{pn+1}}</a>
                    </li>
                </template>

            </template>
            <template v-else>
                <li v-for="pn in totalPage" :class="{ 'active': (page == pn) }">
                    <a href="javascript:;" @click="setPage(pn)">{{pn+1}}</a>
                </li>
            </template>

            <li :disabled="__is_last_page" :class="{'disabled':__is_last_page}"><a href="javascript:;" @click="nextPage">下一页</a></li>
        </ul>
    </div>

</template>

<script lang="babel">

    export default{
        props:{
            "total":{
                type:Number,
                default:0
            },
            "pageSize":{
                type:Number,
                default: 20
            },
            "page":{
                type:Number,
                default:1
            }
        },
        computed:{
            "totalPage":function () {
                var self = this;
                return Math.ceil(self.total/self.pageSize)
            },
            "__is_first_page":function () {
                return this.page === 0;
            },
            "__is_last_page":function () {
                return this.page === this.totalPage-1;
            },
            "numbers_start":function () {
                var self = this, numbers_start;
                if( self.totalPage < self.number ){
                    numbers_start = self.totalPage;
                }else if(self.page+1 < self.number){
                    numbers_start = self.number;
                }else{
                    numbers_start = 3;
                }
                return numbers_start;
            },
            "numbers_mid":function () {
                var self = this, numbers_mid = [];
                if( self.totalPage > self.number && self.page > self.number-2 && self.page -1 < self.totalPage - self.number ){
                    for( var i = (self.page-1) ; i<self.page+2; i++  ){
                        numbers_mid.push(i);
                    }
                }
                return numbers_mid;
            },
            "numbers_end":function () {
                var self = this, numbers_end = [];
                if(self.totalPage > self.number){
                    if( self.page > self.totalPage - self.number ){
                        for( var i = (self.totalPage-self.number) ; i<self.totalPage; i++  ){
                            numbers_end.push(i);
                        }
                    }else{
                        numbers_end.push(self.totalPage-1);
                    }
                }
                return numbers_end;
            }
        },
        data(){
            return{
                number:5
            }
        },
        methods:{
            setPage(p){
                var self = this;
                self.page = p;
                self.$emit('change-page', p)
            },
            prevPage(){
                var self = this;
                if(self.page > 1){
                    self.page --;
                    self.setPage(self.page);
                }
            },
            nextPage(){
                var self = this;
                if(self.page < self.totalPage){
                    self.page ++;
                    self.setPage(self.page);
                }
            },
            goToPage(){
                var self = this, p = self.$els.page.value;
                if( /^[-+]?[0-9]+$/.test(p) && p != ''){
                    p = parseInt(p)-1;
                    if(p >= 0 && p < self.totalPage ){
                        self.setPage(p);
                    }else{
                        self.$els.page.value = self.page+1;
                    }
                }else{
                    self.$els.page.value = self.page+1;
                }
            }
        }
    }
</script>