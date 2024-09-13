package example.handlers

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

class LoginHandler {
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
    //处理点击登录发送用户名和密码
    suspend fun sendLogin(operation: String, cardNumber: String, password: String):LoginBackData? {
        //处理点击页面框发送的操作
        val send_Login = LoginData(operation,cardNumber,encryptPassword(password,PUBLIC_KEY_STRING))
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(LoginData::class.java)
        val toJson = jsonAdapter.toJson(send_Login)
        println("Serialized JSON: $toJson")
        return withContext(Dispatchers.IO) {
            val serverAddress = "10.208.72.178"
            val serverPort = 4444
            var server_Login: LoginBackData? = null
            try {
                Socket(serverAddress, serverPort).use { socket ->
                    // 发送数据到服务器
                    val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                    writer.write("$toJson\n")
                    writer.flush()
                    println("Data sent to server")

                    // 接收服务器的响应
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                    val response = reader.readLine()
                    println("Response from server: $response")

                    // 反序列化服务器的响应
                    val responseAdapter = moshi.adapter(LoginBackData::class.java)
                    server_Login = responseAdapter.fromJson(response)

                }
            } catch (e: IOException) {
                e.printStackTrace()
                println("Network error: Failed to send data to server.")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Unexpected error: ${e.message}")
            }
            server_Login // 返回反序列化后的服务器响应

        }
    }
}




// 定义一个数据类来封装登录信息
data class LoginData(
    val operation: String,
    val cardNumber: String,
    val password: String
)

//定义一个数据类接收服务器返回信息：
data class LoginBackData(
    val status: String,
    val cardNumber: Int? = null,
    val password: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val role: String? = null,
    val balance: String? = null,
)

