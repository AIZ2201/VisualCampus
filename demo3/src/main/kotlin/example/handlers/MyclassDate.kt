package example.handlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import example.MyclassDate_list
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import example.Mygrade_list
import example.User
import org.jetbrains.annotations.Async

data class Request_classData(
    val cardNumber: String,
    val operation: String
)

data class class_date(
    val courseName: String,
    val classroomName: String,
    val classDate: Int,
    val courseBegin: Int,
    val courseEnd: Int,
)

data class ServerResponse_classdate(
    val status: String,
    val schedule: List<class_date>?
)

class MyclassDate {
    suspend fun send_dateInfo(user: User): ServerResponse_classdate? {
        // 定义 cardNumber 和 operation
        val cardNumber = user.cardnumber.toString()
        val operation = "check_schedule"

        // 创建 RequestData 实例
        val requestData = Request_classData(operation = operation, cardNumber = cardNumber)

        // 创建 Moshi 实例
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // 将 requestData 序列化为 JSON 字符串
        val jsonAdapter = moshi.adapter(Request_classData::class.java)
        val toJson = jsonAdapter.toJson(requestData)
        println("Serialized JSON: $toJson")

        // 发送数据到服务器并返回反序列化后的响应
        return withContext(Dispatchers.IO) {
            sendToclassdate_server(toJson)
        }
    }

    private fun sendToclassdate_server(jsonData: String): ServerResponse_classdate? {
        val serverAddress = "10.208.72.178"
        val serverPort = 4444
        var serverResponse: ServerResponse_classdate? = null
        try {
            Socket(serverAddress, serverPort).use { socket ->
                val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                writer.write("$jsonData\n")
                writer.flush()
                println("Data sent to server")

                // 接收服务器响应
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                val response = reader.readLine()

                // 打印服务器响应
                println("从服务器接收到的响应: $response")

                // 解析并反序列化
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                val jsonAdapter = moshi.adapter(ServerResponse_classdate::class.java)
                serverResponse = jsonAdapter.fromJson(response)

                // 检查反序列化后的对象，并处理数据
                serverResponse?.let {
                    when (it.status) {
                        "success" -> {
                            it.schedule?.let { schedule ->
                                MyclassDate_list.updateDate(schedule) // 更新到单例对象中
                                println("课表已更新")
                            } ?: run {
                                println("课表为空")
                                MyclassDate_list.updateDate(emptyList()) // 确保课表为空时清空数据
                            }
                        }
                        "not_found" -> {
                            println("未找到课程")
                            MyclassDate_list.updateDate(emptyList()) // 清空课表数据
                        }
                        else -> {
                            println("服务器返回未知状态: ${it.status}")
                        }
                    }
                } ?: run {
                    println("反序列化失败")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to send")
        }
        return serverResponse
    }
}


