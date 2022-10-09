<template>
    <div class="main-loading animated" v-if="isShowLoading" transition="fade">
        <div class="main-loading-txt">
            <img src="../../images/loading-cylon-red.svg">
            <span>加载中，请稍后...</span>
        </div>
    </div>
</template>
<style lang="less">
    .main-loading{
        position: fixed;
        left: 0;
        top: 0;
        right: 0;
        bottom: 0;
        z-index: 99999999;
        background-color: rgba(255,255,255, .8);
    }
    .main-loading-txt{
        text-align: center;
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        color: #3C8DBC;
        font-size: 14px;
        img{
            display: block;
            width: 56px;
            height: 56px;
            margin: auto;
        }
    }
</style>
<script lang="babel">

    export default{
        vuex:{
            getters:{
                isShowLoading: ({system})=> system.isShowLoading
            }
        }
    }
</script>
