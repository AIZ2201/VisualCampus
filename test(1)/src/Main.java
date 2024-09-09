import com.output.login_page;//引入新页面功能时记得引入对应java类
import com.output.studentStatus_page;
import com.output.store_page;
import net.sf.json.JSONObject;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Main {
    public static final String PRIVATE_KEY_STRING = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDm3uPxryqm8NwOrh5xIaV5rDDkx8Eb896LOGvKMU+LqFeTIop8s0KRWna0fYdAK/rB0IgsFVC+QSa24YhKdiIdl+HT0Son7ZucWFjxOoTFu84KKJOKZFekIYx1tENx7TuMPWr+23tFIWxtXA6AN0mL3/vnQ2HzKtjRgnC22gVjPp5oCocrFz8v9byHUy92sJoyObCvP+u9eft6afliN2t1OJsCaFEOEvj63r+qTywCY6hpKk4TSlKItz+oyPfkUgTOil16YmHU9UYLa+Z1vDvJe0l5NeOjn048B+YMoZ9kxC3LxYvswQXmlsDTN+u56pJY/5uB3OIqT7Tncbf3i9HnAgMBAAECggEABFRXt/eN+DV6Gvhe78YuwTpebFvmniyTls7HDrKgCEFu6v5x1GbUL7+fjEDyYVyxxiKv+wunSPoVzMYr9SGEAEQR4xQAE2GcUnFBsbxkMwUTlxMtuSGqlxRd+vMXdLJ7NGUOdZsOE7zNuLtKNlNTGMFpiuuzkYjTKhKlJHiwMy03UvG773LQJGm0tqSNwoc4YZU02u4VP3yXhqg8dfR1HZB8XLtrrV+sUnK3ynx2TM6CvbPfn/Cy3k86W7tVRJmy8AjtAzAnN8AZ6iKXUz5yzlgjGRuILEKvJoxeqVdnKiOXxrPCYv8t7ctHRUvvY+ksPF0hhmijl3le7cCcOIS8uQKBgQD1AFEeCq4mpHdg0R27nivEAuov0rSH3z9qD7GHeUWWEo0IuPTOmSX7Xi70jEh5kRZ2lS40GyugU+7XQj7V1/zPeqnm2v/0VYfi3BxaoOWQioQoCDo4TSXmOJd0vG4ek6B/sLUt4XMSWiK/UN3hNmvjR5m7zuPLpAPiMKOYEgN6zQKBgQDxPC11YNPUl5tJGbhjlNNiaU2+6AOKs4+NMvIejOSyLaML424CL+fYt8HdnQk9vSl2GxhcqfCzqZ5miMPU/cI+wa3WNVNYesc7+awkwe4yRGSanuLSv+dNgs9iKubzr3IEsmLV3fxQP1bUmuNgKCR1rIEvKDYen+zBMPb09cnngwKBgE46oawUAPN5xFx3qSTFJnhZ7ekDQH25/Qeipy74vA1lMv87/38QS55tvlR7jpCykRegHraojk+NPVAiaOnjI2gkZKe/+cZMoS0A11Tg1yxF7ljR97UOfTF7lHDD0e2VecQL6m4R9KpBeaLY8V/9/oj9zcq4DuhxIIRSv4nJBq9pAoGAA61eKAbk073H4TJiLCptmndudvWEcE3SZ12acTrGlay6aoj5+VCPoS8T5nPpWyaSBjndgjQY9jQktP1qtslzd49uPfCsJ5IfjVt980V9gwil2/GGFZ5VQUo4LQ/oW1iWTVoGxanPmc2NXkpKIwIZpC2P/o8HXCol6r/7U7qXbE8CgYBqhwAn/jRba/HrOJqUnwMWDpGg2Yvqdpm2GvS+fRJ11rol/rwBHeRlZy4CMDTwUDNRbVjVpsK198Pvn+NnPzpzYBya9m2UH7Z+mqv/APY8fzhzpD1CUsG2+Cw3l3mg1gQlM33om89SFhmWf7yHQaR/cA5OlGF0rE8FTEQUGO7Hnw=="; // 替换为生成的私钥

    public static void main(String[] args) {
        login_page login = new login_page();//new 登录页面
        studentStatus_page studentStatus = new studentStatus_page();
        store_page store = new store_page();

        // 创建一个函数映射表  后续的新操作都添加到表中，注意格式
        Map<String, Function<JSONObject, JSONObject>> functionMap = new HashMap<>();
        functionMap.put("login_submit", login::login_submit);//登录页面的登录操作（上面先new页面，这里“::”后对应login_page中的处理函数
        functionMap.put("studentStatus_view",studentStatus::studentStatus_view);
        functionMap.put("studentStatus_change",studentStatus::studentStatus_change);
        functionMap.put("studentStatus_search",studentStatus::studentStatus_search);
        functionMap.put("store_show",store::store_show);
        functionMap.put("store_getMyTransaction",store::store_getMyTransaction);
        functionMap.put("store_buygoods",store::store_buygoods);
        functionMap.put("store_addProduct",store::store_addProduct);
        functionMap.put("store_search",store::store_search);
        functionMap.put("store_change",store::store_change);
        functionMap.put("store_delete",store::store_delete);
        functionMap.put("store_getAllTransaction",store::store_getAllTransaction);

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
    private static final String PRIVATE_KEY_STRING = Main.PRIVATE_KEY_STRING;

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

            // 解密密码
            String encryptedPassword = object.getString("password");
            String decryptedPassword = decryptPassword(encryptedPassword, PRIVATE_KEY_STRING);
            object.put("password", decryptedPassword);
            System.out.println("解密后的密码: " + decryptedPassword);

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String decryptPassword(String encryptedPassword, String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}