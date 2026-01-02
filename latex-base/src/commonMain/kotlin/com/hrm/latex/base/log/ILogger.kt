package com.hrm.latex.base.log

/**
 * 日志接口，由外部在SDK初始化时注入实现
 */
interface ILogger {
    fun v(tag: String, message: String)
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}
