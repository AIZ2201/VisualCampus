package example.handlers

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.core.*
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.videoio.VideoCapture
import java.io.*
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*


data class ResponseCVanswer(
    val status: String,
    val message: String,
)

data class FaceData(val operation: String, val cardNumber: Int, val image: String)

class My_openCV {

    // 初始化人脸探测器
    private lateinit var faceDetector: CascadeClassifier
    private val PATH_PREFIX = "./face"
    private var i = 0

    // 初始化标志位，标志检测完成
    private var isFaceDetectionComplete = false

    // 初始化
    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        // 加载人脸检测分类器
        val property = "./opencv/build/etc/haarcascades/haarcascade_frontalface_alt.xml"
        println(property)
        faceDetector = CascadeClassifier(property)

        // 创建文件夹（如果不存在）
        File(PATH_PREFIX).mkdirs()
    }

    // 视频捕获函数
    fun getVideoFromCamera() {
        val capture = VideoCapture(0)  // 打开摄像头
        val video = Mat()
        if (capture.isOpened) {
            while (!isFaceDetectionComplete) {  // 检测到人脸后退出循环
                capture.read(video)
                if (!video.empty()) {
                    HighGui.imshow("Face_capture", getFace(video))
                }
                // 增大 waitKey 的时间，确保窗口有足够时间刷新
                val index = HighGui.waitKey(300)  // 增加到30毫秒或更长时间
                if (index == 3) {  // 按下 ESC 键时退出
                    break
                }
            }
        } else {
            println("摄像头未开启")
        }

        // 释放摄像头资源
        capture.release()

        println("摄像头已关闭")

        // 引入5秒延迟
        println("将在5秒后尝试关闭窗口")
        // 尝试关闭窗口
        HighGui.destroyWindow("Face_capture")
    }

    // 人脸检测函数
    private fun getFace(image: Mat): Mat {
        if (isFaceDetectionComplete) {
            return image  // 如果检测完成，直接返回
        }

        val face = MatOfRect()
        faceDetector.detectMultiScale(image, face)
        val rects = face.toArray()
        println("匹配到 ${rects.size} 个人脸")

        if (rects.isNotEmpty()) {
            rects.forEach { rect ->
                Imgproc.rectangle(
                    image,
                    Point(rect.x.toDouble(), rect.y.toDouble()),
                    Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                    Scalar(0.0, 255.0, 0.0)  // 绿色框
                )
                Imgproc.putText(
                    image, "Human", Point(rect.x.toDouble(), rect.y.toDouble()),
                    Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX, 1.0, Scalar(0.0, 255.0, 0.0), 1, Imgproc.LINE_AA, false
                )
            }
            i++

            if (i == 3) {
                // 保存图像
                val saved = Imgcodecs.imwrite("$PATH_PREFIX/face.jpg", image)
                if (saved) {
                    println("Image successfully saved at $PATH_PREFIX/face.jpg")
                } else {
                    println("Failed to save image at $PATH_PREFIX/face.jpg")
                }

                // 设置标志位，检测完成，退出识别
                isFaceDetectionComplete = true
            }
        }

        return image
    }

    // 连接服务器并发送数据
    suspend fun connectToServerAndSendData(cardNumber: Int): ResponseCVanswer? {
        return withContext(Dispatchers.IO) {
            var serverResponse: ResponseCVanswer? = null
            val serverAddress = "10.208.72.178"
            val serverPort = 4444
            try {
                // 构建Socket连接
                Socket(serverAddress, serverPort).use { socket ->
                    println("Connected to server.")

                    // 创建Moshi对象
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter: JsonAdapter<FaceData> = moshi.adapter(FaceData::class.java)

                    // 构建 FaceData 对象
                    val imageFile = File("$PATH_PREFIX/face.jpg")
                    if (!imageFile.exists()) {
                        println("Image file not found: $imageFile")
                        return@withContext null
                    }

                    // 读取图片并转换为 Base64 编码
                    val imageBytes = Files.readAllBytes(imageFile.toPath())
                    val encodedImage = Base64.getEncoder().encodeToString(imageBytes)

                    // 序列化成 JSON 字符串
                    val faceData = FaceData("save_image", cardNumber, encodedImage)
                    val jsonString = jsonAdapter.toJson(faceData)
                    println("Serialized JSON: $jsonString")

                    // 发送数据到服务器
                    val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                    writer.write("$jsonString\n")
                    writer.flush()
                    println("Data sent to server")

                    // 接收服务器的响应
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                    val response = reader.readLine()
                    println("Response from server: $response")

                    // 反序列化服务器的响应
                    val responseAdapter = moshi.adapter(ResponseCVanswer::class.java)
                    serverResponse = responseAdapter.fromJson(response)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                println("Network error: Failed to send data to server.")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Unexpected error: ${e.message}")
            }
            serverResponse // 返回反序列化后的服务器响应
        }
    }
}

