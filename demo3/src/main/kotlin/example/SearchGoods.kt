package example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import example.handlers.*
import kotlinx.coroutines.launch
import example.handlers.CartItem

@Composable
fun ShoppingCart(cartItems: List<CartItem>, onQuantityChange: (CartItem, Int) -> Unit, onRemoveItem: (CartItem) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "购物车",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        Divider()

        cartItems.forEach { cartItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    load = { loadImageBitmap(cartItem.goods.pictureLink) },
                    painterFor = { BitmapPainter(it) },
                    contentDescription = "商品图片",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = cartItem.goods.name, fontWeight = FontWeight.Bold)
                    Text(text = "ID: ${cartItem.goods.productID}")
                }

                // 商品数量增减按钮
                Row {
                    IconButton(onClick = {
                        if (cartItem.quantity > 1) {
                            onQuantityChange(cartItem, cartItem.quantity - 1)
                        } else {
                            onRemoveItem(cartItem) // 数量为1时，移除商品
                        }
                    }) {
                        Icon(
                            painter = painterResource("image/ShopIcon/sub.svg"),
                            contentDescription = null,
                            modifier = Modifier
                                .size(10.dp), // 设置图标大小
                        )
                    }
                    Text(text = cartItem.quantity.toString(), modifier = Modifier.align(Alignment.CenterVertically))
                    IconButton(onClick = { onQuantityChange(cartItem, cartItem.quantity + 1) }) {
                        Icon(
                            painter = painterResource("image/ShopIcon/add.svg"),
                            contentDescription = null,
                            modifier = Modifier
                                .size(10.dp), // 设置图标大小
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun GoodsDisplay(
    goods: Goods,
    onAddToCart:(Goods)->Unit
) {
    var currentStock by remember { mutableStateOf(goods.stock) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        )
        {
            AsyncImage(
                load = { loadImageBitmap(goods.pictureLink) },
                painterFor = { BitmapPainter(it) },
                contentDescription = "Sample",
                modifier = Modifier.width(200.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 商品名称
        Text(
            text = goods.name,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 商品价格
        Text(
            text = "¥${goods.price}",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 商品描述
        Text(
            text = goods.description,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 商品库存和销量
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "库存：$currentStock",
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "销量：${goods.sales}",
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 添加至购物车按钮
        Button(
            onClick = {
                if (currentStock > 0) {
                    onAddToCart(goods)
                }
            },
            enabled = currentStock > 0,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("添加至购物车")
        }
    }
}

@Composable
fun SearchGoodsBox(tempUser:tempUser, selectedCategory: String,
                   onGoodsListChange: (List<Goods>?) -> Unit,
                   onSearchTextChange: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.15f)
            .padding(0.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = {
                Text("搜索商品")
            },
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f)
                .padding(10.dp)
                .align(Alignment.CenterVertically)
        )

        Button(
            onClick = {
                println("我想要搜索$searchText")
                val searchGoodsSend = SearchGoodsSend("store_search", tempUser.CardID, tempUser.password, searchText,selectedCategory)
                val handler = ShowGoodsHandler()
                val goodsList = handler.handleAction(searchGoodsSend)
                onGoodsListChange(goodsList?.product)
                onSearchTextChange(searchText)
            },
            modifier = Modifier
                .size(72.dp)
                .weight(1f)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .padding(10.dp)
        ) {
            Text("搜索")
        }
    }
}

@Composable//分类栏
fun CatogoryDropdownMenu(
    tempUser:tempUser,
    searchText: String,
    selectedCategory:String,
    onGoodsListChange: (List<Goods>?) -> Unit,
    onCategorySelected:(String)->Unit,
)
{
    val categories= listOf("全部","居家","数码","文娱","护肤","穿搭","美食","运动")
    Box(
        modifier = Modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    )
    {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Box(modifier = Modifier.padding(end = 8.dp))
            {
                Text(
                    text = "分类",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,

                )
            }
            categories.forEach { category ->
                Box(modifier = Modifier.padding(end = 8.dp))
                {
                    Text(
                        text = category,
                        color = if (category == selectedCategory) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                            .clickable {
                                onCategorySelected(category)
                                val searchGoodsSend = SearchGoodsSend(
                                    "store_search",
                                    tempUser.CardID,
                                    tempUser.password,
                                    searchText,
                                    selectedCategory
                                )
                                val handler = ShowGoodsHandler()
                                val goodsList = handler.handleAction(searchGoodsSend)
                                onGoodsListChange(goodsList?.product)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun GoodsList(
    goodsListState: List<Goods>?,
    onGoodsListChange: (List<Goods>) -> Unit,
    onAddToCart: (Goods) -> Unit // 添加商品至购物车的回调
) {
    // 处理缺失情况
    val goods = goodsListState ?: emptyList()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // 将商品分为每行3个进行展示
        items(goods.chunked(3)) { rowItems ->
            Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f).padding(16.dp)) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        GoodsDisplay(
                            goods = item,
                            onAddToCart = { selectedGoods ->
                                // 更新商品列表
                                val updatedList = goods.toMutableList()
                                val index = updatedList.indexOf(item)
                                if (index != -1) {
                                    updatedList[index] = selectedGoods
                                    onGoodsListChange(updatedList)
                                }
                                // 将商品添加至购物车
                                onAddToCart(selectedGoods)
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun BuyGoods(
    tempUser: tempUser,
    buygoods: List<CartItem>?,
    searchText: String,
    selectedCategory: String,
    onChangeBuyGood: (List<CartItem>) -> Unit,
    onGoodsListChange: (List<Goods>?) -> Unit
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showBuyConfirmDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                Button(
                    onClick = {
                        showClearConfirmDialog = true
                    },
                ) {
                    Text(text = "清空")
                }
            }
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                Button(
                    onClick = {
                        showBuyConfirmDialog = true
                    }
                ) {
                    Text(text = "购买")
                }
            }
        }
    }

    // Clear Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("确认清空") },
            text = { Text("你确定要清空购物车吗?") },
            confirmButton = {
                Button(onClick = {
                    showClearConfirmDialog = false
                    onChangeBuyGood(emptyList())
                    resultMessage = "购物车已清空"
                    showResultDialog = true
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showClearConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // Purchase Confirmation Dialog
    if (showBuyConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBuyConfirmDialog = false },
            title = { Text("确认购买") },
            text = { Text("你确定要购买这些商品吗?") },
            confirmButton = {
                Button(onClick = {
                    showBuyConfirmDialog = false
                    val goods = buygoods ?: emptyList()
                    val BGhandler = BuyGoodsHandler()
                    val buyGoodssend = BuyGoodsSend("store_buygoods", tempUser.CardID, tempUser.password, goods, getTime())
                    val buygoodsback = BGhandler.handleAction(buyGoodssend)
                    if (buygoodsback != null) {
                        if (buygoodsback.status == "success") {
                            onChangeBuyGood(emptyList())
                            resultMessage = "购买成功"
                            val searchGoodsSend = SearchGoodsSend("store_search", tempUser.CardID, tempUser.password, searchText, selectedCategory)
                            val handler = ShowGoodsHandler()
                            val goodsList = handler.handleAction(searchGoodsSend)
                            onGoodsListChange(goodsList?.product)
                        } else {
                            resultMessage = "购买失败: ${buygoodsback.message}"
                        }
                        showResultDialog = true
                    }
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showBuyConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // Result Dialog
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("操作结果") },
            text = { Text(resultMessage) },
            confirmButton = {
                Button(onClick = { showResultDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GoodsScreen(tempUser: tempUser) {
    var goodsList by remember { mutableStateOf<List<Goods>?>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("全部") }
    var searchText by remember { mutableStateOf("") }
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }

    // ScaffoldState 用于控制 BackdropScaffold 的状态
    val scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed)
    val coroutineScope = rememberCoroutineScope()

    // 使用 LaunchedEffect 确保第一次进入屏幕时执行一次初始化逻辑
    LaunchedEffect(Unit) {
        val guessSend = GuessSend(
            "store_guess",
            tempUser.CardID,
            tempUser.password,
            "",
            "全部"
        )
        val handler = GuessHandler()
        val firstgoodsList = handler.handleAction(guessSend)?.recommendedProducts

        println("执行初始化推荐")
        goodsList = firstgoodsList
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // BackdropScaffold 实现
        BackdropScaffold(
            scaffoldState = scaffoldState,
            appBar = {
                TopAppBar(
                    title = { Text("东大商店") },
                    backgroundColor = MaterialTheme.colors.background
                )
            },
            backLayerContent = {
                // 这里放置背景内容，比如搜索框、分类菜单等
                Column {
                    SearchGoodsBox(tempUser, selectedCategory, onGoodsListChange = {
                        if (it != null) {
                            goodsList = it
                        }
                    }, onSearchTextChange = { searchText = it })

                    CatogoryDropdownMenu(tempUser, searchText, selectedCategory, onGoodsListChange = {
                        if (it != null) {
                            goodsList = it
                        }
                    }, onCategorySelected = { selectedCategory = it })
                }

                // 放置商品列表
                GoodsList(
                    goodsListState = goodsList,
                    onGoodsListChange = { goodsList = it },
                    onAddToCart = { selectedGoods ->
                        // 查找购物车中是否已有该商品
                        val existingItem = cartItems.find { it.goods.productID == selectedGoods.productID }

                        if (existingItem != null) {
                            cartItems = cartItems.map {
                                if (it.goods.productID == selectedGoods.productID) it.copy(quantity = it.quantity + 1)
                                else it
                            }
                        } else {
                            cartItems = cartItems + CartItem(selectedGoods, 1)
                        }
                    }
                )
            },
            frontLayerContent = {
                Column {
                    Box {
                        ShoppingCart(
                            cartItems = cartItems,
                            onQuantityChange = { cartItem, newQuantity -> // 更新商品数量
                                cartItems = cartItems.map {
                                    if (it.goods.productID == cartItem.goods.productID) it.copy(quantity = newQuantity)
                                    else it
                                }
                            },
                            onRemoveItem = { cartItem -> // 移除商品
                                cartItems = cartItems.filter { it.goods.productID != cartItem.goods.productID }
                            }
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        BuyGoods(
                            tempUser,
                            cartItems,
                            searchText,
                            selectedCategory,
                            onChangeBuyGood = { cartItems = it },
                            onGoodsListChange = {
                                if (it != null) {
                                    goodsList = it
                                }
                            })
                    }
                }
            },
            stickyFrontLayer = false, // 设置前层不会遮挡下层，直到弹出
            frontLayerScrimColor = Color.Transparent, // 不会遮住底部的内容
            backLayerBackgroundColor = Color.Transparent, // 背景层透明
            frontLayerElevation = 0.dp // 减少前层的阴影
        )

        // FloatingActionButton 放置在 BackdropScaffold 外部，用于切换 BackdropScaffold 的状态
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    if (scaffoldState.isConcealed) {
                        scaffoldState.reveal() // 展开背景层
                    } else {
                        scaffoldState.conceal() // 折叠背景层
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // 按钮位于右下角
                .padding(16.dp),
            backgroundColor = MaterialTheme.colors.primary
        ) {
           // Icon(Icons.Default.Menu, contentDescription = "Toggle Backdrop")
            Icon(
                painter = painterResource("image/ShopIcon/gouwucheman.svg"),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(15.dp) ,// 设置图标大小
            )
        }
    }
}

@Composable
fun Store(user:User)
{
    val tempUser = tempUser(user.name, user.cardnumber, user.password)
    GoodsScreen(tempUser)
}


// 示例商品数据
val exampleGoodsList = listOf(
    Goods(
        productID = 1,
        name = "Wireless Mouse",
        price = 29.99,
        pictureLink = "https://www.wanandroid.com/blogimgs/42da12d8-de56-4439-b40c-eab66c227a4b.png",
        stock = 50,
        sales = 200,
        description = "A wireless mouse with ergonomic design.",
        select="数码"
    ),
    Goods(
        productID = 2,
        name = "Mechanical Keyboard",
        price = 89.99,
        pictureLink = "https://www.wanandroid.com/blogimgs/42da12d8-de56-4439-b40c-eab66c227a4b.png",
        stock = 30,
        sales = 150,
        description = "A mechanical keyboard with RGB lighting.",
        "数码"
    ),
    Goods(
        productID = 3,
        name = "HD Monitor",
        price = 199.99,
        pictureLink = "https://www.wanandroid.com/blogimgs/42da12d8-de56-4439-b40c-eab66c227a4b.png",
        stock = 20,
        sales = 80,
        description = "A 24-inch full HD monitor with vibrant colors.",
        select ="数码"
    ),
    Goods(
        productID = 4,
        name = "USB-C Hub",
        price = 19.99,
        pictureLink = "https://www.wanandroid.com/blogimgs/42da12d8-de56-4439-b40c-eab66c227a4b.png",
        stock = 100,
        sales = 300,
        description = "A compact USB-C hub with multiple ports.",
        "文娱"
    )
)


//    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
//    val coroutineScope = rememberCoroutineScope()


