<template>
    <div id="app-root">
        <router-view></router-view>
    </div>
</template>

<script lang="babel">
    import store from '../../vuex/store';

    export default {
        store
    }
</script>