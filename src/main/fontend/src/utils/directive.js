import DateRangePicker from 'bootstrap-daterangepicker'
import 'select2'
import 'select2/dist/css/select2.min.css'

$.fn.daterangepicker = function(options, callback) {
    this.each(function() {
        var el = $(this);
        if (el.data('daterangepicker'))
            el.data('daterangepicker').remove();
        el.data('daterangepicker', new DateRangePicker(el, options, callback));
    });
    return this;
};

exports.select2 = {
    twoWay: true,
    priority: 1000,
    bind() {
        let self = this;
        $(self.el).select2().change(function (e) {
            self.set($(this).val())
        });
    },
    update: function (value) {
        if( value ){
            $(this.el).val(value);
        }
        $(this.el).trigger('change')
    },
    unbind: function () {
        $(this.el).off().select2('destroy')
    }
}

exports.dateRangePicker = {
    twoWay: true,
    params:['options'],
    bind() {
        let self = this,
            FORMAT = "YYYY-MM-DD",
            options = Object.assign({
                "locale": {
                    "format": FORMAT,
                    "separator": " ~ ",
                    "applyLabel": "确定",
                    "cancelLabel": "取消",
                    "fromLabel": "从",
                    "toLabel": "到",
                    "customRangeLabel": "Custom",
                    "weekLabel": "W",
                    "daysOfWeek": ["日","一","二","三","四","五","六"],
                    "monthNames": ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],
                    "firstDay": 1
                }
            }, self.params.options);
        $(self.el).daterangepicker( options, function(start, end) {
            self.set({ startDate:start.format(FORMAT), endDate:end.format(FORMAT) });
        })
    },
    update: function ({ startDate, endDate}) {
        let daterangepicker = $(this.el)
            .val(startDate + " ~ " + endDate)
            .data('daterangepicker');

        daterangepicker.setStartDate(startDate);
        daterangepicker.setEndDate(endDate);
    },
    unbind: function () {
        $(this.el).off().daterangepicker('destroy')
    }
}