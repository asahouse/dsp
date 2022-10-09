/**
 * 这里定义我们vue组件要操作的动作事件, 比如请求一个异步操作,获取其他组件的状态等
 */

import * as type from '../mutation-types'
// import advertisersApi from '../../api/advertisers'
import backendSideMenu from '../../config/BackendSideMenu'
import frontendSideMenu from '../../config/FrontendSideMenu'

/**
 * 设置侧边栏数据
 *
 * @param dispatch
 * @param isShow
 */
export const getSysSidebarData = ({ dispatch, state }, callback) => {
    dispatch(type.SET_SYSTEM_SIDEBAR, state.system.isAdminView?backendSideMenu:frontendSideMenu);
};

/**
 * 切换角色状态
 * @param dispatch
 */
export const switchAdminView = ({ dispatch, state }) => {
    dispatch(type.SWITCH_ADMIN_VIEW);
    if(state.system.isAdminView){
        layer.toast("管理员界面切换成功");
    }else {
        layer.toast("前台界面切换成功");
    }
};
/**
 * 初始角色View，从localstroage读取
 * @param dispatch
 */
export const setAdminView = ({ dispatch }, isAdminView) => {
    dispatch(type.SET_ADMIN_VIEW, isAdminView);
};



/**
 * 系统内容区域高度
 * @param dispatch
 * @param h
 */
export const setContentHeight = ({ dispatch }, h)=>{
    dispatch( type.SET_CONT_HEIGHT, h );
};



export const toggleSysSidebar = ({ dispatch }, isShow)=>{
    dispatch( type.TOGGLE_SYSTEM_SIDEBAR, isShow );
};

export const toggleMainLoading = ({ dispatch }, isShow)=>{
    dispatch( type.TOGGLE_MAIN_LOADING, isShow );
};
