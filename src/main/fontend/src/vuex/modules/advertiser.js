import {
    SET_UPLOAD_TOKEN,
    SET_CREATIVE_GROUP_LIST
} from '../mutation-types'

/**
 *
 */
const state = {
    creative:{
        group_list:[],
        uploadToken:{
            image:"",
            video:""
        }
    }
};

//mutations
const mutations = {
    [SET_UPLOAD_TOKEN](state, { tokenType, token }){
        const key = ["image",'video'][tokenType];
        state.creative.uploadToken[key] = token;
    },
    [SET_CREATIVE_GROUP_LIST](state, list){
        state.creative.group_list = list;
    }
};

export default {
    state,
    mutations
}