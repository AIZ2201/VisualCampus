package com.output;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class FaceCompare {

    public static final Gson json = new Gson();

    /*public static void main(String[] args) {

        int port = 4445; // 端口号

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                     BufferedReader br = new BufferedReader(isr);
                     OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
                     BufferedWriter rw = new BufferedWriter(osw)) {

                    System.out.println("Client connected");

                    while (true) {
                        String line = br.readLine();
                        if (line == null) {
                            System.out.println("客户端断开连接");
                            break;
                        }
                        StringBuilder sb = new StringBuilder(line);
                        while (!line.trim().endsWith("}")) {
                            line = br.readLine();
                            sb.append(line);
                        }
                        // 假设从客户端接收到的数据
                        JsonObject clientJson = json.fromJson(String.valueOf(sb), JsonObject.class);
                        int cardNumber = clientJson.get("cardNumber").getAsInt();
                        var face = new WebFaceCompare(
                                "https://api.xf-yun.com/v1/private/s67c9c78c",
                                "f07f0eac",  //请填写控制台获取的APPID,
                                "NjkxZmE4ZmY1M2FiMTVlNWM1YzIyMTU5",  //请填写控制台获取的APISecret
                                "2bf75dd780c22a884a93738b1faf813f",  //请填写控制台获取的APIKey
                                null,
                                null,
                                "s67c9c78c",
                                cardNumber
                        );

                        try {
                            // 加载MySQL JDBC驱动程序
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            var resp =face.doRequest();
                            //System.out.println("接口返回结果："+resp);
                            ResponseData respData = json.fromJson(resp, ResponseData.class);
                            String textBase64 = "";
                            if (respData.getPayLoad().getFaceCompareResult() != null) {
                                textBase64 = respData.getPayLoad().getFaceCompareResult().getText();
                                String text = new String(Base64.getDecoder().decode(textBase64));
                                System.out.println("人脸比对结果(text)base64解码后：");
                                System.out.println(text);

                                // 使用 Gson 解析 JSON 字符串
                                Gson gson = new Gson();
                                JsonObject jsonObject = gson.fromJson(text, JsonObject.class);

                                // 提取 "score" 字段的值
                                if (jsonObject.has("score")) {
                                    double score = jsonObject.get("score").getAsDouble();
                                    System.out.println("提取的score值为: " + score);
                                    // 将处理结果发送回客户端
                                    if(score>=0.70) {
                                        JsonObject result = new JsonObject();
                                        result.addProperty("status", true);
                                        rw.write(result.toString() + "\n");
                                        System.out.println("响应已发送给客户端: " + result);
                                    }else{
                                        JsonObject result = new JsonObject();
                                        result.addProperty("status", false);
                                        rw.write(result.toString() + "\n");
                                    }
                                    rw.flush();
                                } else {
                                    System.out.println("JSON中没有score字段");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/

    // 将 cardNumber 作为参数传入
    public static double compareFace(int cardNumber) {
        try {
            // 创建人脸比对对象，传入 cardNumber
            var face = new FaceCompare(
                    "https://api.xf-yun.com/v1/private/s67c9c78c",
                    "f07f0eac",  //请填写控制台获取的APPID,
                    "NjkxZmE4ZmY1M2FiMTVlNWM1YzIyMTU5",  //请填写控制台获取的APISecret
                    "2bf75dd780c22a884a93738b1faf813f",  //请填写控制台获取的APIKey
                    null,
                    null,
                    "s67c9c78c",
                    cardNumber
            );

            // 发送人脸比对请求
            var resp = face.doRequest();
            ResponseData respData = new Gson().fromJson(resp, ResponseData.class);
            String textBase64 = "";

            if (respData.getPayLoad().getFaceCompareResult() != null) {
                textBase64 = respData.getPayLoad().getFaceCompareResult().getText();
                String text = new String(Base64.getDecoder().decode(textBase64));
                System.out.println("人脸比对结果(text)base64解码后：");
                System.out.println(text);

                // 使用 Gson 解析 JSON 字符串
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(text, JsonObject.class);

                // 提取 "score" 字段的值
                if (jsonObject.has("score")) {
                    double score = jsonObject.get("score").getAsDouble();
                    System.out.println("提取的score值为: " + score);
                    return score; // 返回比对的 score 值
                } else {
                    System.out.println("JSON中没有score字段");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // 若发生错误，返回 -1
    }

    private String requestUrl;
    private String apiSecret;
    private String apiKey;
    private String imagePath1;
    private String imagePath2;
    private String appid;
    private String serviceId;
    private Integer cardNumber;

    static String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=true";
    static String username = "root";
    static String password = "mlyy720813";

    public FaceCompare(String requestUrl, String appid, String apiSecret, String apiKey, String imagePath1, String imagePath2, String serviceId,Integer cardNumber) {
        this.requestUrl = requestUrl;
        this.appid = appid;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.imagePath1 = imagePath1;
        this.imagePath2 = imagePath2;
        this.serviceId = serviceId;
        this.cardNumber = cardNumber;
    }

    // 构建url
    public String buildRequetUrl() {
        return assembleRequestUrl(this.requestUrl, this.apiKey, this.apiSecret);
    }

    // 从数据库1读取image数据
    private byte[] readImage1(int cardNumber) throws Exception {


        // 使用 try-with-resources 语法来自动关闭资源
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement("SELECT image_data FROM images_table WHERE cardNumber = ?")) {

            pstmt.setInt(1, cardNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("image_data"); // 返回数据库中的图片数据
                } else {
                    throw new Exception("No image found for cardNumber: " + cardNumber);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Database error: " + e.getMessage(), e);
        }
    }

    // 从数据库2读取image数据
    private byte[] readImage2(int cardNumber) throws Exception {

        // 使用 try-with-resources 语法来自动关闭资源
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement("SELECT image FROM image_from_client WHERE cardNumber = ?")) {

            pstmt.setInt(1, cardNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("image"); // 返回数据库中的图片数据
                } else {
                    throw new Exception("No image found for cardNumber: " + cardNumber);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Database error: " + e.getMessage(), e);
        }
    }

   /* private byte[] readImage(String imagePath) throws IOException {
        InputStream is = new FileInputStream(imagePath);
        return is.readAllBytes();
    }*/

    //构建参数
    private String  buildParam() throws Exception {
        var req = new JsonObject();

        //平台参数
        var header = new JsonObject();
        header.addProperty("app_id",appid);
        header.addProperty("status",3);

        //功能参数
        var parameter = new JsonObject();
        var inputAcp = new JsonObject();
        var result = new JsonObject();
        inputAcp.addProperty("service_kind","face_compare");//face_compare:人脸1:1比对
        //构建face_detect_result段参数
        result.addProperty("encoding","utf8");
        result.addProperty("compress","raw");
        result.addProperty("format","json");
        inputAcp.add("face_compare_result",result);//face_detect_result
        parameter.add(this.serviceId,inputAcp);

        //请求数据
        var payload = new JsonObject();
        var payloadImage1 = new JsonObject();
        payloadImage1.addProperty("encoding","jpg"); //jpg:jpg格式,jpeg:jpeg格式,png:png格式,bmp:bmp格式
        payloadImage1.addProperty("status",3);   //3:一次性传完
        payloadImage1.addProperty("image", Base64.getEncoder().encodeToString(readImage1(cardNumber))); //图像数据，base64
        payload.add("input1",payloadImage1);
        var payloadImage2 = new JsonObject();
        payloadImage2.addProperty("encoding","jpg"); //jpg:jpg格式,jpeg:jpeg格式,png:png格式,bmp:bmp格式
        payloadImage2.addProperty("status",3);   //3:一次性传完
        payloadImage2.addProperty("image", Base64.getEncoder().encodeToString(readImage2(cardNumber))); //图像数据，base64
        payload.add("input2",payloadImage2);

        req.add("header",header);
        req.add("parameter",parameter);
        req.add("payload",payload);
        return req.toString();
    }

    private String makeRequest() throws Exception {
        String url = buildRequetUrl();
        System.out.println("url=>" + url);
        var realUrl = new URL(url);
        var connection = realUrl.openConnection();
        var httpURLConnection = (HttpURLConnection) connection;
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-type","application/json");

        var out = httpURLConnection.getOutputStream();
        var params = buildParam();
        //System.out.println("参数=>"+params);
        out.write(params.getBytes());
        out.flush();
        InputStream is = null;
        try{
            is = httpURLConnection.getInputStream();
            //System.out.println("code is "+httpURLConnection.getResponseCode()+";"+"message is "+httpURLConnection.getResponseMessage());
        }catch (Exception e){
            is = httpURLConnection.getErrorStream();
            var resp = is.readAllBytes();
            throw new Exception("make request error:"+"code is "+httpURLConnection.getResponseCode()+";"+httpURLConnection.getResponseMessage()+new String(resp));
        }
        var resp = is.readAllBytes();
        return new String(resp);
    }

    public String doRequest() throws Exception {
        return this.makeRequest();
    }

    //构建url
    public static String assembleRequestUrl(String requestUrl, String apiKey, String apiSecret) {
        URL url = null;
        // 替换调schema前缀 ，原因是URL库不支持解析包含ws,wss schema的url
        String  httpRequestUrl = requestUrl.replace("ws://", "http://").replace("wss://","https://" );
        try {
            url = new URL(httpRequestUrl);
            //获取当前日期并格式化
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = format.format(new Date());
            //System.out.println("date=>" + date);

            String host = url.getHost();
            StringBuilder builder = new StringBuilder("host: ").append(host).append("\n").//
                    append("date: ").append(date).append("\n").//
                    append("POST ").append(url.getPath()).append(" HTTP/1.1");
            //System.out.println("builder=>" + builder);
            Charset charset = Charset.forName("UTF-8");
            Mac mac = Mac.getInstance("hmacsha256");
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
            mac.init(spec);
            byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
            String sha = Base64.getEncoder().encodeToString(hexDigits);
            //System.out.println("sha=>" + sha);

            String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
            //System.out.println("authorization=>" + authorization);
            String authBase = Base64.getEncoder().encodeToString(authorization.getBytes(charset));
            //System.out.println("authBase=>" + authBase);
            return String.format("%s?authorization=%s&host=%s&date=%s", requestUrl, URLEncoder.encode(authBase), URLEncoder.encode(host), URLEncoder.encode(date));
        } catch (Exception e) {
            throw new RuntimeException("assemble requestUrl error:"+e.getMessage());
        }
    }

    public static class ResponseData {
        private Header header;
        private PayLoad payload;
        public Header getHeader() {
            return header;
        }
        public PayLoad getPayLoad() {
            return payload;
        }
    }
    public static class Header {
        private int code;
        private String message;
        private String sid;
        public int getCode() {
            return code;
        }
        public String getMessage() {
            return message;
        }
        public String getSid() {
            return sid;
        }
    }
    public static class PayLoad {
        private FaceResult face_compare_result;
        public FaceResult getFaceCompareResult() {
            return face_compare_result;
        }
    }
    public static class FaceResult {
        private String compress;
        private String encoding;
        private String format;
        private String text;
        public String getCompress() {
            return compress;
        }
        public String getEncoding() {
            return encoding;
        }
        public String getFormat() {
            return format;
        }
        public String getText() {
            return text;
        }
    }
}


