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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LibrarySideNavBar(selectedMenu: String, onMenuClick: (String) -> Unit) {
    val menuItems = listOf("搜索图书", "我的图书", "修改图书信息", "添加图书","查看所有借阅信息") // 图书馆页面的菜单项

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
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center, // 垂直和水平居中对齐
                    modifier = Modifier.fillMaxSize() // 使Box填充整个卡片
                ) {
                    Text(
                        text = "图书馆",
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
                        painter = painterResource("image/Library/$itemName.svg"),
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
fun Library_MainContent(user: User, selectedMenu: String) {
    println("Current selectedMenu: $selectedMenu") // 打印当前选中的菜单项
    when (selectedMenu) {
        "搜索图书" -> SearchBooksContent(user) // 搜索图书
        "我的图书" -> MyBooksContent(user) // 我的图书
        "修改图书信息" -> EditBooksContent(user) // 修改图书
        "添加图书" -> AddBooksContent(user) // 添加图书
        "查看所有借阅信息"->showAllTran(user)
        else -> Text("请选择一个菜单项", style = MaterialTheme.typography.h4)
    }
}
@Composable
fun showAllTran(user: User) {
    BorrowedBookApp(user = user)
}

@Composable
fun SearchBooksContent(user: User) {
    SearchBookScreen(user)
}

@Composable
fun MyBooksContent(user: User) {
        UserBookScreen(user)

}

@Composable
fun EditBooksContent(user: User) {
    if (user.role == "admin") {
        ChangeBookScreen(user)
    } else {
        FailureWindow()
    }
}

@Composable
fun AddBooksContent(user: User) {
    if (user.role == "admin") {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            ADBookScreen(user)
        }
    } else {
        FailureWindow()
    }
}


@Composable
@Preview
fun Library_Select(user: User) {
    val (selectedMenu, setSelectedMenu) = remember { mutableStateOf("搜索图书") }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxHeight()) {
            LibrarySideNavBar(selectedMenu, onMenuClick = { menu ->
                setSelectedMenu(menu)
            })

            Library_MainContent(user, selectedMenu = selectedMenu)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        val adminUser = User(123456789, "李华", "111","admin")
        Library_Select(adminUser)
    }
}
