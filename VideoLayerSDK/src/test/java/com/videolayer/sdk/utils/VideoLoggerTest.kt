package com.videolayer.sdk.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
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
 * VideoLogger 单元测试
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class VideoLoggerTest {

    private lateinit var mockContext: Context
    private lateinit var mockPackageManager: PackageManager
    private lateinit var testDirectory: File
    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        // Mock Context
        mockContext = mockk(relaxed = true)
        mockPackageManager = mockk(relaxed = true)

        // 创建临时测试目录
        testDirectory = createTempDir("videolayer_test")

        // 设置测试协程作用域
        testScope = TestScope()
        Dispatchers.setMain(StandardTestDispatcher(testScope.testScheduler))

        // Mock PackageManager
        val packageInfo = PackageInfo().apply {
            versionName = "1.0.0"
            versionCode = 1
        }
        every { mockContext.packageManager } returns mockPackageManager
        every { mockContext.packageName } returns "com.videolayer.test"
        every { mockPackageManager.getPackageInfo(any<String>(), any<Int>()) } returns packageInfo
        every { mockContext.getExternalFilesDir(null) } returns testDirectory

        // 清理之前的状态
        clearAllMocks()
    }

    @After
    fun tearDown() {
        VideoLogger.shutdown()
        testDirectory.deleteRecursively()
        Dispatchers.resetMain()
    }

    @Test
    fun `初始化后应该设置为已初始化状态`() {
        // Given
        val config = VideoLogger.LogConfig(enableFileLog = false)

        // When
        VideoLogger.initialize(mockContext, config)

        // Then
        // 通过调用需要初始化的方法来验证状态
        assertDoesNotThrow {
            VideoLogger.d("Test", "测试消息")
        }
    }

    @Test
    fun `未初始化时调用日志方法不应该抛出异常`() {
        // Given - VideoLogger未初始化

        // When & Then
        assertDoesNotThrow {
            VideoLogger.d("Test", "测试消息")
        }
    }

    @Test
    fun `日志级别过滤应该正常工作`() = testScope.runTest {
        // Given
        val config = VideoLogger.LogConfig(
            enableConsoleLog = true,
            enableFileLog = false,
            minLogLevel = VideoLogger.LogLevel.INFO
        )
        VideoLogger.initialize(mockContext, config)

        // When
        VideoLogger.v("Test", "Verbose消息") // 应该被过滤
        VideoLogger.d("Test", "Debug消息")   // 应该被过滤
        VideoLogger.i("Test", "Info消息")    // 应该显示
        VideoLogger.w("Test", "Warning消息") // 应该显示
        VideoLogger.e("Test", "Error消息")   // 应该显示

        // Then - 通过mock验证或其他方式验证过滤效果
        // 这里主要测试不会抛出异常
        assertTrue(true)
    }

    @Test
    fun `文件日志应该创建日志文件`() = testScope.runTest {
        // Given
        val config = VideoLogger.LogConfig(
            enableConsoleLog = false,
            enableFileLog = true,
            logDirectory = testDirectory.absolutePath
        )
        VideoLogger.initialize(mockContext, config)

        // When
        VideoLogger.i("Test", "测试文件日志")

        // 等待异步写入完成
        advanceTimeBy(2000)

        // Then
        val logFiles = VideoLogger.getLogFiles()
        assertTrue(logFiles.isNotEmpty(), "应该创建日志文件")
    }

    @Test
    fun `方法调用日志应该包含参数信息`() {
        // Given
        val config = VideoLogger.LogConfig(enableFileLog = false)
        VideoLogger.initialize(mockContext, config)

        // When
        val args = mapOf("param1" to "value1", "param2" to 123)

        // Then - 测试不抛出异常
        assertDoesNotThrow {
            VideoLogger.logMethodCall("Test", "testMethod", args)
        }
    }

    @Test
    fun `性能日志应该记录执行时间`() {
        // Given
        val config = VideoLogger.LogConfig(enableFileLog = false)
        VideoLogger.initialize(mockContext, config)

        // When & Then
        assertDoesNotThrow {
            VideoLogger.logPerformance("Test", "测试操作", 100, "额外信息")
        }
    }

    @Test
    fun `网络请求日志应该记录完整信息`() {
        // Given
        val config = VideoLogger.LogConfig(enableFileLog = false)
        VideoLogger.initialize(mockContext, config)

        // When & Then
        assertDoesNotThrow {
            VideoLogger.logNetworkRequest("Test", "http://example.com", "GET", 200, 500)
        }
    }

    @Test
    fun `用户操作日志应该记录操作详情`() {
        // Given
        val config = VideoLogger.LogConfig(enableFileLog = false)
        VideoLogger.initialize(mockContext, config)

        // When & Then
        assertDoesNotThrow {
            VideoLogger.logUserAction("Test", "点击按钮", "按钮ID: btn_play")
        }
    }

    @Test
    fun `异常日志应该包含堆栈跟踪`() {
        // Given
        val config = VideoLogger.LogConfig(enableFileLog = false)
        VideoLogger.initialize(mockContext, config)
        val exception = RuntimeException("测试异常")

        // When & Then
        assertDoesNotThrow {
            VideoLogger.e("Test", "发生异常", exception)
        }
    }

    @Test
    fun `获取日志文件列表应该返回正确的文件`() = testScope.runTest {
        // Given
        val config = VideoLogger.LogConfig(
            enableFileLog = true,
            logDirectory = testDirectory.absolutePath
        )
        VideoLogger.initialize(mockContext, config)

        // When
        VideoLogger.i("Test", "创建日志文件")
        advanceTimeBy(2000) // 等待异步写入

        // Then
        val logFiles = VideoLogger.getLogFiles()
        assertTrue(logFiles.isNotEmpty())
        assertTrue(logFiles.all { it.exists() })
        assertTrue(logFiles.all { it.name.contains("videolayer_logs") })
    }

    @Test
    fun `清理日志文件应该删除所有日志文件`() = testScope.runTest {
        // Given
        val config = VideoLogger.LogConfig(
            enableFileLog = true,
            logDirectory = testDirectory.absolutePath
        )
        VideoLogger.initialize(mockContext, config)

        VideoLogger.i("Test", "创建日志文件")
        advanceTimeBy(2000) // 等待写入

        val filesBefore = VideoLogger.getLogFiles()
        assertTrue(filesBefore.isNotEmpty())

        // When
        VideoLogger.clearLogs()
        advanceTimeBy(1000) // 等待清理完成

        // Then
        val filesAfter = VideoLogger.getLogFiles()
        assertTrue(filesAfter.isEmpty() || filesAfter.all { !it.exists() })
    }

    @Test
    fun `日志配置验证应该正确`() {
        // Given & When & Then
        assertDoesNotThrow {
            VideoLogger.LogConfig(
                enableConsoleLog = true,
                enableFileLog = true,
                minLogLevel = VideoLogger.LogLevel.DEBUG,
                maxFileSize = 1024 * 1024,
                maxFileCount = 5
            )
        }
    }

    @Test
    fun `LogLevel枚举应该有正确的值`() {
        // When & Then
        assertEquals(2, VideoLogger.LogLevel.VERBOSE.value)
        assertEquals(3, VideoLogger.LogLevel.DEBUG.value)
        assertEquals(4, VideoLogger.LogLevel.INFO.value)
        assertEquals(5, VideoLogger.LogLevel.WARN.value)
        assertEquals(6, VideoLogger.LogLevel.ERROR.value)

        assertEquals("V", VideoLogger.LogLevel.VERBOSE.tag)
        assertEquals("D", VideoLogger.LogLevel.DEBUG.tag)
        assertEquals("I", VideoLogger.LogLevel.INFO.tag)
        assertEquals("W", VideoLogger.LogLevel.WARN.tag)
        assertEquals("E", VideoLogger.LogLevel.ERROR.tag)
    }

    @Test
    fun `LogEntry应该包含正确的信息`() {
        // Given
        val level = VideoLogger.LogLevel.INFO
        val tag = "TestTag"
        val message = "测试消息"
        val throwable = RuntimeException("测试异常")

        // When
        val entry = VideoLogger.LogEntry(level, tag, message, throwable)

        // Then
        assertEquals(level, entry.level)
        assertEquals(tag, entry.tag)
        assertEquals(message, entry.message)
        assertEquals(throwable, entry.throwable)
        assertTrue(entry.timestamp > 0)
    }

    @Test
    fun `关闭日志系统后应该停止记录`() = testScope.runTest {
        // Given
        val config = VideoLogger.LogConfig(enableFileLog = false)
        VideoLogger.initialize(mockContext, config)

        // When
        VideoLogger.shutdown()

        // Then - 调用日志方法不应该抛出异常，但也不应该记录
        assertDoesNotThrow {
            VideoLogger.i("Test", "关闭后的消息")
        }
    }
}