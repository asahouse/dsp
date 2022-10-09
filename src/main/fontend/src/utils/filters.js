/**
 * 自定义过滤器
 */

// import tradeData from '../data/tradeData'

/**
 *  格式化文章發布時間
 */
exports.customTime = item => {
    let nowTime = new Date().getTime();
    let minuteTime = 60*1000;
    let hourTime = 60*minuteTime;
    let dayTime = 24*hourTime;
    let monthTime = dayTime * 30;
    let yearTime = monthTime * 12;

    let publishTime = new Date(item).getTime();
    let historyTime = parseInt(nowTime) - parseInt(publishTime);
    let descTime;
    if(historyTime >= yearTime){
        //按年算
        descTime = parseInt(historyTime/yearTime) + '年前';
    }else if(historyTime< yearTime && historyTime >= monthTime){
        //按月算
        descTime = parseInt(historyTime/monthTime) + '月前';
    }else if(historyTime< monthTime && historyTime>= dayTime){
        //按天算
        descTime = parseInt(historyTime/dayTime) + '天前';
    }else if(historyTime< dayTime && historyTime>= hourTime){
        //按小时算
        descTime = parseInt(historyTime/hourTime) + '小时前';
    }else if(historyTime< hourTime && historyTime>= minuteTime){
        //按分钟算
        descTime = parseInt(historyTime/minuteTime) + '分钟前';
    }else{
        descTime = '刚刚';
    }
    return descTime;
}
/**
 * 格式时间
 * @param time
 * @returns {string}
 */
exports.formatDate =  time => {
    let tmpDate = new Date(time)
    let year = tmpDate.getFullYear()
    let mathon = tmpDate.getMonth() + 1
    let day = tmpDate.getDate()
    let hours = tmpDate.getHours()
    let minutes = tmpDate.getMinutes()
    return year + '-' + mathon + '-' + day + ' ' + hours + ':' + minutes
}

exports.currencyDisplay = {
    // model -> view
    // 在更新 `<input>` 元素之前格式化值
    read: function(val) {
        return val?val.toFixed(2):0.00
    },
    // view -> model
    // 在写回数据之前格式化值
    write: function(val, oldVal) {
        var number = +val.replace(/[^\d.]/g, '')
        return isNaN(number) ? 0 : parseFloat(number.toFixed(2))
    }
}

/*
exports.getTradeName = tradeId => {
    let trade = tradeData.filter(({id})=>tradeId==id);
    return (trade[0]&&trade[0].name)?trade[0].name:"";
}*/
