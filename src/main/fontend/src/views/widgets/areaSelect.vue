<template>
    <div>
        <div class="nc_areas clearfix" v-el:nc-area>
            <div class="pull-left nc_areas_item"
                 v-for="item in areaTreeData | orderBy 'sort'"
                 :class="{'nc_areas_menu':item.children, 'unCheckAll': item.status==2}"
                 @keyup.esc="escAreaItem"
            >
                <input id="atd_{{item.code}}" class="magic-checkbox" type="checkbox" v-model="item.checked">
                <label @click.prevent="checkProvince($event,item)">{{item.name}}</label>
                <template v-if="item.children">
                    <div class="nc_areas_m"
                         @click="toggleAreaItem($event, item)">
                        <i class="fa fa-ellipsis-v"></i>
                    </div>
                    <div class="nc_area_citys">
                        <div class="nc_area_c" v-for="c in item.children">
                            <input id="atd_{{c.code}}" class="magic-checkbox" type="checkbox"
                                   v-model="c.checked">
                            <label @click="checkCity(item, c)">{{c.name}}</label>
                        </div>
                    </div>
                </template>
                <template v-else>
                    <div class="nc_areas_m">
                        <i class="fa fa-ellipsis-v"></i>
                    </div>
                </template>
            </div>
        </div>
        <div class="nc_locations">
            <table class="table table-condensed table-bordered table-hover text-center">
                <thead>
                <tr>
                    <th style="width: 40px;"></th>
                    <th style="width: 60px;">代码</th>
                    <th class="text-left">位置</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="lo in locations">
                    <td>
                        <button class="btn btn-default btn-xs" type="button" @click="removeLocation($event, lo)"><i class="fa fa-times"></i></button>
                    </td>
                    <td>{{lo.code}}</td>
                    <td class="text-left">{{lo.name}}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <button class="btn btn-default btn-sm" type="button" @click="clearAllLocations">清除所选</button>
    </div>
</template>

<script lang="babel">
    import areaData from "../../data/areaCode";

    var areaTreeData = [], cArr = [];
    areaData.forEach((item)=>{
        if(item[0].lastIndexOf("0000") == 2){
            areaTreeData.push({
                "name":item[1],
                "code":item[0],
                "checked": false,
                "sort":0,
                "status":0
            });
        }else if(item[0].lastIndexOf("00") == 4){
            cArr.push({
                "name":item[1],
                "code":item[0],
                "checked": false
            });
        }
    });
    cArr.forEach((c) => {
        areaTreeData.forEach((p,idx)=>{
            if(c.code.substr(0,2) == p.code.substr(0,2)){
                if(!p["children"]){
                    p["children"] = []
                }
                c.parentCode = p.code;
                areaTreeData[idx]['sort'] = 1;
                areaTreeData[idx]["children"].push(c);
            }
        });
    });


    export default{
        props:['selected'],
        data(){
            return{
                areaTreeData,
                locationMenuCode:0
            }
        },
        methods:{
            // 捕获esc事件
            escAreaItem(e){
                $(e.target).find('.nc_areas_m').trigger('click');
            },
            // 展示城市
            toggleAreaItem(e, item){
                e.preventDefault();
                var self = this;
                self.locationMenuCode = self.locationMenuCode==item.code?-1:item.code;
            },
            // 选中省
            checkProvince(e,item){
                e&&e.stopPropagation();
                e&&e.preventDefault();

                item.checked = !item.checked;
                if(item.checked){
                    item.status = 1;
                    if( item.children && item.children.length > 0 ){
                        item.children.forEach((c)=>{
                            c.checked = true;
                    });
                    }
                }else{
                    item.status = 0;
                    if( item.children && item.children.length > 0 ){
                        item.children.forEach((c)=>{
                            c.checked = false;
                    });
                    }
                }
            },
            // 选中市
            checkCity(parent, child){
                child.checked = !child.checked;
                var ccd = 0;
                if( parent.children && parent.children.length > 0 ){
                    parent.children.forEach((c)=>{
                        if(c.checked) ccd++;
                    });
                }
                if( parent.children && ccd > 0){
                    if( ccd == parent.children.length){
                        parent.status = 1;
                        parent.checked = true;
                    }else{
                        parent.status = 2;
                        parent.checked = true;
                    }
                }else{
                    parent.status = 0;
                    parent.checked = false
                }
            },
            // 清除位置
            clearAllLocations(e){
                e.preventDefault();
                var self = this;
                self.areaTreeData.forEach((item)=>{
                    if(item.checked) self.checkProvince(e,item);
                });
            },
            // 删除已选位置
            removeLocation(e, location){
                e.preventDefault();
                var self = this;
                if( !location.checked ) return;

                if( location.parentCode ){
                    self.areaTreeData.forEach((item)=>{
                        if( item.code == location.parentCode){
                            self.checkCity(item, location);
                        }
                    });
                }else{
                    self.checkProvince(e, location);
                }
            },
            reset() {
                var self = this;

                self.areaTreeData.forEach((item)=>{
                    item.checked = false;
                    if( item.children && item.children.length > 0){
                        item.children.forEach((ci)=>{
                            ci.checked = false;
                        })
                    }
                })

                self.selected.forEach((item)=>{
                    if( item.parentCode ){
                        self.areaTreeData.forEach((item2)=>{
                            if( item2.code == item.parentCode){
                                self.checkCity(item2, item);
                            }
                        });
                    }else{
                        self.checkProvince(false, item);
                    }
                });
            }
        },
        ready(){
            var self = this;

            // 点击出现子菜单
            $(self.$els.ncArea).delegate('.nc_areas_m', 'click', function (e) {
                e.preventDefault();
                e.stopPropagation();
                $(this).parent()
                        .toggleClass('active')
                        .siblings()
                        .removeClass('active');
            });
            $(self.$el).on('click', function (e) {
                if( e.target.tagName !== 'LABEL'){
                    $(this).find('.nc_areas_menu.active').removeClass('active');
                }
            });
        },
        computed:{
            locations(){
                var self = this, locationItems = [];
                self.areaTreeData.forEach((item)=>{
                    if( item.checked ){
                        if( item.status == 1){
                            locationItems.push(item);
                        }
                        if( item.status == 2){
                            if(item.children){
                                item.children.forEach((c)=>{
                                    if( c.checked ){
                                        locationItems.push(c);
                                    }
                                });
                            }
                        }
                    }
                });
                self.$emit('selected-locations', locationItems);
                return locationItems;
            }
        },
    }
</script>