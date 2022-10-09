import contHeader from '../../public/contHeader.vue';
import { modal } from 'vue-strap'

import resource from '../../../api/resource'

import { campaignMap } from '../../../config/maps'
import moment from 'moment'
import areaSelect from '../../widgets/areaSelect.vue'


export default {
    data(){
        var self = this;
        return {
            campaignMap,
            dateRangeOption: {
                minDate: moment().format('YYYY-MM-DD'),
            },
            campaignDate: {
                startDate: moment().format('YYYY-MM- DD'),
                endDate: moment().add(1, 'M').format('YYYY-MM-DD'),
            },
            campaign: {
                name: "",
                status: 0,
                budgetType: 0,
                budget: 0.00,
                bid: 0.00,
                startDate: moment().format('YYYY-MM-DD'),
                endDate: moment().add(1, 'M').format('YYYY-MM-DD')
            },
            isShowDirectiveModal: false,
            isShowAreaModal: false,
            currentObjective: {
                tag: {},
                categories: []
            },
            campaignObjectivesTmp: {},
            campaignRes: resource.advertisers.campaign(self.user.id),
            renderDataCount:0,
            tmpLocations:[],
            selectedLocations:[]
        }
    },
    computed: {
        campaignObjectives(){
            var self = this, campaignObjectives = {};
            self.categoryData.tag.forEach((tag)=> {
                if (!campaignObjectives[tag.tagCode]) {
                    campaignObjectives[tag.tagCode] = {};
                }
                if( self.categoryData.all[tag.tagCode] ){
                    self.categoryData.all[tag.tagCode].forEach((item)=> {
                        campaignObjectives[tag.tagCode][item.category] = [];
                    });
                }
            });
            return campaignObjectives;
        },
        showObjectiveTable(){
            var self = this, isEmpty = true;
            if (_.isEmpty(self.campaignObjectivesTmp)) {
                isEmpty = true;
            } else {
                for (var k in self.campaignObjectivesTmp) {
                    for (var k2 in self.campaignObjectivesTmp[k]) {
                        if (self.campaignObjectivesTmp[k][k2] && self.campaignObjectivesTmp[k][k2].length > 0) {
                            isEmpty = false;
                        }
                    }
                }
            }
            if( self.selectedLocations.length > 0){
                isEmpty = false;
            }
            return !isEmpty;
        },
        campaignData(){
            var self = this, _tmpObjectives = {}, campaignObjectives = [];
            for (var k in self.campaignObjectivesTmp) {
                for (var k2 in self.campaignObjectivesTmp[k]) {
                    if (self.campaignObjectivesTmp[k][k2] && self.campaignObjectivesTmp[k][k2].length > 0) {
                        if (!_tmpObjectives[k]) _tmpObjectives[k] = [];
                        self.campaignObjectivesTmp[k][k2].forEach(({categoryDictionaryId})=> {
                            _tmpObjectives[k].push(categoryDictionaryId);
                        });
                    }
                }
            }
            for (k in _tmpObjectives) {
                campaignObjectives.push({
                    categoryTagCode: k, categoryDictionaryIds: _tmpObjectives[k]
                });
            }

            return {campaign: self.campaign, campaignObjectives};
        }
    },
    watch: {
        campaignDate(){
            let self = this, {startDate, endDate} = self.campaignDate;
            self.campaign.startDate = startDate;
            self.campaign.endDate = endDate;
        },
        // 展示定向的弹窗
        isShowDirectiveModal(val){
            let self = this, tagCode = self.currentObjective.tag.tagCode;
            if (!val) {
                self.$set('campaignObjectives', self.$deepClone(self.campaignObjectivesTmp));
                setTimeout(()=> {
                    self.currentObjective = {tag: {}, categories: []};
                }, 500);
            }
        }
    },
    methods: {
        showDirectiveModal(tag){
            var self = this;
            self.isShowDirectiveModal = true;
            self.$set('currentObjective', {tag, categories: self.categoryData.all[tag.tagCode]});
        },
        saveObjectives(){
            var self = this, tagCode = self.currentObjective.tag.tagCode;
            self.$set('campaignObjectivesTmp', self.$deepClone(self.campaignObjectives));
            self.isShowDirectiveModal = false;
        },
        isEmptyCategory(cate){
            var isEmpty = true;
            for (var k in cate) {
                if (cate[k] && cate[k].length > 0) isEmpty = false;
            }
            return isEmpty;
        },
        saveCampaign(action){
            var self = this;

            self.campaignRes[self.campaignData.campaign.id?'put':'post'](self.campaignData)
                .then(({statusCode, campaign})=> {
                    if (statusCode == 200) {
                        layer.toast("操作成功");
                        if (action == 'next') {
                            self.$router.go('/advertisers/campaigns/' + campaign.id + '/creative');
                        }else{
                            self.$router.go('/advertisers/campaigns/' + campaign.id);
                        }
                    }
                });
        },
        locationChange(locations){
            this.tmpLocations = locations;
            console.log( this.tmpLocations )
        },
        setLocations(){
            var self = this, selectedLocations = [];
            self.isShowAreaModal = false;
            self.tmpLocations.forEach((item)=>{
                selectedLocations.push(item);
            });
            self.selectedLocations = selectedLocations;
        },
        cancelLocations(){
            var self = this;
            self.$refs.areaSelect.reset();
            self.isShowAreaModal = false;
        }
    },
    components: {
        contHeader, modal, areaSelect
    },
};