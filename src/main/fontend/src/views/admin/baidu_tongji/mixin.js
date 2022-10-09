import contHeader from '../../public/contHeader.vue';
import tjApi from '../../../api/tongji';
import storeUtil from '../../../utils/storeUtil'


export default {
    data(){
        const siteList = storeUtil.get('____tj_site_list')||[];
        return{
            siteList,
            tjApi,
            currentSiteId:0
        }
    },
    created(){

        let self = this;
        if( !self.siteList ) {
            self.tjApi.siteList().then(({data}) => {
                self.$set('siteList', data);
                storeUtil.set('____tj_site_list', self.siteList);
            });
        }


    },
    components:{
        contHeader
    }
}