package example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import example.handlers.ChangePasswordSend
import example.handlers.changePasswordHandler

@Composable
fun ChangePasswordScreen(user: User) {
    // 保存输入的旧密码和新密码
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    // 保存输入错误信息
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // 控制对话框显示的状态
    var showDialog by remember { mutableStateOf(false) }
    // 控制成功对话框显示的状态
    var showSuccessDialog by remember { mutableStateOf(false) }

    // 主界面布局
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "修改密码",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 旧密码输入框
        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("旧密码") },
            visualTransformation = PasswordVisualTransformation(), // 密码隐藏显示
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 新密码输入框
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("新密码") },
            visualTransformation = PasswordVisualTransformation(), // 密码隐藏显示
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 错误信息文本
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // 修改按钮
        Button(
            onClick = {
                // 触发对话框显示
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("修改密码")
        }
    }

    // 确认对话框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text("确认修改密码") },
            text = { Text("你确定要修改密码吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        if (oldPassword.isBlank() || newPassword.isBlank()) {
                            errorMessage = "旧密码和新密码不能为空"
                        } else {
                            // 执行修改密码操作
                            val changepassword = ChangePasswordSend(
                                "studentStatus_changePassword", user.cardnumber, user.password, oldPassword, newPassword
                            )
                            val CPhandler = changePasswordHandler()
                            val changepasswordback = CPhandler.handleAction(changepassword)
                            if (changepasswordback != null) {
                                if (changepasswordback.status == "success") {
                                    // 更新用户密码
                                    user.password = newPassword
                                    // 显示成功对话框
                                    showSuccessDialog = true
                                } else {
                                    errorMessage = "密码修改失败"
                                }
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }

    // 成功对话框
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
            },
            title = { Text("密码修改成功") },
            text = { Text("您的密码已成功修改。") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                    }
                ) {
                    Text("确认")
                }
            }
        )
    }
}
