
const apiUrl = "http://2016culture.digi-campaign.com/tj/index.php";
const basePath = "/v2/admin/analysis/";


const siteData = (data)=>{
    return $.ajax({
        url: apiUrl,
        async: false,
        data:Object.assign({a:"site-data"}, data),
        dataType: "jsonp",
        // jsonpCallback: "cbk",
        success({header}){
            if( header.status !== 0){
                layer.msg(header.failures[0].message, {time: 3000});
            }
        }
    });
};


export default {
    siteList(){
        return $.ajax({
            url: basePath + "baidu-sites",
            dataType: "json",
        });
    },
    overview(request){
        return $.ajax({
            type: 'post',
            url: basePath + "baidu-datas",
            contentType: "application/json",
            data: JSON.stringify(request),
            dataType: "json"
        });
    },
    data(data){
        return $.ajax({
            type: 'post',
            url: basePath + "baidu-data",
            contentType: "application/json",
            data: JSON.stringify(data),
            dataType: "json"
        });
    },
    datas(requisite, requests){
        return $.ajax({
            type: 'post',
            url: basePath + "baidu-datas",
            contentType: "application/json",
            data: JSON.stringify({
                requisite, requests
            }),
            dataType: "json"
        });
    }
    /*overview:{
        getTimeTrendRpt(siteId, start_date, end_date, metrics, start_date2, end_date2){
            return siteData({
                siteId,
                method:'overview/getTimeTrendRpt',
                start_date,
                end_date,
                start_date2,
                end_date2,
                metrics
            });
        }
    }*/

}