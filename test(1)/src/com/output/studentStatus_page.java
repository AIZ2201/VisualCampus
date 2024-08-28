package com.output;
import net.sf.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

public class studentStatus_page {
    private int cardnumber;
    private String password;

    //学籍管理页面查看学籍操作的处理函数
    public JSONObject studentStatus_view(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            // 获取客户端传来的 cardnumber 和 password
            cardnumber = user.getInt("cardNumber");
            password = user.getString("password");

            // 准备查询语句
            String query = "SELECT * FROM student WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardnumber);

            // 遍历结果集，验证用户密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (password.equals(dbPassword)) {
                    object.put("status", "success");
                    object.put("cardNumber", cardnumber);
                    object.put("password", password);
                    object.put("name", resultSet.getString("name"));
                    object.put("studentNumber",resultSet.getString(("studentNumber")));
                    object.put("gender", resultSet.getString("gender"));
                    object.put("major", resultSet.getString("major"));
                    object.put("school", resultSet.getString("school"));
                    object.put("studentStat", resultSet.getString("studentStat"));
                    object.put("enrollment", resultSet.getDate("enrollment"));
                    object.put("birthPlace", resultSet.getString("birthPlace"));
                    object.put("politicalStat", resultSet.getString("politicalStat"));
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
