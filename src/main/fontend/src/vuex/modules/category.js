import {
    STORE__CATEGORY_ALL,
    STORE__CATEGORY_TAG
} from '../mutation-types'
import storeUtil from '../../utils/storeUtil'

const state = {
    all: storeUtil.get(STORE__CATEGORY_ALL)||{},
    tag: storeUtil.get(STORE__CATEGORY_TAG)||[],
};

//mutations
const mutations = {
    [STORE__CATEGORY_ALL](state, data){
        state.all = data;
    },
    [STORE__CATEGORY_TAG](state, data){
        state.tag = data;
    }
};

export default {
    state,
    mutations
}