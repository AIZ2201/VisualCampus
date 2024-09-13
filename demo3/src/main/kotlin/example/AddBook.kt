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
import example.handlers.NewBook
import example.handlers.DeleteBookHandler
import example.handlers.DeleteBookSend

@Composable
fun AddBookTextItem(label: String, initialValue: String, onValueChange: (String) -> Unit) {//可修改的对话框组件
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
fun AddBookTextItems(tempUser: tempUser) {
    var bookIsbn by remember { mutableStateOf("") }
    var bookName by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var bookPress by remember { mutableStateOf("") }
    var bookDescription by remember { mutableStateOf("") }
    var bookPlace by remember { mutableStateOf("") }
    var bookPhoto by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(2.dp)
    ) {
        Text("添加书籍", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .padding(4.dp)
                    .weight(1f)
            ) {
                AddBookTextItem(label = "书籍ISBN", initialValue = bookIsbn, onValueChange = { bookIsbn = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddBookTextItem(label = "书名", initialValue = bookName, onValueChange = { bookName = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddBookTextItem(label = "作者", initialValue = bookAuthor, onValueChange = { bookAuthor = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .weight(1f)
            ) {
                AddBookTextItem(label = "出版社", initialValue = bookPress, onValueChange = { bookPress = it })
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
                AddBookTextItem(label = "描述", initialValue = bookDescription, onValueChange = { bookDescription = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .weight(2f)
            ) {
                AddBookTextItem(label = "位置", initialValue = bookPlace, onValueChange = { bookPlace = it })
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
                AddBookTextItem(label = "书本图片链接", initialValue = bookPhoto, onValueChange = { bookPhoto = it })
            }
        }

        val isFormComplete = bookIsbn.isNotBlank() &&
                bookName.isNotBlank() &&
                bookAuthor.isNotBlank() &&
                bookPress.isNotBlank() &&
                bookDescription.isNotBlank() &&
                bookPlace.isNotBlank()

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                // 创建书籍信息对象
                val updatedBook = NewBook(
                    bookIsbn = bookIsbn,
                    bookName = bookName,
                    bookAuthor = bookAuthor,
                    bookPress = bookPress,
                    bookDescription = bookDescription,
                    bookPlace = bookPlace,
                    bookPhoto = bookPhoto,
                )

                // 调用处理函数并获取返回值
                val ABhandler = AddBookHandler()
                val addbooksend = AddBookSend("library_addBook", tempUser.CardID, updatedBook)
                val addbookback = ABhandler.handleAction(addbooksend)

                // 根据返回值更新UI
                if (addbookback != null) {
                    if (addbookback == "success") {
                        // 清空输入框
                        bookIsbn = ""
                        bookName = ""
                        bookAuthor = ""
                        bookPress = ""
                        bookDescription = ""
                        bookPlace = ""
                        bookPhoto = ""
                        dialogMessage = "书籍添加成功！"
                    } else {
                        dialogMessage = "书籍添加失败，请重试。"
                    }
                    showDialog = true
                }
            },
            enabled = isFormComplete, // 只有在所有字段都不为空时，按钮才可点击
            modifier = Modifier
                .align(Alignment.End)
                .width(120.dp)
                .height(60.dp)
        ) {
            Text("添加")
        }

        // 弹出提示对话框
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("提示") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

@Composable
fun ADBookTitle()
{
    Column(
        modifier = Modifier.padding(16.dp)
    )
    {
        Text(
            text = "增删图书",
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
            text = "增删图书馆书籍",
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
fun DeleteBookItem(tempUser: tempUser) {
    var searchText by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text("删除书籍", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("请输入书号") },
                modifier = Modifier
                    .weight(3f)
                    .padding(2.dp)
                    .align(Alignment.CenterVertically)
            )

            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text("删除")
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("确认删除") },
                text = { Text("你确定要删除书号为 $searchText 的书籍吗?") },
                confirmButton = {
                    Button(onClick = {
                        showConfirmDialog = false
                        // Handle the deletion
                        println("我想要删除 $searchText")
                        val deletebooksend = DeleteBookSend("library_remBook", tempUser.CardID, searchText)
                        val DBhandler = DeleteBookHandler()
                        val deletebookback = DBhandler.handleAction(deletebooksend)
                        resultMessage = if (deletebookback == "success") {
                            "书籍删除成功"
                        } else {
                            "书籍删除失败: $deletebookback"
                        }
                        showResultDialog = true
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

        if (showResultDialog) {
            AlertDialog(
                onDismissRequest = { showResultDialog = false },
                title = { Text("删除结果") },
                text = { Text(resultMessage) },
                confirmButton = {
                    Button(onClick = { showResultDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

@Composable
fun ADBookScreen(user:User)
{

    var tempUser=tempUser(
    user.name,user.cardnumber,user.password)
    Box(
        modifier = Modifier.fillMaxHeight()
    )
    {
        Column(modifier = Modifier.fillMaxHeight()) {
            ADBookTitle()
            Box(
                modifier = Modifier.fillMaxHeight(0.7f)
            )
            {
                AddBookTextItems(tempUser)
            }
            DeleteBookItem(tempUser)
        }
    }
}
