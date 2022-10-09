/**
 * middlewares 中间件处理的东西，这里主要开发时候在控制台查看一些处理日志， vuex 本身自带
 */

import createLogger from 'vuex/logger'
import { debug } from '../config/'
import storeMiddleware from './storeMiddleware'

export default debug? [createLogger(), storeMiddleware]
    : [storeMiddleware]