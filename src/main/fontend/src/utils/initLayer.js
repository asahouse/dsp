export default function () {
    var defaultTimer = 2000;
    var $functions = {
        /**
         * 成功消息
         *
         * @param {String} title
         * @param {String} message
         * @param {Integer} timer
         *
         * @return {boolean}
         */
        success: function(title, message, timer) {
            if( !message ){
                message = title;
                title = '温馨提示';
            }
            return layer.alert(message, {
                title:title||'提示',
                icon: 1,
                time: timer||defaultTimer
            })

        },

        /**
         * 失败消息
         *
         * @param {String} title
         * @param {String} message
         * @param {Integer} timer
         *
         * @return {boolean}
         */
        error: function(title, message, timer) {
            if( !message ){
                message = title;
                title = '出错了';
            }
            return layer.alert(message, {
                title:title,
                icon: 2,
                time: timer||defaultTimer
            })

        },

        /**
         * 警告消息
         *
         * @param {String}   title
         * @param {String}   message
         * @param {Function} callback
         * @param {String}   confirmButtonText
         * @param {Boolean}  closeOnConfirm
         * @param {Boolean}  showCancelButton
         *
         * @return {Boolean}
         */
        warning: function(title, message, timer, callback, confirmButtonText, closeOnConfirm, showCancelButton, closeOnCancel) {
            var btn = [confirmButtonText || '确定'];
            showCancelButton&&btn.push('取消');

            if( !message || typeof message == 'function' ){
                callback = message;
                message = title;
                title = '警告';
            }

            var index = layer.alert(message, {
                title:title,
                icon: 3,
                btn: btn,
                time: timer||defaultTimer,
                yes: function(index){
                    closeOnConfirm&&closeOnConfirm();
                    layer.close(index);
                },
                cancel:function (index) {
                    closeOnCancel&&closeOnCancel();
                }
            });
            setTimeout(()=>{
                callback&&callback();
            }, timer||defaultTimer)
            return index;
        },

        toast:function (message, cbk) {
            layer.msg(message, { time: defaultTimer }, cbk);
        }
    }

    layer.success = $functions.success;
    layer.warning = $functions.warning;
    layer.error = $functions.error;
    layer.toast = $functions.toast;
}