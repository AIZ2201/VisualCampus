package com.output;
import net.sf.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login_page {
    private int cardnumber;
    private String password;

    public JSONObject login_submit(JSONObject user) {
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
                    object.put("cardNumber", cardnumber);
                    object.put("password", password);
                    object.put("name", resultSet.getString("name"));
                    object.put("gender", resultSet.getString("gender"));
                    object.put("phone", resultSet.getString("phone"));
                    object.put("email", resultSet.getString("email"));
                    object.put("role", resultSet.getString("role"));
                    break; // 找到匹配项后跳出循环
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
}
