package example.handlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import example.Myclass_list
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

data class pingjiao_info(
    val operation: String,
    val cardNumber: String
)

data class NeedEvaluated(
    val courseName: String,
    val teacherName: String
)

data class receive_Status(
    val status: String,
    val need_evaluated: List<NeedEvaluated>
)


data class Request_pingjiao(
    val operation: String,
    val cardNumber: String,
    val teacherName: String,
    val courseName: String,
    val scores: List<Int>,
)

data class ResponseStatus(
    val status: String,
    val message: String
)
class My_pingjiao {

    // 发送评教信息并接收 receive_Status 类型的响应
    suspend fun sendpingjiao_info(sendpingjiao_Info: pingjiao_info): receive_Status? {
        return withContext(Dispatchers.IO) {
            // 使用 Moshi 序列化对象为 JSON
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = moshi.adapter(pingjiao_info::class.java)
            val jsonData = jsonAdapter.toJson(sendpingjiao_Info) // 使用正确的变量名

            // 发送 JSON 数据到服务器并返回响应
            sendToserver_pingjiao_forReceiveStatus(jsonData)
        }
    }

    // 发送请求并接收 ResponseStatus 类型的响应
    suspend fun sendInfo(request: Request_pingjiao): ResponseStatus {
        return withContext(Dispatchers.IO) {
            // 使用 Moshi 序列化对象为 JSON
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = moshi.adapter(Request_pingjiao::class.java)
            val jsonData = jsonAdapter.toJson(request)

            // 发送 JSON 数据到服务器并返回响应
            sendToserver_pingjiao(jsonData)
        }
    }

    // 私有函数：发送数据并接收 ResponseStatus 类型的响应
    private fun sendToserver_pingjiao(jsonData: String): ResponseStatus {
        val serverAddress = "10.208.72.178"
        val serverPort = 4444
        return try {
            Socket(serverAddress, serverPort).use { socket ->
                val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                writer.write("$jsonData\n")
                writer.flush()
                println("Data sent to server")

                // 接收服务器响应
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                val response = reader.readLine()

                // 解析并反序列化服务器响应
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val responseAdapter = moshi.adapter(ResponseStatus::class.java)
                val responseStatus = responseAdapter.fromJson(response)

                // 返回解析后的响应对象
                responseStatus ?: ResponseStatus("failed", "Invalid response from server")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to send")
            // 返回失败状态
            ResponseStatus("failed", "Failed to send data to server")
        }
    }

    // 新增函数：发送数据并接收 receive_Status 类型的响应
    private fun sendToserver_pingjiao_forReceiveStatus(jsonData: String): receive_Status? {
        val serverAddress = "10.208.72.178"
        val serverPort = 4444
        return try {
            Socket(serverAddress, serverPort).use { socket ->
                val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                writer.write("$jsonData\n")
                writer.flush()
                println("Data sent to server")

                // 接收服务器响应
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                val response = reader.readLine()
                println(response)
                // 解析并反序列化服务器响应
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val responseAdapter = moshi.adapter(receive_Status::class.java)
                val receiveStatus = responseAdapter.fromJson(response)

                // 返回解析后的响应对象
                receiveStatus
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to send")
            // 返回 null 表示失败
            null
        }
    }
}

