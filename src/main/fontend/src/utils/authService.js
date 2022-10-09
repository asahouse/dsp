/**
 * 验证服务控制
 */

import storeUtil from '../utils/storeUtil'


// 未操作过期时间
const expire = 1000*60*30;

/**
 * 判断用户是否登录
 * @returns {*|boolean}
 */
const isLogin = ()=>{
    let user = getUserInfo();
    return (user&&user.id!==-1)
}

/**
 * 获取用户信息
 * @returns {*}
 */
const getUserInfo = ()=>{
    return storeUtil.get('user');
}

/**
 * 保存用户信息
 * @param userInfo
 */
const setUserInfo = (userInfo)=>storeUtil.set('user', userInfo, expire);

/**
 * 登出
 */
const logout = ()=>storeUtil.set('user', null, -1);

/**
 * 延长用户登录时间
 */
const refreshUserInfo = ()=>{
    setUserInfo(getUserInfo());
}

export default {
    getUserInfo,
    setUserInfo,
    refreshUserInfo,
    logout,
    isLogin
}