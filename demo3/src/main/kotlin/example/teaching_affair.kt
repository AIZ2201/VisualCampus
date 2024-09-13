package example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.animation.animateContentSize
import androidx.compose.material.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import example.handlers.*
import example.handlers.class_date
import example.handlers.class_info
import java.io.File
import javax.swing.JFileChooser
import java.io.*
import java.net.Socket
import example.handlers.CourseSchedule
import kotlinx.coroutines.*
import example.handlers.EnterGrade
import java.util.*

data class Option(
    val title: String,
    val description: String,
    val iconRes: String,
    val screen: @Composable () -> Unit // 将要显示的页面内容作为参数传入
)

@Composable
@Preview
fun teaching_affair_App(user:User) {
    var selectedScreen by remember { mutableStateOf<@Composable () -> Unit>({ DefaultScreen() }) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧菜单
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .fillMaxHeight()
                    .background(Color.LightGray.copy(alpha = 0.9f))
//                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                if (user.role == "student") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // 取消左右侧的边距
                            .background(Color.White)
                            .size(65.dp),
                        contentAlignment = Alignment.Center // 在 Box 内部居中对齐
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            elevation = 3.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center, // 垂直和水平居中对齐
                                modifier = Modifier.fillMaxSize() // 使Box填充整个卡片
                            ) {
                                Text(
                                    text = "教务信息",
                                    fontSize = 24.sp, // 设置文本大小
                                    modifier = Modifier.padding(8.dp),
                                    fontFamily = FontFamily.Serif, // 使用Serif字体
                                    textAlign = TextAlign.Center // 水平居中对齐
                                )
                            }
                        }
                    }
                    // 创建多个选项
                    val options = listOf(
                        Option("我的课表", "查看课表", "image/kebiao.png", { TimetableScreenWithLoading(user) }),
                        Option("我的成绩", "查看成绩单", "image/chengjidan.png", { GradesScreenWithLoading(user) }),
                        Option("我的课堂", "查看教学班信息", "image/kebiao.png", { ClassroomScreen(user) }),
                        Option("评教", "查看评教结果", "image/pingjiao.png", { EvaluationScreen(user) }),
                        Option("选课", "进入选课页面", "image/a-xuanke1.png", { CourseSelectionScreen(user) }),
                    )

                    options.forEachIndexed { index, option ->
                        Box(
                            modifier = Modifier.fillMaxWidth().size(65.dp)
                        )
                        {
                            createOption(option) {
                                handleOptionClick(
                                    user,
                                    option.title,
                                    option.screen,
                                    selectedScreenSetter = { selectedScreen = it })
                            }
                        }
//                        if (index < options.size - 1) {
//                            Divider(
//                                color = Color.DarkGray,
//                                thickness = 1.dp,
//                                modifier = Modifier.padding(vertical = 0.5.dp)
//                            )
//                        }
                    }
                }

                if (user.role == "teacher") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // 取消左右侧的边距
                            .background(Color.White)
                            .size(65.dp),
                        contentAlignment = Alignment.Center // 在 Box 内部居中对齐
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            elevation = 3.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center, // 垂直和水平居中对齐
                                modifier = Modifier.fillMaxSize() // 使Box填充整个卡片
                            ) {
                                Text(
                                    text = "教学信息",
                                    fontSize = 24.sp, // 设置文本大小
                                    modifier = Modifier.padding(8.dp),
                                    fontFamily = FontFamily.Serif, // 使用Serif字体
                                    textAlign = TextAlign.Center // 水平居中对齐
                                )
                            }
                        }
                    }
                    // 创建多个选项
                    val options_1 = listOf(
                        Option("教师界面", "进入评教页面", "image/pingjiao_1.png", { EnterTeacher(user) }),
                    )

                    options_1.forEachIndexed { index, option ->
                        Box(
                            modifier = Modifier.fillMaxWidth().size(65.dp)
                        )
                        {
                            createOption(option) {
                                handleOptionClick(
                                    user,
                                    option.title,
                                    option.screen,
                                    selectedScreenSetter = { selectedScreen = it })
                            }
                        }
                    }
                }

                if (user.role == "admin") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // 取消左右侧的边距
                            .background(Color.White)
                            .size(65.dp),
                        contentAlignment = Alignment.Center // 在 Box 内部居中对齐
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            elevation = 3.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center, // 垂直和水平居中对齐
                                modifier = Modifier.fillMaxSize() // 使Box填充整个卡片
                            ) {
                                Text(
                                    text = "教务工具",
                                    fontSize = 24.sp, // 设置文本大小
                                    modifier = Modifier.padding(8.dp),
                                    fontFamily = FontFamily.Serif, // 使用Serif字体
                                    textAlign = TextAlign.Center // 水平居中对齐
                                )
                            }
                        }
                    }

                    // 创建多个选项
                    val options_1 = listOf(
                        Option(
                            "评教结果",
                            "进入评教页面",
                            "image/pingjiao_1.png",
                            { EvaluationResultsScreenWithScreen() }),
                        Option("录入成绩", "录入课程成绩", "image/diannaoluru.png", { EnterGradesScreen() }),
                        Option("教务排课", "请进行排课", "image/paike.png", { EnterCourseScheduling() })
                    )

                    options_1.forEachIndexed { index, option ->
                        Box(
                            modifier = Modifier.fillMaxWidth().size(65.dp)
                        )
                        {
                            createOption(option) {
                                handleOptionClick(
                                    user,
                                    option.title,
                                    option.screen,
                                    selectedScreenSetter = { selectedScreen = it })
                            }
                        }
                    }
                }


            }
            // 右侧内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                selectedScreen() // 动态显示当前选中的页面内容
            }
        }
    }
}

