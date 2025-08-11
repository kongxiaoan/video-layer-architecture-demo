package com.videolayer.sdk.utils

import android.content.Context
import io.mockk.mockk
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
 * Logger扩展功能测试
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LoggerExtensionsTest {

    private lateinit var mockContext: Context
    private lateinit var testDirectory: File
    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        testDirectory = createTempDir("videolayer_extensions_test")
        testScope = TestScope()

        val config = VideoLogger.LogConfig(
            enableFileLog = false,
            enableConsoleLog = true
        )
        VideoLogger.initialize(mockContext, config)
    }

    @After
    fun tearDown() {
        VideoLogger.shutdown()
        testDirectory.deleteRecursively()
    }

    @Test
    fun `measureAndLog应该执行操作并返回结果`() {
        // Given
        val expectedResult = "测试结果"
        val tag = "TestMeasure"
        val operation = "测试操作"

        // When
        val result = VideoLogger.measureAndLog(tag, operation) {
            Thread.sleep(10) // 模拟耗时操作
            expectedResult
        }

        // Then
        assertEquals(expectedResult, result)
    }

    @Test
    fun `measureAndLog应该处理异常`() {
        // Given
        val tag = "TestMeasure"
        val operation = "异常操作"
        val exception = RuntimeException("测试异常")

        // When & Then
        assertFailsWith<RuntimeException> {
            VideoLogger.measureAndLog(tag, operation) {
                throw exception
            }
        }
    }

    @Test
    fun `measureAndLogSuspend应该执行挂起操作`() = testScope.runTest {
        // Given
        val expectedResult = 42
        val tag = "TestSuspend"
        val operation = "挂起操作"

        // When
        val result = VideoLogger.measureAndLogSuspend(tag, operation) {
            kotlinx.coroutines.delay(10)
            expectedResult
        }

        // Then
        assertEquals(expectedResult, result)
    }

    @Test
    fun `safeExecute应该捕获异常而不抛出`() {
        // Given
        val tag = "TestSafe"
        val operation = "安全操作"
        var executed = false

        // When
        VideoLogger.safeExecute(tag, operation) {
            executed = true
            throw RuntimeException("测试异常")
        }

        // Then
        assertTrue(executed, "操作应该被执行")
        // 没有异常抛出说明测试通过
    }

    @Test
    fun `safeExecute应该正常执行无异常的操作`() {
        // Given
        val tag = "TestSafe"
        val operation = "正常操作"
        var executed = false

        // When
        VideoLogger.safeExecute(tag, operation) {
            executed = true
        }

        // Then
        assertTrue(executed, "操作应该被执行")
    }

    @Test
    fun `launchWithLog应该记录协程执行过程`() = testScope.runTest {
        // Given
        val tag = "TestCoroutine"
        val operation = "协程操作"
        var executed = false

        // When
        testScope.launchWithLog(tag, operation) {
            executed = true
        }

        // 等待协程完成
        advanceUntilIdle()

        // Then
        assertTrue(executed, "协程操作应该被执行")
    }

    @Test
    fun `launchWithLog应该捕获协程异常`() = testScope.runTest {
        // Given
        val tag = "TestCoroutine"
        val operation = "异常协程"
        var exceptionThrown = false

        // When
        testScope.launchWithLog(tag, operation) {
            exceptionThrown = true
            throw RuntimeException("协程异常")
        }

        // 等待协程完成
        advanceUntilIdle()

        // Then
        assertTrue(exceptionThrown, "异常应该被抛出")
        // 没有未捕获异常说明测试通过
    }

    @Test
    fun `logIf应该在条件为true时记录日志`() {
        // Given
        val condition = true
        val tag = "TestCondition"
        var messageGenerated = false

        // When
        VideoLogger.logIf(condition, VideoLogger.LogLevel.INFO, tag) {
            messageGenerated = true
            "条件日志消息"
        }

        // Then
        assertTrue(messageGenerated, "条件为true时应该生成消息")
    }

    @Test
    fun `logIf应该在条件为false时跳过日志`() {
        // Given
        val condition = false
        val tag = "TestCondition"
        var messageGenerated = false

        // When
        VideoLogger.logIf(condition, VideoLogger.LogLevel.INFO, tag) {
            messageGenerated = true
            "条件日志消息"
        }

        // Then
        assertFalse(messageGenerated, "条件为false时不应该生成消息")
    }

    @Test
    fun `logIf应该支持所有日志级别`() {
        // Given
        val condition = true
        val tag = "TestLevels"

        // When & Then - 测试所有级别不抛出异常
        assertDoesNotThrow {
            VideoLogger.logIf(condition, VideoLogger.LogLevel.VERBOSE, tag) { "Verbose消息" }
            VideoLogger.logIf(condition, VideoLogger.LogLevel.DEBUG, tag) { "Debug消息" }
            VideoLogger.logIf(condition, VideoLogger.LogLevel.INFO, tag) { "Info消息" }
            VideoLogger.logIf(condition, VideoLogger.LogLevel.WARN, tag) { "Warn消息" }
            VideoLogger.logIf(condition, VideoLogger.LogLevel.ERROR, tag) { "Error消息" }
        }
    }

    @Test
    fun `扩展方法应该与不同数据类型协作`() {
        // Given
        val tag = "TestTypes"

        // When & Then
        assertDoesNotThrow {
            VideoLogger.measureAndLog(tag, "整数操作") { 42 }
            VideoLogger.measureAndLog(tag, "字符串操作") { "结果" }
            VideoLogger.measureAndLog(tag, "布尔操作") { true }
            VideoLogger.measureAndLog(tag, "列表操作") { listOf(1, 2, 3) }
            VideoLogger.measureAndLog(tag, "空操作") { Unit }
        }
    }

    @Test
    fun `扩展方法应该保持原始返回值类型`() {
        // Given
        val tag = "TestReturnTypes"

        // When
        val intResult: Int = VideoLogger.measureAndLog(tag, "整数") { 42 }
        val stringResult: String = VideoLogger.measureAndLog(tag, "字符串") { "test" }
        val boolResult: Boolean = VideoLogger.measureAndLog(tag, "布尔") { true }
        val listResult: List<String> = VideoLogger.measureAndLog(tag, "列表") { listOf("a", "b") }

        // Then
        assertEquals(42, intResult)
        assertEquals("test", stringResult)
        assertEquals(true, boolResult)
        assertEquals(listOf("a", "b"), listResult)
    }
}