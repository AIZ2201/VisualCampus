package example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.material.TextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerHoverIcon
import example.handlers.LoginHandler
import example.handlers.LoginBackData
import example.handlers.My_openCV
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import kotlinx.coroutines.launch
import example.handlers.change_password



private const val PUBLIC_KEY_STRING = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5t7j8a8qpvDcDq4ecSGleaww5MfBG/PeizhryjFPi6hXkyKKfLNCkVp2tH2HQCv6wdCILBVQvkEmtuGISnYiHZfh09EqJ+2bnFhY8TqExbvOCiiTimRXpCGMdbRDce07jD1q/tt7RSFsbVwOgDdJi9/750Nh8yrY0YJwttoFYz6eaAqHKxc/L/W8h1MvdrCaMjmwrz/rvXn7emn5YjdrdTibAmhRDhL4+t6/qk8sAmOoaSpOE0pSiLc/qMj35FIEzopdemJh1PVGC2vmdbw7yXtJeTXjo59OPAfmDKGfZMQty8WL7MEF5pbA0zfrueqSWP+bgdziKk+053G394vR5wIDAQAB" // 替换为生成的公钥

// 定义一个数据类来封装登录信息
data class LoginData(
    val operation:String,
    val cardNumber: Int,
    val password: String
)
//定义一个数据类处理返回信息
data class LoginBackData(
    val status: String,
    val cardNumber: Long,
    val password: String,
    val name: String,
    val gender: String,
    val phone: String,
    val email: String,
    val role: String
)


@Preview
@Composable
fun Login_App(onLoginSuccess: (User) -> Unit,onMessage:(String)->Unit) {
    // 定义状态变量
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) } // 控制登录失败弹窗
    var showForgotPasswordPage by remember { mutableStateOf(false) } // 控制显示“忘记密码”页面的状态

    // 加载图片资源
    val backgroundImage: Painter = painterResource("image/login_1.jpg")
    val logoImage: Painter = painterResource("image/schoolicon.png")
    val presentImage: Painter = painterResource("image/background_image.jpg")

    var cardNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // 显示错误提示对话框
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(text = "错误") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("确定")
                }
            }
        )
    }

    // 使用 Box 作为容器来叠加布局
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 设置背景图片
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize().blur(radius = 16.dp)
        )

        // 中央登录表单区域
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.6f)
                .align(Alignment.Center)
        ) {
            Image(
                painter = presentImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // 在白色画布内布局
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp)
                    .align(Alignment.CenterStart)
            ) {
                // 校徽图片
                Image(
                    painter = logoImage,
                    contentDescription = "校徽",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(bottom = 16.dp)
                )

                // 输入文本框1 - 用户名
                TextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("用户名", color = Color.Black) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Gray,
                        textColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(bottom = 8.dp)
                )

                // 输入文本框2 - 密码
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码", color = Color.Black) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Gray,
                        textColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(bottom = 16.dp)
                )

                // 登录按钮
                Button(
                    onClick = {
                        when {
                            cardNumber.isEmpty() -> {
                                errorMessage = "用户名不能为空"
                                showErrorDialog = true
                            }
                            password.isEmpty() -> {
                                errorMessage = "密码不能为空"
                                showErrorDialog = true
                            }
                            !cardNumber.matches(Regex("\\d{9}")) -> {
                                errorMessage = "用户名必须是9位数字"
                                showErrorDialog = true
                            }
                            else -> {
                                val Login = LoginHandler()
                                scope.launch {
                                    val response = Login.sendLogin("login_submit", cardNumber, password)
                                    if (response != null) {
                                        if (response.status == "success") {
                                            println(response)
                                            val user=User(response.cardNumber!!,response.name!!,response.password!!,response.role!!)
                                            onLoginSuccess(user)  // 调用成功的处理
                                            onMessage("home")
                                        } else {
                                            // 显示登录失败对话框，并清空密码框
                                            showDialog.value = true
                                            password = ""  // 清空密码框
                                        }
                                    } else {
                                        // 网络或服务器异常处理
                                        errorMessage = "服务器无响应"
                                        showErrorDialog = true
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.3f)
                ) {
                    Text("登录")
                }

                // 忘记密码链接
                Text(
                    text = "忘记密码",
                    color = Color.Blue,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            showForgotPasswordPage = true  // 点击后显示“忘记密码”页面
                        }
                        .pointerHoverIcon(PointerIcon.Hand)
                )
            }
        }

        // 如果点击了“忘记密码”，显示额外页面
        if (showForgotPasswordPage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000))  // 半透明背景
                    .align(Alignment.Center)         // 居中显示
            ) {
                ForgotPasswordPage(onClose = {
                    showForgotPasswordPage = false
                    // 返回后清空用户名和密码框
                    cardNumber = ""
                    password = ""
                })  // 传递关闭事件并清空输入框
            }
        }
    }

    // 显示登录失败提示对话框
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "登录失败") },
            text = { Text("一卡通号或密码输入有误。") },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("确定")
                }
            }
        )
    }
}




