

<template>
    <section class="content-header">
        <h1>
            {{title}}
            <small>{{subTitle}}</small>
        </h1>
        <slot>
            <ol class="breadcrumb" v-if="breadcrumb">
                <li v-for="bc in breadcrumb"><a v-link="bc.link"><i v-if="bc.icon" :class="bc.icon"></i> {{bc.title}}</a></li>
                <li class="active">{{title}}</li>
            </ol>
        </slot>
    </section>
</template>

<script>
    export default{
        props:['title', 'subTitle', 'breadcrumb']
    }
</script>