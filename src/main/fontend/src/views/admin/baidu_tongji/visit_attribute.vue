
<template>
    <div>
        <cont-header
                title="投放活动监测"
                sub-title="Activity Monitoring">
            <select v-select2="currentSiteId"
                    class="form-control"
                    data-class="site-switch"
                    v-model="currentSiteId"
            >
                <template v-for="item in siteList">
                    <option :value="item.site_id">{{item.domain}}</option>
                </template>
            </select>
        </cont-header>

        <section class="content">



        </section>
    </div>
</template>

<script>
    import mixin from './mixin'
    import moment from 'moment'
    import echarts from 'echarts'

    export default{
        mixins: [mixin],
        data(){
            return{
            
            }
        },
        components:{
        
        }
    }
</script>
