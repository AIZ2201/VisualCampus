package example.handlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import example.MyclassDate_list
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

data class send_change_password(
    val operation:String,
    val cardNumber:String,
    val new_password:String,
)

data class response_change_password(
    val status:String,
)

class change_password {
    //处理点击发送按钮的事件
    suspend fun sendToserver_newpassword(operation: String, cardNumber: String,new_password: String) :response_change_password?{
        val send_newpassword=send_change_password(operation,cardNumber,new_password)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(send_change_password::class.java)
        val toJson = jsonAdapter.toJson(send_newpassword)
        println("Serialized JSON: $toJson")
        return withContext(Dispatchers.IO) {
            val serverAddress = "10.208.72.178"
            val serverPort = 4444
            var server_password_Response: response_change_password? = null
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
                    val responseAdapter = moshi.adapter(response_change_password::class.java)
                    server_password_Response = responseAdapter.fromJson(response)

                }
            } catch (e: IOException) {
                e.printStackTrace()
                println("Network error: Failed to send data to server.")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Unexpected error: ${e.message}")
            }
            server_password_Response // 返回反序列化后的服务器响应

        }

    }

}