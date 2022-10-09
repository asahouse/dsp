<template>
    <div class="btn-group">
        <slot></slot>
        <slot name="dropdown-menu"></slot>
    </div>
</template>
<script lang="babel">
    import EventListener from '../../utils/EventListener'
    import _ from 'lodash'

    export default {
        methods: {
            close(e){
                e&&e.preventDefault();
                this.$el.classList.remove('open')
            },
            toggleDropdown(e) {
                e&&e.preventDefault();
                this.$el.classList.toggle('open')
            }
        },
        ready() {
            const el = this.$el
            const toggle = el.querySelector('[data-toggle="dropdown"]')
            const toggleFn = _.debounce( this.toggleDropdown, 20 );
            if (toggle)
            {
                toggle.style.borderRadius = '4px'
                toggle.addEventListener('click', function (e) {
                    toggleFn(e);
                })
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