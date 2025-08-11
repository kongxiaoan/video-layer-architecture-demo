package com.videolayer.sdk.utils


import org.junit.Test
import kotlin.test.*

/**
 * 日志配置类测试
 */
class LogConfigTest {

    @Test
    fun `默认配置应该有正确的默认值`() {
        // When
        val config = VideoLogger.LogConfig()

        // Then
        assertTrue(config.enableConsoleLog)
        assertTrue(config.enableFileLog)
        assertEquals(VideoLogger.LogLevel.DEBUG, config.minLogLevel)
        assertNull(config.logDirectory)
        assertTrue(config.enableEncryption)
        assertEquals(10 * 1024 * 1024L, config.maxFileSize)
        assertEquals(5, config.maxFileCount)
    }

    @Test
    fun `自定义配置应该保持设定值`() {
        // Given
        val customConfig = VideoLogger.LogConfig(
            enableConsoleLog = false,
            enableFileLog = false,
            minLogLevel = VideoLogger.LogLevel.ERROR,
            logDirectory = "/custom/path",
            enableEncryption = false,
            maxFileSize = 20 * 1024 * 1024L,
            maxFileCount = 10
        )

        // Then
        assertFalse(customConfig.enableConsoleLog)
        assertFalse(customConfig.enableFileLog)
        assertEquals(VideoLogger.LogLevel.ERROR, customConfig.minLogLevel)
        assertEquals("/custom/path", customConfig.logDirectory)
        assertFalse(customConfig.enableEncryption)
        assertEquals(20 * 1024 * 1024L, customConfig.maxFileSize)
        assertEquals(10, customConfig.maxFileCount)
    }

    @Test
    fun `配置数据类应该支持复制`() {
        // Given
        val originalConfig = VideoLogger.LogConfig(
            enableConsoleLog = true,
            maxFileSize = 5 * 1024 * 1024L
        )

        // When
        val copiedConfig = originalConfig.copy(
            enableConsoleLog = false,
            maxFileCount = 20
        )

        // Then
        assertFalse(copiedConfig.enableConsoleLog)
        assertEquals(20, copiedConfig.maxFileCount)
        assertEquals(5 * 1024 * 1024L, copiedConfig.maxFileSize) // 保持原值
        assertTrue(copiedConfig.enableFileLog) // 保持原值
    }

    @Test
    fun `配置应该支持相等性比较`() {
        // Given
        val config1 = VideoLogger.LogConfig(enableConsoleLog = true, maxFileCount = 5)
        val config2 = VideoLogger.LogConfig(enableConsoleLog = true, maxFileCount = 5)
        val config3 = VideoLogger.LogConfig(enableConsoleLog = false, maxFileCount = 5)

        // Then
        assertEquals(config1, config2)
        assertNotEquals(config1, config3)
    }

    @Test
    fun `配置应该有正确的toString表示`() {
        // Given
        val config = VideoLogger.LogConfig(
            enableConsoleLog = true,
            enableFileLog = false
        )

        // When
        val stringRepresentation = config.toString()

        // Then
        assertTrue(stringRepresentation.contains("enableConsoleLog=true"))
        assertTrue(stringRepresentation.contains("enableFileLog=false"))
    }
}