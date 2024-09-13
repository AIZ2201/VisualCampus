package example.handlers

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import example.handlers.Goods
data class NewGoods(
    val name: String,
    val price: Double,
    val pictureLink: String,
    val stock: Int,
    val description: String,
    val select:String
)
data class AddGoodsSend(
    val operation: String,
    val cardNumber: Int,
    val password:String,
    val newGoods: NewGoods
)

data class AddGoodsBack(
    val status: String
)

class AddGoodsHandler {
    val PUBLIC_KEY_STRING: String = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5t7j8a8qpvDcDq4ecSGleaww5MfBG/PeizhryjFPi6hXkyKKfLNCkVp2tH2HQCv6wdCILBVQvkEmtuGISnYiHZfh09EqJ+2bnFhY8TqExbvOCiiTimRXpCGMdbRDce07jD1q/tt7RSFsbVwOgDdJi9/750Nh8yrY0YJwttoFYz6eaAqHKxc/L/W8h1MvdrCaMjmwrz/rvXn7emn5YjdrdTibAmhRDhL4+t6/qk8sAmOoaSpOE0pSiLc/qMj35FIEzopdemJh1PVGC2vmdbw7yXtJeTXjo59OPAfmDKGfZMQty8WL7MEF5pbA0zfrueqSWP+bgdziKk+053G394vR5wIDAQAB"

    @Throws(java.lang.Exception::class)
    private fun encryptPassword(password: String, publicKeyString: String): String {
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyString)
        val spec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(spec)

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val encryptedBytes = cipher.doFinal(password.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun handleAction(data: AddGoodsSend): String? {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // Adding KotlinJsonAdapterFactory
            .build()

        //进行加密
        val senddata=AddGoodsSend(data.operation,data.cardNumber,encryptPassword(data.password,PUBLIC_KEY_STRING),data.newGoods)

        // Serialize
        val jsonAdapter = moshi.adapter(AddGoodsSend::class.java)
        val requestJson = jsonAdapter.toJson(senddata)
        println("toJson = $requestJson")

        // Send data to server and receive response
        val responseJson = sendToServer(requestJson)
        val jsonAdapter1 = moshi.adapter(AddGoodsBack::class.java)
        println("Received JSON: $responseJson")

        return try {
            jsonAdapter1.fromJson(responseJson)?.status
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

private fun sendToServer(jsonData: String): String {
    val serverAddress = "10.208.72.178" // Domain
    val serverPort = 4444 // Port
    return try {
        // Create socket to server
        println("Creating socket")
        val socket = Socket(serverAddress, serverPort)

        // Send data
        println("Sending data")
        val outputStream = socket.getOutputStream()
        val writer = OutputStreamWriter(outputStream, "UTF-8")
        val updatedData = jsonData + '\n'
        writer.write(updatedData)
        writer.flush()

        println("Data sent to server")
        // Receive server response
        val inputStream = socket.getInputStream()
        val reader = InputStreamReader(inputStream, "UTF-8")
        val response = reader.readText()

        // Close connections
        println("Closing")
        writer.close()
        reader.close()
        socket.close()

        response
    } catch (e: Exception) {
        e.printStackTrace()
        println("Failed to send")
        ""
    }
}

