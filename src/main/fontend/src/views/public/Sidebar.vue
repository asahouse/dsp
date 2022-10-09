<template>
    <aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
            <!-- sidebar menu -->
            <ul class="sidebar-menu">
                <template v-for="sbd in sidebarData">
                    <li class="header">{{sbd.name}}</li>
                    <sidebar-tree v-for="child in sbd.children" :child="child"></sidebar-tree>
                </template>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>
</template>
<style lang="less">
    .sidebar-menu{
        margin-bottom: 20px;
    }
    .sidebar-menu .treeview-menu.menu-open{
        display: block;
    }
    .skin-blue .sidebar-menu > li:hover > a{
        border-left-color: transparent;
    }
    .skin-blue .sidebar-menu > li > .treeview-menu{ background: transparent; }
</style>
<script lang="babel">
    import { screenSizes, sidebarExpandOnHover } from '../../config/index';
    import { getSysSidebarData, toggleSysSidebar } from '../../vuex/actions/system'
    import storeUtil from 'store'

    import sidebarTree from './SidebarTree.vue'

    export default{
        data(){
            return{
                screenSizes,
                sidebarExpandOnHover
            }
        },
        vuex:{
            getters:{
                sidebarData:({ system }) => system.sidebar,
                showSidebar:({ system }) => system.showSidebar,
                isAdminView({ system }){
                    return system.isAdminView;
                }
            },
            actions:{
                getSysSidebarData,
                toggleSysSidebar
            }
        },
        watch:{
            isAdminView:{
                deep:true,
                handler() {
                    let self = this;
                    self.getSysSidebarData();
                    self.$router.go(self.sidebarData[0]['children'][0]['link']);
                }
            },
            showSidebar() {
                let self = this;
                self.expandSidebar();
            }
        },
        ready(){
            let animationSpeed = 500,
                menu = ".sidebar",
                self = this;

            // 获取侧边栏数据
            self.getSysSidebarData();

            self.$nextTick(()=>{
                self.expandSidebar();
            });

            /*$(document).delegate( menu + ' li a', 'click',function (e) {
                e.preventDefault();
                e.stopPropagation();

                let $this = $(this);
                let checkElement = $this.next();

                if ((checkElement.is('.treeview-menu')) && (checkElement.is(':visible')) && (!$('body').hasClass('sidebar-collapse'))) {
                    checkElement.slideUp(animationSpeed, function () {
                        checkElement.removeClass('menu-open');
                        self.$parent.fix();
                    });
                    checkElement.parent("li").removeClass("active");
                }
                else if ((checkElement.is('.treeview-menu')) && (!checkElement.is(':visible'))) {
                    let parent = $this.parents('ul').first();
                    let ul = parent.find('ul:visible').slideUp(animationSpeed);
                    ul.removeClass('menu-open');
                    let parent_li = $this.parent("li");
                    checkElement.slideDown(animationSpeed, function () {
                        checkElement.addClass('menu-open');
                        parent.find('li.active').removeClass('active');
                        parent_li.addClass('active');
                        //Fix the layout in case the sidebar stretches over the height of the window
                        self.$parent.fix();
                    });
                }
                if (checkElement.is('.treeview-menu')) {
                    e.preventDefault();
                }
            });*/
            self.sidebarExpandOnHover&&self.expandOnHover();

            $(window).resize(()=>{
                self.scrollSidebar();
            });

        },
        methods:{
            scrollSidebar(){
                let self = this, sidebar = $(self.$el).find('.sidebar');
                sidebar.slimScroll({destroy: true}).height("auto");
                sidebar.slimscroll({
                    height: ($(window).height() - $(".main-header").height()) + "px",
                    color: "rgba(0,0,0,0.2)",
                    size: "3px"
                });
            },
            toggleSidebar:function (event) {
                event.preventDefault();
                let self = this;
                self.toggleSysSidebar( !self.showSidebar );
            },
            expandSidebar:function () {
                let $body = $("body");
                let self = this;
                if( !self.showSidebar ){
                    if ($(window).width() > (self.screenSizes.sm - 1)) {
                        $body.addClass('sidebar-collapse');
                    }else {
                        $body.addClass('sidebar-open');
                    }
                }else{
                    if ($(window).width() > (self.screenSizes.sm - 1)) {
                        $body.removeClass('sidebar-collapse');
                    }else {
                        $body.removeClass('sidebar-open').removeClass('sidebar-collapse');
                    }
                }
                self.$parent.$broadcast('toggle-system-sidebar');
                setTimeout(()=>{
                    $(window).trigger('resize')
                }, 500);
            },
            expandOnHover: function () {
                let self = this,
                    screenWidth = self.screenSizes.sm - 1;

                //Expand sidebar on hover
                /*$('.main-sidebar').hover(function () {
                    if ($('body').hasClass('sidebar-mini')
                            && $("body").hasClass('sidebar-collapse')
                            && $(window).width() > screenWidth) {
                        self.expand();
                    }
                }, function () {
                    if ($('body').hasClass('sidebar-mini')
                            && $('body').hasClass('sidebar-expanded-on-hover')
                            && $(window).width() > screenWidth) {
                        self.collapse();
                    }
                });*/
            },
            expand: function () {
                $("body").removeClass('sidebar-collapse').addClass('sidebar-expanded-on-hover');
            },
            collapse: function () {
                if ($('body').hasClass('sidebar-expanded-on-hover')) {
                    $('body').removeClass('sidebar-expanded-on-hover').addClass('sidebar-collapse');
                }
            }
        },
        components:{
            sidebarTree
        }
    }
</script>