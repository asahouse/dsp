
<script lang="babel">
    import _ from 'lodash'
    import { spinner } from 'vue-strap';
    import { getUploadToken, getCreativeGroupList } from '../../vuex/actions/advertiser'

    export default{
        props:{
            show: {
                require: true,
                type: Boolean,
            },
            editCreative:{
                type: Object
            }
        },
        data(){
            return {
                cdnPath:"http://dsp.light.gz.cn/",
                uploadServer:'http://upload.qiniu.com',
                showSizeHelpBlock:false, // 是否展示尺寸提示
                invalidUploadObj:false, // 视频格式是否正确
                uploader:{},
                invalidTxt:'',
                spinnerTxt:'请稍候',
                groupSort:['favorite', 'createDate']
            }
        },
        vuex:{
            actions:{
                getUploadToken,
                getCreativeGroupList
            },
            getters:{
                uploadToken:({advertiser})=> advertiser.creative.uploadToken,
                creativeGroupList:({advertiser})=>advertiser.creative.group_list
            }
        },
        computed:{
            materialItem(){
                var self = this, materialItem = {};
                for( let k in self.creative ){
                    if( self.creative[k] != ""){
                        materialItem[k] = self.creative[k];
                    }
                }
                return materialItem;
            },
            creativeGroupListOrderBy(){
                var self = this;
                return _.orderBy(self.creativeGroupList, self.groupSort, ['asc']);
            }
        },
        components:{
            spinner
        },
        watch:{
//            "editCreative":{
//                deep: true,
//                handler: function (val) {
//                    var self = this;
//                    self.$set("creative", JSON.parse(JSON.stringify(val)));
//                }
//            }
        },
        methods:{
            onCreativeFormTouched(){
                this.$validate('groupId', true)
            },
            close() {
                this.$emit('close');
            },
            initComponent(){
                var self = this;
                // 获取uploadToken
                self.spinnerTxt = "请稍候";
                self.$refs.spinner.show();

                self.getUploadToken(0, function () {

                    // 设置上传组件
                    self.setUploader();
                    self.$refs.spinner.hide();
                });
            },
        }
    }
</script>