
<template>
    <cont-header
            title="控制台"
            sub-title="control panel" ></cont-header>
    <section class="content">
        <div class="row">
            <div v-for="item in sidebarData[0]['children']" class="col-lg-2 col-md-3 col-sm-4 col-xs-6">
                <div :class="'small-box ' + bgs[$index]">
                    <div class="inner">
                        <h3>{{item.name}}</h3>
                        <p>{{item.subName}}</p>
                    </div>
                    <div class="icon">
                        <i :class="item.icon"></i>
                    </div>
                    <a v-link="item.link" class="small-box-footer">查看更多 <i class="fa fa-arrow-circle-right"></i></a>
                </div>
            </div>
        </div>
    </section>
</template>

<script>
    import contHeader from '../public/contHeader.vue';
    export default{
        data(){
            return{
                bgs:['bg-aqua', 'bg-green', 'bg-yellow', 'bg-red']
            }
        },
        vuex:{
            getters:{
                sidebarData:({ system }) => system.sidebar
            }
        },
        components:{
            contHeader
        }
    }
</script>
