import { API_ROOT } from "../config/index"

const request = function (option) {
    option = typeof option=='string'?{ url:option }:option;
    let ajaxSetting = Object.assign({
        url:"",
        dataType:'json',
        data:{},
        success:function ({ statusCode, message}) {
            layer.closeAll();
            if( statusCode != 200){
                if( statusCode == 401 ){
                    layer.msg('请登录');
                    setTimeout(()=>{
                        window.router.go('/login');
                    }, 1000)
                }else{
                    layer.msg('出错了：' + message);
                    console.log('path:'+ ajaxSetting.url +', error:' + statusCode + ', message:' + message)
                }
            }
        }
    },option);
    ajaxSetting.url = API_ROOT + ajaxSetting.url;

    const doAjax = (method, data={}, opt)=>{
        opt = opt||{};
        let options = Object.assign({}, ajaxSetting, opt);
        options.method = method;

        if( !options.contentType ){
            if( method.toLowerCase() == 'get'){
                options.data = data;
            }else{
                options.contentType = "application/json";
                options.data = JSON.stringify(data);
            }
        }else if(options.contentType&&options.contentType == "application/json"){
            options.data = JSON.stringify(data);
        }else{
            options.data = data;
        }
        if( method.toLowerCase() != 'get') layer.msg('加载中...', { time:1000*10 });
        return $.ajax(options)
    }
    return {
        ajaxSetting,
        get: (data, opt)=>doAjax('GET', data, opt),
        post: (data, opt)=>doAjax('POST', data, opt),
        put: (data, opt)=>doAjax('PUT', data, opt),
        delete: (data, opt)=>doAjax('DELETE', data, opt)
    };
}

export default {

    // 后台资源
    admin:{
        advertiser(){
            let methods = request('admin/advertiser');
            // 获取一个广告主
            methods.getOne = (id) => methods.get({}, {url: methods.ajaxSetting.url + '/' + id });
            return methods;
        },
        review:{
            audit:()=>request('admin/material/audit')
        },
        campaign(advId){
            let methods = request(`admin/campaign?advId=${advId}`);
            methods.getOne = (id) => methods.get({}, { url: `${API_ROOT}campaign/${id}?advId=${advId}` });
            methods.getAll = (params) => methods.get(params, { url: `${API_ROOT}admin/campaign` });
            return methods;
        },
        // *当天消费金额, 百度的有时间差, 并不实时, 需要连通RTB数据才适合输出当天消费金额
        dashboard: ()=> request('admin/dashboard'),

        report:{
            /**
             * rtp 竞价报告
             */
            rtbReport:()=>request(`admin/report/rtbReport`),
            /**
             * 消费报告
             */
            consumeReport:()=>request(`admin/report/consumeReport`),
            /**
             * 广告主报告
             */
            advertiserReport:()=>request(`admin/report/advertiserReport`),
            /**
             * RTB分创意
             */
            creativeRTBReport:()=>request(`admin/report/creativeRTBReport`),
        }
    },


    advertisers:{
        creatives:{
            group(advId){
                let methods = request(`creatives/group?advId=${advId}`);
                methods.getOne = (id) => methods.get({}, { url: `${API_ROOT}creatives/group/${id}?advId=${advId}` });
                return methods;
            },

            material(advId, gId){
                let methods = request(`creatives/material?advId=${advId}&cId=${gId}`);
                methods.getOne = (id) => methods.get({}, { url: `${API_ROOT}creatives/material/${id}?advId=${advId}` });
                return methods;
            },

            upload:{
                token:()=>request(`creatives/upload/token`)
            },

        },

        campaign(advId){
            let methods = request(`campaign?advId=${advId}`);
            // 获取一个组
            methods.getOne = (id) => methods.get({}, { url: `${API_ROOT}campaign/${id}?advId=${advId}` });
            return methods;
        }
    },

    category:{
        all:()=>request('category/all'),
        tag:()=>request('category/tag'),
    },

    login:()=>request('login')

}