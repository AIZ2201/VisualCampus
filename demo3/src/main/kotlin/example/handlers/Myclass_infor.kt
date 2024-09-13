package example.handlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import example.Myclass_list
import example.User
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import example.class_info
data class Request_classinfor(
    val cardNumber: String,
    val operation: String
)

data class class_info(
    val courseName: String,
    val teacherName: String,
    val qqGroup:Int,
    val classroomName:String,
    val duration:String,
    val week:List<String>,
    val time:List<String>,
    val introduction:String,
)

data class ServerResponse_classinfo(
    val status: String,
    val mycourse: List<example.handlers.class_info>
)

class Myclass_infor {
    suspend fun send_classInfo(user: User) {
        // 定义 cardNumber 和 operation
        val cardNumber = user.cardnumber.toString()
        val operation = "check_class"

        // 创建 RequestData 实例
        val requestData = Request_classinfor(operation = operation,cardNumber = cardNumber)

        // 创建 Moshi 实例
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // 将 requestData 序列化为 JSON 字符串
        val jsonAdapter = moshi.adapter(Request_classinfor::class.java)
        val toJson = jsonAdapter.toJson(requestData)
        println("Serialized JSON: $toJson")

        // 发送数据到服务器
        withContext(Dispatchers.IO) {
            sendToclassinfo_server(toJson)
        }
    }
    private fun sendToclassinfo_server(jsonData: String) {
        val serverAddress = "10.208.72.178"
        val serverPort = 4444
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

                val jsonAdapter = moshi.adapter(ServerResponse_classinfo::class.java)
                val serverResponse = jsonAdapter.fromJson(response)
                // 检查反序列化后的对象，并处理数据
                serverResponse?.let {
                    if (it.status == "success") {
                         Myclass_list.updateClassInfo(it.mycourse)// 更新到单例对象中

                    } else {
                        println("服务器返回错误状态: ${it.status}")
                    }
                } ?: run {
                    println("反序列化失败")
                }
            }


        }  catch (e: Exception) {
            e.printStackTrace()
            println("Failed to send")
        }
    }
}