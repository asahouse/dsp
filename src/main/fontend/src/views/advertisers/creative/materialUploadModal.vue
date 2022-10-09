<style lang="less" src="../../../less/creativeUploadBox.less" scoped></style>
<style>
    .box-footer .webuploader-pick{
        background: #f4f4f4;
        color: #444;
        border: 1px solid #ddd;
        padding: 4px 10px;
    }
</style>
<template>

    <div role="dialog" class="modal fade" v-show="show">
        <div class="creative-upload-box">
            <spinner id="box-spinner" :size="lg" :fixed="(true,false)" :text="spinnerTxt" v-ref:spinner></spinner>

            <div class="row">
                <div class="col-sm-6 left-cont">
                    <div class="box box-solid">
                        <div class="box-header with-border">
                            <strong>物料上传</strong>
                        </div>
                        <div class="box-body no-padding" id="m-list-body">

                            <ul :class="['material-list']">
                                <li v-for="m in materialList" @click="checkedMaterial(m)" :class="{'file-invalid':!!m.fileInvalid}">
                                    <template v-if="m.file">
                                        <div class="img-thumb">
                                            <img :src="m.base64_thumb" alt="m.name">
                                        </div>
                                        <h4 class="row">
                                            <div class="col-sm-12">{{m.name}}</div>
                                            <small class="col-sm-4">宽度：{{m.width}} </small>
                                            <small class="col-sm-4">高度：{{m.height}} </small>
                                            <small class="col-sm-4">行业：{{$tradeName(m.tradeId)}}</small>
                                        </h4>
                                        <p v-if="!m.fileInvalid">目标地址：<span>{{m.landingUrl==''?'请填写目标地址':m.landingUrl}}</span></p>
                                        <p class="text-danger" v-if="!!m.fileInvalid">{{m.fileInvalid}}</p>
                                        <p class="text-danger field-invalid" v-if="m.fieldInvalid&&m.fieldInvalid.length>0">{{m.fieldInvalid.join(', ')}}</p>
                                        <button class="del-material btn btn-danger btn-xs"
                                                v-if="!!m.fileInvalid"
                                                @click.prevent="deleteMaterial(m)">
                                            <i class="fa fa-trash"></i>
                                        </button>
                                    </template>
                                    <template v-else>
                                        <div class="img-thumb">
                                            <img :src="m.url+'?110x110'" alt="m.name">
                                        </div>
                                        <h4 class="row">
                                            <div class="col-sm-12">{{m.name}}</div>
                                            <small class="col-sm-4">宽度：{{m.width}} </small>
                                            <small class="col-sm-4">高度：{{m.height}} </small>
                                            <small class="col-sm-4">行业：{{$tradeName(m.tradeId)}}</small>
                                        </h4>
                                        <p v-if="!m.fileInvalid">目标地址：
                                            <span>{{m.landingUrl==''?'请填写目标地址':m.landingUrl}}</span>
                                        </p>
                                        <p class="text-danger field-invalid" v-if="m.fieldInvalid&&m.fieldInvalid.length>0">{{m.fieldInvalid.join(', ')}}</p>
                                    </template>
                                    <input type="checkbox" class="magic-checkbox"  v-model="checkedMaterials" :value="m">
                                    <label></label>
                                </li>
                            </ul>

                        </div>
                        <div class="box-footer">
                            <input type="checkbox" class="magic-checkbox" id="edit-all-materials" v-model="isEditAll">
                            <label for="edit-all-materials" class="pull-left" v-show="materialList.length!=0" >
                                编辑全部({{checkedMaterials.length}}/{{validMaterialList.length}})
                            </label>
                            <template v-if="!editMode">
                                <div id="multi-uploader-2" class="pull-right" :style="{'visibility':(materialList.length==0||validMaterialList.length>=10)?'hidden':'visible'}"></div>
                                <button class="pull-right btn btn-sm btn-danger" v-if="materialList.length>0" @click.prevent="emptyMaterialList">
                                    <i class="fa fa-trash"></i> 清空
                                </button>
                            </template>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 right-cont">
                    <div class="box box-solid">
                        <div class="box-header with-border">
                            <h3 class="box-title"></h3>
                            <div class="box-tools pull-right">
                                <button type="button" class="btn btn-box-tool" @click="close"><span aria-hidden="true">×</span></button>
                            </div>
                        </div>
                        <validator name="materialForm">
                            <form>
                                <div class="box-body">

                                    <div class="form-group">
                                        <label for="name">物料名称 </label>
                                        <input type="text" class="form-control" id="name" placeholder="物料名称，可不填" v-model="material.name" >
                                    </div>

                                    <div class="form-group">
                                        <label>状态</label>
                                        <div>
                                            <div class="radio-inline" v-for="(key,status) in creativeMap.status">
                                                <input :id="'material_status_'+key" class="magic-radio" type="radio" v-model="material.status" :value="key">
                                                <label :for="'material_status_'+key">{{status}}</label>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label for="name">行业</label>
                                        <select v-select2="material.tradeId"
                                                class="form-control"
                                                data-placeholder="请选择行业"
                                                data-allow-clear="true"
                                                v-model="material.tradeId"
                                                style="width:100%;"
                                        >
                                            <option value="" v-if="editMode">请选择</option>
                                            <template v-for="trade in tradeAdvert">
                                                <optgroup :label="trade.category">
                                                    <template v-for="td in trade.subs">
                                                        <option :value="td.categoryId">{{td.category}}</option>
                                                    </template>
                                                </optgroup>
                                            </template>
                                        </select>
                                    </div>

                                    <div class="form-group"
                                         :class="{'has-error': ($materialForm.touched && $materialForm.landingUrl.url_when_exit) || (fieldInvalid.length>0) }">
                                        <label for="landingUrl">
                                            目标地址URL
                                            <span v-if="($materialForm.touched && $materialForm.landingUrl.url_when_exit) || (fieldInvalid.length>0)">
                                                （请输入正确格式URL）
                                            </span>
                                        </label>
                                        <input type="url" class="form-control" id="landingUrl" placeholder="http://"
                                               v-model="material.landingUrl"
                                               v-validate:landing-url="['url_when_exit']" >
                                    </div>

                                    <div class="alert alert-danger field-invalid animated" transition="fade" v-if="fieldInvalid.length>0">
                                        {{fieldInvalid.join(", ")}}
                                    </div>

                                </div>
                                <div class="box-footer">
                                    <div class="pull-left question-circle" v-show="materialList.length>0 && !editMode">
                                        <span class="btn btn-link"
                                              @mouseover="showSizeHelpBlock=true"
                                              @mouseout="showSizeHelpBlock=false">
                                            <i class="fa fa-question-circle-o" ></i> 上传限制
                                        </span>
                                        <div class="popover fade-transition top" v-show="showSizeHelpBlock">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <strong>大小限制：</strong> 小于200KB <br>
                                                <strong>支持的类型：</strong> JPG, GIF, PNG <br>
                                                <strong>尺寸限制：</strong>
                                                <p>
                                                    {{imageSize.join(", ")}}
                                                </p>
                                            </div>
                                        </div>
                                    </div>

                                    <button type="button"
                                            @click.prevent="saveCreative"
                                            class="btn btn-primary pull-right"
                                            :disabled="$materialForm.invalid || (!isMaterialModify&&editMode) ||(fieldInvalid.length>0)"
                                            v-show="checkedMaterials.length!=0">保存物料</button>
                                    <button type="button" @click="close" class="btn btn-default pull-right">关闭</button>
                                </div>
                            </form>
                        </validator>
                    </div>
                </div>
            </div>


            <div class="no-material text-center" v-show="materialList.length==0">
                <div class="border-dash">
                    <span>
                        <i class="fa fa-picture-o"></i>
                    </span>
                    <div id="multi-uploader-1"></div>
                    <p>
                        或将照片拖到这里，单次最多可选10张
                    </p>
                    <div class="text-muted well well-sm no-shadow img-size-desc">
                        <span class="title">上传限制</span>
                        <strong>大小：</strong> 小于200KB <br>
                        <strong>支持的类型：</strong> JPG, GIF, PNG <br>
                        <strong>尺寸限制：</strong> {{imageSize.join(", ")}}
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>
<script lang="babel">
    import Vue from 'vue'
    import _ from 'lodash'
    import { spinner } from 'vue-strap';
    import imageSize from '../../../data/imageSizeData'
    import resource from '../../../api/resource'
    import creativeBoxMixin from "../../mixins/creativeBoxMixin.vue"
    import { getUploadToken } from '../../../vuex/actions/advertiser'
    import { creativeMap } from '../../../config/maps'
    import { qiniuPath, uploadServer } from '../../../config/index'
    import IScroll from 'iscroll'
    import WebUploader from 'webuploader'
    import 'webuploader/dist/webuploader.css'

    export default{
        props:['show', 'materialList', 'campaignId', 'editMode'],
        data(){
            return{
                creativeMap,
                imageSize,
                isEditAll:false,
                material:{
                    tradeId: "",
                    name:"",
                    landingUrl:"",
                    status:0,
                    type:1
                },
                fieldInvalid:[],
                checkedMaterials:[],
                multiUploader:{},
                qiniuPath,
                uploadServer,
                showSizeHelpBlock:false, // 是否展示尺寸提示
                invalidTxt:'',
                spinnerTxt:'请稍候',
                groupSort:['star', 'createDate']
            }
        },
        vuex:{
            actions:{
                getUploadToken
            },
            getters:{
                uploadToken:({advertiser})=> advertiser.creative.uploadToken
            }
        },
        validators:{
            url_when_exit(val) {
                return (val&&val!='')?/^(http\:\/\/|https\:\/\/)(.{4,})$/.test(val):true;
            }
        },
        computed:{
            materialRes(){
                var self = this;
                return resource.advertisers.creatives.material(self.user.id, self.campaignId)
            },
            validMaterialList(){
                var self = this, fieldInvalid = {};
                var vml = self.materialList.filter(function (m) {
                    let { tradeId,landingUrl } = m;
                    if( !tradeId ){
                        if(!fieldInvalid['请选择行业']){
                            fieldInvalid['请选择行业'] = [];
                        }
                        fieldInvalid['请选择行业'].push(m);
                    }
                    if( landingUrl == "" || !(/^(http\:\/\/|https\:\/\/)(.{4,})$/.test(landingUrl)) ){
                        if(!fieldInvalid['请输入正确格式的目标地址']){
                            fieldInvalid['请输入正确格式的目标地址'] = [];
                        }
                        fieldInvalid['请输入正确格式的目标地址'].push(m);
                    }
                    return !m.fileInvalid
                });

                self.fieldInvalid = Object.keys(fieldInvalid);
                return vml;
            },
            postMaterialData(){
                var self = this, postMaterialData = [];
                self.checkedMaterials.forEach(({ id, name, status, width, height, url, tradeId, landingUrl})=>{
                    let material = { name, status, landingUrl, tradeId };
                    if(id){
                        material = _.extend(material, { id });
                    }else{
                        material = _.extend(material, { width, height, url, type:1 });
                    }
                    postMaterialData.push(material)
                });
                return self.fieldInvalid.length>0?[]:postMaterialData;
            },
            isMaterialModify(){
                let self = this,
                base = {
                    tradeId: self.user.qualification.tradeId,
                    name:"",
                    landingUrl:"",
                    status:0,
                    type:1
                },
                isMaterialModify = false;
                for( let k in self.material){
                    if( base[k] != self.material[k]){
                        isMaterialModify = true;
                        break;
                    }
                }
                return isMaterialModify
            }
        },
        watch:{
            show(val){
                var self = this;
                if (val) {
                    self.showLayout();
                } else {
                    self.hideLayout();
                }
            },
            materialList(){
                var self = this;
                _.debounce(()=>self.mListIScroll&&self.mListIScroll.refresh(), 200)();
            },
            isEditAll(val){
                var self = this;
                if( self.materialList.length > 0){
                    if( self.checkedMaterials.length != self.validMaterialList.length && val){
                        self.checkedMaterials = self.validMaterialList;
                    }

                    if( self.checkedMaterials.length == self.validMaterialList.length && !val){
                        self.checkedMaterials = [];
                    }
                }
            },
            checkedMaterials(){
                var self = this;
                self.isEditAll = (self.checkedMaterials.length == self.validMaterialList.length)
                self.$validate(true)
            },
            material:{
                deep: true,
                handler(val){
                    var self = this;
                    if( self.materialList&& self.materialList.length >0 ){
                        self.materialList.forEach((m, index)=>{
                            let name = val.name==""?m.temp_name:(self.checkedMaterials.length == 1?val.name:val.name + "_" + index);
                            let landingUrl = val.landingUrl==""?m.temp_landing_url:val.landingUrl;
                            let tradeId = val.tradeId==""?m.temp_trade_id:val.tradeId;

                            self.materialList[index] = (!m.fileInvalid && self.checkedMaterials.indexOf(m)!=-1)?_.extend(m, val, { name, landingUrl, tradeId }):m;
                        });
                    }
                }
            }
        },
        methods:{
            insert(){
                var self = this;
                self.materialRes.post({ materials: self.postMaterialData })
                        .then((data)=>{
                            self.$refs.spinner.hide();
                            let {statusCode, message} = data;
                            if( statusCode == 200){
                                self.spinnerTxt = '操作成功';
                                layer.toast('操作成功');
                                setTimeout(()=>{
                                    self.$emit('add-material-success');
                                }, 200);
                            }else{
                                layer.warning('操作失败', message, 1000, function () {
                                    self.$emit('add-material-error', message);
                                });
                            }
                        });
            },
            save(){
                var self = this;

                self.materialRes.put({ materials: self.postMaterialData })
                        .then((data)=>{
                            self.$refs.spinner.hide();
                            let {statusCode, message} = data;
                            if( statusCode == 200){
                                self.spinnerTxt = '操作成功';
                                layer.toast('操作成功');
                                setTimeout(()=>{
                                    self.$emit('add-material-success');
                                }, 200);
                            }else{
                                layer.warning('操作失败', message, 1000, function () {
                                    self.$emit('add-material-error');
                                });
                            }
                        });
            },
            showLayout(){
                let self = this, body = document.body;
                self.$el.style.display = 'block';
                body.classList.add('modal-open');
                self.$el.classList.add('in');

                if(self.mListIScroll){
                    self.mListIScroll.refresh();
                }else{
                    self.mListIScroll = new IScroll( self.$el.querySelector('#m-list-body'),{
                        interactiveScrollbars: true,
                        click:true,
                        scrollX: true,
                        scrollY: true,
                        mouseWheel: true,
                        scrollbars: true,
                        probeType:3
                    });
                }

                self.$nextTick(()=>{
                    if( !self.editMode ) {
                        // 获取uploadToken
                        self.spinnerTxt = "请稍候";
                        self.$refs.spinner.show();
                        self.material.tradeId = self.user.qualification.tradeId;
                        self.getUploadToken(0, function () {
                            self.setUploader();
                            self.$refs.spinner.hide();
                        });
                    }else{
                        self.materialList.forEach((m, index)=>{
                            self.materialList[index] = _.extend(m, { 'temp_name':m.name, 'temp_landing_url': m.landingUrl, 'temp_trade_id':m.tradeId });
                        });
                        self.checkedMaterials = self.validMaterialList;
                    }
                });
            },
            hideLayout(){
                let self = this, body = document.body;
                self.$el.classList.remove('in');
                self.emptyMaterialList();
                self.material = {
                    tradeId: self.user.qualification.tradeId,
                    name:"",
                    landingUrl:"",
                    status:0,
                    type:1
                };
                if( self.multiUploader && !self.editMode ){
                    self.multiUploader.reset();
                    self.multiUploader.destroy();
                }
                self.$resetValidation();

                setTimeout(()=> {
                    self.$el.style.display = 'none';
                    body.classList.remove('modal-open');
                    // 销毁组件, 重置参数
                }, 300)
            },
            emptyMaterialList(){
                let self = this;
                self.materialList = [];
                self.checkedMaterials = [];
                if(!self.editMode) self.multiUploader.reset();
            },
            deleteMaterial(m){
                let self = this;
                self.$removeItem(self.materialList, m);
            },
            checkedMaterial:_.debounce(function (m) {
                let self = this, index = self.checkedMaterials.indexOf(m);

                if (m.fileInvalid) return;

                if (index != -1) {
                    self.checkedMaterials.splice(index, 1);
                } else {
                    self.checkedMaterials.push(m);
                }
            }, 20),
            // 保存物料
            saveCreative(){
                var self = this;
                self.$refs.spinner.show();
                if( self.editMode ){
                    self.save();
                }else {
                    self.multiUploader.upload();
                }
            },
            setUploader(){
                var self = this, uploadedCount=0;

                self.multiUploader = WebUploader.create({
                    swf: '../vendors/webuploader/Uploader.swf',
                    server: self.uploadServer,
                    pick: {
                        id:'#multi-uploader-1',
                        label: '点击选择图片'
                    },
                    formData:{
                        token: self.uploadToken.image
                    },
                    dnd:'.no-material',
                    disableGlobalDnd: true,
                    // 只允许选择图片文件。
                    accept: {
                        title: 'Images',
                        extensions: 'gif,jpg,jpeg,png',
                        mimeTypes: 'image/*'
                    },
                    fileNumLimit:10,
                    fileSingleSizeLimit: 1024 * 200,
                    compress:false,
                    resize: false
                });

                self.multiUploader.addButton({
                    id: '#multi-uploader-2',
                    label: '<i class="fa fa-picture-o"></i> 继续添加'
                });

                self.multiUploader.on('beforeFileQueued', function (file) {
                    uploadedCount = 0;
                    var promise = new Promise((resolve)=>{
                        let fileInvalid = '';
                        // 大小判断

                        self.multiUploader.makeThumb( file, function( error, base64 ) {
                            if ( error ) {
                                fileInvalid = "读取文件错误,请重新选择";
                                resolve({
                                    name: _.uniqueId('m_'),
                                    file: file,
                                    fileInvalid,
                                });
                            } else {
                                self.$loadImageAsync(base64)
                                    .then((img)=>{
                                        self.multiUploader.makeThumb( file, function( error, base64_thumb ) {
                                            if( Math.ceil(file.size/1024) > 200 ){
                                                fileInvalid = "图片超过200KB,请重新选择";
                                            }else if(imageSize.indexOf(img.width + "*" + img.height) == -1){
                                                fileInvalid = "非法的图片尺寸 " + img.width + "*" + img.height + "，请重新添加";
                                            }else{
                                                fileInvalid = ''
                                            }

                                            let _filename = file.name.substr(0, 20);
                                            resolve({
                                                name: _.uniqueId( _filename + '_') + "_" + img.width + "_" + img.height,
                                                temp_name: _.uniqueId( _filename + '_') + "_" + img.width + "_" + img.height,
                                                file: file,
                                                width: img.width,
                                                height: img.height,
                                                fileInvalid,
                                                base64,
                                                base64_thumb
                                            });
                                        }, 100, 100);
                                    });
                            }
                        }, 1, 1);

                    });

                    promise.then( material =>{
                        material = Object.assign({}, self.material, material);
                        if( self.materialList.length < 10){
                            self.materialList.push(material);
                            if( material.fileInvalid == ''){
                                self.checkedMaterials.push(material);
                            }else{
                                self.multiUploader.removeFile(file, true)
                            }
                        }else{
                            layer.toast('只能选择10个图片');
                            self.multiUploader.removeFile(file, true);
                            return false;
                        }
                    });

                });

                self.multiUploader.on('startUpload', function () {
                    self.spinnerTxt = "上传图片中...";
                });
                // 文件成功
                self.multiUploader.on('uploadSuccess', function (file, { key }) {
                    uploadedCount ++;
                    self.spinnerTxt = "上传图片中..." + uploadedCount + "/" + self.checkedMaterials.length;
                    self.checkedMaterials.forEach((m)=>{
                        if( m.file == file){
                            Vue.set(m, 'url', self.qiniuPath + key);
                        }
                    });
                });
                self.multiUploader.on('uploadFinished',()=>{
                    self.insert();
                });
                // 失败
                self.multiUploader.on('error', function (type) {
                   console.log( type  )
                });

            },
            close() {
                this.$emit('close');
            }
        },
        components:{
            spinner
        },
    }
</script>