<template>
    <div class="animated fadeIn">
        <Loading></Loading>
        <main-navigation v-ref:main-nav></main-navigation>
        <main-sidebar v-ref:sidebar></main-sidebar>
        <div class="content-wrapper" :style="{ 'minHeight':contWrapperHeight+'px' }">
            <router-view></router-view>
        </div>
    </div>
</template>

<script lang="babel">

    import mainNavigation from '../Public/Navigation.vue';
    import Loading from '../Public/loading.vue';
    import mainSidebar from '../Public/Sidebar.vue';
    import { alert, modal } from 'vue-strap'
    import { fixedSidebar } from '../../config/index';
    import { setContentHeight } from "../../vuex/actions/system";
    import _ from 'lodash'
    import { getAll, getTag } from '../../vuex/actions/category'

    export default{
        data() {
            return {
                fixedSidebar,
                hasInitScroll:false,
                toastOption:{
                    maxToasts: 6,
                    position: 'top right',
                    theme: 'info',
                    timeLife: 3000,
                    closeBtn: true,
                }
            }
        },
        vuex:{
            getters:{
                contWrapperHeight:({ system })=>system.contentHeight
            },
            actions:{
                setContentHeight,
                getAllCategory: getAll,
                getCategoryTag: getTag
            }
        },
        ready(){
            var self = this;
            self.watchFix();
            document.body.className = "skin-blue-light sidebar-mini fixed";

            self.getAllCategory();
            self.getCategoryTag();
        },
        methods:{
            watchFix: function () {
                var _this = this;

                $(window, ".wrapper").resize(_.debounce(function () {
                    _this.fix();
                }, 500));
                _this.fixedSidebar&& document.querySelector('body').classList.add('fixed');
                _this.fix();
            },
            fix: function () {
                var self = this;
                var neg = $('.main-header').outerHeight() + $('.main-footer').outerHeight();
                var window_height = $(window).height();
                var sidebar_height = $(".sidebar").height();
                if ($("body").hasClass("fixed")) {
                    self.setContentHeight(window_height);
                } else {
                    var postSetWidth;
                    if (window_height >= sidebar_height) {
                        self.setContentHeight(window_height - neg);
                        postSetWidth = window_height - neg;
                    } else {
                        self.setContentHeight(sidebar_height);
                        postSetWidth = sidebar_height;
                    }

                    var controlSidebar = $(".control-sidebar");
                    if (typeof controlSidebar !== "undefined") {
                        if (controlSidebar.height() > postSetWidth)
                            self.setContentHeight(controlSidebar.height());
                    }

                }
            },
        },
        components:{
            mainNavigation,
            mainSidebar,
            Loading
        }
    }
</script>