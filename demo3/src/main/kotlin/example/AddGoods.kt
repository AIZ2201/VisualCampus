package example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import example.handlers.*
import example.handlers.NewGoods
@Composable
fun ADGoodsTitle()
{
    Column(
        modifier = Modifier.padding(16.dp)
    )
    {
        Text(
            text = "增删商品",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                //.shadow(8.dp) // 添加阴影
                .padding(0.dp),
            color = Color.Black,
            textAlign = TextAlign.Center,
        )
        // 标题栏
        Text(
            text = "增删商店商品",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                //.shadow(8.dp) // 添加阴影
                .padding(0.dp),
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AddGoodsTextItem(label: String, initialValue: String, onValueChange: (String) -> Unit) {
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
            unfocusedLabelColor = Color.LightGray
        ),
        modifier = Modifier
            .background(Color.Transparent)
            .padding(0.dp)
            .fillMaxWidth()
    )
}

@Composable
fun AddGoodsTextItems(tempUser: tempUser) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var pictureLink by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var select by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(2.dp)
    ) {
        Text("添加商品", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddGoodsTextItem(label = "名称", initialValue = name, onValueChange = { name = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddGoodsTextItem(label = "价格", initialValue = price, onValueChange = { price = it })
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddGoodsTextItem(label = "图片链接", initialValue = pictureLink, onValueChange = { pictureLink = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddGoodsTextItem(label = "库存", initialValue = stock, onValueChange = { stock = it })
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddGoodsTextItem(label = "描述", initialValue = description, onValueChange = { description = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddGoodsTextItem(label = "分类", initialValue = select, onValueChange = { select = it })
            }
        }
        val isFormComplete =
            name.isNotBlank() &&
                    price.isNotBlank() &&
                    pictureLink.isNotBlank() &&
                    stock.isNotBlank() &&
                    description.isNotBlank()
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val updatedGoods = NewGoods(
                    name = name,
                    price = price.toDouble(),
                    pictureLink = pictureLink,
                    stock = stock.toInt(),
                    description = description,
                    select = select
                )
                println("准备建立链接")
                println("此时的商品信息为：$updatedGoods")
                // 根据需求调用相应的处理函数
                val AGhandler = AddGoodsHandler()
                val addGoodsSend = AddGoodsSend("store_addProduct", tempUser.CardID, tempUser.password, updatedGoods)
                val addGoodsBack = AGhandler.handleAction(addGoodsSend)
                if (addGoodsBack != null) {
                    println(addGoodsBack)
                    if (addGoodsBack == "success") {
                        name = ""
                        price = ""
                        pictureLink = ""
                        stock = ""
                        description = ""

                        dialogMessage = "商品添加成功！"
                    } else {
                        dialogMessage = "商品添加失败，请重试。"
                    }
                    showDialog = true
                }
            },
            enabled = isFormComplete,
            modifier = Modifier.align(Alignment.End).width(120.dp).height(60.dp)
        ) {
            Text("添加")
        }

        // 提示对话框
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("提示") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}


@Composable
fun MyGoodsScreen(user:User) {
    var tempUser = tempUser(user.name,user.cardnumber,user.password)
    Box(
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            ADGoodsTitle()
            Box(
                modifier = Modifier.fillMaxHeight(0.7f)
            ) {
                AddGoodsTextItems(tempUser)
            }
        }
    }
}


//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//        MyGoodsScreen()
//    }
//}
