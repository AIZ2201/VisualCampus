package example

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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign


@Composable
fun StoreSideNavBar(selectedMenu: String, onMenuClick: (String) -> Unit) {
    val menuItems = listOf("商城主页", "我的订单", "上架商品", "修改商品","查看所有交易信息") // 修改菜单项

    Column(
        modifier = Modifier
            .fillMaxWidth(0.25f)
            .fillMaxHeight()
            .background(Color.LightGray.copy(alpha = 0.9f)), // 设置导航栏背景颜色
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
                        text = "商城管理",
                        fontSize = 24.sp, // 设置文本大小
                        modifier = Modifier.padding(8.dp),
                        fontFamily = FontFamily.Serif, // 使用Serif字体
                        textAlign = TextAlign.Center // 水平居中对齐
                    )
                }
            }
        }

        menuItems.forEachIndexed { index, itemName ->
            val isSelected = selectedMenu == itemName // 判断当前项是否被选中
            val backgroundColor = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent // 选中项背景加深
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
                        painter = painterResource("image/Store/$itemName.svg"),
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
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(8.dp) // 垂直居中对齐
                    )
                }

            }
        }
    }
}

@Composable
fun Store_MainContent(user: User, selectedMenu: String) {
    println("Current selectedMenu: $selectedMenu") // 打印当前选中的菜单项
    when (selectedMenu) {
        "商城主页" -> StoreHomePage(user) // 商城主页
        "我的订单" -> MyOrdersPage(user) // 我的订单
        "上架商品" -> AddProductPage(user) // 上架商品
        "修改商品" -> EditProductPage(user) // 修改商品
        "查看所有交易信息"->AllDealPage(user)
        else -> Text("请选择一个菜单项", style = MaterialTheme.typography.h4)
    }
}

@Composable
fun AllDealPage(user: User) {
    AllDealScreen(user)
}
@Composable
fun StoreHomePage(user: User) {
   Store(user)
}

@Composable
fun MyOrdersPage(user: User) {
    MyDealScreen(user)
}

@Composable
fun AddProductPage(user: User) {
    if (user.role == "admin") {
        MyGoodsScreen(user)
    } else {
        FailureWindow()
    }
}

@Composable
fun EditProductPage(user: User) {
    if (user.role == "admin") {
        ChangeGoodsScreen(user)
    } else {
        FailureWindow()
    }
}


@Composable
@Preview
fun Store_Select(user: User) {
    val (selectedMenu, setSelectedMenu) = remember { mutableStateOf("商城主页") }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxHeight()) {
            StoreSideNavBar(selectedMenu, onMenuClick = { menu ->
                setSelectedMenu(menu)
            })

            Store_MainContent(user, selectedMenu = selectedMenu)
        }
    }
}
