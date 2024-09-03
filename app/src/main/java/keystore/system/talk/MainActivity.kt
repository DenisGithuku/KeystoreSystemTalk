package keystore.system.talk

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import keystore.system.talk.ui.theme.KeystoreSystemTalkTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val cryptoManager = CryptoManager()
    private val Context.dataStore by dataStore(
        fileName = "app_data.json", serializer = AppDataSerializer(cryptoManager)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KeystoreSystemTalkTheme {
                var serverUrl by remember {
                    mutableStateOf("")
                }
                var accessToken by remember {
                    mutableStateOf("")
                }

                var appData by remember {
                    mutableStateOf(AppData())
                }

                val scope = rememberCoroutineScope()


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(modifier = Modifier.fillMaxWidth(),
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        placeholder = { Text(text = "Server url") })
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(modifier = Modifier.fillMaxWidth(),
                        value = accessToken,
                        onValueChange = { accessToken = it },
                        placeholder = { Text(text = "Access token") })
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            if (serverUrl.isEmpty() || accessToken.isEmpty()) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Invalid data",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            scope.launch {
                                dataStore.updateData {
                                    AppData(
                                        serverUrl, accessToken
                                    )
                                }.also {
                                    serverUrl = ""
                                    accessToken = ""
                                }
                            }
                        }) {
                            Text(text = "Persist")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = {
                            scope.launch {
                                Log.d("dat", dataStore.data.first().toString())
                                appData = dataStore.data.first()
                            }
                        }) {
                            Text(
                                text = "Load"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = appData.toString()
                    )
                }
            }
        }
    }
}
