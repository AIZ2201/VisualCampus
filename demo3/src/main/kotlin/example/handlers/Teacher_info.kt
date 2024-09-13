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


data class sendTeacherinfo(
    val operation: String,
    val teacherName:String,
)

data class Course_Student(
    val cardNumber: String,  // 学号
    val name: String         // 学生姓名
)

data class TeacherCourse(
    val courseName: String,           // 课程名称
    val classroomName: String,        // 教室名称
    val capacity: Int,                // 教室容量
    val weekRange: String,            // 周次范围
    val courseDate: List<List<List<String>>>?, // 修改为嵌套列表以适应 JSON
    val selectCount: Int,             // 选课人数
    val students: List<Course_Student> // 选修该课程的学生列表
)

data class ResponseTeacher(
    val status: String,            // 请求的状态
    val teacher: List<TeacherCourse> // 教师教授的课程信息
)


class Teacher_info {
    //处理点击教师页面处理的信息
    suspend fun send_Teacher(operation: String, teacherName: String):ResponseTeacher? {
        val send_Teacher_info = sendTeacherinfo(operation, teacherName)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(sendTeacherinfo::class.java)
        val toJson = jsonAdapter.toJson(send_Teacher_info)
        println("Serialized JSON: $toJson")
        return withContext(Dispatchers.IO) {
            val serverAddress = "10.208.72.178"
            val serverPort = 4444
            var server_Teacher_esponse: ResponseTeacher? = null
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
                    val responseAdapter = moshi.adapter(ResponseTeacher::class.java)
                    server_Teacher_esponse = responseAdapter.fromJson(response)

                }
            } catch (e: IOException) {
                e.printStackTrace()
                println("Network error: Failed to send data to server.")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Unexpected error: ${e.message}")
            }

            server_Teacher_esponse
        }
    }
}