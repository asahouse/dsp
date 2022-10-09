<style scoped>
    .magic-checkbox+label, .magic-radio+label{
        padding-left: 14px;
        height: 14px;
        display: inline-block;
        top: 2px;
    }
    .magic-checkbox+label:before, .magic-radio+label:before{
        width: 14px;
        height: 14px;
    }
    .magic-checkbox+label:after{
        top: 0;
        left: 5px;
        width: 5px;
        height: 10px;
        border-width: 1px;
    }
    .td-inner{
        border:1px solid #f4f4f4;
        border-top:0;
        border-left: 0;
        height: 32px;
        padding: 8px 5px;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
    }
</style>

<template>
    <td :style="column.style">

        <template v-if="column.checkbox">
            <div class="td-inner" :style="{ width: column.style.width }">
                <input :id="'data_'+dataIdx+'_'+data.id" type="checkbox" class="magic-checkbox" v-model="$parent.checkItems" :value="data" >
                <label></label>
            </div>
        </template>
        <template v-else>
            <template v-if="!!column.template">
                <div class="td-inner"
                     :style="{ width: column.style.width }"
                     v-html="(column.template.bind(this))(dataIdx, data)"></div>
            </template>
            <template v-else>
                <div class="td-inner" :style="{ width: column.style.width }">
                    {{data[column.field] || $parent.options.undefinedText}}
                </div>
            </template>
        </template>
    </td>
</template>

<script lang="babel">
    import EventListener from '../../utils/EventListener'

    export default{
        props:{
            column:Object,
            data:Object,
            dataIdx:Number,
            gridData:Array
        },
        ready() {
            var self = this;
            self.$nextTick(()=>{
                if( self.column.template ){
                    self.bindEvent();
                }
            });
        },
        methods:{
            "bindEvent":function() {
                var self = this;

                const $el = $(self.$el);

                var actm;
                $el.delegate('[v-action]', "click", function (e) {
                    e.stopPropagation();
                    e.preventDefault();

                    if(actm) clearTimeout(actm);
                    actm = setTimeout(()=>{
                        var action = $(this).attr('v-action');
                        if(action && action != ""){
                            self.$parent.callActions(action, self.data);
                            $el.find('.open').removeClass('open');
                        }
                    }, 20);
                });

                const $dropdown = $el.find('[data-toggle="dropdown"]');
                if( $dropdown.length >0 ){

                    $dropdown.parents('td').css({ 'overflow':'visible' });
                    $el.find('.td-inner').css({ 'overflow':'visible' });

                    var dtm;
                    $el.delegate('[data-toggle="dropdown"]', "click", function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        var _this = $(this);
                        if( dtm ) clearTimeout(dtm);
                        dtm = setTimeout(()=>{
                            _this.parent().toggleClass('open');
                            _this.parents('tr').siblings().find('.open').removeClass('open');
                        },10);
                    });


                }

                $(document).on('click', function (e) {
                    try{
                        if( !self.$el.contains(e.target) ){
                            $el.find('.open').removeClass('open');
                        }
                    }catch (e){}
                });
            }
        },
        beforeDestroy() {
            if (this._closeEvent) this._closeEvent.remove()
        }
    }
</script>