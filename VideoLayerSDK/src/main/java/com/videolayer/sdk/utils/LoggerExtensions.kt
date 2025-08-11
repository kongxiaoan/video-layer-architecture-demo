package com.videolayer.sdk.utils


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

/**
 * 日志工具扩展功能
 */

/**
 * 测量方法执行时间并记录日志
 */
inline fun <T> VideoLogger.measureAndLog(
    tag: String,
    operation: String,
    crossinline action: () -> T
): T {
    var result: T
    val duration = measureTimeMillis {
        result = action()
    }
    logPerformance(tag, operation, duration)
    return result
}

/**
 * 测量挂起方法执行时间并记录日志
 */
suspend inline fun <T> VideoLogger.measureAndLogSuspend(
    tag: String,
    operation: String,
    crossinline action: suspend () -> T
): T {
    var result: T
    val duration = measureTimeMillis {
        result = action()
    }
    logPerformance(tag, operation, duration)
    return result
}

/**
 * 记录异常但不抛出
 */
inline fun VideoLogger.safeExecute(
    tag: String,
    operation: String,
    crossinline action: () -> Unit
) {
    try {
        action()
    } catch (e: Exception) {
        e(tag, "执行 $operation 时发生异常", e)
    }
}

/**
 * 记录协程异常
 */
fun CoroutineScope.launchWithLog(
    tag: String,
    operation: String,
    block: suspend CoroutineScope.() -> Unit
) {
    launch {
        try {
            VideoLogger.d(tag, "开始执行协程: $operation")
            block()
            VideoLogger.d(tag, "协程执行完成: $operation")
        } catch (e: Exception) {
            VideoLogger.e(tag, "协程执行异常: $operation", e)
        }
    }
}

/**
 * 条件日志记录
 */
fun VideoLogger.logIf(
    condition: Boolean,
    level: VideoLogger.LogLevel,
    tag: String,
    message: () -> String
) {
    if (condition) {
        when (level) {
            VideoLogger.LogLevel.VERBOSE -> v(tag, message())
            VideoLogger.LogLevel.DEBUG -> d(tag, message())
            VideoLogger.LogLevel.INFO -> i(tag, message())
            VideoLogger.LogLevel.WARN -> w(tag, message())
            VideoLogger.LogLevel.ERROR -> e(tag, message())
        }
    }
}