
<template>
    <template v-if="treeData.children && treeData.children.length>0">
        <div class="treeSelectBox box box-info">
            <div class="box-header with-border">
                <h4 class="box-title"><span>{{treeData.name}}</span></h4>
                <div class="box-tools pull-right">
                    <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                </div>
            </div>
            <div class="box-body" v-show="false">
                <tree-select-box
                        v-for="item in treeData.children"
                        :tree-data="item"></tree-select-box>
            </div>
        </div>
    </template>
    <template v-else>
        <button class="btn btn-sm btn-default">{{treeData.name}}</button>
    </template>
</template>
<style lang="less" src="../../less/treeSelectBox.less"></style>
<script>
    export default{
        name:"tree-select-box",
        props:["treeData"]
    }
</script>