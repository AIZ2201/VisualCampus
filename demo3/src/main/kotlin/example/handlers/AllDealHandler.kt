package example.handlers

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

// 修改为 AllDealSend 数据类
data class AllDealSend(
    val operation: String,
    val cardNumber: Int,
    val password: String
)

// 修改为 AllDealBack 数据类
data class AllDealBack(
    val status: String,
    val transactions: List<Transaction>  // 修改为 Deal 列表
)

// 修改为 AllDealHandler 类
class AllDealHandler {
    val PUBLIC_KEY_STRING: String = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5t7j8a8qpvDcDq4ecSGleaww5MfBG/PeizhryjFPi6hXkyKKfLNCkVp2tH2HQCv6wdCILBVQvkEmtuGISnYiHZfh09EqJ+2bnFhY8TqExbvOCiiTimRXpCGMdbRDce07jD1q/tt7RSFsbVwOgDdJi9/750Nh8yrY0YJwttoFYz6eaAqHKxc/L/W8h1MvdrCaMjmwrz/rvXn7emn5YjdrdTibAmhRDhL4+t6/qk8sAmOoaSpOE0pSiLc/qMj35FIEzopdemJh1PVGC2vmdbw7yXtJeTXjo59OPAfmDKGfZMQty8WL7MEF5pbA0zfrueqSWP+bgdziKk+053G394vR5wIDAQAB"

    @Throws(java.lang.Exception::class)
    private fun encryptPassword(password: String, publicKeyString: String): String {
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyString)
        val spec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(spec)

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val encryptedBytes = cipher.doFinal(password.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    // 修改为处理 AllDealSend 数据的方法
    fun handleAction(data: AllDealSend): AllDealBack? {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // 添加 KotlinJsonAdapterFactory
            .build()

        // 加密密码
        val sendData = AllDealSend(data.operation, data.cardNumber, encryptPassword(data.password, PUBLIC_KEY_STRING))

        // 序列化
        val jsonAdapter = moshi.adapter(AllDealSend::class.java)
        val requestJson = jsonAdapter.toJson(sendData)
        println("toJson = $requestJson")

        val responseJson = sendToServer(requestJson)
        println(responseJson)

        val jsonAdapter1 = moshi.adapter(AllDealBack::class.java)
        val showDealsBack = jsonAdapter1.fromJson(responseJson)
        return showDealsBack
    }
}

// 发送数据到服务器的方法
private fun sendToServer(jsonData: String): String {
    val serverAddress = "10.208.72.178" // 域名
    val serverPort = 4444 // 端口
    return try {
        // 创建 socket 连接服务器
        val socket = Socket(serverAddress, serverPort)

        // 发送数据
        val outputStream = socket.getOutputStream()
        val writer = OutputStreamWriter(outputStream, "UTF-8")
        val updateData = jsonData + '\n'
        writer.write(updateData)
        writer.flush()
        println("Data sends to server")

        // 接收服务器返回的数据
        val inputStream = socket.getInputStream()
        val reader = InputStreamReader(inputStream, "UTF-8")
        val response = reader.readText()

        // 关闭资源
        println("关闭")
        writer.close()
        reader.close()
        socket.close()

        response
    } catch (e: Exception) {
        e.printStackTrace()
        println("failed to send")
        ""
    }
}
