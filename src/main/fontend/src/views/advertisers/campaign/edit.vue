<script lang="babel">
    import '../../../less/campaign.less'
    import template from './form.html'
    import campaignMixin from "./mixin"

    export default {
        template,
        mixins: [campaignMixin],
        data(){
            return {
                addedObjectives:[]
            };
        },
        created(){
            var self = this;

            var page = layer.msg("请稍候...", { time: 20000 });
            self.campaignRes.getOne(self.$route.params.id)
                .then(({statusCode, campaign})=>{
                    layer.close(page);
                    if(statusCode == 200){
                        let { id, name, status, budgetType, budget, bid, startDate, endDate, campaignObjectives } = campaign;
                        self.$set("campaign", { id, name, status, budgetType, budget, bid, startDate, endDate });

                        if(campaignObjectives&&campaignObjectives.length>0){
                            self.addedObjectives = campaignObjectives;
                        }

                    }else{
                        layer.toast("活动数据加载失败...", function () {
                            self.$router.go('/advertisers/campaigns')
                        });
                    }
                });
        },
        watch:{
            addedObjectives(){
                var self = this;
                self.categoryData.tag.forEach((tag)=> {
                    self.categoryData.all[tag.tagCode]&&self.categoryData.all[tag.tagCode].forEach((item)=> {
                        if( self.addedObjectives && self.addedObjectives.length > 0){
                            self.addedObjectives.forEach(({categoryTagCode, categoryDictionaryId})=>{
                                if(categoryTagCode==tag.tagCode){
                                    item.subs.forEach((subItem)=>{
                                        if(subItem.categoryDictionaryId == categoryDictionaryId){
                                            self.campaignObjectives[tag.tagCode][item.category].push(subItem);
                                        }
                                    });
                                }
                            });
                        }
                    });
                });
                self.$set('campaignObjectivesTmp', self.$deepClone(self.campaignObjectives));
            }
        }
    };
</script>