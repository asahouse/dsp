import { setAdminView, toggleMainLoading } from '../vuex/actions/system';
import storeUtil from '../utils/storeUtil'
import NProgress from "nprogress"

export default function (router) {
    router.map({
        '/advertisers':{
            name:'advertisers',
            // 是否验证登录
            auth:true,
            component: resolve=>require(['../views/layouts/mainContainer.vue'], resolve),
            subRoutes:{
                "/index": {
                    component: resolve=>require(['../views/advertisers/index.vue'], resolve)
                },
                "/creative/:campaignId":{
                    component: resolve=>require(['../views/advertisers/creative/index.vue'], resolve)
                },
                "/campaigns":{
                    component: resolve=>require(['../views/advertisers/campaign/index.vue'], resolve)
                },
                "/campaigns/add":{
                    component: resolve=>require(['../views/advertisers/campaign/add.vue'], resolve)
                },
                "/campaigns/:id":{
                    component: resolve=>require(['../views/advertisers/campaign/edit.vue'], resolve)
                },
                "/campaigns/:campaignId/creative":{
                    component: resolve=>require(['../views/advertisers/creative/index.vue'], resolve)
                }
            }
        },
        '/admin':{
            name:'admin',
            // 是否验证登录
            auth:true,
            component: resolve=>require(['../views/layouts/mainContainer.vue'], resolve),
            subRoutes:{
                // 控制台
                '/dashboard':{
                    component: resolve=> require(['../views/admin/index.vue'], resolve)
                },
                // 广告主
                '/advertisers':{
                    component: resolve=> require(['../views/admin/advertisers/index.vue'], resolve)
                },

                // 广告主
                '/advertisers/add':{
                    component: resolve=> require(['../views/admin/advertisers/add.vue'], resolve)
                },
                '/advertisers/edit/:id':{
                    component: resolve=> require(['../views/admin/advertisers/edit.vue'], resolve)
                },

                // 后台活动
                "/campaigns":{
                    component: resolve=>require(['../views/admin/campaign/index.vue'], resolve)
                },
                "/campaigns/add":{
                    component: resolve=>require(['../views/admin/campaign/add.vue'], resolve)
                },
                "/campaigns/:id":{
                    component: resolve=>require(['../views/admin/campaign/edit.vue'], resolve)
                },
                "/campaigns/:campaignId/creative":{
                    component: resolve=>require(['../views/advertisers/creative/index.vue'], resolve)
                },

                //审核
                '/review':{
                    component: resolve=> require(['../views/admin/review/list.vue'], resolve)
                },
                '/review_item':{
                    component: resolve=> require(['../views/admin/review/audit.vue'], resolve)
                },
                '/review_item/:id':{
                    component: resolve=> require(['../views/admin/review/audit.vue'], resolve)
                },

                // RTP 竞价报告
                "/admin/reports/rtb": {
                    component: resolve=> require(['../views/admin/reports/rtb.vue'], resolve)
                },
                // 消费报告
                "/admin/reports/consume": {
                    component: resolve=> require(['../views/admin/reports/consume.vue'], resolve)
                },
                // 广告主报告
                "/admin/reports/advertiser": {
                    component: resolve=> require(['../views/admin/reports/advertiser.vue'], resolve)
                },
                // RTB分创意报告
                "/admin/reports/creativeRTB": {
                    component: resolve=> require(['../views/admin/reports/creativeRTB.vue'], resolve)
                },

                // 消费信息统计
                "/admin/stat/consume": {
                    component: resolve=> require(['../views/admin/stat/consume.vue'], resolve)
                },
                // RTB情况信息统计
                "/admin/stat/rtb": {
                    component: resolve=> require(['../views/admin/stat/rtb.vue'], resolve)
                },
                // 监测信息统计
                "/admin/stat/monitor": {
                    component: resolve=> require(['../views/admin/stat/monitor.vue'], resolve)
                },
                // 活动信息统计
                "/admin/stat/campaign": {
                    component: resolve=> require(['../views/admin/stat/campaign.vue'], resolve)
                },

                // 投放工具
                '/tools/analysis':{
                    component: resolve=> require(['../views/admin/tools/analysis.vue'], resolve)
                },

                '/tools/tongji':{
                    component: resolve=> require(['../views/public/building.vue'], resolve)
                },
                '/tools/seo':{
                    component: resolve=> require(['../views/public/building.vue'], resolve)
                },
                '/tools/district':{
                    component: resolve=> require(['../views/public/building.vue'], resolve)
                },
                '/tools/monitoring':{
                    component: resolve=> require(['../views/public/building.vue'], resolve)
                },

                // 投放工具
                '/bdtj':{
                    component: resolve=> require(['../views/admin/baidu_tongji/index.vue'], resolve)
                },
                '/bdtj/:siteId':{
                    component: resolve=> require(['../views/admin/baidu_tongji/index.vue'], resolve)
                },
                '/bdtj_visit/:siteId':{
                    component: resolve=> require(['../views/admin/baidu_tongji/visit_attribute.vue'], resolve)
                },
                '/bdtj_visit':{
                    component: resolve=> require(['../views/public/building.vue'], resolve)
                    // component: resolve=> require(['../views/admin/baidu_tongji/visit_attribute.vue'], resolve)
                }
            }
        },
        // 前台界面
        '/':{
            name:'home',
            component:(resolve)=>require(['../views/layouts/emptyContainer.vue'], resolve),
            subRoutes:{
                '/login':{
                    name:"login",
                    component: resolve=> require(['../views/index/Login.vue'], resolve)
                },
                /*'/data':{
                    component: resolve=> require(['../views/index/data.vue'], resolve)
                },*/
                '/error':{
                    name:"login",
                    component: resolve=> require(['../views/Public/NotFound.vue'], resolve)
                },
            }
        },

        '*':{
            component: resolve=> require(['../views/Public/NotFound.vue'], resolve)
        }
    });

    router.alias({
        '/': '/login',
        '/advertisers/':'/advertisers/index',
    });

    router.beforeEach(function (transition) {
        NProgress.start();
        toggleMainLoading(router.app.$store, true);

        if( transition.to.auth ){
            if( router.app.user.id != -1 ){
                if(transition.to.name == 'admin'){
                    setAdminView(router.app.$store, true);
                    if( router.app.user.isAdmin ){
                        transition.next();
                    }else{
                        router.go({ path:"/error" });
                    }
                }else{
                    setAdminView(router.app.$store, false);
                    transition.next();
                }
            }else{
                storeUtil.set('baseSetting.lastUrl', transition.to.path);
                router.go({ path:"/login" });
            }
        }else{
            transition.next();
        }
    });

    router.afterEach(()=>{
        NProgress.done();
        toggleMainLoading(router.app.$store, false);
    });
};