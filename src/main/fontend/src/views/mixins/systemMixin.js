import _ from 'lodash'

export default{
    vuex:{
        getters:{
            categoryData({category}) {

                let masses_tag = category.all['MASSES_TAG'].filter((cate)=>{
                    return cate.category == '固有属性'
                })[0]['subs'];
                return {
                    all: _.extend({}, category.all, {'MASSES_TAG':masses_tag}),
                    tag: category.tag.filter(t=>(t.tagCode!='TRADE_ADVERT')),
                };
            },
            tradeAdvert:({category})=>category.all.TRADE_ADVERT,
            user: ({user})=>user,
            systemContentHeight:({system})=>system.contentHeight
        }
    }
}