@Composable
fun ForgotPasswordPage(onClose: () -> Unit) {
    val scope = rememberCoroutineScope()

    // 定义页面的背景颜色和顶栏颜色
    val backgroundColor = Color(0xFFF0F0F0)  // 浅灰色背景
    val appBarColor = Color(0xFF6200EE)      // 例如紫色顶栏

    // 追踪状态，是否已经成功获取人脸识别验证
    var isSuccess by remember { mutableStateOf(false) }

    // 追踪密码输入的状态
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // 追踪一卡通号输入的状态
    var cardNumber by remember { mutableStateOf("") }

    // 用于弹出错误提示框
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // 用于弹出成功提示框
    var showSuccessDialog by remember { mutableStateOf(false) }

    // 用于弹出人脸不匹配提示框
    var showFaceMismatchDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 如果已经成功通过人脸识别验证，显示 "用户: cardNumber"，否则显示默认标题
                    if (isSuccess) {
                        Text(
                            "用户: $cardNumber",
                            color = Color.White,
                            style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp)
                        )
                    } else {
                        Text(
                            "忘记密码页面",
                            color = Color.White,
                            style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp)
                        )
                    }
                },
                backgroundColor = appBarColor,  // 顶栏背景色
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "关闭", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = backgroundColor  // 设置 Scaffold 的背景色
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()  // 占满整个屏幕
                .background(backgroundColor)  // 设置背景色
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,  // 垂直居中
            horizontalAlignment = Alignment.CenterHorizontally  // 水平居中
        ) {
            if (!isSuccess) {
                // 人脸识别验证部分
                Text(
                    text = "请先输入一卡通号并进行人脸识别核验身份",
                    color = Color.Black,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)  // 调整字体大小
                )

                // 一卡通号输入框
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("一卡通号") },  // 标签显示在输入框上方
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,  // 聚焦时的边框颜色
                        cursorColor = Color.Blue  // 光标颜色
                    ),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.4f)  // 限制输入框的宽度为屏幕的40%
                        .widthIn(max = 300.dp)  // 设置输入框的最大宽度为300dp
                )

                // 提交按钮进行人脸识别
                Button(
                    onClick = {
                        // 先检查输入是否合法
                        when {
                            cardNumber.isEmpty() -> {
                                errorMessage = "一卡通号不能为空"
                                showErrorDialog = true
                            }
                            cardNumber.length != 9 || !cardNumber.all { it.isDigit() } -> {
                                errorMessage = "一卡通号必须是9位数字"
                                showErrorDialog = true
                            }
                            else -> {
                                // 输入合法，进行人脸识别
                                scope.launch {
                                    val myOpenCV = My_openCV()
                                    myOpenCV.getVideoFromCamera()  // 捕获视频和人脸检测
                                    val responseCV = myOpenCV.connectToServerAndSendData(cardNumber.toInt())  // 传递 cardNumber

                                    // 检查返回的状态
                                    val response_status = responseCV?.status
                                    if (response_status == "success") {
                                        // 更新状态为成功
                                        isSuccess = true
                                    } else if (response_status == "failed") {
                                        // 人脸不匹配，弹出提示框
                                        showFaceMismatchDialog = true
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(0.3f)  // 按钮宽度为屏幕的30%
                        .widthIn(max = 200.dp)  // 限制按钮最大宽度为200dp
                ) {
                    Text("进行人脸识别核验身份", style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp))  // 按钮文字大小
                }
            } else {
                // 修改密码和确认密码部分
                Text(
                    text = "请输入新密码",
                    color = Color.Black,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                )

                // 新密码输入框
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("新密码") },  // 标签显示在输入框上方
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,  // 聚焦时的边框颜色
                        cursorColor = Color.Blue  // 光标颜色
                    ),
                    visualTransformation = PasswordVisualTransformation(),  // 隐藏密码输入
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.4f)  // 限制输入框的宽度为屏幕的40%
                        .widthIn(max = 300.dp)  // 设置输入框的最大宽度为300dp
                )

                // 确认密码输入框
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认密码") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,  // 聚焦时的边框颜色
                        cursorColor = Color.Blue  // 光标颜色
                    ),
                    visualTransformation = PasswordVisualTransformation(),  // 隐藏密码输入
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.4f)
                        .widthIn(max = 300.dp)  // 设置输入框的最大宽度为300dp
                )

                // 提交按钮
                Button(
                    onClick = {
                        if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                            // 处理修改密码的逻辑
                            println("新密码已提交: $newPassword")

                            // 调用更改密码的网络请求
                            val sendChangePassword = change_password()
                            scope.launch {
                                val response = sendChangePassword.sendToserver_newpassword(
                                    "change_password",
                                    cardNumber,
                                    newPassword
                                )
                                if (response != null) {
                                    println(response.status)
                                    if (response.status == "success") {
                                        showSuccessDialog = true  // 显示成功提示框
                                        newPassword = ""  // 清空密码输入框
                                        confirmPassword = ""  // 清空确认密码输入框
                                    }
                                }
                            }
                        } else {
                            // 密码不匹配或为空时的处理
                            println("两次输入的密码不匹配或密码为空")
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(0.4f)  // 按钮宽度为屏幕的50%
                        .widthIn(max = 200.dp)  // 限制按钮最大宽度为200dp
                ) {
                    Text("提交新密码", style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp))
                }
            }

            // 错误提示框
            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("输入错误") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(onClick = { showErrorDialog = false }) {
                            Text("确定")
                        }
                    }
                )
            }

            // 成功提示框
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("修改成功") },
                    text = { Text("您的密码已成功修改") },
                    confirmButton = {
                        Button(onClick = { showSuccessDialog = false }) {
                            Text("确定")
                        }
                    }
                )
            }

            // 人脸不匹配提示框
            if (showFaceMismatchDialog) {
                AlertDialog(
                    onDismissRequest = { showFaceMismatchDialog = false },
                    title = { Text("人脸不匹配") },
                    text = { Text("未能成功匹配人脸或该账号不存在，请重试。") },
                    confirmButton = {
                        Button(onClick = { showFaceMismatchDialog = false }) {
                            Text("确定")
                        }
                    }
                )
            }
        }
    }
}











@Throws(Exception::class)
fun encryptPassword(password: String, publicKeyString: String): String? {
    val publicKeyBytes = Base64.getDecoder().decode(publicKeyString)
    val spec = X509EncodedKeySpec(publicKeyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    val publicKey: PublicKey = keyFactory.generatePublic(spec)

    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)

    val encryptedBytes = cipher.doFinal(password.toByteArray(Charsets.UTF_8))
    return Base64.getEncoder().encodeToString(encryptedBytes)
}

//fun main() = application {
//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "登录页面",
//        state = rememberWindowState(width = 900.dp, height = 700.dp) // 固定窗口大小
//    ) {
//        Login_App()
//    }
//}

