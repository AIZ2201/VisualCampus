package example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import example.handlers.*

@Composable
fun DeletionForm(user: User) {
    var cardNumber by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var confirmationDialogVisible by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    if (confirmationDialogVisible) {
        AlertDialog(
            onDismissRequest = { confirmationDialogVisible = false },
            title = { Text("确认退学") },
            text = { Text("确定要为该学生办理退学吗？") },
            confirmButton = {
                TextButton(onClick = {
                    confirmationDialogVisible = false
                    isSubmitting = true

                    // Simulate the delete student action
                    val deleteStudentSend = DeleteStudentSend("studentStatus_delete", user.cardnumber, user.password, cardNumber)
                    val DShandle = DeleteInformationHandler()
                    val deleteStudentBack = DShandle.handleAction(deleteStudentSend)

                    feedbackMessage = deleteStudentBack?.message ?: "操作失败"
                    isSubmitting = false
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmationDialogVisible = false }) {
                    Text("取消")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "办理退学",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(bottom = 8.dp),
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = "为学生办理退学",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(bottom = 16.dp),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = cardNumber,
            onValueChange = { newValue -> cardNumber = newValue },
            label = { Text("一卡通号") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                confirmationDialogVisible = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = !isSubmitting
        ) {
            Text("确定")
        }

        if (feedbackMessage.isNotEmpty()) {
            Text(
                text = feedbackMessage,
                color = if (feedbackMessage == "操作失败") Color.Red else Color.Green,
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        val user=User(123,"李华","123","admin")
        DeletionForm(user)
    }
}



