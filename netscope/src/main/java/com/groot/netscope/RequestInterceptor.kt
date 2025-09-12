package com.apiinspector

import com.groot.netscope.RequestStore
import com.groot.netscope.models.ApiRequest
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.UUID

class RequestInterceptor(private val requestStore: RequestStore) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        val apiRequest = ApiRequest(
            id = UUID.randomUUID().toString(),
            method = request.method,
            url = request.url.toString(),
            headers = request.headers.toMultimap(),
            requestBody = getRequestBodyString(request.body),
            timestamp = startTime
        )

        requestStore.addRequest(apiRequest)

        return try {
            val response = chain.proceed(request)
            val endTime = System.currentTimeMillis()

            val updatedRequest = apiRequest.copy(
                statusCode = response.code,
                responseHeaders = response.headers.toMultimap(),
                responseBody = getResponseBodyString(response),
                duration = endTime - startTime,
                isCompleted = true
            )

            requestStore.updateRequest(updatedRequest)
            response
        } catch (e: IOException) {
            val updatedRequest = apiRequest.copy(
                error = e.message,
                duration = System.currentTimeMillis() - startTime,
                isCompleted = true
            )
            requestStore.updateRequest(updatedRequest)
            throw e
        }
    }

    private fun getRequestBodyString(body: RequestBody?): String? {
        if (body == null) return null

        return try {
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            val content = buffer.readUtf8()
            if (content.length > 1000) content.take(1000) + "..." else content
        } catch (e: Exception) {
            "Error reading request body: ${e.message}"
        }
    }

    private fun getResponseBodyString(response: Response): String? {
        val responseBody = response.body ?: return null

        return try {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            val content = buffer.clone().readUtf8()
            if (content.length > 1000) content.take(1000) + "..." else content
        } catch (e: Exception) {
            "Error reading response body: ${e.message}"
        }
    }
}