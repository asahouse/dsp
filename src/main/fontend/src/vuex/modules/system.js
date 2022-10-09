import {
    TOGGLE_SYSTEM_SIDEBAR,
    SET_SYSTEM_SIDEBAR,
    SET_ADMIN_VIEW,
    SWITCH_ADMIN_VIEW,
    SHOW_SYSTEM_ALERT,
    HIDE_SYSTEM_ALERT,
    SHOW_SYSTEM_CONFIRM,
    HIDE_SYSTEM_CONFIRM,
    RESET_SYSTEM_CONFIRM,
    SET_TOAST,
    SET_CONT_HEIGHT,
    TOGGLE_MAIN_LOADING
} from '../mutation-types'
import storeUtil from '../../utils/storeUtil'

const state = {
    sidebar:[],
    showSidebar: !storeUtil.get('baseSetting.showSidebar'), // 展示菜单
    // 切换角色开关
    isAdminView: !!storeUtil.get('baseSetting.isAdminView'),
    // 系统alert状态
    systemAlert:{
        show:false,
        type:'danger',
        width:"400px",
        duration: 3000,
        placement:"top",
        title:"",
        content:""
    },
    systemConfirm:{
        show:false,
        title:"",
        content:"",
        ok:function () {},
        no:function () {}
    },
    contentHeight:300,
    toast:{
        msg:"",
        options:""
    },
    isShowLoading: false
};

//mutations
const mutations = {
    // 设置侧边栏数据
    [SET_SYSTEM_SIDEBAR](state, data){
        state.sidebar = data;
    },

    // 切换角色开关
    [SWITCH_ADMIN_VIEW](state){
        state.isAdminView = !state.isAdminView;
        storeUtil.set('baseSetting.isAdminView', state.isAdminView)
    },
    // 切换角色
    [SET_ADMIN_VIEW](state, isAdminView){
        state.isAdminView = isAdminView;
    },
    // 展示系统alert
    [SHOW_SYSTEM_ALERT](state, config){
        // 合并配置
        for( let key in config ){
            if( typeof state.systemAlert[key] !== 'undefined'){
                state.systemAlert[key] = config[key];
            }
        }
        state.systemAlert.show = true;
    },
    // 隐藏系统alert
    [HIDE_SYSTEM_ALERT](state){
        state.systemAlert.show = false;
    },
    // 展示系统 systemConfirm
    [SHOW_SYSTEM_CONFIRM](state, config){
        // 合并配置
        for( let key in config ){
            if( typeof state.systemConfirm[key] !== 'undefined'){
                state.systemConfirm[key] = config[key];
            }
        }
        state.systemConfirm.show = true;
    },
    // 隐藏系统 systemConfirm
    [HIDE_SYSTEM_CONFIRM](state){
        // reset state
        state.systemConfirm.show = false;
    },
    // 隐藏系统 systemConfirm
    [RESET_SYSTEM_CONFIRM](state){
        // reset state
        state.systemConfirm = Object.assign({},state.systemConfirm,{ title:'', content:'', ok: function(){}, no:function(){} });
    },

    [TOGGLE_SYSTEM_SIDEBAR](state, isShow){
        state.showSidebar = isShow;
        storeUtil.set('baseSetting.showSidebar', !isShow, 1000*60*60*24*7);
    },

    [SET_TOAST](state, msg, option={}){
        state.toast = {msg, option};
    },

    [SET_CONT_HEIGHT](state, height){
        state.contentHeight = height
    },

    [TOGGLE_MAIN_LOADING](state, isShow){
        state.isShowLoading = isShow
    },

};

export default {
    state,
    mutations
}