/**
 * 任务列表
 */

var util = require('./util');


module.exports = function (gulp, plugins, config) {

    var copyHandler = function (file, dest, base) {
        var gulp_src;
        if( base == ''){
            gulp_src = gulp.src(file);
        }else if( base ){
            gulp_src = gulp.src(file, { base: base })
        }else{
            gulp_src = gulp.src(file, { base: config.src })
        }
        return gulp_src.pipe(gulp.dest( dest ));
    };

    var watchHandler = function (type, file) {


        // var target = file.match(/^fontend\/src[\/|\\](.*?)[\/|\\]/)[1];

        var paths = ['less','images','scripts','vendors','media', 'html'], target;

        paths.forEach(function (p) {
            if( file.indexOf( p ) !== -1 && file.indexOf('tmp__') == -1 ){
                target = p;
            }
        });

        util.log(target);

        switch (target) {
            case 'less':
                task.renderLess();
                util.log('render less');
                break;
            case 'images':
                if (type === 'removed') {
                    var tmp = file.replace(/src/, 'dev');
                    plugins.del([tmp]);
                } else {
                    task.copyImg();
                }
                util.log('copy images');
                break;
            case 'vendors':
                if (type === 'removed') {
                    var tmp = file.replace('src', 'dev');
                    plugins.del([tmp]);
                } else {
                    task.copyVendors();
                }
                util.log('copy vendors');
                break;
            case 'media':
                if (type === 'removed') {
                    var tmp = file.replace('src', 'dev');
                    plugins.del([tmp]);
                } else {
                    task.copyMedia();
                }
                util.log('copy media');
                break;
            case 'html':
                if (type === 'removed') {
                    var tmp = file.replace('src', 'dev');
                    plugins.del([tmp]);
                } else {
                    task.develop.copyHtml()
                }
                util.log('copy html');
                break;
        }
    };

    var develop_task = {
        copyHtml:function () {
            return copyHandler(config.src + 'html/*.html', config.dest + "html/", config.src + 'html/');
        },
        watch:function (cb) {
            //监听文件
            var watcher = gulp.watch(
                [config.src + "**/*"],
                {ignored: /[\/\\]\./}
            );

            watcher
                .on('change', function (file) {
                    util.log(file + ' has been changed');
                    watchHandler('changed', file);
                })
                .on('add', function (file) {
                    util.log(file + ' has been added');
                    watchHandler('add', file);
                })
                .on('unlink', function (file) {
                    util.log(file + ' is deleted');
                    watchHandler('removed', file);
                });

            cb();
        }
    };


    var production_task = {
        renderSrcLess:function (cbk) {
            return gulp.src([ config.src + 'less/' + config.less.fileName, config.dest + 'styles/*' ])
                .pipe(
                    plugins.if('*.less', plugins.less() )
                )
                .pipe(
                    plugins.autoprefixer({
                        browsers: ["> 0%"], // 主流浏览器的最新两个版本
                        cascade: false // 是否美化属性值
                    })
                )
                .pipe(gulp.dest(config.src + "styles/"));
        },
        html:function () {
            return gulp.src( config.src + 'html/*.html',　{ base:config.src + 'html/'} )
                .pipe( plugins.useref() )
                .pipe( plugins.if('*.html', plugins.htmlMinifier({collapseWhitespace: true}) ) )
                .pipe( plugins.if('*.css', plugins.cssnano({
                    safe: true,
                    reduceTransforms: false,
                    advanced: false,
                    compatibility: 'ie7',
                    keepSpecialComments: 0
                }) ) )
                .pipe( plugins.if('*.js', plugins.uglify()) )
                .pipe( gulp.dest( config.dest + "html/" ) )
                .on('end', function (done) {
                    util.log('delete src styles');
                    return plugins.del([config.src + '/styles', config.dest + '/styles/webpack.css'], done);
                });
        },
        reversion:function (cb) {
            var revAll = new plugins.revAll({
                fileNameManifest: 'manifest.json',
                dontRenameFile: ['.html', '.php'],
                transformFilename: function (file, hash) {
                    var filename = plugins.path.basename(file.path);
                    var ext = plugins.path.extname(file.path);

                    if (/^\d+\..*\.js$/.test(filename)) {
                        return filename;
                    } else {
                        return plugins.path.basename(file.path, ext) + '.' + hash.substr(0, 8) + ext;
                    }

                }
            });

            if (config['reversion']) {
                return gulp.src([
                    config.dest + '/**/*',
                    '!' + config.dest + '/WEB-INF',
                    '!' + config.dest + '/WEB-INF/*',
                    // '!' + config.dest + '/fonts/',
                    // '!' + config.dest + '/fonts/*'
                    ])
                    .pipe( revAll.revision() )
                    .pipe( gulp.dest( config.dest ) )
                    .pipe(
                        plugins.revDeleteOriginal({
                            exclude: /(.html|.htm|.c.js)$/
                        })
                    )
                    .pipe( revAll.manifestFile() )
                    .pipe( gulp.dest( config.dest ) );
            } else {
                cb();
            }
        },
        delManifest:function (done) {
            return plugins.del([config.dest + '/manifest.json'], done);
        }
    };

    var task = {
        cleanDest:function (done) {
            return plugins.del([
                config.dest + '/**/*',
                '!' + config.dest + '/WEB-INF',
                '!' + config.dest + '/WEB-INF/*'
            ], done);
        },
        copyFiles:function (done) {
            if( config.copyFiles.length > 0 ){
                return copyHandler( config.copyFiles, config.dest );
            }else{
                done();
            }
        },
        copyFonts:function () {
            return copyHandler( config.fontfiles, config.dest + 'fonts/', '' );
        },
        copyImg:function () {
            return copyHandler( config.src + config.paths.img, config.dest );
        },
        copyMedia:function () {
            return copyHandler( config.src + config.paths.media, config.dest );
        },
        copyVendors:function () {
            return copyHandler( config.src + config.paths.vendors, config.dest );
        },
        imagesMin:function () {
            return gulp.src( config.src + 'images/*.{png,jpg,gif,svg}' ,{ base: config.src })
                .pipe(plugins.imagemin({
                    optimizationLevel: 5, //类型：Number  默认：3  取值范围：0-7（优化等级）
                    progressive: true, //类型：Boolean 默认：false 无损压缩jpg图片
                    use: [plugins.pngquant()]
                }))
                .pipe(gulp.dest( config.dest));
        },
        renderLess:function () {
            return gulp.src( config.src + 'less/' + config.less.fileName )
                .pipe(plugins.less())
                .pipe(
                    plugins.autoprefixer({
                        browsers: ["> 0%"], // 主流浏览器的最新两个版本
                        cascade: false // 是否美化属性值
                    })
                )
                .pipe(gulp.dest(config.dest + "styles/"));
        },
        webpack:function (cbk) {

            var wpHandle = gulp.src( config.mapFiles )
                .pipe( plugins.named())
                .pipe( plugins.webpack(config.webpack) )
                .pipe( gulp.dest( config.dest + 'scripts' ) );

            if (config['reversion']) {
                return wpHandle;
            }else{
                cbk();
            }
        },
        develop:develop_task,
        production:production_task
    };

    // 配置展示名称
    function setDisplayName(task) {
        for( var k in task){
            if( typeof task[k] === 'function'){
                task[k].displayName = k;
            }else if(typeof task[k] === 'object'){
                setDisplayName(task[k]);
            }
        }
    }
    setDisplayName(task);

    return task;
};