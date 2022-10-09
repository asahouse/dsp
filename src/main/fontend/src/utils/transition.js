/**
 *  通过 Vue.js 的过渡系统，可以在元素从 DOM 中插入或移除时自动应用过渡效果。Vue.js 会在适当的时机为你触发 CSS 过渡或动画
 *  为了应用过渡效果，需要在目标元素上使用 transition 特性：<div v-if="show" transition="my-transition"></div>
 */


exports.bounce = {
    enterClass:"bounceInLeft",
    leaveClass:"bounceOutRight",
    type: 'animation'
}

exports.flip = {
    enterClass:"flipInX",
    leaveClass:"flipOutX",
    type: 'animation'
}

exports.roll = {
    enterClass:"rollIn",
    leaveClass:"rollOut",
    type: 'animation'
}

exports.fadeLR = {
    enterClass:"fadeInLeftBig",
    leaveClass:"fadeOutRightBig",
    type: 'animation'
}

exports.fade = {
    enterClass:"fadeIn",
    leaveClass:"fadeOut",
    type: 'animation'
}