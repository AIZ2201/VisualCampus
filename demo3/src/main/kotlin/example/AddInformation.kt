package example

import androidx.compose.foundation.background
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
import example.handlers.*


@Composable
fun AddInformationTitle()
{
    Column(
        modifier = Modifier.padding(16.dp)
    )
    {
        Text(
            text = "添加学生",
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
            text = "添加新学生信息",
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
fun AddInformation(user:User) {
    var name by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var studentNumber by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var studentStat by remember { mutableStateOf("") }
    var enrollment by remember { mutableStateOf("") }
    var birthPlace by remember { mutableStateOf("") }
    var politicalStat by remember { mutableStateOf("") }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var responseMessage by remember { mutableStateOf("") }

    val isFormValid by remember {
        derivedStateOf {
            name.isNotEmpty() &&
                    cardNumber.isNotEmpty() &&
                    studentNumber.isNotEmpty() &&
                    major.isNotEmpty() &&
                    school.isNotEmpty() &&
                    enrollment.isNotEmpty() &&
                    birthPlace.isNotEmpty()
        }
    }
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
                AdmnTextBox(label = "性别", initialValue = gender , onValueChange = {gender=it})
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
                AdmnTextBox(label = "学籍状态", initialValue = studentStat, onValueChange = {studentStat=it })
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
                AdmnTextBox(label = "入学时间", initialValue = enrollment, onValueChange = { enrollment = it })
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
            ) {
                val politicalMap = mapOf(
                    PoliticialStat.PartyMember to "党员",
                    PoliticialStat.Members to "群众",
                    PoliticialStat.Masses to "团员"
                )
                AdmnTextBox(label = "政治面貌", initialValue = politicalStat, onValueChange = {politicalStat=it})
            }
        }
        val genderMap = mapOf(
            "男" to Gender.MALE,
            "女" to Gender.FEMALE,
        )
        val StatMap=mapOf(
            "在籍" to Status.ON,
            "不在籍" to Status.OFF
        )
        val PolitMap=mapOf(
            "群众" to PoliticialStat.Masses,
            "团员" to PoliticialStat.Members,
            "党员" to PoliticialStat.PartyMember
        )

        // Confirmation Button
        Button(
            onClick = { showConfirmDialog = true },
            enabled = isFormValid,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.End)
        ) {
            Text("添加")
        }

        // Show Confirmation Dialog
        if (showConfirmDialog) {
            ConfirmDialog(
                onConfirm = {
                    showConfirmDialog = false
                    // Proceed with adding the student
                    val genderMap = mapOf(
                        "男" to Gender.MALE,
                        "女" to Gender.FEMALE,
                    )
                    val statMap = mapOf(
                        "在籍" to Status.ON,
                        "不在籍" to Status.OFF
                    )
                    val politMap = mapOf(
                        "群众" to PoliticialStat.Masses,
                        "团员" to PoliticialStat.Members,
                        "党员" to PoliticialStat.PartyMember
                    )

                    val newStudent = StudentBack(
                        name = name,
                        cardNumber = cardNumber.toIntOrNull() ?: 0,
                        studentNumber = studentNumber,
                        gender = genderMap[gender] ?: Gender.UNKNOWN,
                        major = major,
                        school = school,
                        studentStat = statMap[studentStat] ?: Status.UNKNOWN,
                        enrollment = enrollment,
                        birthPlace = birthPlace,
                        politicalStat = politMap[politicalStat] ?: PoliticialStat.UNKNOWN
                    )

                    println("新学生信息为：$newStudent")
                    val addStudentSend = AddStudentSend("studentStatus_add", user.cardnumber, user.password, newStudent)
                    val AIHandler = AddInformationHandler()
                    val message = AIHandler.handleAction(addStudentSend)
                    responseMessage = message?.message ?: "操作失败"
                    showMessageDialog = true

                    if (message?.status == "success") {
                        name = ""
                        cardNumber = ""
                        studentNumber = ""
                        gender = ""
                        major = ""
                        school = ""
                        studentStat = ""
                        enrollment = ""
                        birthPlace = ""
                        politicalStat = ""
                    }
                },
                onDismiss = { showConfirmDialog = false }
            )
        }

        // Show Message Dialog
        if (showMessageDialog) {
            MessageDialog(
                message = responseMessage,
                onDismiss = { showMessageDialog = false }
            )
        }
    }
}

@Composable
fun ConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "确认操作") },
        text = { Text("你确定要添加这个学生信息吗？") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun MessageDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "操作结果") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
fun AddInformationScreen(user:User)
{
    Column {
        AddInformationTitle()
        AddInformation(user)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        val user=User(123,"李华","123","admin")
        AddInformationScreen(user)
    }
}