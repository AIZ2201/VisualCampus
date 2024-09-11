package com.output;

import DataSQL.DataAccessObject;
import net.sf.json.JSONObject;

import java.sql.SQLException;
import java.util.Base64;

public class ImageToDatabase {

    public JSONObject faceImageCompare(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");
        int row;
        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            Integer studentID = user.getInt("cardNumber");
            String encodedImage = user.getString("image");

            // 将 Base64 图片解码为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(encodedImage);

            try {
                // 插入语句，记录评教结果
                String insert = "INSERT INTO image_from_client(cardNumber, image) VALUES (?, ?)";
                row = dataAccessObject.executeInsert(insert, studentID,imageBytes);

                if(row>0)
                {
                    object.put("message", "Data inserted successfully.");
                    double score = FaceCompare.compareFace(studentID);
                    if(score>0.65)
                    {
                        object.put("status", "success");
                    }else{
                        object.put("status", "failed");
                    }

                }
                else {
                    object.put("status", "failed");
                    object.put("message", "Data insertion failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
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

