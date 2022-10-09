<style lang="less" scoped>
    .grid-header{
        position: relative;
        min-height: 35px;
    }
    .grid-header-column{
        top: 0;
        border-right: 1px solid #f4f4f4;
        position: absolute;
        .th-inner{
            white-space: nowrap;
            -ms-text-overflow: ellipsis; text-overflow: ellipsis;
            overflow: hidden;
            padding: 9px 5px;
            line-height: 1.42857143;
            min-height: 35px;
            vertical-align: top;
            width: 99%;
            cursor: default;
        }
        &:last-child{
             border-right: 0;
         }
        .fa{
            font-size: 15px;
            color: #999;
        }
        &.sorting{
            &>.th-inner{
                  cursor: pointer;
              }
         }
        &.sorting:after,
        &.sorting_asc:after,
        &.sorting_desc:after{
             position: absolute;
             top: 8px;
             right: 8px;
             display: block;
             font-family: 'Glyphicons Halflings';
             opacity: 0.5;
             cursor: pointer;
         }
        &.sorting:after{
             opacity: 0.2;
             content: "\e150";
         }
        &.sorting_desc:after{
             content: "\e156";
             opacity: 0.5;
         }
        &.sorting_asc:after{
             content: "\e155";
             opacity: 0.5;
         }
        &:last-child{
             min-width:100px;
         }
    }
    .column-spliter{
        position: absolute;
        width: 7px;
        right: -3px;
        top: 0;
        bottom: 0;
        z-index: 2;
        cursor: col-resize;
    }
    .magic-checkbox+label, .magic-radio+label{
        padding-left: 14px;
        height: 14px;
        display: inline-block;
        top: 2px;
    }
    .magic-checkbox+label:before, .magic-radio+label:before{
        width: 14px;
        height: 14px;
    }
    .magic-checkbox+label:after{
        top: 0;
        left: 5px;
        width: 5px;
        height: 10px;
        border-width: 1px;
    }
</style>
<template>
    <div class="grid-header" @mousemove="mousemove" @mouseup="mouseup" :style="headerStyle">
        <template v-for="(cidx,column) in columns">
            <div
                    class="grid-header-column"
                    v-if="column.visible !== false"
                    @click="thClickHandle($event,column)"
                    :class="[column.field, {'sorting':column.sortable }]"
                    :style="style(column.style, (cidx==columns.length-1))"
                    :data-field="column.field">
                <div class="th-inner">
                    <template v-if="column.checkbox">
                        <!--<span v-if="checkAllItems" class="fa fa-check-square" ></span>-->
                        <!--<span v-else class="fa fa-square-o"></span>-->
                        <input type="checkbox" class="magic-checkbox" v-model="checkAllItems">
                        <label></label>
                    </template>
                    <template v-else>
                        {{column.title}}
                    </template>
                </div>
                <div class="column-spliter"
                     @mousedown="mousedown($event, cidx)"
                ></div>
            </div>
        </template>
    </div>
</template>

<script lang="babel">
    import _ from 'lodash'

    export default{
        props:['columns', 'checkAllItems', 'headerStyle'],
        data(){
            return{
                hasMouseDown:false,
                startOffset:0,
                moveOffsetTemp:0,
                moveOffset:0,
                index:-1
            }
        },
        methods:{
            mousedown(e, index){
                e.preventDefault();
                var self = this;
                self.hasMouseDown = true;
                self.moveOffset = 0;
                self.startOffset = e.pageX;
                self.index = index;
            },
            mousemove(e){
                e.preventDefault();
                var self = this;
                if( !self.hasMouseDown ){
                    return;
                }
                self.moveOffset = e.pageX - self.startOffset;
                self.$emit('resize-column', self.index, self.moveOffset);
            },
            mouseup(e){
                e.preventDefault();
                e.stopPropagation();
                var self = this;
                if( !self.hasMouseDown ){
                    return;
                }
                self.hasMouseDown = false;
                self.$emit('resize-column', self.index, self.moveOffset, true);
            },
            "style": function (style, isLast) {
                var self = this;
                if(isLast){
                    style.minWidth = style.width||self.baseColumnWidth;
                    style.width = 'auto'
                    style.right = 0;
                }
                return style;
            },
            "thClickHandle": _.debounce(function (e, c) {
                this.$parent.thClickHandle(e, c);
            }, 50)
        }
    }
</script>