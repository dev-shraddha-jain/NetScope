package com.groot.netscope.models

data class ApiRequest(
    val id: String,
    val method: String,
    val url: String,
    val headers: Map<String, List<String>>,
    val requestBody: String? = null,
    val timestamp: Long,
    val statusCode: Int? = null,
    val responseHeaders: Map<String, List<String>>? = null,
    val responseBody: String? = null,
    val duration: Long? = null,
    val error: String? = null,
    val isCompleted: Boolean = false
)