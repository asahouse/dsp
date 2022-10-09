import {
    SET_USER_INFO,
    CLEAR_USER_INFO
} from '../mutation-types'
import authService from '../../utils/authService'

/**
 * 程序开始从localStorage 读取
 * @type {*|{id: number, userName: string, isAdmin: boolean}}
 */
const state = authService.getUserInfo()||{
    "id": -1,
    "userName": "",
    "isAdmin": false
};

//mutations
const mutations = {
    [SET_USER_INFO](state, data){
        for(let k in data){
            state[k] = data[k];
        }
    },
    [CLEAR_USER_INFO](state){
        state = {};
    }

};

export default {
    state,
    mutations
}