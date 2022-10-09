<template>
    <div>
        <cont-header title="新增广告主"
                     sub-title="add advertiser"
                     :breadcrumb="[{title:'广告主', link:'/admin/advertisers', icon:'iconfont icon-guanggaozhu' }]"
                    ></cont-header>

        <section class="content">
            <div class="row">
                <div class="col-sm-12">
                    <div class="box">
                        <validator name="advertiserForm">
                            <form novalidate @submit="saveAdvertiser">
                                <div class="row">
                                    <div class="col-xs-6">
                                        <div class="box-header"><h3 class="box-title">基本信息</h3></div>
                                    </div>
                                    <div class="col-xs-6">
                                        <div class="box-header"><h3 class="box-title">BES 广告主客户信息</h3></div>
                                    </div>
                                </div>
                                <div class="box-body">

                                    <div class="row">
                                        <div class="col-xs-6">
                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.userName.invalid }">
                                                <label>用户名</label>
                                                <input type="text" class="form-control" placeholder="请输入用户名"
                                                       v-model="advertiser.userName"
                                                       v-validate:user-name="{
                                                            required:{ rule:true, message: '用户名不能为空'},
                                                            strAndNum:{ rule:true, message:'用户名必须由英文或数字组合'},
                                                            minlength:{ rule:4, message:'用户名长度不能小于4'},
                                                            maxlength:{ rule:16, message:'用户名长度不能超过16'},
                                                       }"
                                                >
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='userName'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>

                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.password.invalid }">
                                                <label>密码</label>
                                                <input type="password" class="form-control" placeholder="密码" v-model="password_plain"
                                                       v-validate:password="{
                                                           required:{ rule:true, message: '密码不能为空'},
                                                           minlength:{ rule:4, message:'密码长度不能小于6'},
                                                       }"
                                                >
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='password'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>

                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.confirm.invalid }">
                                                <label>确认密码</label>
                                                <input type="password" class="form-control" placeholder="密码" v-model="password_confirm"
                                                       v-validate:confirm="{
                                                           required:{ rule:true, message: '不能为空'},
                                                           confirm: { rule: password_plain, message: '密码输入不一致' }
                                                       }"
                                                >
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='confirm'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>

                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.email.invalid }">
                                                <label>邮箱</label>
                                                <input type="text" class="form-control" placeholder="请输入有效的邮箱地址" v-model="advertiser.email"
                                                       v-validate:email="{ email:{ rule:true, message: '请输入有效的邮箱地址' } }"
                                                >
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='email'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>
                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.balance.invalid }">
                                                <label>账户余额</label>
                                                <div class="input-group">
                                                    <span class="input-group-addon">￥</span>
                                                    <input type="number" class="form-control" placeholder="请输入账户余额" v-model="advertiser.balance | currencyDisplay"
                                                           v-validate:balance="{ required:{ rule:true, message:'请输入账户余额' } }"
                                                    >
                                                </div>
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='balance'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>

                                            <div class="row">
                                                <div class="col-xs-6">
                                                    <div class="form-group">
                                                        <label>是否管理员</label>
                                                        <div>
                                                            <div class="radio-inline">
                                                                <input id="ai_1" class="magic-radio" type="radio" v-model="advertiser.isAdmin" :value="true">
                                                                <label for="ai_1">是</label>
                                                            </div>
                                                            <div class="radio-inline">
                                                                <input  id="ai_2" class="magic-radio" type="radio" v-model="advertiser.isAdmin" :value="false">
                                                                <label for="ai_2">否</label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-xs-6">
                                                    <div class="form-group">
                                                        <label>状态</label>
                                                        <div>
                                                            <div class="radio-inline">
                                                                <input id="as_1" class="magic-radio" type="radio" v-model="advertiser.status" value="1">
                                                                <label for="as_1">启用</label>
                                                            </div>
                                                            <div class="radio-inline">
                                                                <input id="as_2" class="magic-radio" type="radio" v-model="advertiser.status" value="0">
                                                                <label for="as_2">禁用</label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xs-6">

                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.companyName.invalid }">
                                                <label>公司名称</label>
                                                <input type="text" class="form-control" placeholder="请输入客户名称" v-model="advertiser.qualification.companyName"
                                                       v-validate:company-name="{required:{ rule:true, message:'请输入公司名称' }}"
                                                >
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='companyName'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>
                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.licence.invalid }">
                                                <label>营业执照</label>
                                                <input type="text" class="form-control" placeholder="请输入营业执照" v-model="advertiser.qualification.licence"
                                                       v-validate:licence="{ required:{ rule:true, message:'请输入营业执照' } }"
                                                >
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='licence'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>

                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.siteName.invalid }">
                                                <label>网站名称</label>
                                                <input type="text" class="form-control" placeholder="请输入网站名称" v-model="advertiser.qualification.siteName"
                                                       v-validate:site-name="{required:{ rule:true, message:'请输入网站名称' }}"
                                                >
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='siteName'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>
                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.officialSite.invalid }">
                                                <label>网站URL</label>
                                                <input type="text" class="form-control" placeholder="请输入网站URL" v-model="advertiser.qualification.officialSite"
                                                       v-validate:official-site="{ url:{ rule:true, message:'请输入网站URL' } }"
                                                >
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='officialSite'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>
                                            <div class="form-group" :class="{ 'has-error': $advertiserForm.touched && $advertiserForm.tradeId.invalid }">
                                                <label>广告行业</label>
                                                <div>
                                                    <select v-select2="advertiser.qualification.tradeId"
                                                            class="form-control"
                                                            data-placeholder="请选择行业"
                                                            v-model="advertiser.qualification.tradeId"
                                                            style="width:100%;"
                                                            v-validate:trade-id="{required:{ rule:true, message:'请选择行业' }}"
                                                    >
                                                        <option value="">请选择</option>
                                                        <template v-for="trade in tradeAdvert">
                                                            <optgroup :label="trade.category">
                                                                <template v-for="td in trade.subs">
                                                                    <option :value="td.categoryId">{{td.category}}</option>
                                                                </template>
                                                            </optgroup>
                                                        </template>
                                                    </select>
                                                </div>
                                                <div class="help-block">
                                                    <template v-for="error in $advertiserForm.errors">
                                                        <span style="margin-right: 5px;" v-if="$advertiserForm.touched&&error.field=='tradeId'">{{error.message}}</span>
                                                    </template>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                                <div class="box-footer">
                                    <div class="btn-group">
                                        <a v-link="'/admin/advertisers'" class="btn btn-default"><i class="fa fa-angle-left"></i> 返回</a>
                                        <button type="submit" class="btn btn-primary" :disabled="$advertiserForm.invalid">保存</button>
                                    </div>
                                </div>
                            </form>
                        </validator>
                    </div>
                </div>
            </div>
        </section>
    </div>
</template>
<style scoped>
    .box{ min-height:390px; }
</style>
<script>
    import contHeader from '../../public/contHeader.vue';
    import resource from '../../../api/resource'
    import md5 from 'md5'

    export default{
        data(){
            return{
                advResource:resource.admin.advertiser(),
                password_plain:"",
                password_confirm:"",
                advertiser: {
                    "email": "",
                    "isAdmin": false,
                    "password": "",
                    "balance": 0.00,
                    "status": 1,
                    "userName": "",
                    "qualification":{
                        "licence":"",
                        "companyName":"",
                        "officialSite":"",
                        "siteName":"",
                        "tradeId": ""
                    }
                }
            }
        },
        watch:{
            "password_plain":function (val) {
                if(val != ''){
                    this.advertiser.password = md5(val);
                }
            }
        },
        methods:{
            saveAdvertiser(e){
                e.preventDefault();
                var self = this;
                self.advResource
                        .post( self.advertiser)
                        .then(({ statusCode })=>{
                            if( statusCode == 200){
                                layer.msg('操作成功', { time:1000 }, function () {
                                    self.$router.go('/admin/advertisers');
                                });
                            }
                        });

            }
        },
        components:{
            contHeader
        }
    }
</script>
