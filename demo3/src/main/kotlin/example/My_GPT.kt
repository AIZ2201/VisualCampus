
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun My_GPT() {
    var userInput by remember { mutableStateOf("") }
    val chatLog = remember { mutableStateListOf<Pair<String, Boolean>>() } // Boolean 用于判断消息是发送的还是接收的
    val coroutineScope = rememberCoroutineScope()

    var socket by remember { mutableStateOf<Socket?>(null) }
    var writer by remember { mutableStateOf<PrintWriter?>(null) }
    var reader by remember { mutableStateOf<BufferedReader?>(null) }

    LaunchedEffect(Unit) {
        try {
            socket = Socket("10.208.72.178", 4445)
            writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream(), "UTF-8"), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream(), "UTF-8"))
            chatLog.add("欢迎来到SEU学习机器人助手页面，你想要询问什么呢？" to false)

            while (true) {
                delay(5000)
                if (socket?.isConnected != true) {
                    chatLog.add("连接中断" to false)
                    break
                }
            }
        } catch (e: Exception) {
            chatLog.add("Failed to connect to server: ${e.message}" to false)
        }
    }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(Color.White) // 设置为纯白色背景
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, Color(0xFF6200EE))
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                chatLog.forEach { (message, isUser) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start // 根据发送者调整对齐方式
                    ) {
                        if (!isUser) {
                            // 显示接收者的头像
                            Image(
                                painter = painterResource("image/Chatgpt.png"), // 替换为接收者的头像路径
                                contentDescription = "Receiver Avatar",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Text(
                            text = AnnotatedString(message),
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .padding(8.dp)
                                .background(Color(0xFFE0E0E0))
                                .padding(8.dp),
                            fontWeight = FontWeight.Normal,
                            lineHeight = 20.sp,
                            maxLines = Int.MAX_VALUE,
                            softWrap = true
                        )
                        if (isUser) {
                            // 显示发送者的头像
                            Image(
                                painter = painterResource("image/receiver.png"), // 替换为发送者的头像路径
                                contentDescription = "Sender Avatar",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 输入框和发送按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color(0xFF6200EE))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically // 修正错误为 Alignment.CenterVertically
        ) {
            BasicTextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .onPreviewKeyEvent { keyEvent: KeyEvent ->
                        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
                            sendMessageAndClearInput(userInput, chatLog, writer, reader, coroutineScope)
                            userInput = "" // 发送后清空输入框
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    sendMessageAndClearInput(userInput, chatLog, writer, reader, coroutineScope)
                    userInput = "" // 发送后清空输入框
                }),
                singleLine = false
            )
            // 发送按钮
            Image(
                painter = painterResource("image/send-line.png"), // 使用上传的图标文件路径
                contentDescription = "Send Message",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        sendMessageAndClearInput(userInput, chatLog, writer, reader, coroutineScope)
                        userInput = "" // 点击按钮后清空输入框
                    }
            )
        }
    }
}

fun sendMessageAndClearInput(
    input: String,  // 使用 input 作为参数，而不是直接修改 userInput
    chatLog: SnapshotStateList<Pair<String, Boolean>>,
    writer: PrintWriter?,
    reader: BufferedReader?,
    coroutineScope: CoroutineScope
) {
    if (input.isNotBlank()) {
        val messageToSend = input // 使用局部变量 messageToSend 进行发送
        coroutineScope.launch(Dispatchers.IO) {
            sendMessage(messageToSend, chatLog, writer, reader)
        }
    }
}

suspend fun sendMessage(
    userInput: String,
    chatLog: SnapshotStateList<Pair<String, Boolean>>,
    writer: PrintWriter?,
    reader: BufferedReader?
) {
    if (writer != null && reader != null && userInput.isNotBlank()) {
        writer.println(userInput)
        chatLog.add("You: $userInput" to true)

        try {
            val response = reader.readLine()
            if (response != null) {
                chatLog.add("SEU-Robot: $response" to false)
            } else {
                chatLog.add("Error: No response from server" to false)
            }
        } catch (e: Exception) {
            chatLog.add("Error reading response: ${e.message}" to false)
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SEU学习机器人",
        state = rememberWindowState(width = 900.dp, height = 700.dp)
    ) {
        My_GPT()
    }
}

