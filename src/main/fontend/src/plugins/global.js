import * as util from '../utils/utils'

exports.install = function (Vue, options) {

    Object.keys(util).forEach( k => Vue.prototype['$' + k] = util[k] )


    var getTradeName = function (tradeList, tradeId) {
        var result = [];
        if(tradeList&&tradeList.length > 0){
            tradeList.forEach( (item) =>{
                let { category, categoryId, subs } = item;
                if( tradeId == categoryId){
                    result.push(item);
                }else{
                    result = result.concat(getTradeName(subs, tradeId));
                }
            });
        }
        return result;
    }

    /**
     * 获取行业名称
     * @param tradeId
     * @returns {string}
     */
    Vue.prototype.$tradeName = function (tradeId) {
        let matchTrades = getTradeName(this.tradeAdvert, tradeId);
        return matchTrades.length>0?matchTrades[0]['category']:'-';
    };

}