package example

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import example.handlers.MyDealSend
import example.handlers.MyDealHandler
import example.handlers.Transaction

@Composable
fun TransactionList(transactions: List<Transaction>) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
    ) {
        transactions.forEach { transaction ->
            TransactionItem(transaction)
            Divider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun MyDealTitle() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "我的订单",
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
            text = "查看历史订单",
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
fun TransactionItem(transaction: Transaction) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = transaction.name,
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        Row {

            Box(modifier = Modifier.fillMaxWidth(0.7f))
            {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("商品编码: ${transaction.productID}")
                    Text("下单时间: ${transaction.time}")
                    Text("下单账号：${transaction.cardNumber}")
                    transaction.remark?.let { Text("评论: $it") }
                }
            }
            Box()
            {
                Column(modifier = Modifier.padding(vertical = 8.dp))
                {
                    Text("价格: \$${transaction.productPrice}")
                    Text("购买数量: ${transaction.productAmount}")
                }
            }
        }
    }
}
@Preview
@Composable
fun PreviewTransactionList() {
    val sampleTransactions = listOf(
        Transaction(2, "商品1",3799.0, 1, 111, "2024-09-02", "一次愉快的交易"),
        Transaction(3, "商品2",21688.0, 1, 123, "2024-09-06", null)
    )
    TransactionList(transactions = sampleTransactions)
}

@Composable
fun MyDealScreen(user:User) {
    val tempUser = tempUser(user.name,user.cardnumber,user.password)
    val MDhandler=MyDealHandler()
    val mydealsend=MyDealSend("store_getMyTransaction",tempUser.CardID,tempUser.password)
    val mydealback=MDhandler.handleAction(mydealsend)
    Column(modifier = Modifier.padding(16.dp)) {
        MyDealTitle()
        if (mydealback?.status == "success") {
            var list = mydealback?.transactions ?: emptyList()
            TransactionList(transactions = list)
        } else {
            Text("No transactions available")
        }
    }
}
//
//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//        MyDealScreen()
//    }
//}