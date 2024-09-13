package example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import example.handlers.ChangeBookSend
import example.handlers.ChangeBookHandler
import example.handlers.*
import kotlin.collections.List

@Composable
fun ChangeBookTextBox(label: String, initialValue: String, onValueChange: (String) -> Unit) {//可修改的对话框组件

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

@Composable//搜索出来的单个用户项，修改内容与onStudentChange绑定
fun ChangeBookInfoList(tempUser: tempUser, book: Book,
                       onBookChange: (Book) -> Unit
) {
    var bookIsbn by remember { mutableStateOf(book.bookIsbn) }
    var bookName by remember { mutableStateOf(book.bookName) }
    var bookAuthor by remember { mutableStateOf(book.bookAuthor) }
    var bookPress by remember { mutableStateOf(book.bookPress) }
    var bookDescription by remember { mutableStateOf(book.bookDescription) }
    var bookPlace by remember { mutableStateOf(book.bookPlace) }
    var bookPhoto by remember { mutableStateOf(book.bookPhoto) }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

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
                ChangeBookTextBox(label = "书籍ISBN", initialValue = bookIsbn, onValueChange = { bookIsbn = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            ) {
                ChangeBookTextBox(label = "书名", initialValue = bookName, onValueChange = { bookName = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            ) {
                ChangeBookTextBox(label = "作者", initialValue = bookAuthor, onValueChange = { bookAuthor = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(1f)
            ) {
                ChangeBookTextBox(label = "出版社", initialValue = bookPress, onValueChange = { bookPress = it })
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
                ChangeBookTextBox(
                    label = "描述",
                    initialValue = bookDescription,
                    onValueChange = { bookDescription = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            ) {
                ChangeBookTextBox(label = "位置", initialValue = bookPlace, onValueChange = { bookPlace = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            ) {
                ChangeBookTextBox(label = "书籍图片链接", initialValue = bookPhoto, onValueChange = { bookPhoto = it })
            }

        }
        val isFormComplete = bookIsbn.isNotBlank() &&
                bookName.isNotBlank() &&
                bookAuthor.isNotBlank() &&
                bookPress.isNotBlank() &&
                bookDescription.isNotBlank() &&
                bookPlace.isNotBlank()
        Spacer(modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                showConfirmationDialog = true
            },
            enabled = isFormComplete,
            modifier = Modifier.align(Alignment.End).width(120.dp).height(60.dp)
        ) {
            Text("修改")
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("确认修改") },
            text = { Text("你确定要修改这本书的信息吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        // 执行更新操作
                        val updatedBook = Book(
                            bookIsbn = bookIsbn,
                            bookName = bookName,
                            bookAuthor = bookAuthor,
                            bookPress = bookPress,
                            bookDescription = bookDescription,
                            bookPlace = bookPlace,
                            bookStatus = book.bookStatus,
                            comments = book.comments,
                            num = book.num,
                            onCount = book.onCount,
                            bookPhoto = bookPhoto
                        )

                        println("准备建立链接")
                        println("此时的书籍信息为：$updatedBook")

                        val CBhandler = ChangeBookHandler()
                        val changebooksend = ChangeBookSend("library_updBooks", tempUser.CardID, updatedBook)
                        val changebookback = CBhandler.handleAction(changebooksend)
                        if (changebookback != null) {
                            println(changebookback)
                            feedbackMessage = if (changebookback == "success") {
                                onBookChange(updatedBook)
                                "修改成功"
                            } else {
                                "修改失败: $changebookback"
                            }
                            showFeedbackDialog = true
                        }
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text("结果") },
            text = { Text(feedbackMessage) },
            confirmButton = {
                Button(onClick = { showFeedbackDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}


@Composable//可扩展组件，同时传递onStudentChange
fun SearchBookExpandableText(tempUser:tempUser, book: Book,
                             onBookChange: (Book) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

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
                    .size(12.dp)
                    .align(Alignment.CenterVertically)
                    .rotate(if (expanded) 0f else 270f)
            )
            Text(
                text = book.bookName,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
            )
            Text(
                text = book.bookAuthor.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.68f)
                    .padding(0.dp)
            ) {
                ChangeBookInfoList(tempUser,book,onBookChange=onBookChange)
            }
        }
    }
}

@Composable
fun ChangeBookTitle()
{
    Column(
        modifier = Modifier.padding(16.dp)
    )
    {
        Text(
            text = "修改图书",
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
            text = "修改图书信息",
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

@OptIn(ExperimentalMaterialApi::class)
@Composable//搜索框，同时将搜索到的信息与onStudentListChange绑定
fun ChangeBookSearchBox(tempUser:tempUser, onBookListChange: (List<Book>?) -> Unit)
{
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
                    .weight(1f)
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
                val searchBookSend = SearchBookSend("library_searchBooks", tempUser.CardID, tempUser.password, searchText, searchModel)
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

@Composable//展示学生列表，因为被修改的信息不需要被传入SearchBox，因此不需要返回
fun ChangeBookList(tempUser:tempUser, bookListState: List<Book>?,
                   onBookListChange: (List<Book>) -> Unit
) {
    var books = bookListState ?: emptyList()
    Column {
        // 使用 LazyColumn 展示用户列表
        LazyColumn {
            items(books.size) { index ->
                val book = books[index]
                SearchBookExpandableText(tempUser,book = book) { updatedBook ->
                    // 更新特定学生信息
                    val updatedList = books.toMutableList()
                    updatedList[index] =updatedBook
                    onBookListChange (updatedList)
                }
            }
        }
    }
}

@Composable
fun ChangeBookScreen(user:User)
{
    val tempUser=tempUser(user.name,user.cardnumber,user.password)

    var booklist by remember { mutableStateOf<List<Book>?>(null) }
    Column {
        ChangeBookTitle()
        ChangeBookSearchBox(tempUser,onBookListChange = {booklist=it})
        ChangeBookList(tempUser,bookListState=booklist,onBookListChange={booklist=it})
    }
}
//
//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//        ChangeBookScreen()
//    }
//}
