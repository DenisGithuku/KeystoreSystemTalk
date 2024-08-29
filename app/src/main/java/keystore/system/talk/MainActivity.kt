package keystore.system.talk

import android.os.Bundle
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import keystore.system.talk.ui.theme.KeystoreSystemTalkTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cryptoManager = CryptoManager()
        enableEdgeToEdge()
        setContent {
            KeystoreSystemTalkTheme {
                val context = LocalContext.current

                var messageToEncrypt by remember {
                    mutableStateOf("")
                }
                var messageToDecrypt by remember {
                    mutableStateOf("")
                }


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = messageToEncrypt,
                        onValueChange = { messageToEncrypt = it },
                        placeholder = { Text(text = "Enter message to encrypt") })
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (messageToEncrypt.isEmpty()) {
                                    Toast.makeText(context, "Please enter a valid message", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                // Convert string to byte array
                                val bytes = messageToEncrypt.encodeToByteArray()
                                val file = File(filesDir, "secret.txt")
                                if (!file.exists()) {
                                    file.createNewFile()
                                }
                                val fileOutputStream = FileOutputStream(file)
                                messageToDecrypt = cryptoManager.encrypt(bytes,fileOutputStream).decodeToString()
                                messageToEncrypt = ""
                            }
                        ) {
                            Text(
                                text = "Encrypt"
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                val file = File(filesDir, "secret.txt")
                                messageToEncrypt = cryptoManager.decrypt(
                                    inputStream = FileInputStream(file)
                                ).decodeToString()
                                messageToDecrypt = ""
                            }
                        ) {
                            Text(text = "Decrypt")
                        }
                    }
                    Text(
                        text = messageToDecrypt
                    )
                }
            }
        }
    }
}
