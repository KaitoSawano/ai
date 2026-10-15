package com.attic.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ChatScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    val messages by vm.messages.collectAsState()
    val loading by vm.loading.collectAsState()
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Attic Ai") }) }
    ) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().padding(8.dp)
        ) {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                reverseLayout = false
            ) {
                items(messages) { m ->
                    Row(
                        Modifier.fillMaxWidth().padding(4.dp),
                        horizontalArrangement = if (m.role == "user")
                            Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (m.role == "user")
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(m.text, Modifier.padding(10.dp))
                        }
                    }
                }
            }

            if (loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth().padding(4.dp))
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tulis pesan...") }
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        vm.send(input)
                        input = ""
                    },
                    enabled = !loading
                ) { Text("Kirim") }
            }
        }
    }
}
