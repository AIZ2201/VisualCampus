package org.example;

import com.output.*;
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
import java.util.*;
import java.util.function.Function;

import static org.example.Main.PRIVATE_KEY_STRING;

public class Main {
    public static final String PRIVATE_KEY_STRING = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDm3uPxryqm8NwOrh5xIaV5rDDkx8Eb896LOGvKMU+LqFeTIop8s0KRWna0fYdAK/rB0IgsFVC+QSa24YhKdiIdl+HT0Son7ZucWFjxOoTFu84KKJOKZFekIYx1tENx7TuMPWr+23tFIWxtXA6AN0mL3/vnQ2HzKtjRgnC22gVjPp5oCocrFz8v9byHUy92sJoyObCvP+u9eft6afliN2t1OJsCaFEOEvj63r+qTywCY6hpKk4TSlKItz+oyPfkUgTOil16YmHU9UYLa+Z1vDvJe0l5NeOjn048B+YMoZ9kxC3LxYvswQXmlsDTN+u56pJY/5uB3OIqT7Tncbf3i9HnAgMBAAECggEABFRXt/eN+DV6Gvhe78YuwTpebFvmniyTls7HDrKgCEFu6v5x1GbUL7+fjEDyYVyxxiKv+wunSPoVzMYr9SGEAEQR4xQAE2GcUnFBsbxkMwUTlxMtuSGqlxRd+vMXdLJ7NGUOdZsOE7zNuLtKNlNTGMFpiuuzkYjTKhKlJHiwMy03UvG773LQJGm0tqSNwoc4YZU02u4VP3yXhqg8dfR1HZB8XLtrrV+sUnK3ynx2TM6CvbPfn/Cy3k86W7tVRJmy8AjtAzAnN8AZ6iKXUz5yzlgjGRuILEKvJoxeqVdnKiOXxrPCYv8t7ctHRUvvY+ksPF0hhmijl3le7cCcOIS8uQKBgQD1AFEeCq4mpHdg0R27nivEAuov0rSH3z9qD7GHeUWWEo0IuPTOmSX7Xi70jEh5kRZ2lS40GyugU+7XQj7V1/zPeqnm2v/0VYfi3BxaoOWQioQoCDo4TSXmOJd0vG4ek6B/sLUt4XMSWiK/UN3hNmvjR5m7zuPLpAPiMKOYEgN6zQKBgQDxPC11YNPUl5tJGbhjlNNiaU2+6AOKs4+NMvIejOSyLaML424CL+fYt8HdnQk9vSl2GxhcqfCzqZ5miMPU/cI+wa3WNVNYesc7+awkwe4yRGSanuLSv+dNgs9iKubzr3IEsmLV3fxQP1bUmuNgKCR1rIEvKDYen+zBMPb09cnngwKBgE46oawUAPN5xFx3qSTFJnhZ7ekDQH25/Qeipy74vA1lMv87/38QS55tvlR7jpCykRegHraojk+NPVAiaOnjI2gkZKe/+cZMoS0A11Tg1yxF7ljR97UOfTF7lHDD0e2VecQL6m4R9KpBeaLY8V/9/oj9zcq4DuhxIIRSv4nJBq9pAoGAA61eKAbk073H4TJiLCptmndudvWEcE3SZ12acTrGlay6aoj5+VCPoS8T5nPpWyaSBjndgjQY9jQktP1qtslzd49uPfCsJ5IfjVt980V9gwil2/GGFZ5VQUo4LQ/oW1iWTVoGxanPmc2NXkpKIwIZpC2P/o8HXCol6r/7U7qXbE8CgYBqhwAn/jRba/HrOJqUnwMWDpGg2Yvqdpm2GvS+fRJ11rol/rwBHeRlZy4CMDTwUDNRbVjVpsK198Pvn+NnPzpzYBya9m2UH7Z+mqv/APY8fzhzpD1CUsG2+Cw3l3mg1gQlM33om89SFhmWf7yHQaR/cA5OlGF0rE8FTEQUGO7Hnw=="; // 替换为生成的私钥

