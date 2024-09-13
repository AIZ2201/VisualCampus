package example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import example.handlers.AllTranBack
import example.handlers.AllTranHandler
import example.handlers.AllTranSend
import example.handlers.BorrowedBook

@Composable
fun showAllTranTitle() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "所有借阅信息",
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
            text = "查看所有借阅信息",
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
fun BorrowedBookApp(user: User) {
    var searchQuery by remember { mutableStateOf("") }
    var borrowedBooks by remember { mutableStateOf<List<BorrowedBook>>(emptyList()) }
    // 示例借阅书籍数据
//    val borrowedBooks = listOf(
//        BorrowedBook("T001", "2023-01-01", "U001", "B001", "978-1234567890", "计算机科学导论", "2023-02-01"),
//        BorrowedBook("T002", "2023-01-05", "U002", "B002", "978-0987654321", "数据结构与算法", "2023-02-05"),
//        BorrowedBook("T003", "2023-02-01", "U003", "B003", "978-2222222222", "人工智能导论", "2023-03-01")
//    )

    val AThandler=AllTranHandler()
    val atsend=AllTranSend("library_alltran",user.cardnumber)
    val atback= AThandler.handleAction(atsend)
    if(atback!=null){
        borrowedBooks=atback.transactions
    }

    // 根据搜索查询过滤书籍
    val filteredBooks = borrowedBooks.filter { book ->
        searchQuery.isBlank() || book.tranId.contains(searchQuery, true)
                || book.bookName.contains(searchQuery, true)
                || book.tranUserId.contains(searchQuery, true)
                || book.tranIsbn.contains(searchQuery, true)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        showAllTranTitle()
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
            Text("交易编号", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("书名", modifier = Modifier.weight(2f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("借阅时间", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("到期时间", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("一卡通号", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) // 新增一卡通号
        }

        // 表格内容
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            filteredBooks.forEach { book ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(book.tranId, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(book.bookName, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                    Text(book.tranBorrowTime, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(book.tranDueTime, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(user.cardnumber.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center) // 展示一卡通号
                }
            }
        }
    }
}

