import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.singleWindowApplication
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import example.User
import example.handlers.*
import java.time.LocalTime


@Composable
fun BalenceTitle() {
    Column(modifier = Modifier.padding(16.dp)) {
        androidx.compose.material.Text(
            text = "我的账户",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(0.dp),
            color = Color.Black,
            textAlign = TextAlign.Center,
        )
        androidx.compose.material.Text(
            text = "查看我的账户余额",
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

@Composable
fun PersonalAccountPage(user: User) {
    val VBhandler = ViewBalenceHandler()
    val vbsend = ViewBalenceSend("bank_view", user.cardnumber, user.password)
    val vbback = VBhandler.handleAction(vbsend)
    var username = user.name
    var balance by remember { mutableStateOf(vbback?.balance ?: 0.0) } // 个人余额
    val currentHour = LocalTime.now().hour
    val greeting = when {
        currentHour in 5..11 -> "上午好"
        currentHour in 12..17 -> "下午好"
        else -> "晚上好"
    }

    Column {
        BalenceTitle()

        Text(
            text = "个人余额: ¥$balance",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Row with two Cards for Deposit and Withdraw
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Deposit Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp
            ) {
                DepositCard(user, balance) { amount ->
                    balance += amount
                }
            }

            // Withdraw Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp
            ) {
                WithdrawCard(user, balance) { amount ->
                    balance -= amount // 提现后更新余额
                }
            }
        }

        // Enhanced greeting label with dynamic message
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = "$greeting！$username",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun DepositCard(user: User, currentBalance: Double, onConfirm: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val presetAmounts = listOf(100.0, 200.0, 500.0)

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "充值", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Display preset amount buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            presetAmounts.forEach { preset ->
                Button(onClick = { amount = preset.toString() }) {
                    Text("$preset 元", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input custom amount
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("自定义金额") },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm button
        Button(onClick = { showDialog = true }) {
            Text("确认充值", color = Color.White)
        }

        // Confirmation dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("确认充值") },
                text = { Text("您确定要充值 $amount 元吗？") },
                confirmButton = {
                    Button(onClick = {
                        val value = amount.toDoubleOrNull() ?: 0.0
                        val Rhandler = RechargeHandler()
                        val rsend = RechargeSend("bank_recharge", user.cardnumber, user.password, value)
                        val rback = Rhandler.handleAction(rsend)
                        if (rback != null && rback.status == "success") {
                            onConfirm(value)
                        }
                        showDialog = false
                    }) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun WithdrawCard(user: User, currentBalance: Double, onConfirm: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val presetAmounts = listOf(100.0, 200.0, 500.0)
    val amountAsDouble = amount.toDoubleOrNull() ?: 0.0
    val isAmountValid = amountAsDouble <= currentBalance

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "提现", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Display preset amount buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            presetAmounts.forEach { preset ->
                Button(onClick = { amount = preset.toString() }) {
                    Text("$preset 元", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input custom amount
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("自定义金额") },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = !isAmountValid
        )

        // Display insufficient balance warning
        if (!isAmountValid) {
            Text(
                text = "账户余额不足",
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm button
        Button(onClick = { showDialog = true }, enabled = isAmountValid) {
            Text("提现", color = Color.White)
        }

        // Confirmation dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("确认提现") },
                text = { Text("您确定要提现 $amount 元吗？") },
                confirmButton = {
                    Button(onClick = {
                        val value = amountAsDouble
                        val whandler = WithdrawHandler()
                        val wsend = WithdrawSend("bank_withdraw", user.cardnumber, user.password, value)
                        val wback = whandler.handleAction(wsend)
                        if (wback != null && wback.status == "success") {
                            onConfirm(value)
                        }
                        showDialog = false
                    }) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

fun main() = singleWindowApplication {
    val user= User(123,"李华","123","admin")
    PersonalAccountPage(user)
}
