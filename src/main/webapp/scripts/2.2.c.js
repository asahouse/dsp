webpackJsonp([2,23],{93:function(e,t,o){var s,i,c={};s=o(94),i=o(98),e.exports=s||{},e.exports.__esModule&&(e.exports=e.exports.default);var r="function"==typeof e.exports?e.exports.options||(e.exports.options={}):e.exports;i&&(r.template=i),r.computed||(r.computed={}),Object.keys(c).forEach(function(e){var t=c[e];r.computed[e]=function(){return t}})},94:function(e,t,o){"use strict";function s(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var i=o(95),c=s(i);t.default={data:function(){return{bgs:["bg-aqua","bg-green","bg-yellow","bg-red"]}},vuex:{getters:{sidebarData:function(e){var t=e.system;return t.sidebar}}},components:{contHeader:c.default}}},95:function(e,t,o){var s,i,c={};s=o(96),i=o(97),e.exports=s||{},e.exports.__esModule&&(e.exports=e.exports.default);var r="function"==typeof e.exports?e.exports.options||(e.exports.options={}):e.exports;i&&(r.template=i),r.computed||(r.computed={}),Object.keys(c).forEach(function(e){var t=c[e];r.computed[e]=function(){return t}})},96:function(e,t){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.default={props:["title","subTitle","breadcrumb"]}},97:function(e,t){e.exports=' <section class=content-header> <h1> {{title}} <small>{{subTitle}}</small> </h1> <slot> <ol class=breadcrumb v-if=breadcrumb> <li v-for="bc in breadcrumb"><a v-link=bc.link><i v-if=bc.icon :class=bc.icon></i> {{bc.title}}</a></li> <li class=active>{{title}}</li> </ol> </slot> </section> '},98:function(e,t){e.exports=' <cont-header title=控制台 sub-title="control panel"></cont-header> <section class=content> <div class=row> <div v-for="item in sidebarData[0][\'children\']" class="col-lg-2 col-md-3 col-sm-4 col-xs-6"> <div :class="\'small-box \' + bgs[$index]"> <div class=inner> <h3>{{item.name}}</h3> <p>{{item.subName}}</p> </div> <div class=icon> <i :class=item.icon></i> </div> <a v-link=item.link class=small-box-footer>查看更多 <i class="fa fa-arrow-circle-right"></i></a> </div> </div> </div> </section> '}});