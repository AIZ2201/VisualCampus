package example
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TeaSideNavBar(selectedMenu: String, onMenuClick: (String) -> Unit) {
    val menuItems = listOf("展示个人教籍信息", "修改密码") // 添加“修改密码”菜单项

    Column(
        modifier = Modifier
            .fillMaxWidth(0.25f)
            .fillMaxHeight()
            .background(Color.LightGray.copy(alpha=0.9f)), // 设置导航栏背景颜色
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth() // 取消左右侧的边距
                .background(Color.White)
                .size(65.dp),
            contentAlignment = Alignment.Center // 在 Box 内部居中对齐
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = 3.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center, // 垂直和水平居中对齐
                    modifier = Modifier.fillMaxSize() // 使Box填充整个卡片
                ) {
                    Text(
                        text = "教职工信息",
                        fontSize = 24.sp, // 设置文本大小
                        modifier = Modifier.padding(8.dp),
                        fontFamily = FontFamily.Serif, // 使用Serif字体
                        textAlign = TextAlign.Center // 水平居中对齐
                    )
                }
            }
        }

        menuItems.forEach { itemName ->
            val isSelected = selectedMenu == itemName // 判断当前项是否被选中
            val backgroundColor = if (isSelected) Color.White.copy(alpha=0.3f) else Color.Transparent // 选中项背景加深
            val iconColor = if (isSelected) Color.Black else Color.White
            Box(
                modifier = Modifier
                    .fillMaxWidth() // 取消左右侧的边距
                    .background(backgroundColor)
                    .size(65.dp)
                    .clickable {
                        println("Clicked on $itemName") // 打印点击项
                        onMenuClick(itemName)
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, // 垂直居中对齐
                    modifier = Modifier.fillMaxHeight() // 确保 Row 的高度和图标一致
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource("image/Information/$itemName.svg"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp) // 设置图标大小
                            .align(Alignment.CenterVertically), // 垂直居中对齐
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // 增加图标和文本之间的间距（可选）
                    Text(
                        text = itemName,
                        fontSize = 20.sp, // 设置文本大小，确保和图标大小一致
                        modifier = Modifier.align(Alignment.CenterVertically).padding(8.dp) // 垂直居中对齐
                    )
                }
            }
        }
    }
}

@Composable
fun Tea_MainContent(user: User, selectedMenu: String) {
    println("Current selectedMenu: $selectedMenu") // 打印当前选中的菜单项
    when (selectedMenu) {
        "展示个人教籍信息" -> TeaDisplayAcademicInfoContent(user) // 展示个人学籍信息
        "修改密码" -> TeaChangePassword(user)
        else -> Text("请选择一个菜单项", style = MaterialTheme.typography.h4)
    }
}
@Composable
fun TeaChangePassword(user:User)
{
    ChangePasswordScreen(user)
}

@Composable
fun TeaDisplayAcademicInfoContent(user: User) {
    if (user.role=="teacher") {
        showTeainformation(user)
    } else {
        FailureWindow()
    }
}

@Composable
@Preview
fun TeaInformation_Select(user: User) {
    val (selectedMenu, setSelectedMenu) = remember { mutableStateOf("展示个人教籍信息") }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxHeight()) {
            TeaSideNavBar(selectedMenu, onMenuClick = { menu ->
                setSelectedMenu(menu)
            })

            Tea_MainContent(user, selectedMenu = selectedMenu)
        }
    }
}

