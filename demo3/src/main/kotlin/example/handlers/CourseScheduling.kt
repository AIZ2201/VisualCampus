package example.handlers

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import example.MyclassDate_list
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

data class CourseSchedule(
    val operation:String,
    val courseName: String,//课程名
    val courseId: String,//课程ID
    val credit:Double,//课程学分
    val teacherName: String,//老师名
    val classroom: String,//教室位置
    val capacity: Int,//容量
    val weekRange: String,//周范围，如1-4周
    val classDates: List<Class_Time>, // 包含多个上课时间段
)

data class Class_Time(
    val dayOfWeek: String, // 例如 "周一"
    val courseBegin: Int,  // 开始节次
    val courseEnd: Int,     // 结束节次
)
data class Server_schedule_Response(
    val status: String,
    val message: String,
)
class Schedule{
    suspend fun send_schedule_Info(courseSchedule: CourseSchedule): Server_schedule_Response? {
        // 创建 Moshi 实例
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // 将 CourseSchedule 序列化为 JSON 字符串
        val jsonAdapter = moshi.adapter(CourseSchedule::class.java)
        val toJson = jsonAdapter.toJson(courseSchedule)
        println("Serialized JSON: $toJson")

        // 使用挂起函数执行网络操作
        return withContext(Dispatchers.IO) {
            sendToScheduleServer(toJson)
        }
    }

    private fun sendToScheduleServer(jsonData: String): Server_schedule_Response? {
        val serverAddress = "10.208.72.178"  // 替换为实际的服务器地址
        val serverPort = 4444  // 替换为实际的服务器端口
        try {
            Socket(serverAddress, serverPort).use { socket ->
                // 发送数据到服务器
                val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                writer.write("$jsonData\n")
                writer.flush()
                println("Data sent to server")

                // 接收服务器响应
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                val response = reader.readLine()
                println("从服务器接收到的响应: $response")

                // 解析并反序列化
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                val jsonAdapter = moshi.adapter(Server_schedule_Response::class.java)
                return jsonAdapter.fromJson(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to send data to server")
            return null
        }
    }
}