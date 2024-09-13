package example.handlers
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import example.StudentSend
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher



class RenewBookHandler  {

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

    fun handleAction(data:RenewBookSend) :String{

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // 添加 KotlinJsonAdapterFactory
            .build()
        //进行加密
        val senddata=RenewBookSend(data.operation,data.cardNumber,data.bookId)
        // 序列化
        val jsonAdapter = moshi.adapter(RenewBookSend::class.java)
        val requestjson = jsonAdapter.toJson(senddata)
        println("toJson = $requestjson")
        val responseJson=sendToserver(requestjson)

        println("我收到的json:$responseJson")
        val jsonAdapter1=moshi.adapter(RenewBookBack::class.java)
        val message=jsonAdapter1.fromJson(responseJson)
        if (message != null) {
            return  message.status
        }
        else
            return "server back error"
    }
}
private fun sendToserver(jsonData:String):String{
    val serverAddress="10.208.72.178"//域名
    val serverPort=4444//端口
    return try {
        //创建socket到服务器
        val socket = Socket(serverAddress, serverPort)
        //获取数据并发送
        val outputStream = socket.getOutputStream()
        val writer = OutputStreamWriter(outputStream, "UTF-8")
        val updatedata=jsonData+'\n'
        writer.write(updatedata)
        writer.flush()
        println("Data sends to server")

        //接受服务器返回的数据
        val inputStream=socket.getInputStream()
        val reader=InputStreamReader(inputStream,"UTF-8")
        val response=reader.readText()

        //关闭
        println("关闭")
        writer.close()
        reader.close()
        socket.close()

        response
    }//捕获异常
    catch (e: Exception){
        e.printStackTrace()
        println("failed to send ")
        ""
    }
}

data class RenewBookSend(
    val operation: String,
    val cardNumber: Int,
    val bookId:String
)

data class RenewBookBack(
    val status:String
)