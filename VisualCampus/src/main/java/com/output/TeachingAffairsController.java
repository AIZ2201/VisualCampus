package com.output;
import DataSQL.DataAccessObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.output.SeleniumLoginSimulation.readCredentials;

public class TeachingAffairsController {

    private Integer courseID;
    private Integer studentID;
    private static Integer qq=0;

    /*查看我的课程*/
    public JSONObject getSelectedClasses(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            studentID = user.getInt("cardNumber");

            // 准备查询语句获取课程记录
            String query = "SELECT courseName,teacherName,qqGroup,classroomName,duration,week,time,introduction FROM studentselectclass WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, studentID);

            if (!resultSet.isBeforeFirst()) { // 检查是否有返回结果
                object.put("status", "not_found");
                object.put("message", "No course found for the student.");
            } else {
                JSONArray coursesArray = new JSONArray();
                while(resultSet.next()){
                    object.put("status", "success");
                    // 提取课程信息
                    JSONObject courseObject = new JSONObject();
                    courseObject.put("courseName", resultSet.getString("courseName"));
                    courseObject.put("teacherName", resultSet.getString("teacherName"));
                    courseObject.put("qqGroup", resultSet.getInt("qqGroup"));
                    courseObject.put("classroomName", resultSet.getString("classroomName"));
                    courseObject.put("duration", resultSet.getString("duration"));
                    JSONArray weekArray = JSONArray.fromObject(resultSet.getString("week"));
                    courseObject.put("week", weekArray);
                    JSONArray timeArray = JSONArray.fromObject(resultSet.getString("time"));
                    courseObject.put("time", timeArray);
                    courseObject.put("introduction", resultSet.getString("introduction"));

                    coursesArray.add(courseObject);
                }
                object.put("mycourse", coursesArray);

                resultSet.close(); // 关闭 ResultSet
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*查成绩*/
    public JSONObject getGrade(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            studentID = user.getInt("cardNumber");

            // 准备查询语句获取成绩记录
            String query = "SELECT courseName, credit, grade, regular_grade, midterm_grade, final_grade FROM grade WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, studentID);

            if (!resultSet.isBeforeFirst()) { // 检查是否有返回结果
                object.put("status", "not_found");
                object.put("message", "No scores found for the student and course.");
            } else {
                JSONArray coursesArray = new JSONArray();
                while(resultSet.next()) {
                    object.put("status", "success");
                    // 提取成绩信息
                    JSONObject courseObject = new JSONObject();
                    courseObject.put("courseName", resultSet.getString("courseName"));
                    courseObject.put("credit", resultSet.getDouble("credit"));
                    courseObject.put("grade", resultSet.getInt("grade"));
                    courseObject.put("regular_grade", resultSet.getInt("regular_grade"));
                    courseObject.put("midterm_grade", resultSet.getInt("midterm_grade"));
                    courseObject.put("final_grade", resultSet.getInt("final_grade"));

                    coursesArray.add(courseObject);
                }

                object.put("courses", coursesArray);

                resultSet.close(); // 关闭 ResultSet
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*查课表*/
    public JSONObject classSchedule(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            studentID = user.getInt("cardNumber");

            // 准备查询语句获取课表记录
            String query = "SELECT courseName, classroomName, classDate, courseBegin, courseEnd FROM course WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, studentID);

            if (!resultSet.isBeforeFirst()) { // 检查是否有返回结果
                object.put("status", "not_found");
                object.put("message", "No course found for the student.");
            } else {
                JSONArray coursesArray = new JSONArray();
                while(resultSet.next()){
                    object.put("status", "success");
                    // 提取课程信息
                    JSONObject courseObject = new JSONObject();
                    courseObject.put("courseName", resultSet.getString("courseName"));
                    courseObject.put("classroomName", resultSet.getString("classroomName"));
                    courseObject.put("classDate", resultSet.getString("classDate"));
                    courseObject.put("courseBegin", resultSet.getInt("courseBegin"));
                    courseObject.put("courseEnd", resultSet.getInt("courseEnd"));

                    coursesArray.add(courseObject);
                }
                object.put("schedule", coursesArray);

                resultSet.close(); // 关闭 ResultSet
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*发送评教信息*/
    public JSONObject sendEvaluation(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            studentID = user.getInt("cardNumber");

            // 准备查询语句获取课程记录
            String query = "SELECT courseName,teacherName FROM teachingclass WHERE cardNumber = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, studentID);

            if (!resultSet.isBeforeFirst()) { // 检查是否有返回结果
                object.put("status", "not_found");
                object.put("message", "No course found for the student.");
            } else {
                JSONArray coursesArray = new JSONArray();
                while(resultSet.next()){
                    object.put("status", "success");
                    // 提取课程信息
                    JSONObject courseObject = new JSONObject();
                    courseObject.put("courseName", resultSet.getString("courseName"));
                    courseObject.put("teacherName", resultSet.getString("teacherName"));

                    coursesArray.add(courseObject);
                }
                object.put("need_evaluated", coursesArray);

                resultSet.close(); // 关闭 ResultSet
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*学生评教*/
    public JSONObject studentEvaluation(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");
        int row;
        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            studentID = user.getInt("cardNumber");
            String courseName = user.getString("courseName");
            String teacherName = user.getString("teacherName");
            String scoreGet = user.getString("scores");

            try {
                Boolean isEvaluated = dataAccessObject.getIsEvaluated(studentID, courseName,teacherName);
                if (isEvaluated==null) {
                    // 插入语句，记录评教结果
                    String insert = "INSERT INTO evaluationofteaching(cardNumber, courseName, scores,isEvaluated,teacherName) VALUES (?, ?, ?, ?,?)";
                    row = dataAccessObject.executeInsert(insert, studentID,courseName,scoreGet, true,teacherName);

                    // 调用评教脚本 (Selenium)
                    // 初始化 WebDriver 和 WebDriverWait
                    System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\Java\\jdk-21\\bin\\geckodriver.exe");
                    FirefoxOptions options = new FirefoxOptions();
                    options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0");
                    WebDriver driver = new FirefoxDriver(options);
                    WebDriverWait wait = new WebDriverWait(driver, 20);  // 等待最多 20 秒

                    // 读取 credentials.txt 文件中的 userId 和 password
                    String[] credentials = readCredentials("../credentials.txt"); // 确保路径正确
                    String userId = credentials[0];
                    String password = credentials[1];

                    // 调用 `studentEvaluation` 方法执行评教操作
                    evaluationScripts.studentEvaluation(driver, wait, userId, password);

                    object.put("evaluation_status", "evaluation_completed");

                    if(row>0)
                    {
                        object.put("status", "success");
                        object.put("message", "Data inserted successfully.");
                    }
                    else {
                        object.put("status", "failed");
                        object.put("message", "Data insertion failed.");
                    }
                } else {
                    object.put("status", "failed");
                    object.put("message", "You have already completed this evaluation.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*教务处*/
    public JSONObject teacherEvaluation(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            String query1 = "SELECT courseName, teacherName FROM teachingclassenter";
            ResultSet resultSet1 = dataAccessObject.executeQuery(query1);
            List<String> courseNameList = new ArrayList<>();
            List<String> teacherNameList = new ArrayList<>();

            // 处理ResultSet1，将课程名和老师名分别存入List中
            while (resultSet1.next()) {
                String courseName = resultSet1.getString("courseName");
                String teacherName = resultSet1.getString("teacherName");
                courseNameList.add(courseName);
                teacherNameList.add(teacherName);
            }
            resultSet1.close(); // 关闭 resultSet1

            Map<String, List<Integer>> cardNumberMap = new HashMap<>();

            // 获取每个课程和老师的学生卡号
            for (int i = 0; i < courseNameList.size(); i++) {
                String courseName = courseNameList.get(i);
                String teacherName = teacherNameList.get(i);
                String key = courseName + ":" + teacherName;

                String query2 = "SELECT cardNumber FROM teachingclass WHERE courseName = ? AND teacherName = ?";
                ResultSet resultSet2 = dataAccessObject.executeQuery(query2, courseName, teacherName);

                List<Integer> cardNumbers = new ArrayList<>();
                while (resultSet2.next()) {
                    Integer cardNumber = resultSet2.getInt("cardNumber");
                    cardNumbers.add(cardNumber);
                }
                resultSet2.close(); // 关闭 resultSet2

                // 将卡号列表存入map
                if (!cardNumbers.isEmpty()) {
                    cardNumberMap.put(key, cardNumbers);
                }
            }

            // 存储所有课程和老师的平均分结果
            JSONArray resultsArray = new JSONArray();

            // 遍历 cardNumberMap，为每个 key (courseName:teacherName) 和 cardNumber 计算平均分
            for (Map.Entry<String, List<Integer>> entry : cardNumberMap.entrySet()) {
                String key = entry.getKey(); // 获取 key (courseName:teacherName)
                String[] parts = key.split(":");
                String courseName = parts[0];
                String teacherName = parts[1];
                List<Integer> cardNumbers = entry.getValue(); // 获取与该课程相关的学生卡号列表

                List<Double> averageScores = new ArrayList<>();
                int numberOfQuestions = 11; // 假设有4个问题
                for (int i = 0; i < numberOfQuestions; i++) {
                    averageScores.add(0.0);
                }
                int recordCount = 0; // 记录数量

                for (Integer cardNumber : cardNumbers) {
                    // 准备查询语句获取与 courseName 和 cardNumber 相关的评分记录
                    String query = "SELECT scores FROM evaluationofteaching WHERE courseName = ? AND cardNumber = ?";
                    ResultSet resultSet = dataAccessObject.executeQuery(query, courseName, cardNumber);

                    // 处理查询结果
                    while (resultSet.next()) {
                        String scoresJson = resultSet.getString("scores");
                        JSONArray scoresArray = JSONArray.fromObject(scoresJson);
                        List<Integer> scores = JSONArray.toList(scoresArray, Integer.class);

                        // 累加各问题的分数
                        for (int i = 0; i < scores.size(); i++) {
                            averageScores.set(i, averageScores.get(i) + scores.get(i));
                        }
                        recordCount++;
                    }
                    resultSet.close(); // 关闭 resultSet
                }

                // 计算每个问题的平均分
                if (recordCount > 0) {
                    for (int i = 0; i < averageScores.size(); i++) {
                        averageScores.set(i, averageScores.get(i) / recordCount);
                    }

                    // 创建并添加到结果数组
                    JSONObject result = new JSONObject();
                    result.put("courseName", courseName);
                    result.put("teacherName", teacherName);
                    result.put("averageScores", averageScores);
                    resultsArray.add(result);
                }
            }

            // 检查是否有结果
            if (!resultsArray.isEmpty()) {
                object.put("status", "success");
                object.put("results", resultsArray);
            } else {
                object.put("status", "not_found");
                object.put("message", "No evaluations found for the specified courses.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object;
    }

    /*学生选课*/
    public JSONObject studentSelection(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");
        int row;

        //课程介绍
        List<String> courseIntroduction = new ArrayList<>();
        courseIntroduction.add("一门很好的课程");
        courseIntroduction.add("一门非常好的课程");
        courseIntroduction.add("一门很高级的课程");
        courseIntroduction.add("一门超级好的课程");
        courseIntroduction.add("一门极其好的课程");

        //qq群号映射（这里为了简便直接在后端提供qq群号） 这里的qq群号均为虚拟的模拟群号，且群号数量得大于存入的课程数
        List<Integer> qqGroups = new ArrayList<>();
        qqGroups.add(243512334);
        qqGroups.add(354243433);
        qqGroups.add(537463222);
        qqGroups.add(563234556);
        qqGroups.add(235623754);
        qqGroups.add(235654714);
        qqGroups.add(235654707);
        qqGroups.add(235654643);
        qqGroups.add(235653654);
        qqGroups.add(235234754);
        qqGroups.add(244234754);
        qqGroups.add(255234754);
        qqGroups.add(265234754);
        qqGroups.add(365234754);
        qqGroups.add(465234754);
        qqGroups.add(565234754);
        qqGroups.add(665234754);
        qqGroups.add(765234754);
        qqGroups.add(865234754);
        qqGroups.add(965234754);
        qqGroups.add(315234754);
        qqGroups.add(325234754);
        qqGroups.add(335234754);
        qqGroups.add(345234754);

        //上课星期映射
        Map<String, Integer> dayOfWeekMap = new HashMap<>();
        dayOfWeekMap.put("周一", 1);
        dayOfWeekMap.put("周二", 2);
        dayOfWeekMap.put("周三", 3);
        dayOfWeekMap.put("周四", 4);
        dayOfWeekMap.put("周五", 5);
        dayOfWeekMap.put("周六", 6);
        dayOfWeekMap.put("周日", 7);
        //开始上课节数映射
        Map<Integer, String> beginTimeMap = new HashMap<>();
        beginTimeMap.put(1, "8:00");
        beginTimeMap.put(2, "8:50");
        beginTimeMap.put(3, "9:50");
        beginTimeMap.put(4, "10:40");
        beginTimeMap.put(5, "11:30");
        beginTimeMap.put(6, "14:00");
        beginTimeMap.put(7, "14:50");
        beginTimeMap.put(8, "15:50");
        beginTimeMap.put(9, "16:40");
        beginTimeMap.put(10, "17:30");
        beginTimeMap.put(11, "18:30");
        beginTimeMap.put(12, "19:20");
        beginTimeMap.put(13, "20:10");
        //结束上课节数映射
        Map<Integer, String> endTimeMap = new HashMap<>();
        endTimeMap.put(1, "8:45");
        endTimeMap.put(2, "9:35");
        endTimeMap.put(3, "10:35");
        endTimeMap.put(4, "11:25");
        endTimeMap.put(5, "12:15");
        endTimeMap.put(6, "14:45");
        endTimeMap.put(7, "15:35");
        endTimeMap.put(8, "16:35");
        endTimeMap.put(9, "17:25");
        endTimeMap.put(10, "18:15");
        endTimeMap.put(11, "19:15");
        endTimeMap.put(12, "19:05");
        endTimeMap.put(13, "20:55");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            studentID = user.getInt("cardNumber");
            JSONArray courses = user.getJSONArray("courses");

            for (int i = 0; i < courses.size(); i++) {
                JSONObject course = courses.getJSONObject(i);
                String courseName = course.getString("name");
                String courseID = course.getString("code");
                Integer credit = course.getInt("credit");

                JSONArray teachers = course.getJSONArray("teachers");
                for (int j = 0; j < teachers.size(); j++) {
                    JSONObject teacher = teachers.getJSONObject(j);
                    String teacherName = teacher.getString("name");
                    String weekRange = teacher.getString("weekRange");
                    String classroomName = teacher.getString("classroomName");
                    Integer capacity = teacher.getInt("capacity");
                    Integer selectedCount = teacher.getInt("selectedCount");

                    JSONArray classDates = teacher.getJSONArray("classDates");
                    List<String> dayOfWeekList = new ArrayList<>();
                    List<Integer> courseBeginList = new ArrayList<>();
                    List<Integer> courseEndList = new ArrayList<>();

                    for (int k = 0; k < classDates.size(); k++) {
                        JSONObject classDate = classDates.getJSONObject(k);
                        dayOfWeekList.add(classDate.getString("dayOfWeek"));
                        courseBeginList.add(classDate.getInt("courseBegin"));
                        courseEndList.add(classDate.getInt("courseEnd"));
                    }

                    List<Integer> dayOfWeekNumberList = new ArrayList<>();
                    List<String> timeList = new ArrayList<>();
                    for (int k = 0; k < dayOfWeekList.size(); k++) {
                        dayOfWeekNumberList.add(k,dayOfWeekMap.get(dayOfWeekList.get(k)));
                        String startTime = beginTimeMap.get(courseBeginList.get(k));
                        String endTime = endTimeMap.get(courseEndList.get(k));
                        String timeRange = startTime + "-" + endTime;
                        timeList.add(timeRange);
                    }

                    String dayOfWeekJson = JSONArray.fromObject(dayOfWeekList).toString();
                    String courseBeginJson = JSONArray.fromObject(courseBeginList).toString();
                    String courseEndJson = JSONArray.fromObject(courseEndList).toString();

                    // Check if the course is already selected
                    Boolean isSelected = dataAccessObject.getIsSelected(studentID, courseName);
                    if (isSelected == null || !isSelected) {
                        // Insert into the database if not already selected
                        String insert = "INSERT INTO teachingclass(cardNumber,courseID,courseName,credit,teacherName,classroomName," +
                                "capacity,selectCount,weekRange,dayOfWeek,courseBegin,courseEnd,isSelected) VALUES (?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";
                        row = dataAccessObject.executeInsert(insert, studentID, courseID, courseName, credit, teacherName, classroomName,
                                capacity, selectedCount, weekRange, dayOfWeekJson, courseBeginJson, courseEndJson, true);

                        isSelected = dataAccessObject.getIsSelected(studentID, courseName);

                        if (row > 0 && (isSelected != null && isSelected)) {
                            object.put("status", "success");
                            object.put("message", "Data inserted successfully.");

                            // Update selectCount for the course and teacher of teachingclass
                            String updateSelectCount = "UPDATE teachingclass SET selectCount = ? WHERE courseName = ? AND teacherName = ?";
                            dataAccessObject.executeUpdate(updateSelectCount, selectedCount, courseName, teacherName);

                            // Update selectCount for the course and teacher of teachingclassenter
                            String updateSelectCount1 = "UPDATE teachingclassenter SET selectCount = ? WHERE courseName = ? AND teacherName = ?";
                            dataAccessObject.executeUpdate(updateSelectCount1, selectedCount, courseName, teacherName);

                            for(int m=0;m<dayOfWeekList.size();m++) {
                                // Insert partial data into another table, e.g., studentCourseSelection
                                String insertIntoCourse = "INSERT INTO course(cardNumber,courseName, classroomName,classDate," +
                                        "courseBegin,courseEnd) VALUES (?, ?, ?, ?,?,?)";
                                int row2 = dataAccessObject.executeInsert(insertIntoCourse, studentID,courseName, classroomName,dayOfWeekNumberList.get(m),
                                        courseBeginList.get(m), courseEndList.get(m));

                                if (row2 > 0) {
                                    object.put("extraMessage", "Data also inserted into course table.");
                                } else {
                                    object.put("extraMessage", "Failed to insert data into course table.");
                                }
                            }

                            String dayOfWeekNumberJson = JSONArray.fromObject(dayOfWeekNumberList).toString();
                            String timeRangeJson = JSONArray.fromObject(timeList).toString();

                            String insertIntoSelectCourse = "INSERT INTO studentselectclass(cardNumber,courseName,teacherName,qqGroup, classroomName,duration," +
                                    "week,time,introduction) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            int row3 = dataAccessObject.executeInsert(insertIntoSelectCourse, studentID, courseName, teacherName, qqGroups.get(qq), classroomName, weekRange, dayOfWeekNumberJson,
                                    timeRangeJson, courseIntroduction.get(qq));

                            if (row3 > 0) {
                                object.put("extraMessage_2", "Data also inserted into studentselectclass table.");
                                qq++;
                            } else {
                                object.put("extraMessage_2", "Failed to insert data into studentselectclass table.");
                            }
                        } else {
                            object.put("status", "failed");
                            object.put("message", "Data insertion failed.");
                        }
                    } else {
                        object.put("status", "failed");
                        object.put("message", "You have already selected the course.");
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*学生退课*/
    public JSONObject studentDeseclection(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            studentID = user.getInt("cardNumber");
            String courseName = user.getString("courseName");

            String selectQuery = "SELECT teacherName, selectCount FROM teachingclass WHERE cardNumber = ? AND courseName = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(selectQuery, studentID,courseName);
            while(resultSet.next()) {
                int currentSelectCount = resultSet.getInt("selectCount");
                String teacherName = resultSet.getString("teacherName");
                // 删除对应的数据库记录
                String deleteQuery = "DELETE FROM teachingclass WHERE cardNumber = ? AND courseName = ?";
                int row = dataAccessObject.executeUpdate(deleteQuery, studentID, courseName);

                String deleteQuery1 = "DELETE FROM studentselectclass WHERE cardNumber = ? AND courseName = ?";
                int row1 = dataAccessObject.executeUpdate(deleteQuery1, studentID, courseName);

                String deleteQuery2 = "DELETE FROM course WHERE cardNumber = ? AND courseName = ?";
                int row2 = dataAccessObject.executeUpdate(deleteQuery2, studentID, courseName);

                String deleteQuery3 = "DELETE FROM evaluationofteaching WHERE cardNumber = ? AND courseName = ?";
                int row3 = dataAccessObject.executeUpdate(deleteQuery3, studentID, courseName);

                if (row > 0&& row1>0 && row2>0 && row3>0) {
                    // 更新 selectCount
                    String updateQuery = "UPDATE teachingclass SET selectCount = ? WHERE courseName = ? AND teacherName = ?";
                    String updateQuery1 = "UPDATE teachingclassenter SET selectCount = ? WHERE courseName = ? AND teacherName = ?";
                    int newSelectCount = currentSelectCount - 1;
                    dataAccessObject.executeUpdate(updateQuery, newSelectCount, courseName,teacherName);
                    dataAccessObject.executeUpdate(updateQuery1, newSelectCount, courseName,teacherName);
                    object.put("status", "success");
                    object.put("message", "Data deleted successfully.");
                } else {
                    object.put("status", "failed");
                    object.put("message", "No matching record found to delete.");
                }

            }

            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*发送选课内容*/
    public JSONObject sendSelection(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            studentID = user.getInt("cardNumber");
            String courseName = user.getString("courseName");

            // 准备查询语句获取选课人数
            String query1 = "SELECT teacherName,selectCount FROM teachingclass WHERE courseName = ?";
            ResultSet resultSet1 = dataAccessObject.executeQuery(query1,courseName);

            // 准备查询语句获取选课记录
            String query = "SELECT teacherName, selectCount,isSelected FROM teachingclass WHERE cardNumber = ? AND courseName = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, studentID,courseName);

            if (!resultSet.isBeforeFirst()) { // 检查是否有返回结果
                JSONArray coursesArray1 = new JSONArray();
                Set<String> teacherNames = new HashSet<>();
                while(resultSet1.next()){
                    String teacherName = resultSet1.getString("teacherName");
                    if(!teacherNames.contains(teacherName)) {
                        // 提取课程信息
                        JSONObject courseObject1 = new JSONObject();
                        courseObject1.put("courseName", courseName);
                        courseObject1.put("teacherName", resultSet1.getString("teacherName"));
                        courseObject1.put("selectCount", resultSet1.getInt("selectCount"));

                        coursesArray1.add(courseObject1);
                        teacherNames.add(teacherName);
                    }
                }
                object.put("status", "not_found");
                object.put("selected", coursesArray1);
                object.put("message", "No course found for the student.");
            } else {
                JSONArray coursesArray = new JSONArray();
                while(resultSet.next()){
                    object.put("status", "success");
                    // 提取课程信息
                    JSONObject courseObject = new JSONObject();
                    courseObject.put("courseName", courseName);
                    courseObject.put("teacherName", resultSet.getString("teacherName"));
                    courseObject.put("selectCount", resultSet.getInt("selectCount"));
                    courseObject.put("isSelected", resultSet.getBoolean("isSelected"));

                    coursesArray.add(courseObject);
                }
                object.put("selected", coursesArray);

                resultSet1.close();
                resultSet.close(); // 关闭 ResultSet
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*教务添加选课*/
    public JSONObject addCourse(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            String courseName = user.getString("courseName");
            String courseID = user.getString("courseId");
            Double credit = user.getDouble("credit");
            String teacherName = user.getString("teacherName");
            String weekRange = user.getString("weekRange");
            String classroomName = user.getString("classroom");
            Integer capacity = user.getInt("capacity");

            JSONArray classDates = user.getJSONArray("classDates");
            List<String> dayOfWeekList = new ArrayList<>();
            List<Integer> courseBeginList = new ArrayList<>();
            List<Integer> courseEndList = new ArrayList<>();

            for (int k = 0; k < classDates.size(); k++) {
                JSONObject classDate = classDates.getJSONObject(k);
                dayOfWeekList.add(classDate.getString("dayOfWeek"));
                courseBeginList.add(classDate.getInt("courseBegin"));
                courseEndList.add(classDate.getInt("courseEnd"));
            }

            // 检查教室和周范围冲突
            String conflictQuery = "SELECT weekRange, dayOfWeek, courseBegin, courseEnd, classroomName FROM teachingclassenter";
            ResultSet conflictResultSet = dataAccessObject.executeQuery(conflictQuery);

            boolean roomConflict = false;
            boolean timeConflict = false;

            while (conflictResultSet.next()) {
                String existingWeekRange = conflictResultSet.getString("weekRange");
                String existingDayOfWeekJson = conflictResultSet.getString("dayOfWeek");
                String existingCourseBeginJson = conflictResultSet.getString("courseBegin");
                String existingCourseEndJson = conflictResultSet.getString("courseEnd");
                String existingClassroomName = conflictResultSet.getString("classroomName");

                JSONArray existingDayOfWeekArray = JSONArray.fromObject(existingDayOfWeekJson);
                JSONArray existingCourseBeginArray = JSONArray.fromObject(existingCourseBeginJson);
                JSONArray existingCourseEndArray = JSONArray.fromObject(existingCourseEndJson);

                // 判断周范围是否有重叠
                if (overlapWeekRange(weekRange, existingWeekRange)) {
                    // 判断星期几是否有重叠
                    if (overlapDayOfWeek(dayOfWeekList, existingDayOfWeekArray)) {
                        // 判断开始和结束时间是否有重叠
                        if (overlapTime(courseBeginList, courseEndList, existingCourseBeginArray, existingCourseEndArray)) {
                            // 判断教室是否有冲突
                            if (classroomName.equals(existingClassroomName)) {
                                object.put("status", "room_conflict");
                                object.put("message", "Classroom conflict detected: The classroom is already occupied during the specified week range.");
                                roomConflict = true;
                                break;
                            }
                        }
                    }
                }
            }

            if(!roomConflict){
                if(timeConflict){
                    object.put("status", "time_conflict");
                    object.put("message", "Time conflict detected.");
                }else{
                    String insert = "INSERT INTO teachingclassenter(courseID, courseName, credit, teacherName, classroomName, " +
                            "capacity, weekRange, dayOfWeek, courseBegin, courseEnd) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    int row = dataAccessObject.executeInsert(insert, courseID, courseName, credit, teacherName, classroomName,
                            capacity, weekRange, JSONArray.fromObject(dayOfWeekList).toString(), JSONArray.fromObject(courseBeginList).toString(), JSONArray.fromObject(courseEndList).toString());

                    if (row > 0) {
                        object.put("status", "success");
                        object.put("message", "Data inserted successfully.");
                    } else {
                        object.put("status", "failed");
                        object.put("message", "Data insertion failed.");
                    }
                }
            }

            conflictResultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    // 判断两个周范围是否重叠
    private boolean overlapWeekRange(String newRange, String existingRange) {
        // 提取周范围的起始和结束周
        int[] newRangeBounds = parseWeekRange(newRange);
        int[] existingRangeBounds = parseWeekRange(existingRange);

        int newStart = newRangeBounds[0];
        int newEnd = newRangeBounds[1];
        int existingStart = existingRangeBounds[0];
        int existingEnd = existingRangeBounds[1];

        // 判断是否重叠
        return (newStart <= existingEnd && newEnd >= existingStart);
    }

    // 将周范围字符串解析为起始和结束周的数组
    private int[] parseWeekRange(String weekRange) {
        String[] parts = weekRange.replace("周", "").split("-");
        int startWeek = Integer.parseInt(parts[0]);
        int endWeek = Integer.parseInt(parts[1]);
        return new int[] { startWeek, endWeek };
    }

    // 辅助方法：判断星期几是否重叠
    private boolean overlapDayOfWeek(List<String> newDays, JSONArray existingDays) {
        // 判断新课程的星期几是否与现有课程的星期几有重叠
        return !Collections.disjoint(newDays, existingDays);
    }

    // 辅助方法：判断时间是否重叠
    private boolean overlapTime(List<Integer> newBegin, List<Integer> newEnd, JSONArray existingBegin, JSONArray existingEnd) {
        // 判断时间是否重叠
        for (int i = 0; i < newBegin.size(); i++) {
            int newStart = newBegin.get(i);
            int newFinish = newEnd.get(i);
            for (int j = 0; j < existingBegin.size(); j++) {
                int existingStart = existingBegin.getInt(j);
                int existingFinish = existingEnd.getInt(j);
                if (newStart <= existingFinish && newFinish >= existingStart) {
                    return true;
                }
            }
        }
        return false;
    }

    /*发送选课信息*/
    public JSONObject sendSelectionBefore(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 准备查询语句获取 teachingclassenter 表中的所有信息
            String query = "SELECT * FROM teachingclassenter";
            ResultSet resultSet = dataAccessObject.executeQuery(query);

            // 检查查询结果是否为空
            if (!resultSet.isBeforeFirst()) {
                object.put("status", "not_found");
                object.put("message", "No courses found in teachingclassenter.");
            } else {

                // 创建用于存储课程信息的 Map，按 courseID 分组
                Map<String, JSONObject> courseMap = new HashMap<>();

                // 处理查询结果
                while (resultSet.next()) {
                    String courseID = resultSet.getString("courseID");

                    // 如果该课程已经存在，则将老师信息合并
                    if (courseMap.containsKey(courseID)) {
                        JSONObject courseObject = courseMap.get(courseID);
                        JSONArray teacherArray = courseObject.getJSONArray("teachers");

                        // 添加新的老师信息
                        JSONObject teacherObject = new JSONObject();
                        teacherObject.put("teacherName", resultSet.getString("teacherName"));
                        teacherObject.put("classroomName", resultSet.getString("classroomName"));
                        teacherObject.put("capacity", resultSet.getInt("capacity"));
                        teacherObject.put("weekRange", resultSet.getString("weekRange"));
                        teacherObject.put("dayOfWeek", resultSet.getString("dayOfWeek"));
                        teacherObject.put("courseBegin", resultSet.getString("courseBegin"));
                        teacherObject.put("courseEnd", resultSet.getString("courseEnd"));
                        teacherObject.put("selectCount", resultSet.getInt("selectCount"));

                        teacherArray.add(teacherObject);
                    } else {
                        // 如果该课程还不存在，创建新的课程对象并添加到 map 中
                        JSONObject courseObject = new JSONObject();
                        courseObject.put("courseID", courseID);
                        courseObject.put("courseName", resultSet.getString("courseName"));
                        courseObject.put("credit", resultSet.getDouble("credit"));

                        // 创建老师信息数组
                        JSONArray teacherArray = new JSONArray();
                        JSONObject teacherObject = new JSONObject();
                        teacherObject.put("teacherName", resultSet.getString("teacherName"));
                        teacherObject.put("classroomName", resultSet.getString("classroomName"));
                        teacherObject.put("capacity", resultSet.getInt("capacity"));
                        teacherObject.put("weekRange", resultSet.getString("weekRange"));
                        teacherObject.put("dayOfWeek", resultSet.getString("dayOfWeek"));
                        teacherObject.put("courseBegin", resultSet.getString("courseBegin"));
                        teacherObject.put("courseEnd", resultSet.getString("courseEnd"));
                        teacherObject.put("selectCount", resultSet.getInt("selectCount"));

                        teacherArray.add(teacherObject);
                        courseObject.put("teachers", teacherArray);

                        courseMap.put(courseID, courseObject);
                    }
                }

                // 将 Map 转为 JSONArray
                JSONArray coursesArray = new JSONArray();
                coursesArray.addAll(courseMap.values());

                object.put("status", "success");
                object.put("courses", coursesArray);

                resultSet.close(); // 关闭 ResultSet
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*发送待接收成绩的课程*/
    public JSONObject sendGradeCourse(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 准备查询语句获取课程记录
            String query = "SELECT courseName FROM teachingclassenter"; // 使用 DISTINCT 只查询唯一的课程名
            ResultSet resultSet = dataAccessObject.executeQuery(query);

            if (!resultSet.isBeforeFirst()) { // 检查是否有返回结果
                object.put("status", "not_found");
                object.put("message", "No course.");
            } else {
                JSONArray coursesArray = new JSONArray();
                Set<String> courseNamesSet = new HashSet<>(); // 使用 Set 存储课程名以避免重复

                while (resultSet.next()) {
                    String courseName = resultSet.getString("courseName");

                    if (!courseNamesSet.contains(courseName)) {
                        // 添加到 Set 中，确保不重复
                        courseNamesSet.add(courseName);

                        // 创建课程信息对象
                        JSONObject courseObject = new JSONObject();
                        courseObject.put("courseName", courseName);

                        coursesArray.add(courseObject);
                    }
                }

                object.put("status", "success");
                object.put("selected", coursesArray);

                resultSet.close(); // 关闭 ResultSet
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    /*教师查看课程*/
    public JSONObject teacherForCourse(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            String teacherName = user.getString("teacherName");

            // 准备查询语句获取课表记录
            String query = "SELECT courseName, classroomName, capacity, weekRange, dayOfWeek, courseBegin, courseEnd, selectCount FROM teachingclassenter WHERE teacherName = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(query, teacherName);

            if (!resultSet.isBeforeFirst()) { // 检查是否有返回结果
                object.put("status", "not_found");
                object.put("message", "No course found for the teaching.");
            } else {
                JSONArray coursesArray = new JSONArray();
                while (resultSet.next()) {
                    object.put("status", "success");

                    // 提取课程信息
                    JSONObject courseObject = new JSONObject();
                    String courseName = resultSet.getString("courseName");
                    courseObject.put("courseName", courseName);
                    courseObject.put("classroomName", resultSet.getString("classroomName"));
                    courseObject.put("capacity", resultSet.getInt("capacity"));
                    courseObject.put("weekRange", resultSet.getString("weekRange"));

                    // 将 dayOfWeek, courseBegin, courseEnd 放入数组中
                    JSONArray dayOfWeekArray = new JSONArray();
                    JSONArray courseBeginArray = new JSONArray();
                    JSONArray courseEndArray = new JSONArray();

                    // 仅调用一次 resultSet.next() 获取数据
                    dayOfWeekArray.add(resultSet.getString("dayOfWeek"));
                    courseBeginArray.add(resultSet.getString("courseBegin"));
                    courseEndArray.add(resultSet.getString("courseEnd"));

                    // 将 dayOfWeek, courseBegin, courseEnd 合并为 courseDate
                    JSONArray courseDateArray = new JSONArray();
                    for (int i = 0; i < dayOfWeekArray.size(); i++) {
                        JSONArray courseEntry = new JSONArray();
                        courseEntry.add(dayOfWeekArray.get(i));
                        courseEntry.add(courseBeginArray.get(i));
                        courseEntry.add(courseEndArray.get(i));

                        // 将组合好的 courseEntry 放入 courseDateArray
                        courseDateArray.add(courseEntry);
                    }

                    // 最终将 courseDateArray 放入 courseObject 中
                    courseObject.put("courseDate", courseDateArray);
                    courseObject.put("selectCount", resultSet.getInt("selectCount"));

                    // 通过 teacherName 和 courseName 查询学生信息
                    String studentQuery = "SELECT cardNumber FROM studentselectclass WHERE teacherName = ? AND courseName = ?";
                    ResultSet studentResultSet = dataAccessObject.executeQuery(studentQuery, teacherName, courseName);

                    JSONArray studentArray = new JSONArray();
                    while (studentResultSet.next()) {
                        String cardNumber = studentResultSet.getString("cardNumber");

                        // 通过 cardNumber 查询 user 表中的学生姓名
                        String userQuery = "SELECT name FROM user WHERE cardNumber = ?";
                        ResultSet userResultSet = dataAccessObject.executeQuery(userQuery, cardNumber);

                        if (userResultSet.next()) {
                            JSONObject studentObject = new JSONObject();
                            studentObject.put("cardNumber", cardNumber);
                            studentObject.put("name", userResultSet.getString("name"));
                            studentArray.add(studentObject);
                        }
                        userResultSet.close(); // 关闭查询结果集
                    }
                    studentResultSet.close(); // 关闭学生查询结果集

                    // 将学生信息添加到 courseObject 中
                    courseObject.put("students", studentArray);

                    // 将 courseObject 加入 coursesArray
                    coursesArray.add(courseObject);
                }
                object.put("teacher", coursesArray);

                resultSet.close(); // 关闭 ResultSet
            }

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

}