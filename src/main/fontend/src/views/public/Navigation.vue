<template>
    <header class="main-header">
        <!-- Logo -->
        <a href="index.html" class="logo">
            <span class="logo-mini">AC</span>
            <span class="logo-lg">AdCore 智能投放平台</span>
        </a>
        <nav class="navbar navbar-static-top">
            <a href="javascript:void(0);"
               class="sidebar-toggle"
               @click.prevent="$parent.$refs.sidebar.toggleSidebar($event)">
                <span class="sr-only">Toggle</span>
            </a>
            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <li-dropdown :liclass="['dropdown','user','user-menu']">
                        <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown">
                            <span class="hidden-xs"> {{user.userName}} </span>
                        </a>
                        <ul class="dropdown-menu" style="width:160px;">
                            <li class="user-header">
                                    <avatar :username="user.userName"
                                            background-color="#ffffff"
                                            :size="90"
                                            color="#000"
                                    ></avatar>
                                    <p> {{user.userName}} </p>
                            </li>
                            <!--<li class="user-body">
                                <div class="row">
                                    <div class="col-sm-4"><a href="#"> 个人信息</a></div>
                                    <div class="col-sm-4"><a href="#"> 修改密码</a></div>
                                    <div class="col-sm-4"><a href="#"> 邮箱设置</a></div>
                                </div>
                            </li>-->
                            <!-- Menu Footer-->
                            <li class="user-footer">
                                <button @click="logout" class="btn btn-danger btn-flat btn-block">退出登录</button>
                            </li>
                        </ul>
                    </li-dropdown>
                    <!-- Control Sidebar Toggle Button -->
                    <li @click="switchAdminView" v-if="user.isAdmin">
                        <a href="javascript:;" v-if="!isAdminView">
                            <i class="fa fa-gears"></i> <span>后台管理</span>
                        </a>
                        <a href="javascript:;" v-else>
                            <i class="fa fa-home"></i> <span>前台界面</span>
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    </header>

</template>
<style>
    .avatar{
        margin: auto;}
</style>
<script lang="babel" type="text/ecmascript-6">
    import liDropdown from './LiDropdown.vue';
    import { navbarMenuHeight, navbarMenuSlimscrollWidth } from '../../config/index';
    import { switchAdminView } from "../../vuex/actions/system";
    import resource from "../../api/resource"
    import Avatar from 'vue-avatar'
    import authService from '../../utils/authService'

    export default{
        data(){
            return{
                navbarMenuHeight,
                navbarMenuSlimscrollWidth
            }
        },
        vuex:{
            actions:{
                switchAdminView,
            },
            getters:{
                isAdminView:({system})=>system.isAdminView
            }
        },
        methods:{
            logout(){
                var _this = this;
                resource.login().delete().done(function () {
                    authService.logout();
                    _this.$router.go('/login');
                });
            }
        },
        components:{
            liDropdown,
            'avatar': Avatar.Avatar
        }
    }
</script>