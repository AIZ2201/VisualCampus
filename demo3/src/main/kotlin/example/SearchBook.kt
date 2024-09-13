package example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import example.handlers.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import example.handlers.EmptyLibraryHandler
import example.handlers.EmptyLibrarySend
import java.net.URI
import java.awt.Desktop

data class tempUser(
    val username: String,
    val CardID: Int,
    val password: String
)

@Composable
fun CustomHorizontalDivider(color: Color = Color.Gray, thickness: Dp = 1.dp, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchBookBox(tempUser: User, onBookListChange: (List<Book>?) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var searchModel by remember { mutableStateOf("fuzzy") } // 默认搜索模式为"fuzzy"

    val searchOptions = listOf("fuzzy" to "模糊搜索", "accuracy" to "精确搜索")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 下拉框
        Box(
            modifier = Modifier.fillMaxWidth(0.2f)
        )
        {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = searchOptions.find { it.first == searchModel }?.second ?: "选择搜索模式",
                    onValueChange = { /* no-op */ },
                    readOnly = true,
                    label = { Text("搜索模式") },
                    trailingIcon = {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Color.White)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    searchOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            searchModel = option.first
                            expanded = false
                        }) {
                            Text(text = option.second)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("搜索图书") },
            modifier = Modifier
                .weight(3f)
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                println("搜索模式: $searchModel, 搜索内容: $searchText")
                val searchBookSend = SearchBookSend("library_searchBooks", tempUser.cardnumber, tempUser.password, searchText, searchModel)
                val searchBookHandler = SearchBookHandler()
                val bookList = searchBookHandler.handleAction(searchBookSend)
                onBookListChange(bookList)
            },
            modifier = Modifier
                .size(72.dp)
                .padding(10.dp)
        ) {
            Text("搜索")
        }
    }
}

@Composable
fun BookCommandBox(data: BookCommand) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Row {
                Text(
                    text = data.comUserName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(2.dp)
                )
                Text(
                    text = data.comTime,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(2.dp)
                )
            }
            Text(
                text = data.comText,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(2.dp)
            )
        }
    }
}

fun getTime(): String {
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentTime.format(formatter)
}
@Composable
fun Command(tempUser: User, book: Book, onCommentAdded: (Book) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .weight(3f)
                .size(70.dp)
                .padding(10.dp)
                .align(Alignment.CenterVertically)
        )

        Button(
            onClick = {
                println("我想要发表评论")
                val chandler = CommentHandler()
                val bookComment = BookCommand(getTime(), tempUser.cardnumber.toString(), tempUser.name, searchText)
                val commentSend = CommentSend("library_addCom", tempUser.cardnumber, tempUser.password, book.bookIsbn, bookComment)
                val message = chandler.handleAction(commentSend)

                if (message == "success") {
                    println("发表评论成功")
                    val updatedComments = book.comments + bookComment
                    val updatedBook = book.copy(comments = updatedComments)
                    onCommentAdded(updatedBook) // 调用回调函数
                    dialogMessage = "发表评论成功！"
                    searchText=""
                } else {
                    println("发表评论失败")
                    dialogMessage = "发表评论失败，请重试。"
                }

                // 显示弹窗
                showDialog = true
            },
            modifier = Modifier
                .size(60.dp)
                .weight(1f)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .padding(10.dp)
        ) {
            Text("评论")
        }
    }

    // 弹窗显示逻辑
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // 用户点击外部时关闭弹窗
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("确定")
                }
            },
            title = { Text(text = "评论状态") },
            text = { Text(text = dialogMessage) }
        )
    }
}

