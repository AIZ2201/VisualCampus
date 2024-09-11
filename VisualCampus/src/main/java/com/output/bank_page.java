package com.output;
import DataSQL.DataAccessObject;
import net.sf.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

public class bank_page {
    private int cardnumber;
    private String password;

    //银行页面查看账户余额操作的处理函数
    public JSONObject bank_view(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            // 获取客户端传来的 cardnumber 和 password
            cardnumber = user.getInt("cardNumber");
            password = user.getString("password");

            // 准备查询语句
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardnumber);

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (password.equals(dbPassword)) {
                    object.put("status", "success");
                    object.put("balance", resultSet.getDouble("balance"));
                }
            }

            resultSet.close(); // 关闭 ResultSet

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

    //银行页面充值操作的处理函数
    public JSONObject bank_recharge(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber、password
            int cardNumber = user.getInt("cardNumber");
            String password = user.getString("password");
            double balance = user.getDouble("amount");


            // 准备查询语句验证管理员身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardNumber);

            boolean isAuthenticated = false;
            double tempBalance = 0;

            // 遍历结果集，验证管理员密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                tempBalance = resultSet.getDouble("balance");
                if (password.equals(dbPassword)) {
                    isAuthenticated = true;
                    break;
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 准备更新语句
                String updateQuery = "UPDATE user SET balance = ? WHERE cardNumber = ?";

                // 执行更新操作
                int rowsAffected = dataAccessObject.executeUpdate(updateQuery, tempBalance + balance, cardNumber);

                if (rowsAffected > 0) {
                    object.put("status", "success");
                    object.put("message", "Balance information updated successfully.");
                } else {
                    object.put("status", "error");
                    object.put("message", "Failed to update balance information.");
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

    //银行页面提现操作的处理函数
    public JSONObject bank_withdraw(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber、password
            int cardNumber = user.getInt("cardNumber");
            String password = user.getString("password");
            double balance = user.getDouble("amount");


            // 准备查询语句验证管理员身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardNumber);

            boolean isAuthenticated = false;
            double tempBalance = 0;

            // 遍历结果集，验证管理员密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                tempBalance = resultSet.getDouble("balance");
                if(balance > tempBalance) {
                    object.put("status", "error");
                    object.put("message", "Excessive cash withdrawals.");
                }
                if (password.equals(dbPassword)) {
                    isAuthenticated = true;
                    break;
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAuthenticated) {
                // 准备更新语句
                String updateQuery = "UPDATE user SET balance = ? WHERE cardNumber = ?";

                // 执行更新操作
                int rowsAffected = dataAccessObject.executeUpdate(updateQuery, tempBalance - balance, cardNumber);

                if (rowsAffected > 0) {
                    object.put("status", "success");
                    object.put("message", "Balance information updated successfully.");
                } else {
                    object.put("status", "error");
                    object.put("message", "Failed to update balance information.");
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
}
