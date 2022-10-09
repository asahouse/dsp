/**
 * 开发使用
 */
var gulp = require('gulp'),
    gulpLoadPlugins = require('gulp-load-plugins'),
    plugins = gulpLoadPlugins(),
    fs = require('fs'),
    baseConfig = require('./fontend/gulp/config'),
    modRewrite  = require('connect-modrewrite'),
    ExtractTextPlugin = require('extract-text-webpack-plugin');

    plugins.browserSync = require('browser-sync').create();
    plugins.named = require('vinyl-named');
    plugins.del = require('del');
    plugins.path = require('path');
    webpack = require('webpack');
    DefinePlugin = require('./node_modules/webpack/lib/optimize/DedupePlugin');
    plugins.pngquant = require('imagemin-pngquant');

/**
 * 开发配置
 */
var config_dev = baseConfig({
    src:'./fontend/src/',
    taskPrefix: '[dev]',
    dest:"./fontend/dev/", // 发布目录
    copyFiles:[
        './fontend/src/data/*.json',
        './fontend/src/fonts/**/*'
    ],
    webpack:{
        watch: true,
        devtool: 'inline-source-map',
        plugins:[
            new webpack.ProvidePlugin({
                jQuery: "jquery",
                $: "jquery",
            }),
            new webpack.optimize.CommonsChunkPlugin('common.js'),
            new ExtractTextPlugin('../styles/webpack.css', { allChunks: true, }),
            new webpack.OldWatchingPlugin()
        ]
    }
});

/**
 * 发布配置
 */
var config_pro = baseConfig({
    src:'./fontend/src/',
    taskPrefix: '[pro]',
    dest:"./webapp/", // 发布目录
    copyFiles:[
        './fontend/src/fonts/**/*'
    ],
    reversion:true, // reversion
    webpack:{
        plugins:[
            new webpack.ProvidePlugin({
                jQuery: "jquery",
                $: "jquery",
            }),
            new webpack.optimize.DedupePlugin(),
            new webpack.optimize.CommonsChunkPlugin('common.js'),
            new ExtractTextPlugin('../styles/webpack.css', { allChunks: true, }),
            new webpack.DefinePlugin({
                'process.env': {
                    NODE_ENV: '"production"'
                }
            }),
            new webpack.optimize.UglifyJsPlugin({
                // preserveComments:'all',
                sourceMap: false,
                drop_console: true,
                compress: {
                    warnings: false
                },
                mangle: {
                    except: [ '$', 'jQuery']
                }
            })
        ]
    }
});

var _task = require("./fontend/gulp/task"),
    develop_task = _task(gulp, plugins, config_dev);
    production_task = _task(gulp, plugins, config_pro);

var browser = function (dir, port) {
    dir = typeof dir == 'function'?config_dev.dest:dir;
    var proxy = "http://60.205.153.181",
    // var proxy = "http://localhost:8080",
        rulePath = ['v2'],
        proxyRule = [],
        option = {
            // open:false,
            reloadDebounce:1000,
            logFileChanges:false,
            reloadDelay: 0,
            notify: {      //自定制livereload 提醒条
                styles: [
                    "margin: 0",
                    "padding: 5px",
                    "position: fixed",
                    "font-size: 10px",
                    "z-index: 9999",
                    "bottom: 0px",
                    "right: 0px",
                    "border-radius: 0",
                    "border-top-left-radius: 5px",
                    "color: white",
                    "text-align: center"
                ]
            },
            startPath:'/html/index.html',
            port: port||3000,
            server: {
                baseDir: dir,
                middleware: []
            }
        };

    rulePath.forEach(function (rule) {
        proxyRule.push( '^/'+ rule +'/(.*)$ '+proxy+'/'+ rule +'/$1 [P]' );
    });

    option.server.middleware = [modRewrite(proxyRule)];
    plugins.browserSync.init(option);
    plugins.browserSync.watch(dir + '**/*', plugins.browserSync.reload);
};

browser.displayName = "browserSync";

// webServer
gulp.task('browse_dist', function () {
    browser(config_pro.dest, 9000);
});

gulp.task('browse_dev', browser);

//开发编译
gulp.task(
    'build_develop',
    gulp.series(
        develop_task.cleanDest
        ,gulp.parallel(
            develop_task.copyImg
            ,develop_task.copyFiles
            ,develop_task.copyMedia
            ,develop_task.copyFonts
            ,develop_task.copyVendors
            ,develop_task.renderLess
            ,develop_task.develop.copyHtml
        )
        ,develop_task.webpack
        ,develop_task.develop.watch,
        browser
    )
);

// 发布编译
gulp.task(
    'build_production',
    gulp.series(
        production_task.cleanDest
        ,gulp.parallel(
            production_task.copyImg
            ,production_task.copyFiles
            ,production_task.copyMedia
            ,production_task.copyFonts
            ,production_task.webpack
        )
        ,gulp.parallel(
            production_task.production.renderSrcLess
        )
        ,gulp.parallel(
            production_task.production.html
        )
        ,production_task.production.reversion
        ,production_task.production.delManifest
    )
);

