package com.videolayer.sdk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * VideoLayerSDK 专用日志工具
 * 支持文件保存、压缩加密、分级日志等功能
 */
@SuppressLint("StaticFieldLeak")
object VideoLogger {

    private const val TAG = "VideoLayerSDK"
    private const val LOG_FILE_NAME = "videolayer_logs"
    private const val LOG_FILE_EXTENSION = ".log"
    private const val MAX_LOG_FILES = 5
    private const val MAX_LOG_FILE_SIZE = 10 * 1024 * 1024 // 10MB
    private const val ENCRYPTION_KEY = "VideoLayerSDK2025"

    // 日志级别
    enum class LogLevel(val value: Int, val tag: String) {
        VERBOSE(2, "V"),
        DEBUG(3, "D"),
        INFO(4, "I"),
        WARN(5, "W"),
        ERROR(6, "E")
    }

    // 日志配置
    data class LogConfig(
        val enableConsoleLog: Boolean = true,
        val enableFileLog: Boolean = true,
        val minLogLevel: LogLevel = LogLevel.DEBUG,
        val logDirectory: String? = null,
        val enableEncryption: Boolean = true,
        val maxFileSize: Long = MAX_LOG_FILE_SIZE.toLong(),
        val maxFileCount: Int = MAX_LOG_FILES
    )

    private var context: Context? = null
    private var config: LogConfig = LogConfig()
    private var logQueue = ConcurrentLinkedQueue<LogEntry>()
    private var logScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    data class LogEntry(
        val level: LogLevel,
        val tag: String,
        val message: String,
        val throwable: Throwable? = null,
        val timestamp: Long = System.currentTimeMillis()
    )

    /**
     * 初始化日志系统
     */
    fun initialize(context: Context, config: LogConfig = LogConfig()) {
        this.context = context.applicationContext
        this.config = config
        this.isInitialized = true

        if (config.enableFileLog) {
            startLogWriter()
        }

        // 打印初始化信息
        i("VideoLogger", "日志系统初始化完成")
        i("VideoLogger", "设备信息: ${getDeviceInfo()}")
        i("VideoLogger", "应用版本: ${getAppVersion()}")
    }

