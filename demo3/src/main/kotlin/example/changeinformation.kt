package example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import example.handlers.StudentBack
import example.handlers.Gender
import example.handlers.PoliticialStat
import example.handlers.Status
import kotlin.collections.List
import example.handlers.ChangeStudentSend
import example.handlers.changeInformationHandler
import example.handlers.SearchStudentSend
import example.handlers.searchInformationHandler

@Composable
fun AdmnTextBox(label: String, initialValue: String, onValueChange: (String) -> Unit) {//可修改的对话框组件
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
fun AdmnInfoList(
    user:User,
    student: StudentBack,
    onStudentChange: (StudentBack) -> Unit
) {
    var name by remember { mutableStateOf(student.name) }
    var cardNumber by remember { mutableStateOf(student.cardNumber.toString()) }
    var studentNumber by remember { mutableStateOf(student.studentNumber) }
    var gender by remember { mutableStateOf(student.gender) }
    var major by remember { mutableStateOf(student.major) }
    var school by remember { mutableStateOf(student.school) }
    var studentstat by remember { mutableStateOf(student.studentStat) }
    var enrollment by remember { mutableStateOf(student.enrollment) }
    var birthPlace by remember { mutableStateOf(student.birthPlace) }
    var politicalStat by remember { mutableStateOf(student.politicalStat) }

    // 用于控制确认对话框的显示状态
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var receivedMessage by remember { mutableStateOf("") }

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
                AdmnTextBox(label = "姓名", initialValue = name, onValueChange = { name = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            ) {
                AdmnTextBox(label = "一卡通号", initialValue = cardNumber, onValueChange = { cardNumber = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            ) {
                AdmnTextBox(label = "学号", initialValue = studentNumber, onValueChange = { studentNumber = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(1f)
            ) {
                val genderMap = mapOf(
                    Gender.MALE to "男",
                    Gender.FEMALE to "女"
                )
                AdmnTextBox(label = "性别", initialValue = genderMap[gender] ?: "未知", onValueChange = {
                    gender = if (it == "男") Gender.MALE else Gender.FEMALE
                })
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
                AdmnTextBox(label = "专业名称", initialValue = major, onValueChange = { major = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            ) {
                AdmnTextBox(label = "学院名称", initialValue = school, onValueChange = { school = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(1f)
            ) {
                val statusMap = mapOf(
                    Status.ON to "在籍",
                    Status.OFF to "停籍"
                )
                AdmnTextBox(label = "学籍状态", initialValue = statusMap[studentstat] ?: "未知", onValueChange = {
                    studentstat = if (it == "在籍") Status.ON else Status.OFF
                })
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(3f)
            ) {
                AdmnTextBox(label = "入学时间", initialValue = enrollment, onValueChange = {enrollment=it})
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            ) {
                AdmnTextBox(label = "籍贯", initialValue = birthPlace, onValueChange = { birthPlace = it })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(1f)
            )
            {
                val politicalMap = mapOf(
                    PoliticialStat.PartyMember to "党员",
                    PoliticialStat.Members to "群众",
                    PoliticialStat.Masses to "团员"
                )
                AdmnTextBox(label = "政治面貌", initialValue = politicalMap[politicalStat] ?: "未知", onValueChange = {
                    politicalStat = when (it) {
                        "党员" -> PoliticialStat.PartyMember
                        "群众" -> PoliticialStat.Members
                        "团员" -> PoliticialStat.Masses
                        else -> PoliticialStat.Masses
                    }
                })
            }
        }

        Button(
            onClick = {
                showConfirmDialog = true
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.End)
        ) {
            Text("保存修改")
        }
    }

    // 确认保存对话框
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("确认保存") },
            text = { Text("您确定要保存修改吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        // 更新modifystudent
                        val updatestudent = student.copy(
                            name = name,
                            cardNumber = cardNumber.toIntOrNull() ?: student.cardNumber,
                            studentNumber = studentNumber,
                            gender = gender,
                            major = major,
                            school = school,
                            studentStat = studentstat,
                            enrollment = enrollment,
                            birthPlace = birthPlace,
                            politicalStat = politicalStat
                        )
                        onStudentChange(updatestudent)
                        println("准备建立链接")
                        println("此时的学生为：$updatestudent")
                        val changestudentsend = ChangeStudentSend("studentStatus_change", user.cardnumber, user.password, updatestudent)
                        val CIhandler = changeInformationHandler()
                        val message = CIhandler.handleAction(changestudentsend)
                        if(message != null) {
                            if(message.status=="success")
                            {
                            receivedMessage = "保存成功"

                                }
                            else
                            {
                                receivedMessage = "保存失败"
                            }
                            showConfirmDialog = false
                            showMessageDialog = true
                        }
                    }
                ) {
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

    // 显示接收到的信息的对话框
    if (showMessageDialog) {
        AlertDialog(
            onDismissRequest = { showMessageDialog = false },
            title = { Text("操作结果") },
            text = { Text(receivedMessage) },
            confirmButton = {
                Button(onClick = { showMessageDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}


@Composable//可扩展组件，同时传递onStudentChange
fun ExpandableText(user:User,
                    data: StudentBack,
                   onStudentChange: (StudentBack) -> Unit) {
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
                text = data.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
            )
            Text(
                text = data.cardNumber.toString(),
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
                AdmnInfoList(user,student = data, onStudentChange = onStudentChange)
            }
        }
    }
}

@Composable
fun Title()
{
    Column(
        modifier = Modifier.padding(16.dp)
    )
    {
        Text(
            text = "用户信息",
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
            text = "修改个人用户信息",
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

@Composable//搜索框，同时将搜索到的信息与onStudentListChange绑定
fun SearchBox(user:User,onStudentListChange: (List<StudentBack>?) -> Unit)
{
    var searchText  by remember { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.15f)
                .padding(0.dp)
        )
        {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = {
                    Text("搜索学生(模糊搜索)")
                },
                modifier = Modifier
                    .height(72.dp)
                    .weight(3f)
                    .padding(10.dp)
                    .align(Alignment.CenterVertically)
            )

            Button(
                onClick = {
                    println("我想要搜索$searchText")
                    val searchstudentsend=SearchStudentSend("studentStatus_search",user.cardnumber,user.password,searchText)
                    val SIhandler=searchInformationHandler()
                    val studentlist=SIhandler.handleAction(searchstudentsend)
                    // 将搜索结果绑定到外部的 studentListState 变量
                     onStudentListChange(studentlist)
                },
                modifier = Modifier
                    .size(72.dp)
                    .weight(1f)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .padding(10.dp)
            )
            {
                Text("搜索")
            }
    }
}

@Composable//展示学生列表，因为被修改的信息不需要被传入SearchBox，因此不需要返回
fun SearchList( user:User,
                studentListState: List<StudentBack>?,
                onStudentListChange: (List<StudentBack>) -> Unit
                ) {
    var users = studentListState ?: emptyList()
    Column {
        // 使用 LazyColumn 展示用户列表
        LazyColumn {
            items(users.size) { index ->
                val stu = users[index]
                ExpandableText(user,data = stu) { updatedStudent ->
                    // 更新特定学生信息
                    val updatedList = users.toMutableList()
                    updatedList[index] = updatedStudent
                    onStudentListChange (updatedList)
                }
            }
        }
    }
}
@Composable
fun ChangeInformationTitle()
{
    Column(
        modifier = Modifier.padding(16.dp)
    )
    {
        Text(
            text = "修改学生信息",
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
            text = "搜索并修改学生信息",
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
fun ChangeInformation(user:User)
{
    var studentlist by remember { mutableStateOf<List<StudentBack>?>(null) }
    Column {
        ChangeInformationTitle()
        SearchBox(user,onStudentListChange = {studentlist=it})
        SearchList(user,studentListState=studentlist,onStudentListChange={studentlist=it})
    }
}

//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//        SearchScreen()
//    }
//}