//教师查看个人信息
@Composable
@Preview
fun EnterTeacher(user:User) {
        val teacherInfo = Teacher_info()
        val operation = "teacher_course"
        val teacherName = user.name

        var courseList by remember { mutableStateOf<List<TeacherCourse>>(emptyList()) }
        var expandedCourses by remember { mutableStateOf(setOf<Int>()) }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val response = teacherInfo.send_Teacher(operation, teacherName)
                if (response != null && response.status == "success") {
                    courseList = response.teacher
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // 教师名称和欢迎信息
            Text(
                text = "$teacherName 老师",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "欢迎您",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 课程列表
            LazyColumn {
                items(courseList.size) { index ->
                    val course = courseList[index]
                    var isExpanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = 8.dp,
                        backgroundColor = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // 课程名
                            Text(
                                text = course.courseName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isExpanded = !isExpanded
                                    }
                            )

                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("授课教室: ${course.classroomName}")
                                Text("教室容量: ${course.capacity}")
                                Text("周次范围: ${course.weekRange}")
                                Text("课堂人数: ${course.selectCount}")

                                Spacer(modifier = Modifier.height(8.dp))

                                // 显示格式化后的授课时间
                                Text("授课时间:", fontWeight = FontWeight.Bold)
                                course.courseDate?.let { courseDate ->
                                    println("courseDate: $courseDate") // 打印 courseDate

                                    // 处理嵌套的嵌套列表
                                    val days = courseDate[0][0]  // 上课日期
                                    val startTimes = courseDate[0][1] // 开始时间
                                    val endTimes = courseDate[0][2] // 结束时间

                                    // 确保天数、开始时间和结束时间长度一致
                                    if (days.isNotEmpty() && startTimes.isNotEmpty() && endTimes.isNotEmpty() && startTimes.size == endTimes.size) {
                                        val formattedTime =
                                            days.zip(startTimes.zip(endTimes)) { day, (startTime, endTime) ->
                                                "$day $startTime-${endTime}节"
                                            }.joinToString(", ")

                                        Text("授课时间: $formattedTime")
                                    } else {
                                        Text("课程时间信息不完整")
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                var showStudents by remember { mutableStateOf(false) }
                                Button(
                                    onClick = { showStudents = !showStudents },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EA)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        if (showStudents) "隐藏学生列表" else "查看学生列表",
                                        color = Color.White
                                    )
                                }

                                if (showStudents) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("选课学生列表:", fontWeight = FontWeight.Bold)
                                    Column {
                                        course.students.forEach { student ->
                                            Text(text = "${student.name} (${student.cardNumber})", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }



// 通用点击处理函数
fun handleOptionClick(
    user:User,
    title: String,
    screen: @Composable () -> Unit,
    selectedScreenSetter: (ComposableFunction: @Composable () -> Unit) -> Unit
) {
    when (title) {
        "我的课表" -> {
            println("‘我的课表’选项被点击")
            if(user.role=="student")
                selectedScreenSetter(screen)
            else
                selectedScreenSetter({FailureWindow()})
        // 直接显示 TimetableScreenWithLoading
        }
        "我的成绩" -> {
            println("‘我的成绩’选项被点击")
            if(user.role=="student")
                selectedScreenSetter(screen) // 直接显示 GradesScreenWithLoading
            else
                selectedScreenSetter({FailureWindow()})
        }
        "我的课堂" -> {
            println("‘我的课堂’选项被点击")
            if(user.role=="student")
                selectedScreenSetter(screen)//直接显示课程信息
            else
                selectedScreenSetter({FailureWindow()})
        }
        "评教" -> {
            println("‘评教’选项被点击")
            if(user.role=="student")
                selectedScreenSetter(screen)
            else
                selectedScreenSetter({FailureWindow()})
        }
        "选课" -> {
            println("‘选课’选项被点击")
            if(user.role=="student")
            // 切换到选课页面并发送选课信息
            selectedScreenSetter {
                screen() // 正常显示选课页面
            }
            else
                selectedScreenSetter({FailureWindow()})
        }
        "评教结果" -> {
            println("‘评教结果’选项被点击")
            if(user.role=="admin")
            selectedScreenSetter(screen)
            else
                selectedScreenSetter({FailureWindow()})
        }
        "录入成绩" -> {
            println("‘录入成绩’选项被点击")
            if(user.role=="admin")
            selectedScreenSetter(screen)
            else
                selectedScreenSetter({FailureWindow()})
        }
        // 其他选项...
        else -> {
            println("未识别的选项")
            selectedScreenSetter(screen)
        }
    }
}


@Composable
fun createOption(option: Option, onClick: () -> Unit) {
    // 添加选项
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                onClick()
            },
            contentAlignment = Alignment.Center  // 将内容居中对齐
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // 水平对齐方式设置为居中
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(option.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically) // 图标垂直居中
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically) // 列垂直居中
                    .fillMaxWidth()
            ) {
                Text(
                    text = option.title,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

        }
    }
}

// 默认显示页面
@Composable
fun DefaultScreen() {
    Text(
        "欢迎",
        fontSize = 20.sp,

        )
}



// 我的课表页面加载数据
@Composable
fun TimetableScreenWithLoading(user:User) {
    val coroutineScope = rememberCoroutineScope()
    val classDate = remember { mutableStateListOf<class_date>() }
    var isLoading by remember { mutableStateOf(true) }
    var notFound by remember { mutableStateOf(false) } // 增加状态变量

    // 为每个课程动态生成并存储随机颜色
    val courseColors = remember { mutableStateMapOf<String, Color>() }

    // 启动协程加载数据
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val result = MyclassDate().send_dateInfo(user)  // 请求更新数据
            if (result != null) {
                if (result.status == "not_found") {
                    classDate.clear() // 清空现有数据
                    notFound = true // 标记为找不到数据
                } else {
                    val updatedData = MyclassDate_list.getDate() // 获取更新后的数据
                    classDate.clear() // 清空现有数据
                    classDate.addAll(updatedData) // 更新 classDate 列表
                    notFound = false // 重置为找到数据

                    // 为新课程生成颜色，如果该课程没有颜色
                    updatedData.forEach { course ->
                        if (!courseColors.containsKey(course.courseName)) {
                            courseColors[course.courseName] = Color(
                                red = (0..255).random() / 255f,
                                green = (0..255).random() / 255f,
                                blue = (0..255).random() / 255f
                            )
                        }
                    }
                }
            }
            isLoading = false
        }
    }

    TimetableScreen(classDate = classDate, isLoading = isLoading, notFound = notFound, courseColors = courseColors)
}


// TimetableScreen 函数，接收 classDate 和 isLoading 参数
@Composable
fun TimetableScreen(classDate: List<class_date>, isLoading: Boolean, notFound: Boolean, courseColors: Map<String, Color>) {
    var currentWeek by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "我的课表",
                    fontSize = 20.sp,
                    color = Color.Black,
                )
                Text(
                    "查看个人课表",
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        when {
            isLoading -> {
                // 显示加载中的提示
                CircularProgressIndicator(color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            notFound -> {
                // 显示没有找到数据时的提示
                Text(
                    text = "未找到课程表数据。",
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            else -> {
                // 周数选择器
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (currentWeek > 1) currentWeek-- },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFBB86FC),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "上一周")
                    }
                    Text(text = "第 $currentWeek 周", fontSize = 18.sp)
                    Button(
                        onClick = { currentWeek++ },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFBB86FC),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "下一周")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(Modifier.fillMaxWidth().height(700.dp).padding(bottom = 40.dp)) {
                    Row(Modifier.fillMaxSize()) {
                        // 左侧的节次
                        Column(Modifier.weight(1F).fillMaxHeight()) {
                            Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                            Row(Modifier.weight(1F).fillMaxWidth()) {}
                            (1..13).forEach {
                                Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                                Row(
                                    Modifier.weight(1F).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("第 $it 节", fontSize = 12.sp)
                                }
                            }
                            Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                        }

                        // 顶部的星期几和覆盖块的绘制
                        (1..7).forEach { weekday ->
                            Column(Modifier.weight(1F).fillMaxHeight()) {
                                Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                                Row(
                                    Modifier.weight(1F).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = when (weekday) {
                                            1 -> "星期一"
                                            2 -> "星期二"
                                            3 -> "星期三"
                                            4 -> "星期四"
                                            5 -> "星期五"
                                            6 -> "星期六"
                                            7 -> "星期日"
                                            else -> "?"
                                        },
                                        fontSize = 12.sp
                                    )
                                }

                                // 课表的表格和覆盖块的绘制
                                var currentLessonIndex = 1
                                while (currentLessonIndex <= 13) {
                                    Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                                    val classForThisSlot = classDate.find { it.classDate == weekday && it.courseBegin.toInt() == currentLessonIndex }

                                    if (classForThisSlot != null) {
                                        val rowSpan = classForThisSlot.courseEnd.toInt() - classForThisSlot.courseBegin.toInt() + 1
                                        val courseColor = courseColors[classForThisSlot.courseName] ?: Color.Gray  // 获取课程的随机颜色

                                        Box(
                                            modifier = Modifier
                                                .weight(rowSpan.toFloat())
                                                .fillMaxWidth()
                                                .background(courseColor)  // 使用随机颜色
                                                .border(1.dp, Color.White)
                                                .padding(4.dp)
                                        ) {
                                            Column(
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(text = classForThisSlot.courseName, fontSize = 12.sp, color = Color.White)
                                                Text(text = classForThisSlot.classroomName, fontSize = 10.sp, color = Color.White)
                                            }
                                        }

                                        currentLessonIndex += rowSpan // 跳过已经覆盖的节次
                                    } else {
                                        Box(
                                            Modifier
                                                .weight(1F)
                                                .fillMaxWidth()
                                        ) {
                                            // 空白格子
                                        }

                                        currentLessonIndex++
                                    }
                                }
                                Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}



//fun displayclassDate() {
//    val classDate = MyclassDate_list.getDate()
//    println("Fetched class date list: $classDate") // 添加这行来查看数据
//    for (class_date in classDate) {
//        println("Course Name: ${class_date.courseName}")
//        println("classroomName: ${class_date.classroomName}")
//        println("classDate: ${class_date.classDate}")
//        println("classBegin: ${class_date.courseBegin}")
//        println("classEnd: ${class_date.courseEnd}")
//        println("-----------------------")
//    }
//}


















//我的成绩页面
@Composable
fun GradesScreenWithLoading(user:User) {
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope() // 用于启动协程
    val courses = remember { mutableStateListOf<Course>() } // 用于存储课程信息

    // 启动协程加载数据
    LaunchedEffect(Unit) {
        scope.launch {
            // 先发送信息
            Mygrade().sendInfo(user)

            // 然后进入轮询等待接收数据
            while (courses.isEmpty()) {  // 轮询直到拿到数据
                val courseList = Mygrade_list.getCourses().map { handlersCourse ->
                    Course(
                        courseName = handlersCourse.courseName,
                        credit = handlersCourse.credit,
                        grade = handlersCourse.grade,
                        regular_grade = handlersCourse.regular_grade,
                        midterm_grade = handlersCourse.midterm_grade,
                        final_grade = handlersCourse.final_grade
                    )
                }
                if (courseList.isNotEmpty()) {
                    courses.addAll(courseList)
                }
                delay(300) // 每隔0.3秒检查一次
            }
            isLoading = false
        }
    }

    // 传递加载到的数据和加载状态给 GradesScreen
    GradesScreen(courses = courses, isLoading = isLoading)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GradesScreen(courses: List<Course>, isLoading: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()), // 添加滚动功能
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部标题
        Text(
            text = "我的成绩",
            fontSize = 20.sp,
            color = Color.Black
        )
        Text(
            text = "查看个人成绩单",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp)) // 添加一些间距

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .heightIn(min = 100.dp) // 设置一个最小高度以避免内容显示问题
                .border(
                    1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                )
                .padding(16.dp)
        ) {
            if (isLoading) {
                // 显示加载中的提示
                CircularProgressIndicator(color = Color.Gray)
            } else {
                Column {
                    courses.forEach { course ->
                        Text(
                            text = "${course.courseName}      学分：${course.credit}",
                            fontSize = 16.sp,
                            color = Color.Black
                        )

                        Spacer(Modifier.height(8.dp))
                        // 详细成绩信息
                        Text("平时分: ${course.regular_grade}", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(Modifier.height(4.dp))
                        Text("期中: ${course.midterm_grade}", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(Modifier.height(4.dp))
                        Text("期末: ${course.final_grade}", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(Modifier.height(4.dp))
                        Text("总成绩: ${course.grade}", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(20.dp)) // 添加间距
                    }
                }
            }
        }
    }
}


//fun displayCourses() {
//    val courses = Mygrade_list.getCourses()
//
//    for (course in courses) {
//        println("Course Name: ${course.courseName}")
//        println("Credit: ${course.credit}")
//        println("Grade: ${course.grade}")
//        println("Regular Grade: ${course.regular_grade}")
//        println("Midterm Grade: ${course.midterm_grade}")
//        println("Final Grade: ${course.final_grade}")
//        println("-----------------------")
//    }
//}

//我的课堂
@Composable
fun ClassroomScreen(user:User) {
    val coroutineScope = rememberCoroutineScope() // 用于启动协程
    val classInfo = remember { mutableStateListOf<class_info>() } // 用于存储课堂信息

    // 在 Composable 中使用 LaunchedEffect 启动协程加载数据
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            loadClassInfo(user,classInfo)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "我的课堂",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "查看教学班信息",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 使用 Composable 显示加载的数据
        if (classInfo.isEmpty()) {
            Text(text = "正在加载...", fontSize = 16.sp, color = Color.Gray)
        } else {
            classInfo.forEach { info ->
                ClassroomItem(
                    courseName = info.courseName,
                    courseDescription = info.introduction,
                    courseTimes = info.week.zip(info.time) { weekDay, time -> // 将 week 和 time 组合
                        Pair(weekDay, time)
                    },
                    courseLocation = info.classroomName,
                    teacher = info.teacherName,
                    QQ_group = info.qqGroup,
                    duration = info.duration
                )
            }
        }
    }
}

@Composable
fun ClassroomItem(
    courseName: String,
    courseDescription: String,
    courseTimes: List<Pair<String, String>>, // 使用 Pair 将 week 和 time 组合
    courseLocation: String,
    teacher: String,
    QQ_group: Int,
    duration: String
) {
    // 转换 duration 为可读的格式
    val durationText = duration.replace("week", "周") // 转换为 "1-4周"

    // 将 week 和 time 组合后的 Pair 转换为易读的格式
    val timesText = courseTimes.joinToString(separator = "，") { (weekDay, time) ->
        val dayText = when (weekDay) {
            "1" -> "周一"
            "2" -> "周二"
            "3" -> "周三"
            "4" -> "周四"
            "5" -> "周五"
            "6" -> "周六"
            "7" -> "周日"
            else -> "未知"
        }
        "$dayText $time"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = courseName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = courseDescription,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 时间和位置详情
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timesText,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 其他详细信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = courseLocation,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = teacher,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = QQ_group.toString(),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 显示转换后的 duration
        Text(
            text = "周数: $durationText",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

// 加载课堂信息的函数
suspend fun loadClassInfo(user:User,classInfo: MutableList<class_info>) {
    Myclass_infor().send_classInfo(user)
    val infoList = Myclass_list.getClassInfo()

    // 控制台输出获取的信息
    for (info in infoList) {
        println("Course Name: ${info.courseName}")
        println("Teacher Name: ${info.teacherName}")
        println("QQ Group: ${info.qqGroup}")
        println("Classroom Name: ${info.classroomName}")
        println("Duration: ${info.duration}")
        println("Week: ${info.week}")
        println("Time: ${info.time}")
        println("Introduction: ${info.introduction}")
        println("-----------------------")
    }

    classInfo.clear() // 清除旧数据
    classInfo.addAll(infoList) // 添加新数据
}


//评教页面
@Composable
fun EvaluationScreen(user: User) {
    // 声明用于存储评教数据的状态
    val myPingjiao = My_pingjiao()
    val coroutineScope = rememberCoroutineScope()
    val evaluationData = remember { mutableStateOf<receive_Status?>(null) }

    // 使用 LaunchedEffect 加载评教数据
    LaunchedEffect(user.cardnumber) {
        coroutineScope.launch {
            val pingjiaoInfo = pingjiao_info(operation = "evaluate_enter", user.cardnumber.toString())
            evaluationData.value = myPingjiao.sendpingjiao_info(pingjiaoInfo)
        }
    }

    // 显示加载中的状态或实际内容
    evaluationData.value?.let { data ->
        // 一旦评教数据加载完成，展示评教界面
        EvaluationContent(user = user, evaluationData = data)
    } ?: run {
        // 加载数据时显示的状态
        Text("加载中...", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun EvaluationContent(user: User, evaluationData: receive_Status) {
    // 从接收到的数据中提取课程和教师信息
    val coursesWithTeachers = evaluationData.need_evaluated.map { it.courseName to it.teacherName }

    // 存储每个课程的展开状态
    val expandedStates = remember { mutableStateMapOf<String, Boolean>().apply { coursesWithTeachers.forEach { put(it.first, false) } } }
    // 存储每个课程的评分，评分数动态根据每个问题的数量初始化
    val scores = remember { mutableStateMapOf<String, List<MutableState<Float>>>().apply {
        coursesWithTeachers.forEach { put(it.first, List(11) { mutableStateOf(5f) }) }  // 根据问题数量动态生成评分列表
    } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "评教系统",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "请对课程进行评教",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 课程列表
        coursesWithTeachers.forEach { (course, teacher) ->
            item {
                CourseEvaluation(
                    user = user,
                    courseName = course,
                    teacherName = teacher,
                    scores = scores[course] ?: emptyList(),
                    isExpanded = expandedStates[course] ?: false,
                    onExpandChange = { expandedStates[course] = it }
                )
            }
        }
    }
}

@Composable
fun CourseEvaluation(
    user: User,
    courseName: String,
    teacherName: String,
    scores: List<MutableState<Float>>,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
) {
    // 状态变量，用于控制提示框的显示和状态信息
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        // 课程名
        Text(
            text = courseName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandChange(!isExpanded) }
                .padding(vertical = 8.dp)
        )

        // 显示评分内容
        AnimatedVisibility(visible = isExpanded) {
            Column {
                // 显示教师名字
                Text(
                    text = "教师: $teacherName",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 动态生成评价问题，根据评分列表的长度
                val questions = listOf(
                    "1. 我对这门课程和老师的总体评价",
                    "2. 老师课堂无不良言论，价值导向积极，体现了“立德树人”的师者风范。",
                    "3. 老师能够让我了解课程的价值，激发我的学习热情。",
                    "4. 老师能让我明白每次课程的具体学习目标和要求。",
                    "5. 老师教学能够理论联系实际，教学内容具有适当的挑战性。",
                    "6. 我认为老师讲授思路清楚，重点突出，层次分明。",
                    "7. 我认为老师备课充分，为我们提供了丰富的学习资料。",
                    "8. 老师能根据大多数同学的学习情况，合理调整教学安排和进度。",
                    "9. 老师对我们的问题（包括作业和考核）能够给予及时、有帮助的反馈。",
                    "10. 我认为课程的考核评价方式能够反映我的学习成效。",
                    "11. 我认为我能够达到本课程的教学目标，在学习中有所收获。"
                )

                questions.forEachIndexed { index, question ->
                    EvaluationQuestion(
                        question = question,
                        score = scores[index].value,
                        onScoreChange = { scores[index].value = it }
                    )
                }

                // 提交按钮
                Button(
                    onClick = {
                        // 收集评分数据并生成 Request_pingjiao 对象
                        val scoreList = scores.map { it.value.toInt() }
                        val request = Request_pingjiao(
                            operation = "student_evaluate",
                            cardNumber = user.cardnumber.toString(),  // 使用传入的 cardNumber
                            courseName = courseName,
                            teacherName = teacherName,
                            scores = scoreList,
                        )
                        // 使用协程调用 My_pingjiao 类的 sendInfo 方法
                        val myPingjiao = My_pingjiao()
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = myPingjiao.sendInfo(request)
                            // 更新状态信息并显示对话框
                            dialogMessage = if (response.status == "success") {
                                "当前课程评教成功！"
                            } else {
                                "已经评教该课程"
                            }
                            showDialog = true
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "提交", fontSize = 16.sp)
                }
            }
        }
    }

    // 显示提示框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = if (dialogMessage.contains("成功")) "成功" else "失败")
            },
            text = {
                Text(text = dialogMessage)
            },
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



@Composable
fun EvaluationQuestion(
    question: String,
    score: Float,
    onScoreChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = question,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "0", fontSize = 12.sp, color = Color.Gray)
            Slider(
                value = score,
                onValueChange = onScoreChange,
                valueRange = 0f..10f,
                steps = 9, // 0到10之间分9步
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
            Text(text = "10", fontSize = 12.sp, color = Color.Gray)
            Text(
                text = score.toInt().toString(),
                fontSize = 16.sp,
                color = Color.Blue,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


// 示例页面：选课
@Composable
fun CourseSelectionScreen(user:User) {
    var selectionCourses by remember { mutableStateOf(emptyList<SelectionCourse>()) }
    var showConflictDialog by remember { mutableStateOf(false) }
    var conflictMessage by remember { mutableStateOf("") }

    val cardNumber = user.cardnumber
    val operation = "select_send"
    val coroutineScope = rememberCoroutineScope()

    // 创建 My_Select 实例
    val mySelect = My_Select()

    // 在页面加载时调用处理函数获取服务器数据
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val response = mySelect.send_select_page(operation) // 使用实例调用方法
            if (response?.status == "success") {
                selectionCourses = response.courses.map { course ->
                    SelectionCourse(
                        name = course.courseName,
                        code = course.courseID,
                        credit = course.credit.toString(),
                        teachers = course.teachers.map { teacher ->
                            SelectionTeacher(
                                name = teacher.teacherName,
                                weekRange = teacher.weekRange,
                                classDates = teacher.dayOfWeek.zip(teacher.courseBegin.zip(teacher.courseEnd)) { day, (begin, end) ->
                                    ClassTime(
                                        dayOfWeek = day,
                                        courseBegin = begin,
                                        courseEnd = end
                                    )
                                },
                                classroomName = teacher.classroomName,
                                capacity = teacher.capacity,
                                selectedCount = teacher.selectCount
                            )
                        }
                    )
                }
            } else {
                println("Failed to receive data or status not success.")
            }
        }
    }

    // 渲染 UI 代码保持不变
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(selectionCourses) { course ->
            ExpandableCourseCard(
                course = course,
                allCourses = selectionCourses,
                onShowConflictDialog = { showDialog, message ->
                    showConflictDialog = showDialog
                    conflictMessage = message
                },
                onCoursesUpdated = { updatedCourses ->
                    selectionCourses = updatedCourses // 更新课程状态
                },
                cardNumber = cardNumber.toString(),
                operation = "select_enter"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showConflictDialog) {
        AlertDialog(
            onDismissRequest = { showConflictDialog = false },
            title = { Text("课程冲突") },
            text = { Text(conflictMessage) },
            confirmButton = {
                Button(onClick = { showConflictDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
fun ExpandableCourseCard(
    course: SelectionCourse,
    allCourses: List<SelectionCourse>,
    onShowConflictDialog: (Boolean, String) -> Unit,
    onCoursesUpdated: (List<SelectionCourse>) -> Unit,
    cardNumber: String,
    operation: String
) {
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val mySelect = My_Select()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded

                // 点击课程栏时发送请求
                scope.launch {
                    val response = mySelect.send_select_enter(cardNumber, operation, course.name)
                    if (response != null) {
                        println("Received response: ${response.status}")
                        if (response.status == "success" || response.status == "not_found") {
                            // 更新课程状态
                            val updatedCourses = allCourses.map { currentCourse ->
                                if (currentCourse.name == course.name) {
                                    currentCourse.copy(
                                        teachers = currentCourse.teachers.map { teacher ->
                                            val matchedTeacher = response.selected.find { it.teacherName == teacher.name }
                                            if (matchedTeacher != null) {
                                                teacher.copy(
                                                    selectedCount = matchedTeacher.selectCount,
                                                    selected = matchedTeacher.isSelected ?: false // 如果缺少 isSelected 字段，默认为 false
                                                )
                                            } else {
                                                teacher
                                            }
                                        }
                                    )
                                } else {
                                    currentCourse
                                }
                            }
                            onCoursesUpdated(updatedCourses)
                        }
                    } else {
                        println("Failed to receive a response.")
                    }
                }
            }
            .padding(vertical = 8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${course.name}  ${course.code}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                if (expanded && course.teachers.any { it.selected }) {
                    Text("已选", color = Color.Green, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    "学分: ${course.credit}",
                    fontSize = 12.sp,
                    color = Color.Blue,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Blue))
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "限选",
                    fontSize = 12.sp,
                    color = Color.Green,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Green))
                        .padding(4.dp)
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                course.teachers.forEach { teacher ->
                    TeacherCard(
                        teacher = teacher,
                        allCourses = allCourses,
                        currentCourse = course,
                        onShowConflictDialog = onShowConflictDialog,
                        onCoursesUpdated = onCoursesUpdated,
                        cardNumber = cardNumber,
                        operation = operation
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
fun TeacherCard(
    teacher: SelectionTeacher,
    allCourses: List<SelectionCourse>,
    currentCourse: SelectionCourse,
    onShowConflictDialog: (Boolean, String) -> Unit,
    onCoursesUpdated: (List<SelectionCourse>) -> Unit,
    cardNumber: String,
    operation: String
) {
    val scope = rememberCoroutineScope()

    // 判断当前课程是否已有老师被选中
    val courseHasSelectedTeacher = currentCourse.teachers.any { it.selected }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        border = BorderStroke(1.dp, if (teacher.selected) Color.Green else Color.Gray)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(teacher.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("周次: ${teacher.weekRange}", fontSize = 12.sp)
            teacher.classDates.forEach { classTime ->
                Text("时间: ${classTime.dayOfWeek} ${classTime.courseBegin}-${classTime.courseEnd}节", fontSize = 12.sp)
            }
            Text("地点: ${teacher.classroomName}", fontSize = 12.sp)
            Text("教学班容量: ${teacher.capacity}人  已选人数: ${teacher.selectedCount}人", fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            // 检查冲突
                            val conflictingTeachers = allCourses.flatMap { course ->
                                course.teachers.filter { t ->
                                    t.selected && t != teacher && isConflict(t, teacher)
                                }
                            }

                            if (conflictingTeachers.isNotEmpty() && !teacher.selected) {
                                val conflictNames = conflictingTeachers.joinToString { it.name }
                                onShowConflictDialog(true, "课程冲突：与 $conflictNames 时间冲突")
                            } else {
                                // 更新选择状态
                                val updatedCourses = allCourses.map { course ->
                                    if (course == currentCourse) {
                                        // 只更新当前点击的教师状态，其他教师状态保持不变
                                        course.copy(teachers = course.teachers.map { t ->
                                            if (t == teacher) {
                                                // 更新选择状态并切换 selected
                                                val newSelected = !t.selected
                                                t.copy(
                                                    selected = newSelected,
                                                    selectedCount = if (newSelected) t.selectedCount + 1 else t.selectedCount - 1,
                                                    conflict = false
                                                )
                                            } else {
                                                // 其他教师状态保持不变
                                                t
                                            }
                                        })
                                    } else course
                                }

                                onCoursesUpdated(updateConflictStates(updatedCourses))

                                // 判断操作类型并发送选中或退选信息
                                val operationType = if (teacher.selected) "student_deselect" else "student_select"

                                // 发送操作信息
                                val mySelect = My_Select()
                                if (operationType == "student_deselect") {
                                    mySelect.sendDeselectInfo(cardNumber, currentCourse.name) // 发送退选信息，仅包含课程名
                                } else {
                                    mySelect.sendInfo(updatedCourses, cardNumber, operationType) // 传递正确的操作和课程信息
                                }
                            }
                        }
                    },
                    enabled = teacher.selected || !courseHasSelectedTeacher,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (teacher.selected) Color.Gray else MaterialTheme.colors.primary
                    )
                ) {
                    Text(if (teacher.selected) "退选" else "选择")
                }
                if (teacher.conflict) {
                    Text("课程冲突", color = Color.Red, fontSize = 12.sp)
                } else if (teacher.selected) {
                    Text("已选", color = Color.Green, fontSize = 12.sp)
                }
            }
        }
    }
}

fun isConflict(teacher1: SelectionTeacher, teacher2: SelectionTeacher): Boolean {
    // 检查两个教师的所有上课时间段是否有冲突
    for (time1 in teacher1.classDates) {
        for (time2 in teacher2.classDates) {
            val range1 = extractWeekRange(teacher1.weekRange)
            val range2 = extractWeekRange(teacher2.weekRange)
            val weeksOverlap = range1.intersect(range2).isNotEmpty()
            val timeOverlap = time1.courseBegin <= time2.courseEnd && time1.courseEnd >= time2.courseBegin
            if (weeksOverlap && timeOverlap && time1.dayOfWeek == time2.dayOfWeek) {
                return true
            }
        }
    }
    return false
}

fun extractWeekRange(weekString: String): IntRange {
    val numbers = weekString.filter { it.isDigit() || it == '-' }
        .split('-')
        .mapNotNull { it.toIntOrNull() }
    return if (numbers.size == 2) {
        numbers[0]..numbers[1]
    } else if (numbers.size == 1) {
        numbers[0]..numbers[0]
    } else {
        0..0
    }
}

fun updateConflictStates(courses: List<SelectionCourse>): List<SelectionCourse> {
    return courses.map { course ->
        course.copy(teachers = course.teachers.map { teacher ->
            val isConflicting = courses.any { otherCourse ->
                otherCourse.teachers.any { otherTeacher ->
                    otherTeacher.selected && isConflict(teacher, otherTeacher) && otherTeacher != teacher
                }
            }
            teacher.copy(conflict = isConflicting)
        })
    }
}

suspend fun initialCourses(operation: String): List<SelectionCourse> {
    val mySelect = My_Select()
    val response = mySelect.send_select_page(operation)

    return if (response != null && response.status == "success") {
        response.courses.map { course ->
            SelectionCourse(
                name = course.courseName,
                code = course.courseID,
                credit = course.credit.toString(),
                teachers = course.teachers.map { teacher ->
                    SelectionTeacher(
                        name = teacher.teacherName,
                        weekRange = teacher.weekRange,
                        classDates = teacher.dayOfWeek.zip(teacher.courseBegin.zip(teacher.courseEnd)) { day, (begin, end) ->
                            ClassTime(
                                dayOfWeek = day,
                                courseBegin = begin,
                                courseEnd = end
                            )
                        },
                        classroomName = teacher.classroomName,
                        capacity = teacher.capacity,
                        selectedCount = teacher.selectCount
                    )
                }
            )
        }
    } else {
        emptyList()
    }
}


// 示例页面：评教结果
@Composable
fun EvaluationResultsScreenWithScreen() {
    // 状态变量用于存储评教结果
    var evaluationResults by remember { mutableStateOf<List<Result>?>(null) }
    var statusMessage by remember { mutableStateOf("") }

    // 使用 LaunchedEffect 来发送评教请求并获取结果
    LaunchedEffect(Unit) {
        val pingjiao = Pingjiao()
        val result = pingjiao.send_Pingjiao()
        if (result != null) {
            // 检查状态并解析 JSON 响应
            if (result.status == "success") {
                evaluationResults = result.results
                statusMessage = ""
            } else if (result.status == "not_found") {
                statusMessage = "未找到指定课程的评教结果。"
                evaluationResults = null // 清空评教结果
            } else {
                statusMessage = "获取评教结果失败"
                evaluationResults = null
            }
        } else {
            statusMessage = "获取评教结果失败"
            evaluationResults = null
        }
    }

    // 记录每门课程是否展开
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    // 问题列表，与评教中的问题对应
    val questions = listOf(
        "1. 我对这门课程和老师的总体评价",
        "2. 老师课堂无不良言论，价值导向积极，体现了“立德树人”的师者风范。",
        "3. 老师能够让我了解课程的价值，激发我的学习热情。",
        "4. 老师能让我明白每次课程的具体学习目标和要求。",
        "5. 老师教学能够理论联系实际，教学内容具有适当的挑战性。",
        "6. 我认为老师讲授思路清楚，重点突出，层次分明。",
        "7. 我认为老师备课充分，为我们提供了丰富的学习资料。",
        "8. 老师能根据大多数同学的学习情况，合理调整教学安排和进度。",
        "9. 老师对我们的问题（包括作业和考核）能够给予及时、有帮助的反馈。",
        "10. 我认为课程的考核评价方式能够反映我的学习成效。",
        "11. 我认为我能够达到本课程的教学目标，在学习中有所收获。"
    )

    // 评教结果页面布局
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "评教结果",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "查看评教结果",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 动态创建课程条目
        evaluationResults?.forEach { result ->
            item {
                Column {
                    // 显示课程名
                    Text(
                        text = result.courseName,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                // 点击切换展开状态
                                expandedStates[result.courseName] = !(expandedStates[result.courseName] ?: false)
                            }
                    )
                    // 显示教师名
                    Text(
                        text = "教师: ${result.teacherName}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    // 展示每个问题的平均分
                    AnimatedVisibility(visible = expandedStates[result.courseName] == true) {
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            result.averageScores.forEachIndexed { index, score ->
                                EvaluationQuestionResult(
                                    question = questions.getOrNull(index) ?: "未知问题",
                                    averageScore = score.toDouble()
                                )
                            }
                        }
                    }
                }
            }
        }

        // 显示状态信息
        item {
            if (statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    fontSize = 14.sp,
                    color = if (statusMessage.contains("成功") || statusMessage.contains("未找到")) Color.Gray else Color.Red
                )
            }
        }
    }
}

@Composable
fun EvaluationQuestionResult(
    question: String,
    averageScore: Double
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = question,
            fontSize = 14.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "平均得分: ${String.format("%.2f", averageScore)}",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}


// 示例页面：录入成绩

@Composable
@Preview
fun EnterGradesScreen() {
    // 存储从服务器获取的课程名
    var courseNames by remember { mutableStateOf<List<String>>(emptyList()) }

    // 使用 LaunchedEffect 来获取评教结果
    LaunchedEffect(Unit) {
        val myentergrade = EnterGrade()
        val response = myentergrade.send_Entergrade()
        if (response?.status == "success") {
            // 提取课程名并更新状态
            courseNames = response.selected.map { it.courseName }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("录入成绩", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // 动态展示课程列表
        courseNames.forEach { courseName ->
            CourseGradesComponent(courseName)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun CourseGradesComponent(courseName: String) {
    var importStatus by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray) // 添加边框
            .padding(16.dp) // 内边距使得内容不紧贴边框
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(courseName, fontSize = 16.sp)
            Button(
                onClick = {
                    val result = importExcel()
                    if (result != null) {
                        importStatus = "导入成功"
                        sendFileToServer(result, "10.208.72.178", 4444, "import_grades") // 传输文件到指定的服务器和端口，并添加操作
                    } else {
                        importStatus = "导入失败"
                    }
                    showDialog = true // 导入后显示提示框
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("导入 Excel")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 显示导入状态的提示框
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "导入结果") },
                text = { Text(text = importStatus ?: "未知错误") },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

// 定义一个数据类用于存储操作信息
data class OperationData(
    val operation: String,
    val fileName: String,
    val fileSize: Long,
    val fileContent: String  // Base64 编码的文件内容
)

fun importExcel(): File? {
    val fileChooser = JFileChooser()
    fileChooser.dialogTitle = "选择 Excel 文件"
    val result = fileChooser.showOpenDialog(null)

    if (result == JFileChooser.APPROVE_OPTION) {
        return fileChooser.selectedFile
    }
    return null
}

fun sendFileToServer(file: File, serverAddress: String, serverPort: Int, operation: String) {
    try {
        // 创建一个 Socket 连接到服务器
        val socket = Socket(serverAddress, serverPort)
        println("连接到服务器: $serverAddress:$serverPort")

        // 读取文件并将其转换为 Base64 编码的字符串
        val fileInputStream = FileInputStream(file)
        val fileBytes = fileInputStream.readBytes()  // 读取文件的字节内容
        val fileContentBase64 = Base64.getEncoder().encodeToString(fileBytes)  // 将字节内容转为 Base64 编码

        // 创建 Moshi 实例
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(OperationData::class.java)

        // 准备操作数据，将文件内容作为 Base64 编码的字符串
        val fileName = file.name
        val fileSize = file.length()
        val operationData = OperationData(operation, fileName, fileSize, fileContentBase64)

        // 将操作数据序列化为 JSON
        val operationJson = jsonAdapter.toJson(operationData)
        println("Operation JSON: $operationJson")

        // 创建输出流，用于发送数据到服务器
        val outputStream = socket.getOutputStream()
        val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))

        // 发送 JSON 数据
        bufferedWriter.write(operationJson)
        bufferedWriter.flush()

        // 关闭流和 Socket
        fileInputStream.close()
        bufferedWriter.close()
        socket.close()

        println("文件传输成功: $fileName")
    } catch (e: IOException) {
        e.printStackTrace()
        println("文件传输失败")
    }
}


@Composable
@Preview
fun EnterCourseScheduling() {
    var courseName by remember { mutableStateOf("") }
    var courseId by remember { mutableStateOf("") }
    var credit by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var classroom by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var weekRange by remember { mutableStateOf("") }

    // 用于存储多个 Class_Time 对象的列表
    val classDates = remember { mutableStateListOf<Class_Time>() }
    val dayOfWeekList = remember { mutableStateListOf<String>() }
    val courseBeginList = remember { mutableStateListOf<String>() }
    val courseEndList = remember { mutableStateListOf<String>() }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .background(Color.White)
    ) {
        Text(
            "课程排课",
            style = MaterialTheme.typography.h5.copy(color = Color.Black, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = courseName,
            onValueChange = { courseName = it },
            label = { Text("课程名", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        TextField(
            value = courseId,
            onValueChange = { courseId = it },
            label = { Text("课程ID", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        TextField(
            value = credit,
            onValueChange = { credit = it },
            label = { Text("学分", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        TextField(
            value = teacherName,
            onValueChange = { teacherName = it },
            label = { Text("教师姓名", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        TextField(
            value = classroom,
            onValueChange = { classroom = it },
            label = { Text("教室位置", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        TextField(
            value = capacity,
            onValueChange = { capacity = it },
            label = { Text("容量", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        TextField(
            value = weekRange,
            onValueChange = { weekRange = it },
            label = { Text("周范围", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "上课时间段",
            style = MaterialTheme.typography.h6.copy(color = Color.Black, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 动态生成多个上课时间段输入框
        classDates.forEachIndexed { index, _ ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = dayOfWeekList[index],
                    onValueChange = { dayOfWeekList[index] = it },
                    label = { Text("星期几", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                TextField(
                    value = courseBeginList[index],
                    onValueChange = { courseBeginList[index] = it },
                    label = { Text("开始节次", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                TextField(
                    value = courseEndList[index],
                    onValueChange = { courseEndList[index] = it },
                    label = { Text("结束节次", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 添加删除按钮
            Button(
                onClick = {
                    if (classDates.isNotEmpty()) {
                        classDates.removeAt(index)
                        dayOfWeekList.removeAt(index)
                        courseBeginList.removeAt(index)
                        courseEndList.removeAt(index)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
            ) {
                Text("删除该时间段", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                classDates.add(Class_Time("", 0, 0))
                dayOfWeekList.add("")
                courseBeginList.add("")
                courseEndList.add("")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
        ) {
            Text("添加时间段", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val timeList = classDates.mapIndexed { index, _ ->
                    Class_Time(
                        dayOfWeek = dayOfWeekList[index],
                        courseBegin = courseBeginList[index].toIntOrNull() ?: 0,
                        courseEnd = courseEndList[index].toIntOrNull() ?: 0
                    )
                }
                val operation = "add_course"

                val myCourseSchedule = CourseSchedule(
                    operation = operation,
                    courseName = courseName,
                    courseId = courseId,
                    credit = credit.toDoubleOrNull() ?: 0.0,
                    teacherName = teacherName,
                    classroom = classroom,
                    capacity = capacity.toIntOrNull() ?: 0,
                    weekRange = weekRange,
                    classDates = timeList
                )

                coroutineScope.launch {
                    val course_schedule = Schedule()
                    val response = course_schedule.send_schedule_Info(myCourseSchedule)
                    if (response != null) {
                        when (response.status) {
                            "success" -> {
                                dialogMessage = "导入课程成功"
                            }
                            "room_conflict" -> {
                                dialogMessage = "教室冲突"
                            }
                            else -> {
                                dialogMessage = "未知错误"
                            }
                        }
                        showDialog = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("提交", color = Color.White)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "提示", fontWeight = FontWeight.Bold) },
                text = { Text(text = dialogMessage) },
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


//fun main() = application {
//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "教务管理页面",
//        state = rememberWindowState(width = 900.dp, height = 700.dp)
//    ) {
//        teaching_affair_App(user)
//    }
//}