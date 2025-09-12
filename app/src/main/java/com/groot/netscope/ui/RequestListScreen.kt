package com.groot.netscope.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.groot.netscope.models.ApiRequest
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestListScreen(onRequestClick: (String) -> Unit) {
    var deviceUrl = remember { mutableStateOf("") }
    var requests = remember { mutableStateOf<List<ApiRequest>>(emptyList()) }
    var isConnected = remember { mutableStateOf(false) }
    var errorMessage = remember { mutableStateOf("") }

    val client = remember { OkHttpClient() }
    val gson = remember { Gson() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Connection section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Connect to Device",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deviceUrl.value,
                    onValueChange = { deviceUrl.value = it },
                    label = { Text("Device URL (e.g., http://192.168.1.100:8081)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("http://192.168.1.100:8081") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Button(
                        onClick = {
                            if (deviceUrl.value.isNotEmpty()) {
                                fetchRequests(client, gson, deviceUrl.value) { result ->
                                    result.fold(
                                        onSuccess = {
                                            requests.value = it
                                            isConnected.value = true
                                            errorMessage.value = ""
                                        },
                                        onFailure = {
                                            errorMessage.value = it.message ?: "Connection failed"
                                            isConnected.value = false
                                        }
                                    )
                                }
                            }
                        }
                    ) {
                        Text("Connect")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isConnected.value) {
                        Button(
                            onClick = {
                                clearRequests(client, deviceUrl.value) { success ->
                                    if (success) {
//                                        requests.value = emptyList()
                                    }
                                }
                            }
                        ) {
                            Text("Clear")
                        }
                    }
                }

                if (errorMessage.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage.value,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                if (isConnected.value) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connected",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Requests list
        if (requests.value.isNotEmpty()) {
            Text(
                text = "API Requests (${requests.value.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                itemsIndexed(requests.value) { index, request ->
                    RequestItem(
                        request = request,
                        onClick = { onRequestClick(request.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }
        } else if (isConnected.value) {
            Text(
                text = "No requests captured yet",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun RequestItem(request: ApiRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = request.method,
                    fontWeight = FontWeight.Bold,
                    color = when (request.method) {
                        "GET" -> MaterialTheme.colorScheme.primary
                        "POST" -> MaterialTheme.colorScheme.secondary
                        "PUT" -> MaterialTheme.colorScheme.tertiary
                        "DELETE" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )

                request.statusCode?.let { statusCode ->
                    Text(
                        text = statusCode.toString(),
                        fontSize = 12.sp,
                        color = when {
                            statusCode < 300 -> MaterialTheme.colorScheme.primary
                            statusCode < 400 -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = request.url,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(request.timestamp)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                request.duration?.let { duration ->
                    Text(
                        text = "${duration}ms",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun fetchRequests(
    client: OkHttpClient,
    gson: Gson,
    baseUrl: String,
    onResult: (Result<List<ApiRequest>>) -> Unit
) {
    val request = Request.Builder()
        .url("$baseUrl/requests")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult(Result.failure(e))
        }

        override fun onResponse(call: Call, response: Response) {
            try {
                val json = response.body?.string() ?: ""
                val type = object : TypeToken<List<ApiRequest>>() {}.type
                val requests = gson.fromJson<List<ApiRequest>>(json, type)
                onResult(Result.success(requests))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    })
}

private fun clearRequests(
    client: OkHttpClient,
    baseUrl: String,
    onResult: (Boolean) -> Unit
) {
    val request = Request.Builder()
        .url("$baseUrl/clear")
        .post(RequestBody.create(null, ""))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult(false)
        }

        override fun onResponse(call: Call, response: Response) {
            onResult(response.isSuccessful)
        }
    })
}