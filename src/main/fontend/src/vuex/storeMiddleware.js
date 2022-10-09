import storeUtil from '../utils/storeUtil'

export default {
    onMutation ({ type, playload}, state) {
        if( type.indexOf('STORE__') === 0){
            let keys = type.replace('STORE__', '').toLowerCase().split('_'), value = state;
            keys.forEach((k)=>{
                value = value[k];
            });
            storeUtil.set(type, value, 86400000*30);
        }
    }
}