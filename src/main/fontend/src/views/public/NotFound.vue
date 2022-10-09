<style>
    .error-content h2{
        font-size:48px;
    }
</style>
<template>
    <section class="content">
        <div class="error-page text-center">
            <h1>404</h1>
            <h3>你所访问的页面不存在.</h3>
            <hr/>
            <p>资源不存在或者没有访问权限 <a v-link="'/advertisers/index'">返回首页</a> 或 <a v-link="{path:'/login'}">登录</a></p>
            <!-- /.error-content -->
        </div>
        <!-- /.error-page -->
    </section>
</template>