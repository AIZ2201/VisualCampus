import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GptTest {

    private static final String API_KEY = "sk-1M6xwOv2F7ZWN97TmjN8XjB44eFMDQE6HaaxwSNPJZNkMFIk";
    private static final String BASE_URL = "https://api.chatanywhere.tech/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4445)) {
            System.out.println("服务器正在监听端口 4445...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("新客户端连接：" + clientSocket.getRemoteSocketAddress());
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("服务器异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        // 定期检查客户端连接是否活跃
        ScheduledExecutorService heartbeatService = Executors.newSingleThreadScheduledExecutor();
        heartbeatService.scheduleAtFixedRate(() -> {
            if (!clientSocket.isConnected() || clientSocket.isClosed()) {
                System.out.println("客户端连接已关闭或不可用，停止心跳检查");
                heartbeatService.shutdown(); // 关闭心跳机制
            } else {
                System.out.println("客户端连接仍然活跃");
            }
        }, 30, 30, TimeUnit.SECONDS);

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", "You are a helpful assistant."));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("接收到消息：" + inputLine);

                if ("exit".equalsIgnoreCase(inputLine.trim())) {
                    break; // 客户端请求退出
                }

                // 处理其他消息
                messages.add(new Message("user", inputLine));
                String response = callOpenAiApi(messages);
                System.out.println("模型输出：" + response);
                messages.add(new Message("assistant", response));
                out.println(response); // 替换换行符以适应客户端
            }
        } catch (IOException e) {
            System.err.println("处理客户端时发生错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                heartbeatService.shutdown(); // 关闭心跳机制
            } catch (IOException e) {
                System.err.println("关闭客户端连接时发生错误：" + e.getMessage());
            }
        }
    }

    public static String callOpenAiApi(List<Message> messages) throws IOException {
        String requestBody = objectMapper.writeValueAsString(new OpenAiRequest("gpt-4o", messages, 0.8, 0.8));
        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            // 合并多行内容
            StringBuilder combinedContent = new StringBuilder();
            for (JsonNode choice : jsonNode.get("choices")) {
                combinedContent.append(choice.get("message").get("content").asText().replace("\n", " "));
            }
            return combinedContent.toString();
        }
    }

    static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    static class OpenAiRequest {
        private String model;
        private List<Message> messages;
        private double temperature;
        private double topP;

        public OpenAiRequest(String model, List<Message> messages, double temperature, double topP) {
            this.model = model;
            this.messages = messages;
            this.temperature = temperature;
            this.topP = topP;
        }

        public String getModel() {
            return model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getTopP() {
            return topP;
        }
    }
}






