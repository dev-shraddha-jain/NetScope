package com.groot.netscope

import android.content.Context
import android.content.pm.ApplicationInfo
import com.apiinspector.RequestInterceptor
import okhttp3.Interceptor

object ApiInspector {
    private var isInitialized = false
    private var httpServer: HttpServer? = null
    private val requestStore = RequestStore()

    fun init(context: Context, port: Int = 8081): Boolean {
        if (isInitialized) return true

        // Only work in debug builds
        if (!isDebuggable(context)) return false

        try {
            httpServer = HttpServer(port, requestStore)
            httpServer?.start()
            isInitialized = true
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getInterceptor(): Interceptor {
        return RequestInterceptor(requestStore)
    }

    fun stop() {
        httpServer?.stop()
        isInitialized = false
    }

    private fun isDebuggable(context: Context): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}