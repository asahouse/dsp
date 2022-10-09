/**
 * 核心文件，这里就是vuex基本的写法了，先引用vue和vuex 然后user(Vuex),把定义好的 modules , middlewares 引入进来然后返回一个Vuex.store
 */

import Vue from 'vue'
import Vuex from 'vuex'

// 中间件
import middlewares from './middlewares'
// module 也就是 vuex 概念里的 store ,这里把 store 模块拆分
import advertiser from './modules/advertiser'
import category from './modules/category'
import system from './modules/system'
import user from './modules/user'
// 是否开启调试模式
import { debug } from '../config/'

Vue.use(Vuex);
Vue.config.debug = debug;

export default new Vuex.Store({
    modules: {
        advertiser,
        category,
        system,
        user
    },
    strict: debug,
    middlewares
})