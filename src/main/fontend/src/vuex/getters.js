/*
* 定义公用的getters处理 ,例如正常组件里想 computed 一个状态但在vuex里面不知道怎么处理，就可以在这里处理。
* Getters Must Be Pure 这东西必须是个纯函数
* 取个例子：
* // getters.js
 export const isEmptySearchKey = (store) => {
    return store.search.searchKey !== ""
 }
 // Search.vue
 import {isEmptySearchKey} from "../vuex/getters"
 export default{
     vuex: {
         getters: {
            isEmptySearchKey
         },
         actions:{}
     }
 }
* */

export const isEmptySearchKey = (store) => {
    return store.search.searchKey !== ""
}


