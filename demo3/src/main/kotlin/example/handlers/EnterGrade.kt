package example.handlers

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets


data class Enter_grade(
    val operation: String,
)
// 定义 courseName 对象类
data class Enter_Course(
    val courseName: String
)

// 修改 responseEntergrade 数据类
data class responseEntergrade(
    val status: String,
    val selected: List<Enter_Course> // 这里用对象列表代替字符串列表
)

class EnterGrade{
    //点击评教页面框后发送操作
    suspend fun send_Entergrade():  responseEntergrade? {
        // 定义 cardNumber 和 operation
        val operation = "send_course"

        // 创建 RequestData 实例
        val requestData =Enter_grade(operation = operation)

        // 创建 Moshi 实例
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // 将 requestData 序列化为 JSON 字符串
        val jsonAdapter = moshi.adapter(Enter_grade::class.java)
        val toJson = jsonAdapter.toJson(requestData)
        println("Serialized JSON: $toJson")

        // 发送数据到服务器并返回反序列化后的响应
        return withContext(Dispatchers.IO) {
            sendToEntergrade_server(toJson)
        }
    }
    private fun sendToEntergrade_server(jsonData: String): responseEntergrade? {
        val serverAddress = "10.208.72.178"
        val serverPort = 4444
        var serverResponse: responseEntergrade? = null
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

                val jsonAdapter = moshi.adapter(responseEntergrade::class.java)
                serverResponse = jsonAdapter.fromJson(response)


            }

        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to send")
        }
        return serverResponse
    }
}