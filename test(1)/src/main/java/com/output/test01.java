package com.output;
import java.sql.*;

public class test01 {
    public static void main(String[] args) {
        try {

            DataAccessObject dataAccessObject = new DataAccessObject();

            String query = "SELECT * FROM student";
            //执行SQL
            ResultSet resultSet = dataAccessObject.executeQuery(query);
            //循环遍历
            while (resultSet.next()) {
                // 根据需要获取数据
                String column1 = resultSet.getString("cardnumber");
                String column2 = resultSet.getString("password");
                System.out.println(column1 + ", " + column2);
            }


            //String sql = "INSERT INTO student (cardnumber, password) VALUES (?, ?)";
            //dataAccessObject.executeInsert(sql,1,1);


        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();



        }

    }
}
