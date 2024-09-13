package com.output;
import DataSQL.DataAccessObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class store_page {
    private int cardnumber;
    private String password;

    //商店页面猜你喜欢的处理函数
    public JSONObject store_guess(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            // 获取客户端传来的 cardnumber 和 password
            int cardnumber = user.getInt("cardNumber");
            String password = user.getString("password");

            // 准备查询语句验证用户身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardnumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (password.equals(dbPassword)) {
                    isAuthenticated = true;
                    object.put("status", "success");
                    break; // 找到匹配项后跳出循环
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 查询交易记录，按购买时间排序
                String transactionQuery = "SELECT * FROM producttransactionrecord WHERE cardNumber = ? ORDER BY time ASC";
                ResultSet transactionResultSet = dataAccessObject.executeQuery(transactionQuery, cardnumber);

                // 使用 LRU 缓存存储最近购买的两类商品类型
                LinkedHashMap<String, String> lruCache = new LinkedHashMap<String, String>(2, 0.75f, true) {
                    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                        return size() > 2;  // 缓存最大容量为2
                    }
                };

                // 遍历交易记录并更新 LRU 缓存
                while (transactionResultSet.next()) {
                    String productType = transactionResultSet.getString("select");  // 获取商品类型
                    lruCache.put(productType, productType);  // 使用商品类型更新 LRU 缓存
                }

                transactionResultSet.close(); // 关闭交易记录结果集

                // 获取缓存中最近的两类商品类型
                List<String> recentProductTypes = new ArrayList<>(lruCache.values());

                // 如果找到的商品类型少于2类，随机选择商品类型补足
                if (recentProductTypes.size() < 2) {
                    // 查询所有商品类型
                    String allTypesQuery = "SELECT DISTINCT `select` FROM product";
                    ResultSet allTypesResultSet = dataAccessObject.executeQuery(allTypesQuery);

                    List<String> allProductTypes = new ArrayList<>();
                    while (allTypesResultSet.next()) {
                        allProductTypes.add(allTypesResultSet.getString("select"));
                    }
                    allTypesResultSet.close(); // 关闭结果集

                    // 移除已经在 recentProductTypes 中的类型，避免重复
                    allProductTypes.removeAll(recentProductTypes);

                    // 随机选择商品类型来补足到2类
                    Random random = new Random();
                    while (recentProductTypes.size() < 2 && !allProductTypes.isEmpty()) {
                        int randomIndex = random.nextInt(allProductTypes.size());
                        String randomProductType = allProductTypes.remove(randomIndex);
                        recentProductTypes.add(randomProductType);
                    }
                }

                // 创建 JSONArray 来存储商品类型
                JSONArray recommendedProducts = new JSONArray();

                // 遍历缓存中的两类商品类型，查找并展示
                for (String productType : recentProductTypes) {
                    String productQuery = "SELECT * FROM product WHERE `select` = ?";
                    ResultSet productResultSet = dataAccessObject.executeQuery(productQuery, productType);

                    while (productResultSet.next()) {
                        JSONObject productObject = new JSONObject();
                        productObject.put("productID", productResultSet.getInt("productID"));
                        productObject.put("name", productResultSet.getString("name"));
                        productObject.put("price", productResultSet.getDouble("price"));
                        productObject.put("pictureLink", productResultSet.getString("pictureLink"));
                        productObject.put("stock", productResultSet.getInt("stock"));
                        productObject.put("sales", productResultSet.getInt("sales"));
                        productObject.put("description", productResultSet.getString("description"));
                        productObject.put("select", productResultSet.getString("select"));

                        recommendedProducts.add(productObject);  // 将商品信息添加到推荐列表中
                    }

                    productResultSet.close();  // 关闭商品结果集
                }

                // 随机打乱最终的商品推荐列表
                Collections.shuffle(recommendedProducts);

                // 将推荐的商品信息 JSONArray 添加到返回的 JSONObject 中
                object.put("recommendedProducts", recommendedProducts);

            } else {
                object.put("status", "error");
                object.put("message", "User authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

    //商店页面展示商品操作的处理函数
    public JSONObject store_show(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            // 获取客户端传来的 cardnumber 和 password
            int cardnumber = user.getInt("cardNumber");
            String password = user.getString("password");

            // 准备查询语句验证用户身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardnumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (password.equals(dbPassword)) {
                    isAuthenticated = true;
                    object.put("status", "success");
                    break; // 找到匹配项后跳出循环
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 如果用户身份验证成功，则查询storeitem表中的所有商品信息
                String storeQuery = "SELECT * FROM product";
                ResultSet storeResultSet = dataAccessObject.executeQuery(storeQuery);

                // 创建JSONArray来存储商品信息
                JSONArray productArray = new JSONArray();

                // 遍历商品结果集并将每个商品添加到JSONArray中
                while (storeResultSet.next()) {
                    JSONObject productObject = new JSONObject();
                    productObject.put("productID",storeResultSet.getInt("productID"));
                    productObject.put("name", storeResultSet.getString("name"));
                    productObject.put("price", storeResultSet.getDouble("price"));
                    productObject.put("pictureLink", storeResultSet.getString("pictureLink"));
                    productObject.put("stock", storeResultSet.getInt("stock"));
                    productObject.put("sales", storeResultSet.getInt("sales"));
                    productObject.put("description", storeResultSet.getString("description"));
                    productObject.put("select",storeResultSet.getString("select"));

                    productArray.add(productObject);
                }

                storeResultSet.close(); // 关闭商品结果集

                // 将商品信息JSONArray添加到返回的JSONObject中
                object.put("product", productArray);
            } else {
                object.put("status", "error");
                object.put("message", "User authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

    //商店页面展示订单操作的处理函数（此处订单为已支付订单）
    public JSONObject store_getMyTransaction(JSONObject user){
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            // 获取客户端传来的 cardnumber 和 password
            int cardNumber = user.getInt("cardNumber");
            String password = user.getString("password");

            // 准备查询语句验证用户身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (password.equals(dbPassword)) {
                    isAuthenticated = true;
                    object.put("status", "success");
                    break; // 找到匹配项后跳出循环
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 如果用户身份验证成功，则查询producttransactionrecord表中与cardNumber匹配的所有交易记录
                String transactionQuery = "SELECT * FROM producttransactionrecord WHERE cardNumber = ?";
                ResultSet transactionResultSet = dataAccessObject.executeQuery(transactionQuery, cardNumber);

                // 创建JSONArray来存储交易记录
                JSONArray transactionsArray = new JSONArray();

                // 遍历交易结果集并将每个交易记录添加到JSONArray中
                while (transactionResultSet.next()) {
                    JSONObject transactionObject = new JSONObject();
                    transactionObject.put("productID", transactionResultSet.getInt("productID"));
                    transactionObject.put("name", transactionResultSet.getString("name"));
                    transactionObject.put("productPrice", transactionResultSet.getDouble("productPrice"));
                    transactionObject.put("productAmount", transactionResultSet.getInt("productAmount"));
                    transactionObject.put("cardNumber", transactionResultSet.getInt("cardNumber"));
                    java.sql.Date sqlTime = transactionResultSet.getDate("time");
                    if (sqlTime != null) {
                        LocalDate time = sqlTime.toLocalDate();
                        String formattedTime = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        transactionObject.put("time", formattedTime);
                    }
                    transactionObject.put("remark", transactionResultSet.getString("remark"));

                    transactionsArray.add(transactionObject);
                }

                transactionResultSet.close(); // 关闭交易结果集

                // 将交易记录JSONArray添加到返回的JSONObject中
                object.put("transactions", transactionsArray);
            } else {
                object.put("status", "error");
                object.put("message", "User authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

    //商店页面购买商品（支付订单）操作的处理函数
    public JSONObject store_buygoods(JSONObject user){
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber和password
            int CardNumber = user.getInt("cardNumber");
            String Password = user.getString("password");

            // 准备查询语句验证用户身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, CardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (Password.equals(dbPassword)) {
                    isAuthenticated = true;
                    break; // 找到匹配项后跳出循环
                }
            }

            double balance = resultSet.getInt("balance");
            resultSet.close(); // 关闭 ResultSet

            double allPrice = 0;

            if (isAuthenticated) {

                // 获取传回的订单列表
                JSONArray goodsList = user.getJSONArray("goodsList");
                for(int i = 0; i <goodsList.size(); i++) {
                    JSONObject tempGoods = goodsList.getJSONObject(i).getJSONObject("goods");
                    allPrice += tempGoods.getDouble("price") * goodsList.getJSONObject(i).getInt("quantity");
                }

                // 准备查询语句验证用户余额
                String balanceQuery = "SELECT * FROM user WHERE cardNumber = ?";
                ResultSet balanceResultSet = dataAccessObject.executeQuery(balanceQuery, CardNumber);

                // 遍历结果集，验证用户余额是否充足
                while (balanceResultSet.next()) {
                    int tempBalance = balanceResultSet.getInt("balance");
                    if (tempBalance < allPrice) {
                        object.put("status", "error");
                        object.put("message", "余额不足，购买失败");
                        return object;
                    }
                }

                balanceResultSet.close();

                // 遍历每个订单，插入到数据库
                for (int i = 0; i < goodsList.size(); i++) {
                    JSONObject goods = goodsList.getJSONObject(i).getJSONObject("goods");
                    int productID = goods.getInt("productID");
                    String name = goods.getString("name");
                    double productPrice = goods.getDouble("price");
                    int stock = goods.getInt("stock");
                    int sales = goods.getInt("sales");
                    int productAmount = goodsList.getJSONObject(i).getInt("quantity");
                    String select = goods.getString("select");
                    String timeStr = user.getString("time");

                    balance -= productAmount * productPrice;
                    // 将字符串转换为 LocalDate
                    LocalDate time = null;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    try {
                        time = LocalDate.parse(timeStr, formatter);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format for transaction time: " + timeStr);
                    }

                    // 准备插入语句
                    String insertQuery = "INSERT INTO producttransactionrecord (productID, name, productPrice, productAmount, cardNumber, time, `select`) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";

                    // 执行插入操作
                    int rowsAffected = dataAccessObject.executeInsert(insertQuery, productID, name, productPrice, productAmount, CardNumber, time, select);

                    // 检查插入是否成功
                    if (rowsAffected > 0) {
                        object.put("status", "success");

                        // 准备更新语句
                        String updateQuery2 = "UPDATE user SET balance = ? WHERE cardNumber = ?";

                        // 执行更新操作
                        int rowsAffected2 = dataAccessObject.executeUpdate(updateQuery2, balance, CardNumber);

                        // 准备更新语句
                        String updateQuery = "UPDATE product SET stock = ?, sales = ? WHERE productID = ?";

                        // 执行更新操作
                        int productRowsAffected = dataAccessObject.executeUpdate(updateQuery, stock - productAmount, sales + productAmount, productID);

                        if (productRowsAffected > 0 && rowsAffected2 > 0) {
                            //object.put("status", "success");
                            //object.put("message", "Product information updated successfully.");
                        } else {
                            object.put("status", "error");
                            object.put("message", "Failed to update goods information.");
                        }
                    } else {
                        object.put("status", "error");
                        object.put("message", "Failed to record transaction.");
                        break;
                    }
                }
                object.put("message", "Transaction recorded successfully.");
            } else {
                object.put("status", "error");
                object.put("message", "Admin authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

    //商店页面增加商品操作的处理函数
    public JSONObject store_addProduct(JSONObject user){
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber和password
            int CardNumber = user.getInt("cardNumber");
            String Password = user.getString("password");

            // 准备查询语句验证用户身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, CardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (Password.equals(dbPassword)) {
                    isAuthenticated = true;
                    break; // 找到匹配项后跳出循环
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 获取传回的订单列表
                JSONObject newGoods = user.getJSONObject("newGoods");

                String name = newGoods.getString("name");
                double price = newGoods.getDouble("price");
                String pictureLink = newGoods.getString("pictureLink");
                int stock = newGoods.getInt("stock");
                int sales = 0;
                String description = newGoods.getString("description");
                String select = newGoods.getString("select");

                // 准备插入语句
                String insertQuery = "INSERT INTO product (name, price, pictureLink, stock, sales, description, `select`) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                // 执行插入操作
                int rowsAffected = dataAccessObject.executeInsert(insertQuery, name, price, pictureLink, stock, sales, description, select);

                // 检查插入是否成功
                if (rowsAffected > 0) {
                    object.put("status", "success");
                    object.put("message", "Product added successfully.");
                } else {
                    object.put("status", "error");
                    object.put("message", "Failed to add Product.");
                }
            } else {
                object.put("status", "error");
                object.put("message", "Authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

    //商店页面搜索商品操作的处理函数
    public JSONObject store_search(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber、password和被搜索商品的信息
            int CardNumber = user.getInt("cardNumber");
            String Password = user.getString("password");

            String name = user.getString("searchText");
            String select = user.getString("select");
            if(Objects.equals(select, "全部") && name.isEmpty()) {
                object = store_show(user);
                return object;
            }

            // 准备查询语句验证身份
            String Query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(Query, CardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (Password.equals(dbPassword)) {
                    isAuthenticated = true;
                    break;
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 准备查询语句以搜索商品
                String searchQuery = null;
                ResultSet productResultSet = null;
                if(Objects.equals(select, "全部")) {
                    searchQuery = "SELECT * FROM product WHERE name LIKE ?";
                    // 执行查询操作
                    productResultSet = dataAccessObject.executeQuery(searchQuery, "%" + name + "%");
                }
                else {
                    searchQuery = "SELECT * FROM product WHERE name LIKE ? AND `select` = ?";
                    // 执行查询操作
                    productResultSet = dataAccessObject.executeQuery(searchQuery, "%" + name + "%", select);
                }

                JSONArray productArray = new JSONArray();
                while (productResultSet.next()) {
                    JSONObject productJson = new JSONObject();
                    productJson.put("productID", productResultSet.getInt("productID"));
                    productJson.put("name", productResultSet.getString("name"));
                    productJson.put("price", productResultSet.getDouble("price"));
                    productJson.put("pictureLink", productResultSet.getString("pictureLink"));
                    productJson.put("stock", productResultSet.getInt("stock"));
                    productJson.put("sales", productResultSet.getInt("sales"));
                    productJson.put("description", productResultSet.getString("description"));
                    productJson.put("select",productResultSet.getString("select"));
                    productArray.add(productJson);
                }

                productResultSet.close(); // 关闭 ResultSet

                if (productArray.size() > 0) {
                    object.put("status", "success");
                    object.put("message", "Product information retrieved successfully.");
                    object.put("product", productArray);
                } else {
                    object.put("status", "error");
                    object.put("message", "No products found matching the search criteria.");
                    object.put("product", new JSONArray());
                }
            } else {
                object.put("status", "error");
                object.put("message", "Authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

    //商店页面修改商品操作的处理函数
    public JSONObject store_change(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber、password和被修改商品的信息
            int CardNumber = user.getInt("cardNumber");
            String Password = user.getString("password");

            // 被修改商品的商品ID和修改后的信息
            JSONObject goods = user.getJSONObject("goods");
            int productID = goods.getInt("productID");
            String newName = goods.getString("name");
            double newPrice = goods.getDouble("price");
            String newPictureLink = goods.getString("pictureLink");
            int newStock = goods.getInt("stock");
            int newSales = goods.getInt("sales");
            String newDescription = goods.getString("description");
            String newSelect = goods.getString("select");

            // 准备查询语句验证身份
            String Query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(Query, CardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (Password.equals(dbPassword)) {
                    isAuthenticated = true;
                    break;
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 准备更新语句
                String updateQuery = "UPDATE product SET name = ?, price = ?, pictureLink = ?, stock = ?, sales = ?, " +
                        "description = ?, `select` = ? WHERE productID = ?";

                // 执行更新操作
                int rowsAffected = dataAccessObject.executeUpdate(updateQuery, newName, newPrice, newPictureLink,
                        newStock, newSales, newDescription, newSelect, productID);

                if (rowsAffected > 0) {
                    object.put("status", "success");
                    object.put("message", "Product information updated successfully.");
                } else {
                    object.put("status", "error");
                    object.put("message", "Failed to update product information.");
                }
            } else {
                object.put("status", "error");
                object.put("message", "Authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }


        return object; // 返回结果对象
    }

    //商店页面删除商品操作的处理函数
    public JSONObject store_delete(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber、password和被修改商品的信息
            int CardNumber = user.getInt("cardNumber");
            String Password = user.getString("password");

            // 被删除商品的商品ID
            JSONObject goods = user.getJSONObject("goods");
            int productID = goods.getInt("productID");

            // 准备查询语句验证身份
            String Query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(Query, CardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (Password.equals(dbPassword)) {
                    isAuthenticated = true;
                    break;
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 准备删除语句
                String deleteQuery = "DELETE FROM product WHERE productID = ?";

                // 执行删除操作
                int rowsAffected = dataAccessObject.executeDelete(deleteQuery, productID);

                if (rowsAffected > 0) {
                    object.put("status", "success");
                    object.put("message", "Product information deleted successfully.");
                } else {
                    object.put("status", "error");
                    object.put("message", "Failed to delete product information.");
                }
            } else {
                object.put("status", "error");
                object.put("message", "Authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

    //商店页面查看销售情况操作的处理函数
    public JSONObject store_getAllTransaction(JSONObject user){
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            // 获取客户端传来的 cardnumber 和 password
            int cardNumber = user.getInt("cardNumber");
            String password = user.getString("password");

            // 准备查询语句验证用户身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (password.equals(dbPassword)) {
                    isAuthenticated = true;
                    object.put("status", "success");
                    break; // 找到匹配项后跳出循环
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 查询 producttransactionrecord 表中的所有交易记录
                String transactionQuery = "SELECT * FROM producttransactionrecord";
                ResultSet transactionResultSet = dataAccessObject.executeQuery(transactionQuery);

                // 创建 JSONArray 来存储交易记录
                JSONArray transactionsArray = new JSONArray();

                // 遍历交易结果集并将每个交易记录添加到 JSONArray 中
                while (transactionResultSet.next()) {
                    JSONObject transactionObject = new JSONObject();
                    transactionObject.put("productID", transactionResultSet.getInt("productID"));
                    transactionObject.put("name", transactionResultSet.getString("name"));
                    transactionObject.put("productPrice", transactionResultSet.getDouble("productPrice"));
                    transactionObject.put("productAmount", transactionResultSet.getInt("productAmount"));
                    transactionObject.put("cardNumber", transactionResultSet.getInt("cardNumber"));

                    // 获取并格式化时间
                    java.sql.Date sqlTime = transactionResultSet.getDate("time");
                    if (sqlTime != null) {
                        LocalDate time = sqlTime.toLocalDate();
                        String formattedTime = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        transactionObject.put("time", formattedTime);
                    }

                    transactionObject.put("remark", transactionResultSet.getString("remark"));
                    transactionObject.put("select", transactionResultSet.getString("select"));

                    // 将交易记录添加到 JSONArray 中
                    transactionsArray.add(transactionObject);
                }
                transactionResultSet.close(); // 关闭交易结果集

                // 将交易记录JSONArray添加到返回的JSONObject中
                object.put("transactions", transactionsArray);
            } else {
                object.put("status", "error");
                object.put("message", "User authentication failed.");
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }
}
