<template>

    <li class="treeview" v-if="(child.children && child.children.length>0)">
        <a href="javascript:;">
            <i :class="child.icon|| 'fa fa-folder'"></i>
            <span>{{child.name}}</span>
            <!--<i class="fa fa-angle-left pull-right"></i>-->
        </a>
        <ul class="treeview-menu menu-open">
            <sidebar-tree v-for="child in child.children" :child="child"></sidebar-tree>
        </ul>
    </li>
    <li v-else>
        <a v-if="child.disabled" disabled>
            <i :class="child.icon|| 'fa fa-circle-o'"></i>
            <span>{{child.name}}</span>
        </a>
        <a v-if="!child.disabled" v-link="child.link">
            <i :class="child.icon|| 'fa fa-circle-o'"></i>
            <span>{{child.name}}</span>
        </a>
    </li>

</template>
<style lang="less" scoped>
    a[disabled]{
        cursor: not-allowed;
    }
</style>
<script>
    export default{
        name:"sidebar-tree",
        props:{
            child:Object
        }
    }
</script>