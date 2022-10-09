<template>

    <div class="box" v-if="!isRemove">
        <div class="box-header with-border">
            <h3 class="box-title">{{title}} <small>{{subTitle}}</small></h3>s
            <div class="box-tools pull-right">
                <button type="button" class="btn btn-box-tool" @click="collapse">
                    <i class="fa fa-minus"></i></button>
                <button type="button" class="btn btn-box-tool" @click="remove">
                    <i class="fa fa-times"></i></button>
            </div>
        </div>
        <div class="box-body animated fadeIn">
            <slot>no content.</slot>
        </div>
        <div class="box-footer" v-if=" footerCont && footerCont!='' ">
            {{footerCont}}
        </div>
    </div>

</template>

<script>
    export default{
        data:function () {
            return {
                isRemove:false
            }
        },
        props:['title', 'subTitle', 'footerCont'],
        methods:{
            'collapse':function () {
                this.$el.classList.toggle('collapsed-box');
            },
            'remove':function () {
                this.isRemove = true;
            }
        }
    }
</script>