package com.output;
import net.sf.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import DataSQL.DataAccessObject;

public class login_page {
    private int cardnumber;
    private String password;

    //登录页面登录操作的处理函数
    public JSONObject login_submit(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            // 获取客户端传来的 cardNumber 和 password
            int cardnumber = user.getInt("cardNumber");
            String password = user.getString("password");

            // 准备查询语句
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardnumber);

            if (!resultSet.isBeforeFirst()) {
                // 如果结果集中没有记录，表示卡号不存在
                object.put("status", "failed");
                object.put("message", "cardNumber does not exist.");
            } else {
                boolean isPasswordCorrect = false;
                // 遍历结果集，验证用户密码
                while (resultSet.next()) {
                    String dbPassword = resultSet.getString("password");
                    if (password.equals(dbPassword)) {
                        object.put("status", "success");
                        object.put("cardNumber", cardnumber);
                        object.put("password", password);
                        object.put("name", resultSet.getString("name"));
                        object.put("gender", resultSet.getString("gender"));
                        object.put("phone", resultSet.getString("phone"));
                        object.put("email", resultSet.getString("email"));
                        object.put("role", resultSet.getString("role"));
                        isPasswordCorrect = true;
                        break; // 找到匹配项后跳出循环
                    }
                }
                if (!isPasswordCorrect) {
                    object.put("status", "failed");
                    object.put("message", "Password incorrect.");
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


    //登录页面找回密码操作的处理函数
    public JSONObject change_password(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            // 获取客户端传来的 cardNumber 和 new_password
            int cardnumber = user.getInt("cardNumber");
            String newPassword = user.getString("new_password");

            // 准备查询语句，验证用户是否存在
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardnumber);

            boolean passwordUpdated = false;

            // 遍历结果集，验证用户密码并更新密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");

                if (newPassword.equals(dbPassword)) {
                    // 密码未更改，返回提示
                    object.put("status", "failed");
                    object.put("message", "New password cannot be the same as the old password.");
                    return object;
                } else {
                    // 更新密码到数据库
                    String updateQuery = "UPDATE user SET password = ? WHERE cardNumber = ?";
                    String updateQuery2 = "UPDATE student SET password = ? WHERE cardNumber = ?";
                    int rowsAffected = dataAccessObject.executeUpdate(updateQuery, newPassword, cardnumber);
                    int rowsAffected2 = dataAccessObject.executeUpdate(updateQuery2, newPassword, cardnumber);

                    if (rowsAffected > 0 && rowsAffected2 > 0) {
                        object.put("status", "success");
                        passwordUpdated = true;
                    } else {
                        object.put("status", "failed");
                        object.put("message", "Failed to update password.");
                    }
                    break; // 找到匹配项后跳出循环
                }
            }

            if (!passwordUpdated) {
                // 用户未找到或密码更新失败
                object.put("status", "failed");
                object.put("message", "User not found or password update failed.");
            }else{
                object.put("status", "success");
                object.put("message", "Password updated successfully.");
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

}

