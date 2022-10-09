<style>

</style>

<template>
    <div class="tab-panel">
        <div class="tab-bar clearfix">
            <div class="x-tab" v-for="tab in tabs" :class="{'active': currentIndex>=$index}" @click="activeTabs($event, $index)">
                <span class="tab-inner" v-html="tab.title"></span>
            </div>
        </div>
        <div class="tab-scroll-box" :style="{'height':contentHeight+'px'}" v-el:tab-scroll-box>
            <div class="tab-content">
                <slot></slot>
                <section class="content" v-for="tab in tabs" :class="{'hidden': currentIndex!=$index }" :resize="resize">
                    <slot :name="$key">{{$key}}</slot>
                </section>
            </div>
        </div>
    </div>
</template>

<script lang="babel">

    export default{
        props:{
            tabs:{
                type:Object
            },
            contentHeight:{
                type:Number
            }
        },
        data(){
            return{
                contScroll:{},
                currentIndex:0
            }
        },
        ready(){
            var self = this;

            self.contScroll = new IScroll( self.$els.tabScrollBox,{
                interactiveScrollbars: true,
                click: true,
                scrollX: true,
                scrollY: true,
                mouseWheel: true,
                scrollbars: true,
//                probeType:3
            });

            self.$nextTick(()=>{
                setTimeout(()=>{
                    self.contScroll.refresh();
                }, 200);
            });

            self.$nextTick(function () {
                $(self.$el).find('section.content').on('click', function () {
                    self.$nextTick(()=>{
                        setTimeout(()=>{
                            self.contScroll.refresh();
                        }, 200);
                    });
                });
            });

            if( self.$route.query.tabIdx && self.$route.query.tabIdx > 0){
                self.currentIndex = self.$route.query.tabIdx;
            }
        },
        watch:{
            currentIndex:function (v) {
                this.$emit('index-change', v);
            }
        },
        methods:{
            activeTabs(e, index){
                e.preventDefault();
                var self = this;
                self.currentIndex = index;
                self.$router.go({ query:{ tabIdx:self.currentIndex } });
                self.$nextTick(()=>{
                    self.contScroll.refresh();
                });
            },
            nextStep(e){
                e.preventDefault();
                var self = this;
                if( self.currentIndex < Object.keys(self.tabs).length ){
                    self.activeTabs(e, self.currentIndex+1);
                }
            },
            prevStep(e){
                e.preventDefault();
                var self = this;
                if( self.currentIndex >= 0 ){
                    self.activeTabs(e, self.currentIndex-1);
                }
            }
        }
    }
</script>