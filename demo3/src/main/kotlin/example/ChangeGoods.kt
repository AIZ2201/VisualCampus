package example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import example.handlers.*
@Composable
fun ChangeGoodsTextBox(label: String, initialValue: String, onValueChange: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onValueChange(it)
        },
        shape = RoundedCornerShape(16.dp),
        label = { Text(label) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.Black,
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black
        ),
        modifier = Modifier
            .background(Color.Transparent)
            .padding(0.dp)
            .fillMaxWidth()
    )
}

@Composable
fun ChangeGoodsDisplay(
    tempUser: tempUser,
    goods: Goods,
    onGoodsChange: (Goods?) -> Unit
) {
    var productID by remember { mutableStateOf(goods.productID.toString()) }
    var name by remember { mutableStateOf(goods.name) }
    var price by remember { mutableStateOf(goods.price.toString()) }
    var pictureLink by remember { mutableStateOf(goods.pictureLink) }
    var stock by remember { mutableStateOf(goods.stock.toString()) }
    var sales by remember { mutableStateOf(goods.sales.toString()) }
    var description by remember { mutableStateOf(goods.description) }
    var select by remember { mutableStateOf(goods.select) }
    var expanded by remember { mutableStateOf(false) }

    // 控制对话框的状态变量
    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmDialogMessage by remember { mutableStateOf("") }
    var onConfirmAction by remember { mutableStateOf<() -> Unit>({}) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
    ) {
        Row {
            Icon(
                painter = painterResource("image/triangle.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.CenterVertically)
                    .rotate(if (expanded) 0f else 270f)
            )
            Text(
                text = goods.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = goods.productID.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = goods.select,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.2f)
                            .padding(8.dp)
                            .weight(1f)
                    ) {
                        ChangeGoodsTextBox(
                            label = "Product ID",
                            initialValue = productID,
                            onValueChange = { productID = it })
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(8.dp)
                            .weight(2f)
                    ) {
                        ChangeGoodsTextBox(label = "商品名称", initialValue = name, onValueChange = { name = it })
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(8.dp)
                            .weight(2f)
                    ) {
                        ChangeGoodsTextBox(label = "单价", initialValue = price, onValueChange = { price = it })
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(8.dp)
                            .weight(1f)
                    ) {
                        ChangeGoodsTextBox(label = "库存", initialValue = stock, onValueChange = { stock = it })
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(8.dp)
                            .weight(2f)
                    ) {
                        ChangeGoodsTextBox(
                            label = "图片链接",
                            initialValue = pictureLink,
                            onValueChange = { pictureLink = it })
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(8.dp)
                            .weight(2f)
                    ) {
                        ChangeGoodsTextBox(label = "销量", initialValue = sales, onValueChange = { sales = it })
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(8.dp)
                            .weight(2f)
                    ) {
                        ChangeGoodsTextBox(
                            label = "描述",
                            initialValue = description,
                            onValueChange = { description = it })
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(8.dp)
                            .weight(2f)
                    ) {
                        ChangeGoodsTextBox(label = "品类", initialValue = select, onValueChange = { select = it })
                    }
                }
                val isFormComplete = productID.isNotBlank() &&
                        name.isNotBlank() &&
                        price.isNotBlank() &&
                        pictureLink.isNotBlank() &&
                        stock.isNotBlank() &&
                        sales.isNotBlank() &&
                        description.isNotBlank() &&
                        select.isNotBlank()

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Box(
                        modifier = Modifier.align(Alignment.Bottom).padding(8.dp)
                    )
                    {
                        Button(
                            onClick = {
                                // 显示确认对话框
                                confirmDialogMessage = "确定要修改商品信息吗？"
                                onConfirmAction = {
                                    val updatedGoods = Goods(
                                        productID = productID.toInt(),
                                        name = name,
                                        price = price.toDouble(),
                                        pictureLink = pictureLink,
                                        stock = stock.toInt(),
                                        sales = sales.toInt(),
                                        description = description,
                                        select = select
                                    )
                                    val CGhandler = ChangeGoodsHandler()
                                    val changegoodssend =
                                        ChangeGoodsSend("store_change", tempUser.CardID, tempUser.password, updatedGoods)
                                    val changegoodsback = CGhandler.handleAction(changegoodssend)
                                    if (changegoodsback?.status == "success") {
                                        onGoodsChange(updatedGoods)
                                    }
                                }
                                showConfirmDialog = true
                            },
                            modifier = Modifier.width(120.dp).height(60.dp),
                            enabled = isFormComplete, // 只有在所有字段都不为空时，按钮才可点击
                        ) {
                            Text("修改")
                        }
                    }

                    Box(
                        modifier = Modifier.align(Alignment.Bottom).padding(8.dp)
                    )
                    {
                        Button(
                            onClick = {
                                // 显示确认对话框
                                confirmDialogMessage = "确定要删除商品吗？"
                                onConfirmAction = {
                                    val updatedGoods = Goods(
                                        productID = productID.toInt(),
                                        name = name,
                                        price = price.toDouble(),
                                        pictureLink = pictureLink,
                                        stock = stock.toInt(),
                                        sales = sales.toInt(),
                                        description = description,
                                        select = select
                                    )
                                    val DGhandler = DeleteGoodsHandler()
                                    val deletegoodssend =
                                        DeleteGoodsSend("store_delete", tempUser.CardID, tempUser.password, updatedGoods)
                                    val deletegoodsback = DGhandler.handleAction(deletegoodssend)
                                    if (deletegoodsback?.status == "success") {
                                        onGoodsChange(null)
                                    }
                                }
                                showConfirmDialog = true
                            },
                            modifier = Modifier.width(120.dp).height(60.dp)
                        ) {
                            Text("删除")
                        }
                    }
                }
            }
        }
    }

    // 确认对话框
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(text = "确认") },
            text = { Text(text = confirmDialogMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmAction() // 执行确认的操作
                        showConfirmDialog = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}



@Composable
fun ChangeGoodsList(
    tempUser:tempUser,
    goodsListState: List<Goods>?,
    onGoodsListChange: (List<Goods>) -> Unit,
) {
    // Initialize goods with the provided list or an empty list if null
    val goods = goodsListState ?: emptyList()

    // State to keep track of the updated list
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn {
            items(goods.size) { index ->
                val goodItem = goods[index]
                ChangeGoodsDisplay(tempUser, goodItem) { updatedGood ->
                    // 创建一个可变列表副本
                    val updatedGoodsList = goods.toMutableList()

                    // 如果 updatedGood 不为空，则更新列表中的对应项
                    if (updatedGood != null) {
                        updatedGoodsList[index] = updatedGood
                    } else {
                        // 如果 updatedGood 为空，则从列表中删除对应项
                        updatedGoodsList.removeAt(index)
                    }

                    // 调用回调函数，更新整个商品列表
                    onGoodsListChange(updatedGoodsList)
                }
            }
        }

//        goodsListState?.forEach { item ->
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//            ) {
//                // Display and update the goods
//                ChangeGoodsDisplay(user,goods = item) { newGoods ->
//                    // Update the item in the list
//                    updatedGoods = updatedGoods.map {
//
//                        if (it.productID == newGoods.productID) newGoods else it
//                    }
//                    val updatedList = users.toMutableList()
//                    updatedList[index] = updatedStudent
//                    onStudentListChange (updatedList)
//                    val updatedList=
//                     Call the callback to notify about the change
//                    onGoodsListChange(updatedGoods)
//                }

    }
}

@Composable
fun ChangeGoodTitle() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "修改商品",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(0.dp),
            color = Color.Black,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "修改商品信息",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(0.dp),
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChangeGoodsScreen(user: User) {
    var goodsList by remember { mutableStateOf<List<Goods>?>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("全部") }
    var searchText by remember { mutableStateOf("") }
    val tempUser = tempUser(user.name,user.cardnumber,user.password)
    Column {
        ChangeGoodTitle()
        SearchGoodsBox(tempUser, selectedCategory, onGoodsListChange = {
            if (it != null) {
                goodsList = it
            }
        }, onSearchTextChange = { searchText = it })
        // 放置商品列表
        ChangeGoodsList(tempUser,
            goodsList,
            onGoodsListChange = {
                goodsList = it
            })
    }
}

//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//        val tempUser = tempUser("李华", 123, "123")
//        ChangeGoodsScreen(tempUser)
//    }
//}


