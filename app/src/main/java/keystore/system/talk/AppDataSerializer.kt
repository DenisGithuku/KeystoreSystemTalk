package keystore.system.talk

import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class AppData(
    val serverUrl: String? = null, val accessToken: String? = null
)

class AppDataSerializer(
    private val cryptoManager: CryptoManager
) : Serializer<AppData> {
    override val defaultValue: AppData
        get() = AppData()

    override suspend fun readFrom(input: InputStream): AppData {
        val decryptedBytes = cryptoManager.decrypt(input)
        return try {
            Json.decodeFromString(
                deserializer = AppData.serializer(),
                string = decryptedBytes.decodeToString()
            )
        } catch (exception: SerializationException) {
            // handle error gracefully
            exception.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppData, output: OutputStream) {
        cryptoManager.encrypt(
            byteArray = Json.encodeToString(
                serializer = AppData.serializer(),
                value = t
            ).encodeToByteArray(),
            outputStream = output
        )
    }
}