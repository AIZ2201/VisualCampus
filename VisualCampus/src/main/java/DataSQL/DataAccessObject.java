package DataSQL;


import java.sql.*;

public class DataAccessObject {
    private  Connection connection;

    public DataAccessObject() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/vcampus"; // 数据库名
        String username = "root"; // 用户名为 root
        String password = "123456"; // 密码

        //加载MySQL JDBC驱动程序
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //开始连接
        connection = DriverManager.getConnection(url, username, password);
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    //执行SQL
    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeQuery();
    }

    // 执行插入操作
    public int executeInsert(String sql, Object... params) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            int rowsAffected = preparedStatement.executeUpdate();

            // 判断是否插入成功
            return rowsAffected;
        }
    }

    // 执行删除操作
    public int executeDelete(String sql, Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeUpdate();
    }

    // 执行更新操作
    public int executeUpdate(String query, Object... parameters) throws SQLException {
        int affectedRows = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // 设置SQL语句的参数
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }

            // 执行SQL语句
            affectedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return affectedRows;
    }

    public Boolean getIsEvaluated(Integer cardNumber, String courseName,String teacherName) throws SQLException {
        String sql = "SELECT isEvaluated FROM evaluationofteaching WHERE cardNumber = ? AND courseName = ? AND teacherName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // 设置查询参数
            preparedStatement.setInt(1, cardNumber);
            preparedStatement.setString(2, courseName);
            preparedStatement.setString(3, teacherName);

            // 执行查询
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // 获取isEvaluated字段的值
                    return resultSet.getBoolean("isEvaluated");
                } else {
                    // 记录未找到，返回null或抛出异常
                    return null;
                }
            }
        }
    }

    public Boolean getIsSelected(Integer cardNumber, String courseName) throws SQLException {
        String sql = "SELECT isSelected FROM teachingclass WHERE cardNumber = ? AND courseName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // 设置查询参数
            preparedStatement.setInt(1, cardNumber);
            preparedStatement.setString(2, courseName);

            // 执行查询
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // 获取isEvaluated字段的值
                    return resultSet.getBoolean("isSelected");
                } else {
                    // 记录未找到，返回null或抛出异常
                    return null;
                }
            }
        }
    }

}
