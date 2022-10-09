

/**
 * 深度克隆
 * 官方注释说比 JSON.parse(JSON.stringify(obj)) 快
 */
export const deepClone = function(obj) {
    if (Array.isArray(obj)) {
        return obj.map(deepClone)
    } else if (obj && typeof obj === 'object') {
        var cloned = {}
        var keys = Object.keys(obj)
        for (var i = 0, l = keys.length; i < l; i++) {
            var key = keys[i]
            cloned[key] = deepClone(obj[key])
        }
        return cloned
    } else {
        return obj
    }
}


export const loadImageAsync = function(url) {
    return new Promise(function(resolve, reject) {
        var image = new Image();

        image.onload = function() {
            resolve(image);
        };

        image.onerror = function() {
            reject(new Error('Could not load image at ' + url));
        };

        image.src = url;
    });
}

export const removeItem = function (arr, item) {
    if( arr.length > 0){
        var index = arr.indexOf(item);
        return arr.splice(index, 1);
    }
}

export const toHourTime = function (seconds) {
    let d = Math.floor( seconds/(60*60*24) ),
        h = Math.floor( seconds/(60*60) - d*24 ),
        m = Math.floor( seconds/(60) - d*24*60 - h*60 ),
        s = Math.ceil( seconds - d*24*60*60 - h*60*60 - m*60 );
    return seconds>0?( ( d>0?d+'天 ':'' ) + ( h>0?h+'小时 ':'' ) + ( m>0?m+'分钟 ':'' )  + s+'秒 ' ): '-';
}