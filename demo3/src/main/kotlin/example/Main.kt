package example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowState
@Preview
@Composable
fun FailureWindow() {
    Box(
        modifier = Modifier
            .fillMaxSize() // 占满整个屏幕以便在其中居中对齐
            .padding(16.dp), // 添加一些内边距，以防止卡片太靠近边缘
        contentAlignment = Alignment.Center // 在 Box 中居中对齐内容
    ) {
        Card(
            modifier = Modifier
                .width(400.dp) // 设置卡片的宽度
                .height(200.dp), // 设置卡片的高度
            elevation = 8.dp // 卡片的阴影
        ) {
            Box(
                contentAlignment = Alignment.Center, // 在卡片内再次居中对齐文本
                modifier = Modifier.fillMaxSize() // 填充卡片的整个大小
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, // 图标和文本在垂直方向居中对齐
                    horizontalArrangement = Arrangement.Center // 在 Row 中水平居中对齐内容
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning, // 选择合适的图标
                        contentDescription = "Warning Icon", // 图标的内容描述
                        modifier = Modifier.size(24.dp),// 设置图标的大小
                        tint = Color.Red,
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // 添加图标和文本之间的间距
                    Text(
                        "您不具有查看此界面的权限",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

data class User(
    val cardnumber:Int,
    val name: String,
    var password:String,
    val role:String
)
@Composable
fun App(onResizeWindow: (WindowState) -> Unit) {
    // 定义一个状态变量来控制当前页面的显示
    var currentPage by remember { mutableStateOf("login") }
    var user by remember { mutableStateOf<User>(User(-1, "", "", "")) }

    // 根据 currentPage 的值决定显示哪个页面
    when (currentPage) {
        "login" -> {
            Login_App(
                onLoginSuccess = { user = it },
                onMessage = { currentPage = it }
            )
        }
        "home" -> {
            onResizeWindow(rememberWindowState(width = 1500.dp, height = 800.dp)) // 设置 Home 窗口大小
            Select_App(user)
        }
    }
}

fun main() = application {
    var windowState by remember { mutableStateOf(WindowState(width=800.dp, height = 600.dp)) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "数智东南 v2.0",
        state = windowState
    ) {
        App(onResizeWindow = {
            windowState = it
        })
    }
}

