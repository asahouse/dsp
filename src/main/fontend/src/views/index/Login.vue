<style scoped>
.login-box-body{ background-color: transparent; }
.login-box{ margin: 16% auto; }
.login-logo{ margin-bottom: 10px; color: #000; }
.login-logo a{ color: #888;
    font-size: 32px; }
.login-logo a span{
    color: #ccc;
    font-size: 36px;
}
.alert{ border-radius: 0; padding: 8px; opacity: 0.8; }
.login-page, .register-page {
    background: #25282c;
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
}
</style>

<template>
    <div class="login-page">
        <div class="login-box animated fadeIn">
            <div class="login-logo">
                <a v-link="'/advertisers/index'"> <span>AdCore</span> 智能投放平台 <!--<sup>1.0</sup>--></a>
            </div>
            <div class="login-box-body">
                <validator name="formValidation"
                           @modified="onModified">
                    <form method="post" novalidate v-show="showForm">
                        <div class="form-group has-feedback">
                            <input type="text" class="form-control" placeholder="邮箱"
                                   v-validate:user-name="['required','strAndNum']"
                                   v-model='userName'>
                            <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
                        </div>
                        <div class="alert alert-danger"
                             v-if="$formValidation.touched
                                && $formValidation.userName.invalid">
                            <template v-if="$formValidation.userName.required">
                                请输入用户名
                            </template>
                            <template v-if="$formValidation.userName.strAndNum">
                                用户名不正确
                            </template>
                        </div>
                        <div class="form-group has-feedback">
                            <input type="password" class="form-control" placeholder="密码"
                                   @keyup.enter="submit"
                                   v-validate:password="['required']"
                                   v-model='password'>
                            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                        </div>
                        <div class="alert alert-danger"
                             v-if="$formValidation.touched
                                && $formValidation.invalid
                                && $formValidation.password.required">
                            密码不能为空
                        </div>
                        <div class="alert alert-danger"
                             v-if="loginErrorMsg != '' ">
                            用户名或者密码不正确！
                        </div>
                        <div class="row">
                            <div class="col-xs-8">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" v-model="rememberUserName"> 记住密码
                                    </label>
                                </div>
                            </div>
                            <!-- /.col -->
                            <div class="col-xs-4">
                                <button type="button" class="btn btn-primary btn-block btn-flat" :disabled="$formValidation.invalid" @click="submit">登录</button>
                            </div>
                            <!-- /.col -->
                        </div>
                    </form>

                    <div class="alert alert-info text-center" v-if="loginSuccessMsg != ''">
                        {{loginSuccessMsg}}
                    </div>

                </validator>
            </div>
        </div>
    </div>
</template>

<script lang="babel">
    import resource from '../../api/resource'
    import storeUtil from '../../utils/storeUtil'
    import { setUserInfo } from '../../vuex/actions/user'
    import md5 from 'md5'
    import _ from 'lodash'


    export default{
        data(){
            return{
                rememberUserName: true,
                showForm: true,
                loginErrorMsg:"",
                loginSuccessMsg:"",
                userName: storeUtil.get('baseSetting.userName')||"",
                password: storeUtil.get('baseSetting.password')||"",
            }
        },
        vuex:{
            actions:{
                setUserInfo
            }
        },
        methods:{
            onModified(){
                this.loginErrorMsg = '';
            },
            submit(){
                var self = this;
                if(self.$formValidation.valid){
                    self.loginSuccessMsg = "登录中...";
                    self.showForm = false;

                    // 记录用户名
                    if(self.rememberUserName){
                        storeUtil.set('baseSetting.userName', self.userName, 1000*60*60*24*7);
                        storeUtil.set('baseSetting.password', self.password, 1000*60*60*24*7);
                    }else{
                        storeUtil.set('baseSetting.userName', '');
                        storeUtil.set('baseSetting.password', '');
                    }

                    resource.login().get({
                        userName: self.userName,
                        password: md5(self.password)
                    }).done(function ({ statusCode, advertiser, message }) {
                        if(statusCode == 200){
                            self.setUserInfo(advertiser);
                            self.loginSuccessMsg = "加载中,请稍候...";
                            self.loginErrorMsg = "";

                            setTimeout(function () {
                                let url = storeUtil.get('baseSetting.lastUrl') || (advertiser.isAdmin?'/admin/dashboard':'/advertisers/campaigns');
//                                console.log('login redirect', url);
                                self.$router.go( url );
                            },50);

                        }else{
                            self.loginErrorMsg = message;
                            self.showForm = true;
                            self.loginSuccessMsg = "";
                        }
                    });

                }
            }
        }
    }
</script>