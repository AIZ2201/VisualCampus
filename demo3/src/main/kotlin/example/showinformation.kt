package example
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
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
import java.text.SimpleDateFormat
import java.util.*

data class StudentSend(
    val operation:String,
    val cardNumber: Int,
    val password:String,
)
fun calendarToString(calendar: Calendar):String{
    val format=SimpleDateFormat("yyyy-MM-dd")
    return format.format(calendar.time)
}

@Composable
fun GetInformation(data:StudentSend)
{
    val SIHandler=showInformationHandler()
    val dataBack=SIHandler.handleAction(data)
    if(dataBack!=null)
    {
        UserInfoScreen(dataBack)
    } else {
        println("Failed to receive valid data from server")
    }
}

@Composable
fun MyTextBox(label: String, initialValue: String) {
    var text by remember{ mutableStateOf(initialValue)}
    var lbcolor=Color.LightGray
    DisableSelection {
        OutlinedTextField(
            value = text,
            onValueChange = {},
            readOnly = true,//禁用编辑
            enabled = true,//禁用点击与焦点
            shape= RoundedCornerShape(16.dp),
            label={ Text(label) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Gray,

                focusedBorderColor = Color.Black, // 紫色边框
                unfocusedBorderColor = Color.Black, // 紫色边框
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black
            ),
            modifier=Modifier.background(Color.Transparent)
                .padding(0.dp)
                .fillMaxWidth()

        )
    }
}

@Composable
fun UserInfoList(student: StudentBack) {  // 改为 StudentBack
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState) // 启用滚动
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        )
        {
            Box(
                modifier = Modifier.fillMaxWidth(0.2f)
                    .padding(8.dp)
                    .weight(1f)
            )
            {
                MyTextBox(label = "姓名", initialValue = student.name)
            }
            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            )
            {
                MyTextBox(label = "一卡通号", initialValue = student.cardNumber.toString())
            }
            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            )
            {
                MyTextBox(label = "学号", initialValue = student.studentNumber)
            }
            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(1f)
            )
            {
                val genderMap=mapOf(
                    Gender.MALE to "男",
                    Gender.FEMALE to "女"
                )
                MyTextBox(label = "性别", initialValue = genderMap[student.gender]?:"未知")
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        {
            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            )
            {
                MyTextBox(label = "专业名称", initialValue = student.major)
            }
            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            )
            {
                MyTextBox(label = "学院名称", initialValue = student.school)
            }
            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(1f)
            )
            {
                val statusMap= mapOf(
                    Status.ON to "在籍",
                    Status.OFF to "停籍"
                )
                MyTextBox(label = "学籍状态", initialValue = statusMap[student.studentStat]?:"未知")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        )
        {

            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(3f)
            )
            {
//                val calendar=Calendar.getInstance()
//                calendar.setTime(student.enrollment)
//                val dateString:String=calendarToString(calendar)
                MyTextBox(label = "入学时间", initialValue = student.enrollment)
            }

            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(2f)
            )
            {
                MyTextBox(label = "籍贯", initialValue = student.birthPlace)
            }
            Box(
                modifier = Modifier.fillMaxWidth(0.4f)
                    .padding(8.dp)
                    .weight(1f)
            )
            {
                val politicalMap=mapOf(
                    PoliticialStat.PartyMember to "党员",
                    PoliticialStat.Members to "群众",
                    PoliticialStat.Masses to "团员"
                )
                MyTextBox(label = "政治面貌", initialValue = politicalMap[student.politicalStat]?:"未知")
            }
        }
    }
}

@Composable
fun UserInfoScreen(student: StudentBack) {  // 改为 StudentBack
    val titleColor = Color(alpha = 255, red = 255, green = 235, blue = 0)
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                text = "查看个人用户信息",
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

        Box(
            modifier = Modifier.fillMaxHeight(0.9f)
        )
        {
            Card(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .border(1.dp,Color.Black,RoundedCornerShape(8.dp)),
                elevation = 8.dp,

                contentColor = Color.Magenta,
            ) {
                UserInfoList(student)
            }
        }

    }
}

@Composable
fun showinformation(user:User)
{
//    val student=generate()
//    UserInfoScreen(student)
    val studentsend=StudentSend("studentStatus_view",user.cardnumber,user.password)
    GetInformation(studentsend)
}

