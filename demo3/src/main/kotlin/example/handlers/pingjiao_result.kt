package example.handlers

// 文件：pingjiao_result.kt

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import example.MyclassDate_list
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

// 数据类定义



//点击课程时发送
// 主响应数据类
data class Requestpingjiao_result(
    val operation: String,
)
data class pingjiao_result_response(
    val status: String,
    val results: List<Result>
)

// 课程信息的数据类
data class Result(
    val courseName: String,
    val teacherName: String,
    val averageScores: List<Int>  // 将类型改为 List<Int> 以匹配 JSON 数据
)

class Pingjiao{
    //点击评教页面框后发送操作
    suspend fun send_Pingjiao():  pingjiao_result_response? {
        // 定义 cardNumber 和 operation
        val operation = "check_evaluate"

        // 创建 RequestData 实例
        val requestData =Requestpingjiao_result(operation = operation)

        // 创建 Moshi 实例
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // 将 requestData 序列化为 JSON 字符串
        val jsonAdapter = moshi.adapter(Requestpingjiao_result::class.java)
        val toJson = jsonAdapter.toJson(requestData)
        println("Serialized JSON: $toJson")

        // 发送数据到服务器并返回反序列化后的响应
        return withContext(Dispatchers.IO) {
            sendToPingjiao_server(toJson)
        }
    }
    private fun sendToPingjiao_server(jsonData: String):pingjiao_result_response? {
        val serverAddress = "10.208.72.178"
        val serverPort = 4444
        var serverResponse: pingjiao_result_response? = null
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

                val jsonAdapter = moshi.adapter(pingjiao_result_response::class.java)
                serverResponse = jsonAdapter.fromJson(response)


            }

        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to send")
        }
        return serverResponse
    }
}

