package example
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.rememberWindowState
import example.handlers.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun onRenewBook(bookId: String, luser: LibraryUser, tempUser: User): LibraryUser {
    println("续借书籍: $bookId")
    val RNhandler = RenewBookHandler()
    val renewbooksend = RenewBookSend("library_renewBooks", tempUser.cardnumber, bookId)
    val renewbookback = RNhandler.handleAction(renewbooksend)
    if (renewbookback == "success") {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val updatedBorrowedBooks = luser.userBorrowed.map { book ->
            if (book.tranBookId == bookId) {
                // 解析当前到期时间
                val currentDueDate = LocalDate.parse(book.tranDueTime, formatter)
                // 增加三个月
                val newDueDate = currentDueDate.plus(3, ChronoUnit.MONTHS)
                // 格式化新的到期时间
                val newDueDateStr = newDueDate.format(formatter)
                // 更新书籍记录
                book.copy(tranDueTime = newDueDateStr)
            } else {
                book
            }
        }
        println("续借成功")

        return luser.copy(
            userBorrowedNum = updatedBorrowedBooks.size,
            userBorrowed = updatedBorrowedBooks
        )
    } else {
        println("续借失败")
        return luser // 如果续借失败，返回原始的 LibraryUser 对象
    }
}


fun onReturnBook(bookId: String, luser: LibraryUser, tempUser: User): LibraryUser {
    println("还书书籍: $bookId")
    val RBhandler = ReturnBookHandler()
    val returnbooksend = ReturnBookSend("library_returnBooks", tempUser.cardnumber, bookId)
    val returnbookback = RBhandler.handleAction(returnbooksend)

    return if (returnbookback == "success") {
        // 创建一个新的 userBorrowed 列表，排除指定的书籍
        val updatedBorrowedBooks = luser.userBorrowed.filterNot { it.tranBookId == bookId }
        println("还书成功")
        // 构建新的 LibraryUser 对象，并用新的 userBorrowed 列表替换原有的列表
        luser.copy(
            userBorrowedNum = updatedBorrowedBooks.size,
            userBorrowed = updatedBorrowedBooks
        )

    } else {
        println("还书失败")
        luser // 如果还书失败，返回原始的 luser
    }
}


@Composable
fun UserBookScreen(user:User) {
    var mybooksend=MybooksSend("library_getUser",user.cardnumber)
    var MBhandler=MybooksHandler()
    var mybooksBack = MBhandler.handleAction(mybooksend)
    var currentLuser by remember{ mutableStateOf<LibraryUser?>(null) }
    if(mybooksBack!=null&&mybooksBack.status=="success")
    {
        currentLuser = mybooksBack.user
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MybooksTitle()

        Box(
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Column {
                Text("用户信息", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                Text("用户名: ${currentLuser?.userName}")
                Text("已借书籍: ${currentLuser?.userBorrowedNum}/${currentLuser?.userMaxBorrowNum}")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("已借书籍", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))
        currentLuser?.userBorrowed?.forEach { book ->
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .border(1.dp, Color.LightGray),
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                ) {
                    BorrowedBookItem(book = book, currentLuser!!, user) { updatedLuser ->
                        currentLuser = updatedLuser
                    }
                }
            }
        }
    }
}

