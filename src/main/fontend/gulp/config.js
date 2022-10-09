var ExtractTextPlugin = require('extract-text-webpack-plugin');

// gulp 全局配置
module.exports = function (opt) {
    var config = {

        // 入口文件以及所在路径名称
        appList:['entry/app'],

        copyFiles:[],

        paths:{
            img: 'images/**/*',
            vendors: 'vendors/**/*',
            media: 'media/**/*',
        },

        // 需要拷贝的字体
        fontfiles:[
            './node_modules/bootstrap/dist/fonts/*.{otf,eot,svg,ttf,woff,woff2}',
            './node_modules/font-awesome/fonts/*.{otf,eot,svg,ttf,woff,woff2}',
            './src/vendors/ionicons-2.0.1/fonts/*.{otf,eot,svg,ttf,woff,woff2}',
        ],

        // less 需要编译的文件名称，可以用正则
        less:{
            fileName:"style.less"
        },

        webpack:{
            output:{
                filename: "[name].js",
                chunkFilename: "[name].[id].c.js?v=[chunkhash]",
                publicPath:"../scripts/"
            },

            module: {
                // avoid webpack trying to shim process
                noParse: [/es6-promise\.js$/],
                loaders: [
                    {
                        test: /\.vue$/,
                        loader: 'vue'
                    },
                    {
                        test: /\.js$/,
                        // excluding some local linked packages.
                        // for normal use cases only node_modules is needed.
                        exclude: /node_modules|vue\/src|vue-router\/|vue-loader\/|vue-hot-reload-api\//,
                        loader: 'babel'
                    },
                    // { test: /\.css$/, loader: 'css-loader' },
                    // { test: /\.css$/, loader: ExtractTextPlugin.extract('style-loader', 'css-loader?sourceMap' ) },
                    // { test: /\.less$/, loader: 'style!css!less-loader?compress=true'},
                    // { test: /\.css/, loader: 'css-loader?compress=true'},


                    {
                        test: /\.css$/,
                        loader: ExtractTextPlugin.extract('style-loader', 'css-loader'),
                    },{
                        test: /\.less$/,
                        loader: ExtractTextPlugin.extract('style-loader', 'css-loader!less-loader'),
                    },
                    {
                        test: /\.scss$/,
                        loader: ExtractTextPlugin.extract('style-loader', 'css-loader!sass-loader'),
                    },


                    { test: /\.html/, loader: 'html-loader?compress=true'},
                    { test: /\.json/, loader: 'json-loader'},
                    {
                        test: /\.(png|gif|jpg)$/,
                        loader: 'url-loader?limit=8192&name=../images/[name].[ext]'
                    },
                    {
                        test: /\.(woff|woff2|ttf|eot|svg)([\?]?.*)$/,
                        loader: 'url-loader?limit=10000&name=../fonts/[name].[ext]'
                    }
                ]
            },
            babel: {
                presets: ['es2015'],
                plugins: ['transform-runtime']
            },
            vue: {
                loaders: {
                    sass: ExtractTextPlugin.extract('vue-style-loader', 'css-loader!sass-loader'),
                },
            },
        }
    };

    var _config = Object.assign({}, config, opt);
    if( opt.webpack ){
        for (var i in opt.webpack) {
            config.webpack[i] = opt.webpack[i];
        }
        _config.webpack = config.webpack;
    }

    _config.mapFiles = _config.appList.map(function (app) { return _config.src + app + '.js'})

    return _config;
};