@Composable
fun BookCommandList(tempUser: User, book:Book, onBookChange: (Book) -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "读者评价",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(2.dp)
        )
        if (book.comments.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (currentIndex > 0) {
                            currentIndex--
                        }
                    },
                    enabled = currentIndex > 0
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "上一条")
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BookCommandBox(book.comments[currentIndex])
                }

                IconButton(
                    onClick = {
                        if (currentIndex < book.comments.size - 1) {
                            currentIndex++
                        }
                    },
                    enabled = currentIndex < book.comments.size - 1
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "下一条")
                }
            }
        }
        else {
            Text(
                text = "暂无读者评价",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(2.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Command(tempUser, book) { updatedBook ->
            onBookChange(updatedBook)
            // Handle comment addition, which is already taken care of in ExpandableItem
        }
    }
}


@Composable
fun showBookItem(book: MutableState<BookItem>, user: User, onNumChange: (Int) -> Unit) {
    var canBorrow by remember { mutableStateOf(book.value.status == "on") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showResponseDialog by remember { mutableStateOf(false) }
    var responseMessage by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = book.value.bookId,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (book.value.status == "on") "可借阅" else "不可借阅",
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        Box {
            Button(
                onClick = {
                    showConfirmDialog = true
                },
                modifier = Modifier.fillMaxHeight(0.55f),
                enabled = canBorrow
            ) {
                Text("我要借书")
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("确认借书") },
            text = { Text("你确定要借阅这本书吗？") },
            confirmButton = {
                Button(onClick = {
                    showConfirmDialog = false
                    // Handle the borrowing action
                    val BBhandler = BorrowBookHandler()
                    val borrowbooksend = BorrowBookSend("library_borrowBooks", user.cardnumber, book.value.bookId)
                    val borrowbookback = BBhandler.handleAction(borrowbooksend)
                    if (borrowbookback != null) {
                        println(borrowbookback.toString())
                        canBorrow = false
                        onNumChange(1)
                        responseMessage = "借书成功"
                    } else {
                        responseMessage = "借书失败，请稍后再试。"
                    }
                    showResponseDialog = true
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDialog = false }) {
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


@Composable
fun showBookItemList(books: List<BookItem>, user:User,onNumChange:(Int)->Unit) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "藏书清单",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.Start)
        )

        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "书号",
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "借阅状态",
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                books.forEach { bookItem ->
                    val bookItemState = remember { mutableStateOf(bookItem) }
                    showBookItem(bookItemState,user){num->
                        onNumChange(num)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
fun HyperlinkText(text: String, url: String) {
    val annotatedText = AnnotatedString.Builder(text).apply {
        addStyle(
            style = SpanStyle(
                color = Color.Black,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            ),
            start = 0,
            end = text.length
        )
    }.toAnnotatedString()

    ClickableText(
        text = annotatedText,
        onClick = {
            // 打开系统默认浏览器并访问链接
            val desktop = Desktop.getDesktop()
            desktop.browse(URI(url))
        }
    )
}

// 左侧卡片内容（展示 newsEvents）
@Composable
fun NewsEventsCard(newsEvents: List<NewsEvent>) {
    Card(
        modifier = Modifier
            .fillMaxHeight()

            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column {
            Text(
                "最新事件",
                color = Color.Black,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            CustomHorizontalDivider()
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                //Text("新闻活动",color=Color.Black, fontSize = 15.sp)
                items(newsEvents.size) { index ->
                    val event = newsEvents[index]
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        HyperlinkText(text = event.title, url = event.link)
                        Text(
                            text = event.time,
                            style = MaterialTheme.typography.subtitle2,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ActionEventsCard(actionEvents: List<ActionEvent>) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column {
            Text(
                "活动预览",
                color = Color.Black,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            CustomHorizontalDivider()
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(actionEvents.size) { index ->
                    val event = actionEvents[index]
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (event.link.isNotEmpty()) {
                            HyperlinkText(text = event.title, url = event.link)
                        } else {
                            Text(text = event.title, color = androidx.compose.ui.graphics.Color.Gray)
                        }
                        Text(text = event.time, style = MaterialTheme.typography.subtitle2, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyLibrary(tempUser:User)
{
    val ELhandler=EmptyLibraryHandler()
    val emptylibrarysend=EmptyLibrarySend("library_getMes",tempUser.cardnumber)
    val emptylibraryback=ELhandler.handleAction(emptylibrarysend)
//    val emptylibraryback = EmptyLibraryBack(
//        newsEvents = listOf(
//            NewsEvent(
//                time = "08-29",
//                title = "图书馆9月活动预告",
//                link = "http://www.lib.seu.edu.cn/bencandy.php?fid=263&id=10551"
//            ),
//            NewsEvent(
//                time = "08-28",
//                title = "言恭达教授《抱云堂文丛》等图书捐赠暨师生座谈会成功举行",
//                link = "http://www.lib.seu.edu.cn/bencandy.php?fid=263&id=10548"
//            )
//            // 您可以继续添加更多的 NewsEvent
//        ),
//        actionEvents = listOf(
//            ActionEvent(
//                time = "9月1日-9月30日",
//                title = "图书馆九月活动预告",
//                link = "http://www.lib.seu.edu.cn/m_upload/sep-schedule.pdf"
//            ),
//            ActionEvent(
//                time = "9月18日 14:00~15:00",
//                title = "提升学术探索效率：图书馆一站式检索工具解析",
//                link = "http://www.lib.seu.edu.cn/bencandy.php?fid=662&aid=10555"
//            )
//            // 您可以继续添加更多的 ActionEvent
//        ),
//        status = "success"
//    )
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        )
        {
            NewsEventsCard(newsEvents = emptylibraryback?.newsEvents?: emptyList())
        }
        Box(
            modifier = Modifier.weight(1f)
        )
        {
            ActionEventsCard(actionEvents = emptylibraryback?.actionEvents ?: emptyList())
        }
    }
}

@Composable
fun ExpandableItem(data: Book,user:User, onBookListChange: (Book) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var canBorrowNum by remember { mutableStateOf(data.onCount) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { expanded = !expanded }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = data.bookName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(2.dp)
                )
                Text(
                    text = "作者：" + data.bookAuthor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                )
                Text(
                    text = "出版社：" + data.bookPress,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                )
                Text(
                    text = "ISBN：" + data.bookIsbn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                )
                Text(
                    text = "位置：" + data.bookPlace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                )
            }
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "馆藏数量：" + data.num.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(2.dp)
                )
                Text(
                    text = "可借数量：" + canBorrowNum.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(2.dp)
                )
            }
        }
        if (expanded) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight()
            ) {
                Column(modifier = Modifier.fillMaxHeight(0.7f)) {
                    CustomHorizontalDivider(color = Color.Gray, thickness = 1.dp)
                    Row {
                        Box(
                            modifier = Modifier.fillMaxWidth(0.3f)
                        ) {
                            AsyncImage(
                                load = { loadImageBitmap(data.bookPhoto) },
                                painterFor = { BitmapPainter(it) },
                                contentDescription = "Sample",
                                modifier = Modifier.width(200.dp)
                            )
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "简介：" + data.bookDescription,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                            )
                            BookCommandList(user, data){ updatebook->
                                onBookListChange(updatebook)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxHeight()
                    ) {
                        showBookItemList(data.bookStatus,user){num->
                            canBorrowNum=canBorrowNum-num
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBookList(tempUser: User, bookListState: List<Book>?,
                   onBookListChange: (List<Book>?) -> Unit) {

    if(bookListState == null) {
        EmptyLibrary(tempUser)
    }
    else {
        var books = bookListState ?: emptyList()
        Column {
            // 使用 LazyColumn 展示用户列表
            LazyColumn {
                items(books.size) { index ->
                    val book = books[index]
                    ExpandableItem(book, tempUser) { updatedBook ->
                        val updatedList = books.toMutableList()
                        updatedList[index] = updatedBook
                        onBookListChange(updatedList)
                    }
                }
            }
        }
    }
}


@Composable
fun SearchBookTitle() {
    Text(
        text = "图书馆主页",
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
        text = "我的图书馆",
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

@Composable
fun SearchBookScreen(user:User) {
    var booklist by remember { mutableStateOf<List<Book>?>(null) }

    Column {
        SearchBookTitle()
        SearchBookBox(user,onBookListChange = {booklist=it})
        SearchBookList(user,bookListState=booklist,onBookListChange={booklist=it})
    }
}




