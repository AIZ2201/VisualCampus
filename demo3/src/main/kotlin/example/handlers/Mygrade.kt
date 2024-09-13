package example.handlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import example.Mygrade_list
import example.User

data class RequestData(
    val cardNumber: String,
    val operation: String
)

data class Course(
    val courseName: String,
    val credit: Double,
    val grade: Int,
    val regular_grade: Int,
    val midterm_grade: Int,
    val final_grade: Int
)

data class ServerResponse(
    val status: String,
    val courses: List<Course>
)


class Mygrade {
    suspend fun sendInfo(user: User) {
        // 定义 cardNumber 和 operation
        val cardNumber = user.cardnumber.toString()
        val operation = "check_score"

        // 创建 RequestData 实例
        val requestData = RequestData(operation = operation,cardNumber = cardNumber)

        // 创建 Moshi 实例
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // 将 requestData 序列化为 JSON 字符串
        val jsonAdapter = moshi.adapter(RequestData::class.java)
        val toJson = jsonAdapter.toJson(requestData)
        println("Serialized JSON: $toJson")

        // 发送数据到服务器
        withContext(Dispatchers.IO) {
            sendToteachingaffair_server(toJson)
        }
    }

    private fun sendToteachingaffair_server(jsonData: String) {
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

                val jsonAdapter = moshi.adapter(ServerResponse::class.java)
                val serverResponse = jsonAdapter.fromJson(response)

                // 检查反序列化后的对象，并处理数据
                serverResponse?.let {
                    if (it.status == "success") {
                        Mygrade_list.updateCourses(it.courses) // 更新到单例对象中
                        for (course in it.courses) {
                            println("Course: ${course.courseName}")
                            println("Credit: ${course.credit}")
                            println("Grade: ${course.grade}")
                            println("Regular Grade: ${course.regular_grade}")
                            println("Midterm Grade: ${course.midterm_grade}")
                            println("Final Grade: ${course.final_grade}")
                            println("-----------")
                        }
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
