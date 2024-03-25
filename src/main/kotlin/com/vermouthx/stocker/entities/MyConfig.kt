package com.vermouthx.stocker.entities

// 定义一个数据类来匹配 JSON 结构
data class MyConfig(
    val code: String,
    val chiyou: Int
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MyConfig
        return code == other.code
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
