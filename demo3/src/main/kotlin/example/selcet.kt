package example
import My_GPT
import PersonalAccountPage
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.*
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
import androidx.compose.ui.window.rememberWindowState


@Composable
fun SideNavBar(currentColor:Int,maxColor:Int,selectedMenu: String, onMenuClick: (String) -> Unit,onColorChange: (Int) -> Unit) {
    val menuIcons = listOf("icon1", "icon2", "icon3", "icon4", "icon5", "icon6")
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
        .fillMaxWidth(0.07f)
        .fillMaxHeight()
        .background(Color.Transparent), // 设置导航栏背景颜色
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Column{
            menuIcons.forEachIndexed { index, iconName ->
                val isSelected = selectedMenu == iconName // 判断当前项是否被选中
                val backgroundColor =
                    if (isSelected) Color.LightGray.copy(alpha = 0.3f) else Color.Transparent // 选中项背景加深
                val iconColor = if (isSelected) Color.Black else Color.LightGray
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // 取消左右侧的边距
                        .background(backgroundColor)
                        .size(65.dp)
                        .clickable {
                            println("Clicked on $iconName") // 打印点击项
                            onMenuClick(iconName)
                        }
                ) {
                    Icon(
                        painter = painterResource("image/$iconName.png"),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp),// 设置图标大小
                        tint = iconColor
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth().align(Alignment.End),
            contentAlignment = Alignment.BottomCenter
        ) {
            IconButton(
                onClick = {
                    println((currentColor + 1) % maxColor)
                    onColorChange((currentColor + 1) % maxColor)
                },
                modifier = Modifier.size(50.dp).padding(10.dp)
            ) {
                Icon(
                    painter = painterResource("image/color.svg"),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp) // 设置图标大小
                )
            }

        }
    }
}

@Composable
fun MainContent(user:User,selectedMenu: String) {
    println("Current selectedMenu: $selectedMenu") // 打印当前选中的菜单项
    when (selectedMenu) {
        "icon1" -> UserManagementContent(user)
        "icon2" -> AcademicManagementContent(user)
        "icon3" -> BalanceContent(user)
        "icon4" -> LibraryContent(user)
        "icon5" -> StoreContent(user)
        "icon6" -> MyGptContent(user)
        else -> Text("请选择一个菜单项", style = MaterialTheme.typography.h4)
    }
}

@Composable
fun UserManagementContent(user:User) {
    if(user.role=="student") {
        StudentInformation_Select(user)
    }
    if(user.role=="teacher") {
    TeaInformation_Select(user)
    }
    if(user.role=="admin")
    {
        Admin_Select(user)
    }
}

@Composable
fun AcademicManagementContent(user:User) {
    teaching_affair_App(user)
}

@Composable
fun BalanceContent(user:User) {

    Balance_Select(user)
}

@Composable
fun LibraryContent(user:User) {
    if(user.role=="student"||user.role=="teacher")
    {
        StuLibrarySelect(user)
    }
    if(user.role=="admin")
    {
        Library_Select(user)
    }
}

@Composable
fun StoreContent(user:User) {
    if(user.role=="student"||user.role=="teacher")
    {
        StudentStoreSelect(user)
    }
    if(user.role=="admin")
    {
        Store_Select(user)
    }
}

@Composable
fun MyGptContent(user:User) {
    My_GPT()
}


@Composable
@Preview
fun Select_App(user:User) {
    val colors = listOf(Color.White, Color(201,214,202), Color(204,229,255)) // 定义三种颜色
    val (currentColorIndex, setCurrentColorIndex) = remember { mutableStateOf(0) } // 记录当前颜色索引
    val (selectedMenu, setSelectedMenu) = remember { mutableStateOf("icon1") }
    Box(modifier = Modifier.fillMaxSize().background(colors[currentColorIndex]))
    {
        MaterialTheme {
            Row(modifier = Modifier.fillMaxHeight()) {
                SideNavBar(currentColorIndex, colors.size, selectedMenu,
                    onMenuClick = { menu -> setSelectedMenu(menu) },
                    onColorChange = { index -> setCurrentColorIndex(index) }
                )
                MainContent(user, selectedMenu = selectedMenu)
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication,
        title = "VCampus",
        state = rememberWindowState(width = 1200.dp, height = 700.dp)
        ) {
        val studentuser=User(123,"李华","123","student")
        Select_App(studentuser)
    }
}

