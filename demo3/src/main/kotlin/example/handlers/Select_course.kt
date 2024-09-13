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

data class select_page(
    val operation: String,
)
data class ResponseSelectPage(
    val status: String,
    val courses: List<SelectCourse> // 保持 List 类型不变
)

data class SelectCourse(
    val courseID: String,
    val courseName: String,
    val credit: Double,
    val teachers: List<TeacherInfo> // 将 teacherName 改为包含多个教师的列表
)

// 定义教师信息的数据类
data class TeacherInfo(
    val teacherName: String,
    val classroomName: String,
    val capacity: Int,
    val weekRange: String,
    val dayOfWeek: List<String>,
    val courseBegin: List<Int>,
    val courseEnd: List<Int>,
    val selectCount: Int
)


// 数据类定义，调整 SelectionTeacher 以支持多个时间段
data class selet_enter(
    val cardNumber: String,
    val operation: String,
    val courseName: String,
)

data class Server_select_Response(
    val status: String,
    val selected: List<SelectedCourse>
)

data class SelectedCourse(
    val courseName: String,
    val teacherName: String,
    val selectCount: Int,
    val isSelected: Boolean? = false // 修改为可选类型，默认值为 false
)

// 数据类定义，包含所有课程和教师信息
data class FullSelectInfo(
    val cardNumber: String,
    val operation: String,
    val courses: List<SelectionCourse> // 包含所有课程及其详细信息
)

// 保持原有的结构
data class SelectionCourse(
    val name: String,
    val code: String,
    val credit: String,
    val teachers: List<SelectionTeacher>
)

data class SelectionTeacher(
    val name: String,
    val weekRange: String, // 例如 "1-4周"
    val classDates: List<ClassTime>, // 包含多个上课时间段
    val classroomName: String,
    val capacity: Int,
    val selectedCount: Int,
    val conflict: Boolean = false,
    val selected: Boolean = false
)

data class ClassTime(
    val dayOfWeek: String, // 例如 "周一"
    val courseBegin: Int,  // 开始节次
    val courseEnd: Int     // 结束节次
)
data class DeselectInfo(
    val cardNumber: String,
    val operation: String,
    val courseName: String
)

class My_Select {

    //处理点击页面框发送的操作
    suspend fun send_select_page(operation: String): ResponseSelectPage? {
        val send_select_page = select_page(operation)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(select_page::class.java)
        val toJson = jsonAdapter.toJson(send_select_page)
        println("Serialized JSON: $toJson")
        return withContext(Dispatchers.IO) {
            val serverAddress = "10.208.72.178"
            val serverPort = 4444
            var server_select_Response: ResponseSelectPage? = null
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
                    val responseAdapter = moshi.adapter(ResponseSelectPage::class.java)
                    server_select_Response = responseAdapter.fromJson(response)

                }
            } catch (e: IOException) {
                e.printStackTrace()
                println("Network error: Failed to send data to server.")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Unexpected error: ${e.message}")
            }
            server_select_Response // 返回反序列化后的服务器响应

        }
    }



    // 处理点击课程框发送的操作并接收回复
    suspend fun send_select_enter(cardNumber: String, operation: String, courseName: String): Server_select_Response? {
        val enterInfo = selet_enter(
            cardNumber = cardNumber,
            operation = operation,
            courseName = courseName
        )

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter = moshi.adapter(selet_enter::class.java)
        val toJson = jsonAdapter.toJson(enterInfo)
        println("Serialized JSON: $toJson")

        return withContext(Dispatchers.IO) {
            val serverAddress = "10.208.72.178"
            val serverPort = 4444
            var serverResponse: Server_select_Response? = null
            try {
                Socket(serverAddress, serverPort).use { socket ->
                    // 发送数据到服务器
                    val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                    writer.write("$toJson\n")
                    writer.flush()
                    println("Data sent to server")

                    // 接收服务器的响应
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                    val response = reader.readLine()  // 读取服务器的响应
                    println("Response from server: $response")

                    // 反序列化服务器的响应
                    val responseAdapter = moshi.adapter(Server_select_Response::class.java)
                    serverResponse = responseAdapter.fromJson(response)
                    print(serverResponse?.status)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                println("Network error: Failed to send data to server.")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Unexpected error: ${e.message}")
            }
            serverResponse  // 返回反序列化后的服务器响应

        }
    }

    // 处理选课或退选的函数
    suspend fun sendInfo(selectionCourses: List<SelectionCourse>, cardNumber: String, operation: String) {
        val filteredCourses = if (operation == "student_deselect") {
            selectionCourses.filter { course ->
                course.teachers.any { it.selected || it.selectedCount > 0 }
            }.map { course ->
                course.copy(teachers = course.teachers.filter { it.selected || it.selectedCount > 0 })
            }
        } else {
            selectionCourses.map { course ->
                course.copy(teachers = course.teachers.filter { it.selected })
            }.filter { it.teachers.isNotEmpty() }
        }

        val fullSelectInfo = FullSelectInfo(
            cardNumber = cardNumber,
            operation = operation,
            courses = filteredCourses
        )

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter = moshi.adapter(FullSelectInfo::class.java)
        val toJson = jsonAdapter.toJson(fullSelectInfo)
        println("Serialized JSON: $toJson")
        withContext(Dispatchers.IO) {
            sendToSelect_server(toJson)
        }
    }

    // 处理退选操作的函数
    suspend fun sendDeselectInfo(cardNumber: String, courseName: String) {
        val deselectInfo = DeselectInfo(
            cardNumber = cardNumber,
            operation = "student_deselect",
            courseName = courseName
        )

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter = moshi.adapter(DeselectInfo::class.java)
        val toJson = jsonAdapter.toJson(deselectInfo)
        println("Serialized JSON: $toJson")

        withContext(Dispatchers.IO) {
            sendToSelect_server(toJson)
        }
    }


    private fun sendToSelect_server(jsonData: String) {
        val serverAddress = "10.208.72.178"
        val serverPort = 4444
        try {
            Socket(serverAddress, serverPort).use { socket ->
                val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                writer.write("$jsonData\n")
                writer.flush()
                println("Data sent to server")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Network error: Failed to send data to server.")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Unexpected error: ${e.message}")
        }
    }
}





