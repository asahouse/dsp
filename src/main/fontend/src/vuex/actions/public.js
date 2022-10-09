/**
 * 这里定义我们vue组件要操作的动作事件, 比如请求一个异步操作,获取其他组件的状态等
 */

import * as type from './../mutation-types'
import resource from '../../api/resource'

/**
 * 展示菊花
 * @param dispatch
 * @param isShow
 */
export const loading = ({ dispatch }, isShow) => {

    dispatch(type.SET_LOADING_DISPLAY,isShow)

};



