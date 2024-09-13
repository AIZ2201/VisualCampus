package example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import example.handlers.AllDealSend
import example.handlers.AllDealHandler
import example.handlers.AllDealBack
import example.handlers.Transaction

@Composable
fun AllDealTitle() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "所有订单",
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
            text = "查看历史所有交易订单",
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

@Composable
fun TransactionApp(user: User) {
    var searchQuery by remember { mutableStateOf("") }
    var deals by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    val handler = AllDealHandler()
    val send = AllDealSend("store_getAllTransaction", user.cardnumber, user.password)
    val back = handler.handleAction(send)
    if (back != null) {
        deals = back.transactions// 假设 AllDealBack 返回的是 deal 列表
    }

    // 根据搜索查询过滤交易
    val filteredDeals = deals.filter { deal ->
        searchQuery.isBlank() || deal.productID.toString().contains(searchQuery, true)
                || deal.name.contains(searchQuery, true)
                || deal.time.contains(searchQuery, true)
                || deal.cardNumber.toString().contains(searchQuery, true)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        AllDealTitle()
        Spacer(modifier = Modifier.height(16.dp))
        Text("欢迎, ${user.name}!", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("请输入关键词搜索...") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // 表格头部
        Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
            Text("商品编号", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("商品名称", modifier = Modifier.weight(2f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("商品价格", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("数量", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("一卡通号", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("时间", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }

        // 表格内容
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            filteredDeals.forEach { deal ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(deal.productID.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(deal.name, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                    Text(deal.productPrice.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(deal.productAmount.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(deal.cardNumber.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(deal.time, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun AllDealScreen(user:User) {
    TransactionApp(user)
}