@Composable
fun BorrowedBookItem(
    book: BorrowedBook,
    luser: LibraryUser,
    tempUser: User,
    onLuserUpdated: (LibraryUser) -> Unit
) {
    var showRenewConfirmDialog by remember { mutableStateOf(false) }
    var showReturnConfirmDialog by remember { mutableStateOf(false) }
    var showResponseDialog by remember { mutableStateOf(false) }
    var responseMessage by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.bookName,
                fontWeight = FontWeight.Bold
            )
            Text("ID: ${book.tranBookId}")
            Text("借阅时间: ${book.tranBorrowTime}")
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dueDate = LocalDate.parse(book.tranDueTime, dateFormatter)
            val currentDate = LocalDate.now()

            val textColor = if (dueDate.isBefore(currentDate)) Color.Red else Color.Black

            Text(
                text = "归还时间: ${book.tranDueTime}",
                color = textColor
            )
        }
        Button(onClick = {
            showRenewConfirmDialog = true
        }) {
            Text("续借")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            showReturnConfirmDialog = true
        }) {
            Text("还书")
        }
    }

    // Renew Confirmation Dialog
    if (showRenewConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRenewConfirmDialog = false },
            title = { Text("确认续借") },
            text = { Text("你确定要续借这本书吗？") },
            confirmButton = {
                Button(onClick = {
                    showRenewConfirmDialog = false
                    // Handle the renew action
                    val updatedLuser = onRenewBook(book.tranBookId, luser, tempUser)
                    onLuserUpdated(updatedLuser)
                    responseMessage = "续借成功"
                    showResponseDialog = true
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showRenewConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // Return Confirmation Dialog
    if (showReturnConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showReturnConfirmDialog = false },
            title = { Text("确认还书") },
            text = { Text("你确定要归还这本书吗？") },
            confirmButton = {
                Button(onClick = {
                    showReturnConfirmDialog = false
                    // Handle the return action
                    val updatedLuser = onReturnBook(book.tranBookId, luser, tempUser)
                    onLuserUpdated(updatedLuser)
                    responseMessage = "还书成功"
                    showResponseDialog = true
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showReturnConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // Response Dialog
    if (showResponseDialog) {
        AlertDialog(
            onDismissRequest = { showResponseDialog = false },
            title = { Text("操作结果") },
            text = { Text(responseMessage) },
            confirmButton = {
                Button(onClick = { showResponseDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}

//
//fun main() = application {
//    val user=User(213223213,"李华","213","student")
//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "用户信息展示",
//        state = rememberWindowState(width = 800.dp, height = 600.dp)
//    ) {
//        UserBookScreen(user)
//    }
//}

@Composable
fun MybooksTitle() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "我的图书",
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
            text = "查看个人图书信息",
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

//@Composable
//fun PreviewUserBookScreen() {
//    val testUser = LibraryUser(
//        userId = 213223213,
//        userName = "John Doe",
//        userMaxBorrowNum = 3,
//        userBorrowedNum = 2,
//        userBorrowed = listOf(
//            BorrowedBook(
//                tranId = "0f81216d-575c-4848-88b1-fc6bea16b8de",
//                tranBorrowTime = "2024-08-30",
//                tranUserId = "213223213",
//                tranBookId = "3",
//                tranIsbn = "1",
//                bookName = "原神插画集v1.0"
//            ),
//            BorrowedBook(
//                tranId = "d7bc55fe-8198-4f07-9986-d685e1df20fd",
//                tranBorrowTime = "2024-08-30",
//                tranUserId = "213223213",
//                tranBookId = "2",
//                tranIsbn = "1",
//                bookName = "原神插画集v2.0"
//            )
//        )
//    )
//
//    val tempUser = tempUser("lee", 213223213, "123")
//    val luserState = remember { mutableStateOf(testUser) }
//
//    UserBookScreen(luserState, tempUser)
//}


//@Composable//主窗口
//fun UserBookScreen(luser: MutableState<LibraryUser>, tempUser: tempUser) {
//    var searchText by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        MybooksTitle()
//
//        Box(
//            modifier = Modifier.fillMaxWidth(0.5f)
//        ) {
//            Column {
//                Text("用户信息", style = MaterialTheme.typography.h6)
//                Spacer(modifier = Modifier.height(8.dp))
//                Text("用户名: ${luser.value.userName}")
//                Text("已借书籍: ${luser.value.userBorrowedNum}/${luser.value.userMaxBorrowNum}")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("已借书籍", style = MaterialTheme.typography.h6)
//        Spacer(modifier = Modifier.height(16.dp))
//
//        luser.value.userBorrowed.forEach { book ->
//            Box(
//                modifier = Modifier
//                    .padding(2.dp)
//                    .border(1.dp, Color.LightGray)
//            ) {
//                Box(
//                    modifier = Modifier.padding(8.dp)
//                ) {
//                    BorrowedBookItem(
//                        book = book,
//                        luser = luser.value,
//                        tempUser = tempUser
//                    ) { updatedLuser ->
//                        luser.value = updatedLuser
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("书籍借阅", style = MaterialTheme.typography.h6)
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(0.8f)
//                    .fillMaxHeight(0.6f)
//            ) {
//                TextField(
//                    value = searchText,
//                    onValueChange = { searchText = it },
//                    label = {
//                        Text("输入书号")
//                    },
//                    modifier = Modifier
//                        .padding(0.dp)
//                        .fillMaxWidth(0.9f)
//                )
//            }
//            Box {
//                Button(
//                    onClick = {
//                        onBorrowBook(searchText, luser.value, tempUser) { updatedLuser ->
//                            luser.value = updatedLuser
//                        }
//                    },
//                    modifier = Modifier.fillMaxHeight(0.55f),
//                    enabled = luser.value.userBorrowedNum < luser.value.userMaxBorrowNum
//                ) {
//                    Text("我要借书")
//                }
//            }
//        }
//    }
//}
