package com.groot.netscope

import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import java.util.concurrent.CopyOnWriteArrayList

class HttpServer(port: Int, private val requestStore: RequestStore) : NanoHTTPD(port) {

    private val gson = Gson()
    private val sseClients = CopyOnWriteArrayList<SSEClient>()

    init {
        // Listen for new requests and broadcast to SSE clients
        requestStore.addListener { request ->
            broadcastToSSEClients(gson.toJson(request))
        }
    }

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val method = session.method

        // Add CORS headers
        val response = when {
            uri == "/requests" && method == Method.GET -> handleGetRequests()
            uri.startsWith("/request/") && method == Method.GET -> handleGetRequest(uri)
            uri == "/events" && method == Method.GET -> handleSSEConnection(session)
            uri == "/clear" && method == Method.POST -> handleClearRequests()
            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
        }

        addCORSHeaders(response)
        return response
    }

    private fun handleGetRequests(): Response {
        val requests = requestStore.getAllRequests()
        val json = gson.toJson(requests)
        return newFixedLengthResponse(Response.Status.OK, "application/json", json)
    }

    private fun handleGetRequest(uri: String): Response {
        val id = uri.removePrefix("/request/")
        val request = requestStore.getRequest(id)

        return if (request != null) {
            val json = gson.toJson(request)
            newFixedLengthResponse(Response.Status.OK, "application/json", json)
        } else {
            newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Request not found")
        }
    }

    private fun handleSSEConnection(session: IHTTPSession): Response {
        val response = newChunkedResponse(Response.Status.OK, "text/event-stream", null)
        response.addHeader("Cache-Control", "no-cache")
        response.addHeader("Connection", "keep-alive")

        val client = SSEClient()
        sseClients.add(client)

        // Send initial data
        val requests = requestStore.getAllRequests()
        requests.forEach { request ->
            client.send("data: ${gson.toJson(request)}\n\n")
        }

        return response
    }

    private fun handleClearRequests(): Response {
        requestStore.clear()
        return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"success\": true}")
    }

    private fun broadcastToSSEClients(data: String) {
        val message = "data: $data\n\n"
        sseClients.forEach { client ->
            try {
                client.send(message)
            } catch (e: Exception) {
                sseClients.remove(client)
            }
        }
    }

    private fun addCORSHeaders(response: Response) {
        response.addHeader("Access-Control-Allow-Origin", "*")
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        response.addHeader("Access-Control-Allow-Headers", "Content-Type")
    }

    private class SSEClient {
        fun send(message: String) {
            // In a real implementation, you'd need to handle the actual SSE connection
            // This is a simplified version
        }
    }
}