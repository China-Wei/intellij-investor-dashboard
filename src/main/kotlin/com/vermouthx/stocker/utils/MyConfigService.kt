package com.vermouthx.stocker.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.State
import com.vermouthx.stocker.entities.MyConfig
import com.vermouthx.stocker.settings.StockerSetting
import java.io.File
import java.io.IOException

@State(name = "MyConfigService")
class MyConfigService {
    // 假设的配置文件路径（位于 C 盘）
    val configFilePath = "D:\\workspace\\gp.json"
    private var configs: List<MyConfig>? = null

    companion object {
        val instance: MyConfigService
            get() = ApplicationManager.getApplication().getService(MyConfigService::class.java)
    }
    fun getConfigs(): List<MyConfig> {
        if (configs == null) {
            loadConfigs()
        }
        return configs!!
    }

    private fun loadConfigs() {
        val configFile = File(configFilePath)
        if (configFile.exists()) {
            try {
                val mapper = ObjectMapper()
                // 注册 Kotlin 模块，以便可以正确解析 Kotlin 数据类
                mapper.registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule())
                configs = mapper.readValue<List<MyConfig>>(configFile)
            } catch (e: IOException) {
                throw RuntimeException("Failed to load config file", e)
            }
        } else {
            throw RuntimeException("Config file not found at $configFile.path")
        }
    }
}