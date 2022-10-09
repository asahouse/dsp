/**
 * 可以使用 Vue.validator 方法注册自定义验证器。
 */

exports.email = function (val) {
    return /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(val)
}

exports.url = function (val) {
    return /^(http\:\/\/|https\:\/\/)(.{4,})$/.test(val)
}

exports.numeric = function (val/*,rule*/) {
    return /^[-+]?[0-9]+$/.test(val)
}

exports.strAndNum = function (val/*,rule*/) {
    var valid = true;
    if( /[A-Za-z0-9]/.test(val) ){

    }else{
        valid = false;
    }
    return valid
}

exports.confirm = function (val, target) {
    return val === target
}