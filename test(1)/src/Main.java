import com.output.User;
import com.output.login_page;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        login_page login = new login_page();

        // 创建一个函数映射表
        Map<String, Function<JSONObject, JSONObject>> functionMap = new HashMap<>();
        functionMap.put("login_submit", login::login_submit);

        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            System.out.println("服务器已启动，等待连接...");

            while (true) {  // 循环等待客户端连接
                Socket socket = serverSocket.accept();
                System.out.println("客户端已连接");

                // 为每个客户端创建一个新的线程来处理请求
                new ClientHandler(socket, functionMap).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// ClientHandler 类，用于处理客户端请求
class ClientHandler extends Thread {
    private final Socket socket;
    private final Map<String, Function<JSONObject, JSONObject>> functionMap;

    public ClientHandler(Socket socket, Map<String, Function<JSONObject, JSONObject>> functionMap) {
        this.socket = socket;
        this.functionMap = functionMap;
    }

    @Override
    public void run() {
        try (InputStreamReader isr = new InputStreamReader(socket.getInputStream());
             BufferedReader br = new BufferedReader(isr);
             OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
             BufferedWriter rw = new BufferedWriter(osw)) {

            // 读取客户端发送的数据
            String str = br.readLine();
            JSONObject object = JSONObject.fromObject(str);
            System.out.println("接收到的数据: " + object);

            // 处理客户端请求
            String operation = object.getString("operation");
            Function<JSONObject, JSONObject> function = functionMap.get(operation);
            JSONObject result;
            if (function != null) {
                result = function.apply(object);
            } else {
                result = new JSONObject();
                result.put("status", "error");
                result.put("message", "无效的操作: " + operation);
            }

            // 将处理结果发送回客户端
            rw.write(result.toString() + "\n");
            rw.flush();

            System.out.println("响应已发送给客户端: " + result);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}