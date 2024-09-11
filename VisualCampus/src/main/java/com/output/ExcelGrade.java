package com.output;

import DataSQL.DataAccessObject;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Base64;

public class ExcelGrade {

    // 处理客户输入，包括操作和文件上传
    public JSONObject handleClientInput(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            String fileName = user.getString("fileName");
            long fileSize = user.getLong("fileSize");
            String fileContentBase64 = user.getString("fileContent");

            // 文件存储路径
            String filePath = "D://grade_table/" + fileName;

            // 解码文件并保存到本地
            saveBase64File(fileContentBase64, filePath);

            // 解析 Excel 文件并将数据存入数据库
            processExcelFile(filePath);

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }
        return object;
    }

    // 将 Base64 编码的文件内容保存到指定路径
    private void saveBase64File(String base64Content, String filePath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            // 解码 Base64 文件内容
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);

            // 保存解码后的文件内容
            fileOutputStream.write(decodedBytes);
            System.out.println("File saved successfully to: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 解析 Excel 文件并将数据插入数据库
    private void processExcelFile(String filePath) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String jdbcURL = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=true";
        String username = "root";
        String password = "mlyy720813";

        try (InputStream inputStream = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
             Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {

            Sheet sheet = workbook.getSheetAt(0);
            String sql = "INSERT INTO grade (cardNumber, courseName, credit, grade, regular_grade, midterm_grade, final_grade) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // 跳过标题行

                    Cell cardNumberCell = row.getCell(0);
                    String cardNumberString = cardNumberCell != null ? cardNumberCell.getStringCellValue().trim() : "";
                    if (cardNumberString.isEmpty()) {
                        System.err.println("Empty or invalid card number at row " + (row.getRowNum() + 1));
                        continue; // 跳过当前行或根据需求处理
                    }

                    int cardNumber;
                    try {
                        cardNumber = Integer.parseInt(cardNumberString);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid card number format at row " + (row.getRowNum() + 1));
                        continue; // 跳过当前行或根据需求处理
                    }

                    String courseName = row.getCell(1).getStringCellValue();
                    double credit = row.getCell(2).getNumericCellValue();
                    int grade = (int) row.getCell(3).getNumericCellValue();
                    int regularGrade = (int) row.getCell(4).getNumericCellValue();
                    int midtermGrade = (int) row.getCell(5).getNumericCellValue();
                    int finalGrade = (int) row.getCell(6).getNumericCellValue();

                    statement.setInt(1, cardNumber);
                    statement.setString(2, courseName);
                    statement.setDouble(3, credit);
                    statement.setInt(4, grade);
                    statement.setInt(5, regularGrade);
                    statement.setInt(6, midtermGrade);
                    statement.setInt(7, finalGrade);
                    statement.addBatch();
                }

                statement.executeBatch();
                System.out.println("Data has been imported successfully.");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
