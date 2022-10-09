/**
 * 这里定义我们vue组件要操作的动作事件, 比如请求一个异步操作,获取其他组件的状态等
 */

import resource from '../../api/resource'
import User from '../../vuex/modules/user'
import * as type from '../mutation-types'
import storeUtil from '../../utils/storeUtil'

const $creativeGroupRes = resource.advertisers.creatives.group(User.state.id)
const $tokenRes = resource.advertisers.creatives.upload.token()

/**
 * 获取 上传token
 * @param dispatch
 * @param tokenType 令牌类型，0：图片，1：视频
 * @param success
 */
export const getUploadToken = ({ dispatch }, tokenType=0, success)=>{
    if( typeof tokenType == 'function'){
        success = tokenType;
        tokenType = 0;
    }
    const key = ["image",'video'][tokenType];
    var token = storeUtil.get(key + '_ct');

    if( token ){
        dispatch( type.SET_UPLOAD_TOKEN, { tokenType, token } );
        success&&success();
    }else{
        $tokenRes.get({ advId:User.state.id }).then(({ statusCode, token })=>{
            if(statusCode == 200){
                storeUtil.set(key+'_ct', token, 1000*60*20);
                dispatch( type.SET_UPLOAD_TOKEN, { tokenType, token } );
                success&&success();
            }
        });
    }

};

/**
 * 获取并设置创意组
 * @param dispatch
 * @param state
 * @param success
 */
export const getCreativeGroupList = ({ dispatch, state }, success)=>{
    $creativeGroupRes.get({size:2000}).then(function ({ statusCode, list }) {
        if( statusCode == 200 ){
            dispatch( type.SET_CREATIVE_GROUP_LIST, list );
            success&&success();
        }
    });
};

