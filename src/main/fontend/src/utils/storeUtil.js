import store from 'store'

/**
 * storeWithExpiration
 */
export default {
    /**
     * 保存本地 localStorage
     * @param key
     * @param val
     * @param exp 默认时效24小时
     */
    set (key, val, exp=1000*60*60*24) {
        var _this = this;
        if( key.indexOf('.') !== -1 ){
            var keys = key.split(".");
            var _val = _this.get(keys[0])||{}, tmp_str = "_val";
            for( let i=0,len=keys.length; i<len; i++){
                if( i>0 ){
                    tmp_str += "[keys["+ i +"]]";
                    eval("("+ tmp_str +"="+ tmp_str +"||{})")
                }
            }
            tmp_str += "=val";
            eval("("+ tmp_str +")");

            key = keys[0];
            val = _val;
        }

        store.set(key, { val, exp, time:new Date().getTime() })
    },
    get (key) {
        var tmp_str = "info.val=info.val", splitObj = key.indexOf('.') !== -1;
        if( splitObj ){
            var keys = key.split(".");
            key = keys[0];
            for( let i=0,len=keys.length; i<len; i++){
                if( i>0 ){
                    tmp_str += "[keys["+ i +"]]";
                }
            }
        }
        var info = store.get(key);
        if (!info) { return null }
        if ( !info.time || !info.exp || new Date().getTime() - info.time > info.exp) {
            store.remove(key);
            return null;
        }
        if( splitObj ){
            eval("("+ tmp_str +")");
        }
        return info.val
    },
    remove: store.remove
}