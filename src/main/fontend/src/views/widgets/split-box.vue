<style lang="less" src="../../less/split-box.less" scoped></style>

<template>

    <div class="split-box" :style="{ 'height': boxHeight + 'px' }">

        <div class="split-box-up" :style="{ 'height':splitUpContHeight + 'px'}">
            <slot name="up-cont"></slot>
        </div>
        <div class="spliter"
             @mousedown="mousedown"
             @mousemove="mousemove"
             @mouseout="mouseup"
             @mouseup="mouseup"
             :style="showDownCont?'cursor:e-resize;cursor:row-resize;':''">
            <i class="fa fa-caret-down" @click="showDownCont = false" v-if="showDownCont"></i>
            <i class="fa fa-caret-up" @click="showDownCont = true" v-if="!showDownCont"></i>
            <span @click="showDownCont = !showDownCont">{{spliterTitle}}</span>
            <div class="pull-right">
                <slot name="split-cont"></slot>
            </div>
        </div>
        <div class="split-box-down" v-show="showDownCont" :style="{ 'height':splitDownContHeight + 'px'}">
            <slot name="down-cont"></slot>
        </div>

    </div>

</template>

<script lang="babel">

    export default{
        props:{
            "boxHeight":{
                type:Number,
                required:true
            },
            "spliterTitle":{
                type:String,
                default:""
            },
            "showDownCont":{
                type: Boolean,
                default: false
            }
        },
        data(){
            return{
                spliterHeight: 38,
                hasMouseDown:false,
                startOffset:0,
                moveOffsetTemp:0,
                moveOffset:0
            }
        },
        watch:{
            showDownCont:function (val) {
                this.$emit('show-cont', val);
            }
        },
        computed:{
            "splitUpContHeight":function () {
                var self = this;
                return (self.showDownCont?(self.boxHeight-self.spliterHeight)/2 + self.moveOffset:self.boxHeight - self.spliterHeight);
            },
            "splitDownContHeight":function () {
                var self = this;
                return self.boxHeight - self.splitUpContHeight - self.spliterHeight;
            }
        },
        methods:{
            mousedown(e){
                e.preventDefault();
                var self = this;
                if( !self.showDownCont ){
                    return;
                }
                self.hasMouseDown = true;
                self.startOffset = e.pageY;
            },
            mousemove(e){
                e.preventDefault();
                var self = this;
                if( !self.showDownCont || !self.hasMouseDown ){
                    return;
                }
                self.moveOffset = e.pageY - self.startOffset + self.moveOffsetTemp;
            },
            mouseup(e){
                e.preventDefault();
                var self = this;
                if( !self.showDownCont || !self.hasMouseDown ){
                    return;
                }
                self.moveOffsetTemp = self.moveOffset;
                self.hasMouseDown = false;
            }

        }
    }
</script>