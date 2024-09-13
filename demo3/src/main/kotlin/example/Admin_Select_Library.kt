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
fun AdminLibrarySideNavBar(selectedMenu: String, onMenuClick: (String) -> Unit) {
    val menuItems = listOf("搜索图书", "修改图书信息", "添加图书") // 管理员端菜单项

    Column(
        modifier = Modifier
            .fillMaxWidth(0.25f)
            .fillMaxHeight()
            .background(Color.LightGray.copy(alpha = 0.9f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .size(65.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "图书馆管理",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(8.dp),
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        menuItems.forEach { itemName ->
            val isSelected = selectedMenu == itemName
            val backgroundColor = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent
            val iconColor = if (isSelected) Color.Black else Color.White
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .size(65.dp)
                    .clickable {
                        println("Clicked on $itemName")
                        onMenuClick(itemName)
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource("image/Library/$itemName.svg"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically),
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = itemName,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminLibraryMainContent(user: User, selectedMenu: String) {
    println("Current selectedMenu: $selectedMenu")
    when (selectedMenu) {
        "搜索图书" -> SearchBooksContent(user) // 搜索图书
        "修改图书信息" -> EditBooksContent(user) // 修改图书
        "添加图书" -> AddBooksContent(user) // 添加图书
        else -> Text("请选择一个菜单项", style = MaterialTheme.typography.h4)
    }
}

@Composable
fun AdminLibrarySelect(user: User) {
    val (selectedMenu, setSelectedMenu) = remember { mutableStateOf("搜索图书") }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxHeight()) {
            AdminLibrarySideNavBar(selectedMenu, onMenuClick = { menu ->
                setSelectedMenu(menu)
            })

            AdminLibraryMainContent(user, selectedMenu = selectedMenu)
        }
    }
}
