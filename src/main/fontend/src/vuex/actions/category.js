/**
 * 这里定义我们vue组件要操作的动作事件, 比如请求一个异步操作,获取其他组件的状态等
 */

import storeUtil from '../../utils/storeUtil'
import * as type from './../mutation-types'
import resource from '../../api/resource'

const cate_all = resource.category.all();
const cate_tag = resource.category.tag();

export const getAll = ({ dispatch }, cbk) => {
    if(!storeUtil.get(type.STORE__CATEGORY_ALL)){
        cate_all.get()
            .then(({list})=>{
                dispatch( type.STORE__CATEGORY_ALL, list);
                cbk&&cbk();
            } );
    }

};

export const getTag = ({ dispatch }, cbk) => {
    if(!storeUtil.get(type.STORE__CATEGORY_TAG)) {
        cate_tag.get()
            .then(({list})=> {
                dispatch(type.STORE__CATEGORY_TAG, list);
                cbk && cbk();
            });
    }
};



