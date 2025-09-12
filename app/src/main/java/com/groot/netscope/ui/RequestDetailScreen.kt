package com.groot.netscope.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.groot.netscope.models.ApiRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(requestId: String, onBackClick: () -> Unit) {
    var request by remember { mutableStateOf<ApiRequest?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // In a real app, you'd fetch this from the connected device
    // For now, we'll just show the structure

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            request?.let { req ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Request Summary
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Request Summary", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            DetailRow("Method", req.method)
                            DetailRow("URL", req.url)
                            req.statusCode?.let { DetailRow("Status Code", it.toString()) }
                            DetailRow("Timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(req.timestamp)))
                            req.duration?.let { DetailRow("Duration", "${it}ms") }
                            req.error?.let { DetailRow("Error", it) }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Request Headers
                    if (req.headers.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Request Headers", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                req.headers.forEach { (key, values) ->
                                    DetailRow(key, values.joinToString(", "))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Request Body
                    req.requestBody?.let { body ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Request Body", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = body,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Response Headers
                    req.responseHeaders?.let { headers ->
                        if (headers.isNotEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Response Headers", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    headers.forEach { (key, values) ->
                                        DetailRow(key, values.joinToString(", "))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Response Body
                    req.responseBody?.let { body ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Response Body", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = body,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}