    public static final String HEARTBEAT_MESSAGE = "HEARTBEAT";  // 定义心跳消息
    public static void main(String[] args) {
        login_page login_page = new login_page();
        TeachingAffairsController controller = new TeachingAffairsController();
        ImageToDatabase imageToDatabase = new ImageToDatabase();
        ExcelGrade excelGrade = new ExcelGrade();
        studentStatus_page studentStatus = new studentStatus_page();
        store_page store = new store_page();
        bank_page bank = new bank_page();
        library_page library = new library_page();
        // 创建一个函数映射表
        Map<String, Function<JSONObject, JSONObject>> functionMap = new HashMap<>();
        //登录
        functionMap.put("login_submit", login_page::login_submit);
        //忘记密码后修改密码
        functionMap.put("change_password", login_page::change_password);
        //查成绩
        functionMap.put("check_score", controller::getGrade);
        //查课表
        functionMap.put("check_schedule",controller::classSchedule);
        //查看已经选的课
        functionMap.put("check_class",controller::getSelectedClasses);
        //学生评教
        functionMap.put("student_evaluate",controller::studentEvaluation);
        //教务查看评教结果
        functionMap.put("check_evaluate",controller::teacherEvaluation);
        //学生选课
        functionMap.put("student_select",controller::studentSelection);
        //学生退课
        functionMap.put("student_deselect",controller::studentDeseclection);
        //点击课程名时发送每个学生具体信息
        functionMap.put("select_enter",controller::sendSelection);
        //发送评教信息
        functionMap.put("evaluate_enter",controller::sendEvaluation);
        //点击选课时发送信息
        functionMap.put("select_send",controller::sendSelectionBefore);
        //教务处排课
        functionMap.put("add_course",controller::addCourse);
        //发送需要成绩的课程
        functionMap.put("send_course",controller::sendGradeCourse);
        //添加图片
        functionMap.put("save_image",imageToDatabase::faceImageCompare);
        //导入成绩
        functionMap.put("import_grades",excelGrade::handleClientInput);
        functionMap.put("studentStatus_view",studentStatus::studentStatus_view);
        functionMap.put("studentStatus_change",studentStatus::studentStatus_change);
        functionMap.put("studentStatus_search",studentStatus::studentStatus_search);
        functionMap.put("studentStatus_add",studentStatus::studentStatus_add);
        functionMap.put("studentStatus_delete",studentStatus::studentStatus_delete);
        functionMap.put("studentStatus_changePassword",studentStatus::studentStatus_changePassword);
        functionMap.put("store_show",store::store_show);
        functionMap.put("store_getMyTransaction",store::store_getMyTransaction);
        functionMap.put("store_buygoods",store::store_buygoods);
        functionMap.put("store_addProduct",store::store_addProduct);
        functionMap.put("store_search",store::store_search);
        functionMap.put("store_change",store::store_change);
        functionMap.put("store_delete",store::store_delete);
        functionMap.put("store_getAllTransaction",store::store_getAllTransaction);
        functionMap.put("bank_view",bank::bank_view);
        functionMap.put("bank_recharge",bank::bank_recharge);
        functionMap.put("bank_withdraw",bank::bank_withdraw);
        functionMap.put("library_getBooks",library::library_getBooks);
        functionMap.put("library_getUser",library::library_getUser);
        functionMap.put("library_returnBooks",library::library_returnBooks);
        functionMap.put("library_borrowBooks",library::library_borrowBooks);
        functionMap.put("library_renewBooks",library::library_renewBooks);
        functionMap.put("library_searchBooks",library::library_searchBooks);
        functionMap.put("library_updBooks",library::library_updateBook);
        functionMap.put("library_addBook",library::library_addBook);
        functionMap.put("library_remBook",library::library_removeBook);
        functionMap.put("library_addCom",library::library_addcom);
        functionMap.put("library_remCom",library::library_removecom);
        functionMap.put("library_getMes",library::library_message);


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

            // 为适应不同代码风格而进行跳过处理

            // 使用集合存储所有要跳过的操作类型
            Set<String> skipOperations = new HashSet<>(Arrays.asList(
                    "select_send", "change_password", "check_evaluate", "add_course", "send_course",
                    "save_image", "import_grades", "select_enter", "student_select", "evaluate_enter",
                    "student_evaluate", "check_schedule", "student_deselect", "check_score", "check_class",
                    "library_getMes", "library_borrowBooks", "library_getBooks", "library_getUser",
                    "library_returnBooks", "library_renewBooks", "library_searchBooks", "library_updBooks",
                    "library_addBook", "library_remBook", "library_addCom", "library_remCom"
            ));

            // 如果 operation 不在要跳过的操作列表中，则进行密码解密
            if (!skipOperations.contains(operation)) {
                // 解密密码
                String encryptedPassword = object.getString("password");
                String decryptedPassword = decryptPassword(encryptedPassword, PRIVATE_KEY_STRING);
                object.put("password", decryptedPassword);
                System.out.println("解密后的密码: " + decryptedPassword);
            }


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