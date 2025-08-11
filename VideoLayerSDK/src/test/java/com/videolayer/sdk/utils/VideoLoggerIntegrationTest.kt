package com.videolayer.sdk.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.*

/**
 * VideoLogger 集成测试
 * 测试完整的使用场景
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class VideoLoggerIntegrationTest {

    private lateinit var mockContext: Context
    private lateinit var testDirectory: File
    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        testDirectory = createTempDir("videolayer_integration_test")
        testScope = TestScope()

        // Mock PackageManager
        val mockPackageManager = mockk<PackageManager>(relaxed = true)
        val packageInfo = PackageInfo().apply {
            versionName = "1.0.0"
            versionCode = 1
        }

        every { mockContext.packageManager } returns mockPackageManager
        every { mockContext.packageName } returns "com.videolayer.test"
        every { mockPackageManager.getPackageInfo(any<String>(), any<Int>()) } returns packageInfo
        every { mockContext.getExternalFilesDir(null) } returns testDirectory
    }

    @After
    fun tearDown() {
        VideoLogger.shutdown()
        testDirectory.deleteRecursively()
    }

    @Test
    fun `完整的日志使用场景`() = testScope.runTest {
        // Given - 初始化日志系统
        val config = VideoLogger.LogConfig(
            enableConsoleLog = true,
            enableFileLog = true,
            minLogLevel = VideoLogger.LogLevel.DEBUG,
            logDirectory = testDirectory.absolutePath,
            enableEncryption = true
        )
        VideoLogger.initialize(mockContext, config)

        // When - 模拟各种日志使用场景

        // 1. 应用启动日志
        VideoLogger.i("App", "应用启动")
        VideoLogger.logMethodCall("MainActivity", "onCreate", mapOf("savedInstanceState" to null))

        // 2. 用户操作日志
        VideoLogger.logUserAction("VideoPlayer", "播放视频", "videoId=12345")
        VideoLogger.logUserAction("VideoPlayer", "暂停视频")

        // 3. 性能监控
        VideoLogger.measureAndLog("VideoLoader", "加载视频") {
            Thread.sleep(50) // 模拟加载时间
            "视频加载完成"
        }

        // 4. 网络请求日志
        VideoLogger.logNetworkRequest("API", "https://api.example.com/videos", "GET", 200, 250)
        VideoLogger.logNetworkRequest("API", "https://api.example.com/videos", "POST", 201, 150)

        // 5. 异常处理
        val testException = RuntimeException("测试网络异常")
        VideoLogger.e("Network", "网络请求失败", testException)

        // 6. 安全执行
        VideoLogger.safeExecute("FileIO", "读取缓存文件") {
            throw IllegalStateException("文件不存在")
        }

        // 7. 条件日志
        val isDebugMode = true
        VideoLogger.logIf(isDebugMode, VideoLogger.LogLevel.DEBUG, "Debug") { "调试模式已启用" }

        // 等待异步处理完成
        advanceTimeBy(3000)

        // Then - 验证结果
        val logFiles = VideoLogger.getLogFiles()
        assertTrue(logFiles.isNotEmpty(), "应该创建日志文件")

        val logFile = logFiles.first()
        assertTrue(logFile.exists(), "日志文件应该存在")
        assertTrue(logFile.length() > 0, "日志文件应该有内容")

        // 验证文件内容包含预期的日志
        val content = logFile.readText()
        assertTrue(content.contains("应用启动"), "应该包含启动日志")
        assertTrue(content.contains("播放视频"), "应该包含用户操作日志")
        assertTrue(content.contains("加载视频"), "应该包含性能日志")
        assertTrue(content.contains("网络请求失败"), "应该包含异常日志")
    }

    @Test
    fun `日志轮转应该正常工作`() = testScope.runTest {
        // Given - 配置小文件大小强制轮转
        val config = VideoLogger.LogConfig(
            enableFileLog = true,
            logDirectory = testDirectory.absolutePath,
            maxFileSize = 1024, // 1KB
            maxFileCount = 3
        )
        VideoLogger.initialize(mockContext, config)

        // When - 生成大量日志触发轮转
        repeat(100) { i ->
            VideoLogger.i("Test", "这是第${i}条日志消息，用于测试文件轮转功能。".repeat(10))
        }

        advanceTimeBy(5000) // 等待写入完成

        // Then
        val logFiles = VideoLogger.getLogFiles()
        assertTrue(logFiles.size <= config.maxFileCount, "日志文件数量不应超过限制")

        // 验证文件确实有内容
        logFiles.forEach { file ->
            assertTrue(file.exists(), "每个日志文件都应该存在")
            assertTrue(file.length() > 0, "每个日志文件都应该有内容")
        }
    }

    @Test
    fun `加密日志压缩包生成`() = testScope.runTest {
        // Given
        val config = VideoLogger.LogConfig(
            enableFileLog = true,
            logDirectory = testDirectory.absolutePath,
            enableEncryption = true
        )
        VideoLogger.initialize(mockContext, config)

        // 生成一些日志
        VideoLogger.i("Test", "测试日志1")
        VideoLogger.w("Test", "测试日志2")
        VideoLogger.e("Test", "测试日志3", RuntimeException("测试异常"))

        advanceTimeBy(2000) // 等待写入

        // When
        val encryptedZip = VideoLogger.getEncryptedLogZip()

        // Then
        assertNotNull(encryptedZip, "应该生成加密压缩文件")
        assertTrue(encryptedZip.exists(), "压缩文件应该存在")
        assertTrue(encryptedZip.length() > 0, "压缩文件应该有内容")
        assertTrue(encryptedZip.name.contains("videolayer_logs"), "文件名应该包含预期前缀")
    }

    @Test
    fun `并发日志写入应该安全`() = testScope.runTest {
        // Given
        val config = VideoLogger.LogConfig(
            enableFileLog = true,
            logDirectory = testDirectory.absolutePath
        )
        VideoLogger.initialize(mockContext, config)

        // When - 并发写入日志
        val jobs = (1..50).map { i ->
            testScope.launch {
                repeat(10) { j ->
                    VideoLogger.i("Concurrent$i", "并发消息 $j")
                    VideoLogger.logUserAction("User$i", "操作$j")
                    VideoLogger.logPerformance("Perf$i", "操作$j", (10..100).random().toLong())
                }
            }
        }

        jobs.forEach { it.join() }
        advanceTimeBy(3000)

        // Then
        val logFiles = VideoLogger.getLogFiles()
        assertTrue(logFiles.isNotEmpty(), "应该创建日志文件")

        val totalSize = logFiles.sumOf { it.length() }
        assertTrue(totalSize > 0, "应该有日志内容")
    }

    @Test
    fun `清理日志功能完整测试`() = testScope.runTest {
        // Given
        val config = VideoLogger.LogConfig(
            enableFileLog = true,
            logDirectory = testDirectory.absolutePath
        )
        VideoLogger.initialize(mockContext, config)

        // 创建一些日志文件
        VideoLogger.i("Test", "创建日志1")
        VideoLogger.w("Test", "创建日志2")
        advanceTimeBy(2000)

        val filesBefore = VideoLogger.getLogFiles()
        assertTrue(filesBefore.isNotEmpty(), "清理前应该有日志文件")

        // When
        VideoLogger.clearLogs()
        advanceTimeBy(1000)

        // Then
        val filesAfter = VideoLogger.getLogFiles()
        assertTrue(filesAfter.isEmpty() || filesAfter.none { it.exists() }, "清理后应该没有日志文件")
    }

    @Test
    fun `关闭后重新初始化应该正常工作`() {
        // Given
        val config = VideoLogger.LogConfig(enableFileLog = false)
        VideoLogger.initialize(mockContext, config)

        VideoLogger.i("Test", "第一次初始化的日志")

        // When
        VideoLogger.shutdown()

        // 重新初始化
        VideoLogger.initialize(mockContext, config)

        // Then - 重新初始化后应该能正常使用
        assertDoesNotThrow {
            VideoLogger.i("Test", "重新初始化后的日志")
            VideoLogger.logUserAction("Test", "测试操作")
        }
    }
}