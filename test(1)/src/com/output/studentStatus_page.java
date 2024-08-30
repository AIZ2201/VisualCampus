package com.output;
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

                // 执行更新操作
                int rowsAffected = dataAccessObject.executeUpdate(updateQuery, newName, newStudentNumber, newGender,
                        newMajor, newSchool, newStudentStat, newEnrollment, newBirthPlace, newPoliticalStat, studentCardNumber);

                if (rowsAffected > 0) {
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

            // 获取客户端传来的管理员cardnumber、password和被修改学生的信息
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
}
