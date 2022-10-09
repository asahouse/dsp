/**
 * 这里定义我们vue组件要操作的动作事件, 比如请求一个异步操作,获取其他组件的状态等
 */

import * as type from '../mutation-types'
import authService from '../../utils/authService'

/**
 * 设置用户信息
 * @param dispatch
 * @param data
 */
export const setUserInfo = ({ dispatch }, data) => {
    authService.setUserInfo(data);
    dispatch(type.SET_USER_INFO, data);
};

/**
 * 设置用户信息
 * @param dispatch
 * @param data
 */
export const clearUserInfo = ({ dispatch }) => {
    authService.logout();
    dispatch(type.CLEAR_USER_INFO);
};




