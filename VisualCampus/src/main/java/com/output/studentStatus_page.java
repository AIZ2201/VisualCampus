package com.output;
import entity.*;
import DataSQL.DataAccessObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
                    // 将 enrollment 转换为字符串格式（yyyy-MM-dd）
                    java.sql.Date sqlEnrollmentDate = resultSet.getDate("enrollment");
                    if (sqlEnrollmentDate != null) {
                        LocalDate enrollmentDate = sqlEnrollmentDate.toLocalDate();
                        String formattedEnrollmentDate = enrollmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        object.put("enrollment", formattedEnrollmentDate);
                    }
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

    //学籍管理页面管理员修改学籍操作的处理函数
    public JSONObject studentStatus_change(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的管理员cardnumber、password和被修改学生的信息
            int adminCardNumber = user.getInt("cardNumber");
            String adminPassword = user.getString("password");

            // 被修改学生的一卡通号和修改后的信息
            User tempUser = (User) JSONObject.toBean(user, User.class);
            Student student = tempUser.getStudent();
            int studentCardNumber = student.getCardNumber();
            String newName = student.getName();
            String newStudentNumber = student.getStudentNumber();
            String newGender = student.getGender();
            String newMajor = student.getMajor();
            String newSchool = student.getSchool();
            String newStudentStat = student.getStudentStat();
            String newEnrollmentStr = student.getEnrollment();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 将字符串转换为 LocalDate
            LocalDate newEnrollment = null;
            try {
                newEnrollment = LocalDate.parse(newEnrollmentStr, formatter);
            } catch (DateTimeParseException e) {
                // 处理解析异常，比如打印错误信息或采取其他措施
                System.out.println("Invalid date format for enrollment: " + newEnrollmentStr);
                // 可以选择抛出异常，或返回一个默认值
                // enrollment = LocalDate.now(); // 或者根据需要设置一个默认值
            }
            String newBirthPlace = student.getBirthPlace();
            String newPoliticalStat = student.getPoliticalStat();

            // 准备查询语句验证管理员身份
            String adminQuery = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(adminQuery, adminCardNumber);

            boolean isAdminAuthenticated = false;

            // 遍历结果集，验证管理员密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (adminPassword.equals(dbPassword)) {
                    isAdminAuthenticated = true;
                    break;
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAdminAuthenticated) {
                // 准备更新语句
                String updateQuery = "UPDATE student SET name = ?, studentNumber = ?, gender = ?, major = ?, school = ?, " +
                        "studentStat = ?, enrollment = ?, birthPlace = ?, politicalStat = ? WHERE cardNumber = ?";
                String updateQuery2 = "UPDATE user SET name = ?, gender = ? WHERE cardNumber = ?";
                String updateQuery3 = "UPDATE libraryuser SET userName = ? WHERE cardNumber = ?";

                // 执行更新操作
                int rowsAffected = dataAccessObject.executeUpdate(updateQuery, newName, newStudentNumber, newGender,
                        newMajor, newSchool, newStudentStat, newEnrollment, newBirthPlace, newPoliticalStat, studentCardNumber);
                int rowsAffected2 = dataAccessObject.executeUpdate(updateQuery2, newName, newGender, studentCardNumber);
                int rowsAffected3 = dataAccessObject.executeUpdate(updateQuery3, newName, studentCardNumber);


                if (rowsAffected > 0 && rowsAffected2 > 0 && rowsAffected3 > 0) {
                    object.put("status", "success");
                    object.put("message", "Student information updated successfully.");
                } else {
                    object.put("status", "error");
                    object.put("message", "Failed to update student information.");
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

    //学籍管理页面管理员搜索学籍操作的处理函数
    public JSONObject studentStatus_search(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的管理员cardnumber、password和被搜索学生的信息
            int adminCardNumber = user.getInt("cardNumber");
            String adminPassword = user.getString("password");

            String name = user.getString("searchText");

            // 准备查询语句验证管理员身份
            String adminQuery = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(adminQuery, adminCardNumber);

            boolean isAdminAuthenticated = false;

            // 遍历结果集，验证管理员密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (adminPassword.equals(dbPassword)) {
                    isAdminAuthenticated = true;
                    break;
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if (isAdminAuthenticated) {
                // 准备查询语句以搜索学生
                String searchQuery = "SELECT * FROM student WHERE name LIKE ?";

                // 执行查询操作
                ResultSet studentResultSet = dataAccessObject.executeQuery(searchQuery, "%" + name + "%");

                JSONArray studentArray = new JSONArray();
                while (studentResultSet.next()) {
                    JSONObject studentJson = new JSONObject();
                    studentJson.put("cardNumber", studentResultSet.getInt("cardNumber"));
                    studentJson.put("name", studentResultSet.getString("name"));
                    studentJson.put("studentNumber", studentResultSet.getString("studentNumber"));
                    studentJson.put("gender", studentResultSet.getString("gender"));
                    studentJson.put("major", studentResultSet.getString("major"));
                    studentJson.put("school", studentResultSet.getString("school"));
                    studentJson.put("studentStat", studentResultSet.getString("studentStat"));
                    studentJson.put("enrollment", studentResultSet.getString("enrollment"));
                    studentJson.put("birthPlace", studentResultSet.getString("birthPlace"));
                    studentJson.put("politicalStat", studentResultSet.getString("politicalStat"));
                    studentArray.add(studentJson);
                }

                studentResultSet.close(); // 关闭 ResultSet

                if (studentArray.size() > 0) {
                    object.put("status", "success");
                    object.put("message", "Student information retrieved successfully.");
                    object.put("students", studentArray);
                } else {
                    object.put("status", "error");
                    object.put("message", "No students found matching the search criteria.");
                    object.put("students", new JSONArray());
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

    //学籍管理页面添加学籍操作的处理函数
    public JSONObject studentStatus_add(JSONObject user){
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
                JSONObject newStudent = user.getJSONObject("student");

                int stuCardNumber = newStudent.getInt("cardNumber");
                // 1. 校验 cardNumber 是否为 9 位数字
                String cardNumberStr = String.valueOf(stuCardNumber);
                if (cardNumberStr.length() != 9) {
                    //System.out.println("卡号不合法：必须是9位数字");
                    object.put("status", "error");
                    object.put("message", "The card number's not legit.");
                    return object;
                }
                // 2. 检查数据库中是否已经存在该卡号
                String queryTemp = "SELECT COUNT(*) FROM student WHERE cardNumber = ?";
                ResultSet resultSetTemp = dataAccessObject.executeQuery(queryTemp, stuCardNumber);

                if (resultSetTemp.next()) {
                    int count = resultSetTemp.getInt(1);
                    if (count > 0) {
                        //System.out.println("卡号已存在");
                        object.put("status", "error");
                        object.put("message", "Card number already exists.");
                        return object;
                    }
                }
                // 关闭结果集
                resultSetTemp.close();

                String stuPassword =  String.valueOf(stuCardNumber);
                String name = newStudent.getString("name");
                String studentNumber = newStudent.getString("studentNumber");
                String gender = newStudent.getString("gender");
                String major = newStudent.getString("major");
                String school = newStudent.getString("school");
                String studentStat = newStudent.getString("studentStat");
                String enrollmentStr = newStudent.getString("enrollment");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                // 将字符串转换为 LocalDate
                LocalDate enrollment = null;
                try {
                    enrollment = LocalDate.parse(enrollmentStr, formatter);
                } catch (DateTimeParseException e) {
                    // 处理解析异常，比如打印错误信息或采取其他措施
                    System.out.println("Invalid date format for enrollment: " + enrollmentStr);
                    // 可以选择抛出异常，或返回一个默认值
                    // enrollment = LocalDate.now(); // 或者根据需要设置一个默认值
                }
                String birthPlath = newStudent.getString("birthPlace");
                String politicalStat = newStudent.getString("politicalStat");

                // 准备插入语句
                String insertQuery = "INSERT INTO student (cardNumber, password, name, studentNumber, gender, major, school, studentStat, enrollment, birthPlace, politicalStat) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                String insertQuery2 = "INSERT INTO user (cardNumber, password, name, gender, balance, role) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                String insertQuery3 = "INSERT INTO libraryuser (userId, userName, userBorrowedNum, userMaxBorrowNum, userStatus) " +
                        "VALUES (?, ?, ?, ?, ?)";

                // 执行插入操作
                int rowsAffected = dataAccessObject.executeInsert(insertQuery, stuCardNumber, stuPassword, name, studentNumber, gender, major, school, studentStat, enrollment, birthPlath, politicalStat);
                int rowsAffected2 = dataAccessObject.executeInsert(insertQuery2, stuCardNumber, stuPassword, name, gender, 0, "student");
                int rowsAffected3 = dataAccessObject.executeInsert(insertQuery3, stuCardNumber, name, 0, 10, "student");

                // 检查插入是否成功
                if (rowsAffected > 0 && rowsAffected2 > 0 && rowsAffected3 > 0) {
                    object.put("status", "success");
                    object.put("message", "Student added successfully.");
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

    //学籍管理页面删除学生操作的处理函数
    public JSONObject studentStatus_delete(JSONObject user){
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber、password和被修改商品的信息
            int CardNumber = user.getInt("cardNumber");
            String Password = user.getString("password");
            // 被删除学生的学生一卡通号
            int cardNumber = user.getInt("studentCardNum");

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
                String deleteQuery = "DELETE FROM student WHERE cardNumber = ?";
                String deleteQuery2 = "DELETE FROM user WHERE cardNumber = ?";
                String deleteQuery3 = "DELETE FROM libraryuser WHERE cardNumber = ?";

                // 执行删除操作
                int rowsAffected = dataAccessObject.executeDelete(deleteQuery, cardNumber);
                int rowsAffected2 = dataAccessObject.executeDelete(deleteQuery2, cardNumber);
                int rowsAffected3 = dataAccessObject.executeDelete(deleteQuery3, cardNumber);

                if (rowsAffected > 0 && rowsAffected2 > 0 && rowsAffected3 > 0) {
                    object.put("status", "success");
                    object.put("message", "Student information deleted successfully.");
                } else {
                    object.put("status", "error");
                    object.put("message", "Failed to delete student information.");
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

    //学籍管理页面修改密码操作的处理函数
    public JSONObject studentStatus_changePassword(JSONObject user){
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取客户端传来的cardnumber、password
            int cardNumber = user.getInt("cardNumber");
            String password = user.getString("password");
            String oldPassword = user.getString("oldPassword");
            String newPassword = user.getString("newPassword");

            // 准备查询语句验证身份
            String query = "SELECT * FROM user WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, cardNumber);

            boolean isAuthenticated = false;

            // 遍历结果集，验证管理员密码
            while (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (password.equals(dbPassword)) {
                    isAuthenticated = true;
                    break;
                }
            }

            resultSet.close(); // 关闭 ResultSet

            if(!password.equals(oldPassword)){
                object.put("status", "error");
                object.put("message", "Old password does not match existing password.");
                return object;
            }

            if (isAuthenticated && password.equals(oldPassword)) {
                // 准备更新语句
                String updateQuery = "UPDATE student SET password = ? WHERE cardNumber = ?";
                String updateQuery2 = "UPDATE user SET password = ? WHERE cardNumber = ?";

                // 执行更新操作
                int rowsAffected = dataAccessObject.executeUpdate(updateQuery, newPassword, cardNumber);
                int rowsAffected2 = dataAccessObject.executeUpdate(updateQuery2, newPassword, cardNumber);

                if (rowsAffected > 0 && rowsAffected2 > 0) {
                    object.put("status", "success");
                    object.put("message", "Password updated successfully.");
                } else {
                    object.put("status", "error");
                    object.put("message", "Failed to update password.");
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
