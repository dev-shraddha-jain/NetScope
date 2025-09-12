package com.groot.netscope

import com.groot.netscope.models.ApiRequest
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList

class RequestStore {
    private val requests = ConcurrentLinkedQueue<ApiRequest>()
    private val listeners = CopyOnWriteArrayList<(ApiRequest) -> Unit>()
    private val maxRequests = 100

    fun addRequest(request: ApiRequest) {
        requests.offer(request)

        // Keep only last N requests
        while (requests.size > maxRequests) {
            requests.poll()
        }

        notifyListeners(request)
    }

    fun updateRequest(request: ApiRequest) {
        // Remove old version and add updated
        requests.removeIf { it.id == request.id }
        requests.offer(request)
        notifyListeners(request)
    }

    fun getAllRequests(): List<ApiRequest> {
        return requests.toList().sortedByDescending { it.timestamp }
    }

    fun getRequest(id: String): ApiRequest? {
        return requests.find { it.id == id }
    }

    fun addListener(listener: (ApiRequest) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (ApiRequest) -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners(request: ApiRequest) {
        listeners.forEach { it(request) }
    }

    fun clear() {
        requests.clear()
    }
}