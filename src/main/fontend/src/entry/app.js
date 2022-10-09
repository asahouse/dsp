import Vue from 'vue'
import configRouter from '../config/router'
import {sync} from 'vuex-router-sync'
import store from '../vuex/store'
import filters from '../utils/filters'
import directive from '../utils/directive'
import partial from '../utils/partial'
import transition from '../utils/transition'
import validators from '../utils/validators'
import NProgress from "nprogress"
import App from '../views/layouts/App.vue'
import initLayer from '../utils/initLayer'

import '../vendors/slimScroll/jquery.slimscroll.min'
import '../vendors/layui/layer/layer'
import '../vendors/layui/layer/skin/layer.css'

import 'bootstrap/less/bootstrap.less'
import 'admin-lte/dist/css/AdminLTE.min.css'
import 'font-awesome/scss/font-awesome.scss'
import 'animate.css'
import 'ionicons/dist/css/ionicons.min.css'

import systemMixin from '../views/mixins/systemMixin'
Vue.mixin(systemMixin);

import global from '../plugins/global'
Vue.use(global);

import VueRouter from 'vue-router'
Vue.use(VueRouter);

import VueValidator from 'vue-validator'
Vue.use(VueValidator);
Vue.config.warnExpressionErrors = false;

// 装载过滤器
Object.keys(filters).forEach(k => Vue.filter(k, filters[k]));
// 装载指令
Object.keys(directive).forEach(k => Vue.directive(k, directive[k]));
// 装载partial
Object.keys(partial).forEach(k => Vue.partial(k, partial[k]));
// 装载过渡效果
Object.keys(transition).forEach(k => Vue.transition(k, transition[k]));
// 装载公用验证器
Object.keys(validators).forEach(k => Vue.validator(k, validators[k]));

const router = new VueRouter();
configRouter(router);
sync(store, router);
// 启用路由
router.start(Vue.extend(App), '#root');
window.router = router;


$(document)
    .ajaxStart(()=>{
        NProgress.start();
    })
    .ajaxSuccess((event, { responseJSON })=>{
        const res = responseJSON;
        if( res && res.statusCode && res.statusCode == 401 ){
            router.go('/login');
        }
    })
    .ajaxComplete((event, { responseJSON })=>{
        const res = responseJSON;
        if( res && res.statusCode && res.statusCode == 401 ){
            router.go('/login');
        }
    })
    .ajaxStop(()=>{
        NProgress.done();
    });


$.ajaxSetup({
    traditional:true
});

initLayer();