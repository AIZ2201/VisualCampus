package com.output;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class store_page {
    private int cardnumber;
    private String password;

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
                JSONArray itemsArray = new JSONArray();

                // 遍历商品结果集并将每个商品添加到JSONArray中
                while (storeResultSet.next()) {
                    JSONObject itemObject = new JSONObject();
                    itemObject.put("productID",storeResultSet.getInt("productID"));
                    itemObject.put("name", storeResultSet.getString("name"));
                    itemObject.put("price", storeResultSet.getDouble("price"));
                    itemObject.put("pictureLink", storeResultSet.getString("pictureLink"));
                    itemObject.put("stock", storeResultSet.getInt("stock"));
                    itemObject.put("sales", storeResultSet.getInt("sales"));
                    itemObject.put("description", storeResultSet.getString("description"));

                    itemsArray.add(itemObject);
                }

                storeResultSet.close(); // 关闭商品结果集

                // 将商品信息JSONArray添加到返回的JSONObject中
                object.put("items", itemsArray);
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

    //商店页面支付订单操作的处理函数
    public JSONObject store_payTransaction(JSONObject user){
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber和password
            int adminCardNumber = user.getInt("cardNumber");
            String adminPassword = user.getString("password");

            // 准备查询语句验证用户身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, adminCardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (adminPassword.equals(dbPassword)) {
                    isAuthenticated = true;
                    break; // 找到匹配项后跳出循环
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 获取传回的订单列表
                JSONArray transactions = user.getJSONArray("transactions");

                // 遍历每个订单，插入到数据库
                for (int i = 0; i < transactions.size(); i++) {
                    JSONObject transaction = transactions.getJSONObject(i);
                    int productID = transaction.getInt("productID");
                    double productPrice = transaction.getDouble("productPrice");
                    int productAmount = transaction.getInt("productAmount");
                    int cardNumber = transaction.getInt("cardNumber");
                    String timeStr = transaction.getString("time");
                    String remark = transaction.getString("remark");

                    // 将字符串转换为 LocalDate
                    LocalDate time = null;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    try {
                        time = LocalDate.parse(timeStr, formatter);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format for transaction time: " + timeStr);
                    }

                    // 准备插入语句
                    String insertQuery = "INSERT INTO producttransactionrecord (productID, productPrice, productAmount, cardNumber, transactionTime, remark) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

                    // 执行插入操作
                    int rowsAffected = dataAccessObject.executeInsert(insertQuery, productID, productPrice, productAmount, cardNumber, time, remark);

                    // 检查插入是否成功
                    if (rowsAffected > 0) {
                        object.put("status", "success");
                        object.put("message", "Transaction recorded successfully.");
                    } else {
                        object.put("status", "error");
                        object.put("message", "Failed to record transaction.");
                        break;
                    }
                }
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
                User tempUser = (User) JSONObject.toBean(user, User.class);
                Product product = tempUser.getProduct();
                String name = product.getName();
                double price = product.getPrice();
                String pictureLink = product.getPictureLink();
                int stock = product.getStock();
                int sales = product.getSales();
                String description = product.getDescription();

                // 准备插入语句
                String insertQuery = "INSERT INTO product (name, price, pictureLink, stock, sales, description) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                // 执行插入操作
                int rowsAffected = dataAccessObject.executeInsert(insertQuery, name, price, pictureLink, stock, sales, description);

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
                // 准备查询语句以搜索学生
                String searchQuery = "SELECT * FROM product WHERE name LIKE ?";

                // 执行查询操作
                ResultSet productResultSet = dataAccessObject.executeQuery(searchQuery, "%" + name + "%");

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
                    productArray.add(productJson);
                }

                productResultSet.close(); // 关闭 ResultSet

                if (productArray.size() > 0) {
                    object.put("status", "success");
                    object.put("message", "Product information retrieved successfully.");
                    object.put("students", productArray);
                } else {
                    object.put("status", "error");
                    object.put("message", "No products found matching the search criteria.");
                    object.put("students", new JSONArray());
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
            User tempUser = (User) JSONObject.toBean(user, User.class);
            Product product = tempUser.getProduct();
            int productID = product.getProductID();
            String newName = product.getName();
            double newPrice = product.getPrice();
            String newPictureLink = product.getPictureLink();
            int newStock = product.getStock();
            int newSales = product.getSales();
            String newDescription = product.getDescription();

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
                String updateQuery = "UPDATE product SET newName = ?, newPrice = ?, newPictureLink = ?, newStock = ?, newSales = ?, " +
                        "newDescription = ? WHERE productID = ?";

                // 执行更新操作
                int rowsAffected = dataAccessObject.executeUpdate(updateQuery, newName, newPrice, newPictureLink,
                        newStock, newSales, newDescription);

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
            User tempUser = (User) JSONObject.toBean(user, User.class);
            Product product = tempUser.getProduct();
            int productID = product.getProductID();

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

                    // 获取备注
                    transactionObject.put("remark", transactionResultSet.getString("remark"));

                    // 将交易记录添加到 JSONArray 中
                    transactionsArray.add(transactionObject);
                }
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
