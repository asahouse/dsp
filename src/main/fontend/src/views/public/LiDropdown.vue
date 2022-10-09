<template>
    <li :class="liclass">
        <slot></slot>
        <slot name="dropdown-menu"></slot>
    </li>
</template>
<script lang="babel">
    import EventListener from '../../utils/EventListener'
    export default {
        props: ['liclass'],
        methods: {
            toggleDropdown(e) {
                e.stopPropagation();
                e.preventDefault();
                this.$el.classList.toggle('open')
            }
        },
        ready() {
            const el = this.$el
            const toggle = el.querySelector('[data-toggle="dropdown"]')
            if (toggle) {
                toggle.addEventListener('click', this.toggleDropdown)
            }
            this._closeEvent = EventListener.listen(window, 'click', (e)=> {
                if (!el.contains(e.target) || e.target.nodeName.toLowerCase() == 'a') el.classList.remove('open')
            })
        },
        beforeDestroy() {
            if (this._closeEvent) this._closeEvent.remove()
        }
    }
</script>