    /**
     * Verbose日志
     */
    fun v(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.VERBOSE, tag, message, throwable)
    }

    /**
     * Debug日志
     */
    fun d(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.DEBUG, tag, message, throwable)
    }

    /**
     * Info日志
     */
    fun i(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.INFO, tag, message, throwable)
    }

    /**
     * Warning日志
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.WARN, tag, message, throwable)
    }

    /**
     * Error日志
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.ERROR, tag, message, throwable)
    }

    /**
     * 记录方法调用
     */
    fun logMethodCall(tag: String, methodName: String, args: Map<String, Any?> = emptyMap()) {
        val argsString = if (args.isNotEmpty()) {
            args.map { "${it.key}=${it.value}" }.joinToString(", ")
        } else {
            "无参数"
        }
        d(tag, "调用方法: $methodName($argsString)")
    }

    /**
     * 记录性能指标
     */
    fun logPerformance(
        tag: String,
        operation: String,
        durationMs: Long,
        additionalInfo: String = ""
    ) {
        val info = if (additionalInfo.isNotEmpty()) " | $additionalInfo" else ""
        i(tag, "性能统计: $operation 耗时 ${durationMs}ms$info")
    }

    /**
     * 记录网络请求
     */
    fun logNetworkRequest(
        tag: String,
        url: String,
        method: String,
        responseCode: Int,
        durationMs: Long
    ) {
        i(tag, "网络请求: $method $url | 响应码: $responseCode | 耗时: ${durationMs}ms")
    }

    /**
     * 记录用户操作
     */
    fun logUserAction(tag: String, action: String, details: String = "") {
        val detailsString = if (details.isNotEmpty()) " | $details" else ""
        i(tag, "用户操作: $action$detailsString")
    }

    /**
     * 核心日志方法
     */
    private fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
        if (!isInitialized || level.value < config.minLogLevel.value) {
            return
        }

        val fullTag = "[$TAG][$tag]"
        val logEntry = LogEntry(level, fullTag, message, throwable)

        // 控制台输出
        if (config.enableConsoleLog) {
            logToConsole(logEntry)
        }

        // 文件输出
        if (config.enableFileLog) {
            logQueue.offer(logEntry)
        }
    }

    /**
     * 控制台日志输出
     */
    private fun logToConsole(entry: LogEntry) {
        val message = if (entry.throwable != null) {
            "${entry.message}\n${Log.getStackTraceString(entry.throwable)}"
        } else {
            entry.message
        }

        when (entry.level) {
            LogLevel.VERBOSE -> Log.v(entry.tag, message)
            LogLevel.DEBUG -> Log.d(entry.tag, message)
            LogLevel.INFO -> Log.i(entry.tag, message)
            LogLevel.WARN -> Log.w(entry.tag, message)
            LogLevel.ERROR -> Log.e(entry.tag, message)
        }
    }

    /**
     * 启动日志写入协程
     */
    private fun startLogWriter() {
        logScope.launch {
            while (isActive) {
                try {
                    val entries = mutableListOf<LogEntry>()

                    // 批量获取日志条目
                    repeat(100) {
                        val entry = logQueue.poll()
                        if (entry != null) {
                            entries.add(entry)
                        } else {
                            return@repeat
                        }
                    }

                    if (entries.isNotEmpty()) {
                        writeLogsToFile(entries)
                    }

                    delay(1000) // 每秒写入一次
                } catch (e: Exception) {
                    Log.e(TAG, "写入日志文件失败", e)
                }
            }
        }
    }

    /**
     * 写入日志到文件
     */
    private suspend fun writeLogsToFile(entries: List<LogEntry>) = withContext(Dispatchers.IO) {
        val logFile = getCurrentLogFile()

        try {
            FileWriter(logFile, true).use { writer ->
                entries.forEach { entry ->
                    val timestamp = dateFormat.format(Date(entry.timestamp))
                    val logLine = "[$timestamp][${entry.level.tag}][${entry.tag}] ${entry.message}"

                    writer.appendLine(logLine)

                    entry.throwable?.let { throwable ->
                        writer.appendLine("异常堆栈:")
                        throwable.printStackTrace(PrintWriter(writer))
                    }
                }
            }

            // 检查文件大小，必要时轮转
            if (logFile.length() > config.maxFileSize) {
                rotateLogFiles()
            } else {

            }
        } catch (e: Exception) {
            Log.e(TAG, "写入日志文件失败: ${logFile.absolutePath}", e)
        }
    }

    /**
     * 获取当前日志文件
     */
    private fun getCurrentLogFile(): File {
        val logDir = getLogDirectory()
        val today = fileDateFormat.format(Date())
        return File(logDir, "${LOG_FILE_NAME}_${today}${LOG_FILE_EXTENSION}")
    }

    /**
     * 获取日志目录
     */
    private fun getLogDirectory(): File {
        val dir = if (config.logDirectory != null) {
            File(config.logDirectory)
        } else {
            File(context!!.getExternalFilesDir(null), "logs")
        }

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    /**
     * 轮转日志文件
     */
    private fun rotateLogFiles() {
        try {
            val logDir = getLogDirectory()
            val logFiles = logDir.listFiles { _, name ->
                name.startsWith(LOG_FILE_NAME) && name.endsWith(LOG_FILE_EXTENSION)
            }?.sortedByDescending { it.lastModified() }

            if (logFiles != null && logFiles.size >= config.maxFileCount) {
                // 删除最旧的文件
                logFiles.drop(config.maxFileCount - 1).forEach { file ->
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "轮转日志文件失败", e)
        }
    }

    /**
     * 获取所有日志文件
     */
    fun getLogFiles(): List<File> {
        return try {
            val logDir = getLogDirectory()
            logDir.listFiles { _, name ->
                name.startsWith(LOG_FILE_NAME) && name.endsWith(LOG_FILE_EXTENSION)
            }?.toList() ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "获取日志文件失败", e)
            emptyList()
        }
    }

    /**
     * 获取压缩加密的日志文件
     */
    suspend fun getEncryptedLogZip(): File? = withContext(Dispatchers.IO) {
        try {
            val logFiles = getLogFiles()
            if (logFiles.isEmpty()) {
                return@withContext null
            }

            val zipFile =
                File(getLogDirectory(), "videolayer_logs_${System.currentTimeMillis()}.zip")
            val encryptedZipFile = File(
                getLogDirectory(),
                "videolayer_logs_encrypted_${System.currentTimeMillis()}.zip"
            )

            // 创建ZIP文件
            ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
                logFiles.forEach { logFile ->
                    val entry = ZipEntry(logFile.name)
                    zipOut.putNextEntry(entry)

                    logFile.inputStream().use { input ->
                        input.copyTo(zipOut)
                    }
                    zipOut.closeEntry()
                }

                // 添加设备信息
                val deviceInfoEntry = ZipEntry("device_info.txt")
                zipOut.putNextEntry(deviceInfoEntry)
                zipOut.write(getDeviceInfoDetail().toByteArray())
                zipOut.closeEntry()
            }

            // 加密ZIP文件
            if (config.enableEncryption) {
                encryptFile(zipFile, encryptedZipFile)
                zipFile.delete()
                return@withContext encryptedZipFile
            }

            return@withContext zipFile
        } catch (e: Exception) {
            Log.e(TAG, "创建加密日志ZIP失败", e)
            null
        }
    }

    /**
     * 加密文件
     */
    private fun encryptFile(inputFile: File, outputFile: File) {
        val key = generateKey()
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        inputFile.inputStream().use { input ->
            outputFile.outputStream().use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    val encryptedData = cipher.update(buffer, 0, bytesRead)
                    if (encryptedData != null) {
                        output.write(encryptedData)
                    }
                }
                val finalData = cipher.doFinal()
                if (finalData != null) {
                    output.write(finalData)
                }
            }
        }
    }

    /**
     * 生成加密密钥
     */
    private fun generateKey(): SecretKey {
        val keyBytes = ENCRYPTION_KEY.toByteArray()
        val key = ByteArray(16) // AES-128
        System.arraycopy(keyBytes, 0, key, 0, minOf(keyBytes.size, key.size))
        return SecretKeySpec(key, "AES")
    }

    /**
     * 获取设备信息
     */
    private fun getDeviceInfo(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL} (API ${Build.VERSION.SDK_INT})"
    }

    /**
     * 获取详细设备信息
     */
    private fun getDeviceInfoDetail(): String {
        return buildString {
            appendLine("=== 设备信息 ===")
            appendLine("制造商: ${Build.MANUFACTURER}")
            appendLine("设备型号: ${Build.MODEL}")
            appendLine("产品名: ${Build.PRODUCT}")
            appendLine("Android版本: ${Build.VERSION.RELEASE}")
            appendLine("API级别: ${Build.VERSION.SDK_INT}")
            appendLine("CPU架构: ${Build.SUPPORTED_ABIS.joinToString()}")
            appendLine("时间戳: ${Date()}")
            appendLine("应用版本: ${getAppVersion()}")
            appendLine("=== 日志配置 ===")
            appendLine("控制台日志: ${config.enableConsoleLog}")
            appendLine("文件日志: ${config.enableFileLog}")
            appendLine("最小日志级别: ${config.minLogLevel}")
            appendLine("加密: ${config.enableEncryption}")
            appendLine("最大文件大小: ${config.maxFileSize}")
            appendLine("最大文件数量: ${config.maxFileCount}")
        }
    }

    /**
     * 获取应用版本
     */
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
            "${packageInfo.versionName}(${packageInfo.versionCode})"
        } catch (e: Exception) {
            "未知"
        }
    }

    /**
     * 清理日志文件
     */
    fun clearLogs() {
        logScope.launch {
            try {
                getLogFiles().forEach { file ->
                    file.delete()
                }
                i("VideoLogger", "日志文件清理完成")
            } catch (e: Exception) {
                e("VideoLogger", "清理日志文件失败", e)
            }
        }
    }

    /**
     * 关闭日志系统
     */
    fun shutdown() {
        logScope.cancel()
        i("VideoLogger", "日志系统已关闭")
    }
}