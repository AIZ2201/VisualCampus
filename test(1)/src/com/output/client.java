package com.output;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

import net.sf.json.JSONObject;

import javax.crypto.Cipher;

public class client {
    private static final String PUBLIC_KEY_STRING = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5t7j8a8qpvDcDq4ecSGleaww5MfBG/PeizhryjFPi6hXkyKKfLNCkVp2tH2HQCv6wdCILBVQvkEmtuGISnYiHZfh09EqJ+2bnFhY8TqExbvOCiiTimRXpCGMdbRDce07jD1q/tt7RSFsbVwOgDdJi9/750Nh8yrY0YJwttoFYz6eaAqHKxc/L/W8h1MvdrCaMjmwrz/rvXn7emn5YjdrdTibAmhRDhL4+t6/qk8sAmOoaSpOE0pSiLc/qMj35FIEzopdemJh1PVGC2vmdbw7yXtJeTXjo59OPAfmDKGfZMQty8WL7MEF5pbA0zfrueqSWP+bgdziKk+053G394vR5wIDAQAB"; // 替换为生成的公钥

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        InputStreamReader isr;
        BufferedReader br;
        OutputStreamWriter osw;
        BufferedWriter rw;
        try {
            Socket socket = new Socket("10.208.72.178", 4444);
            osw = new OutputStreamWriter(socket.getOutputStream());
            rw = new BufferedWriter(osw);
            isr = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(isr);
            User user = new User();
            System.out.println("operation:");
            user.setOperation(in.nextLine());
            System.out.println("cardNumber:");
            user.setCardNumber(in.nextInt());
            in.nextLine();

            System.out.println("password:");
            user.setPassword(in.nextLine());

            // 加密密码
            String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
            user.setPassword(encryptedPassword);


            JSONObject jsonObject = JSONObject.fromObject(user);
            System.out.println(jsonObject);
            rw.write(jsonObject.toString()+"\n");
            rw.flush();


            String response = br.readLine();
            if (response != null) {
                JSONObject jsonResponse = JSONObject.fromObject(response);
                System.out.println("从服务器接收到的响应: " + jsonResponse);
            }

            // 关闭流和 socket
            br.close();

//            String response = reader.readLine();
//            JSONObject jsonResponse = JSONObject.fromObject(response);
//            System.out.println("从服务器接收到的响应: " + jsonResponse);
//
//            reader.close();
//            String response = br.readLine();
//            System.out.println(response);
//
//            br.close();
            rw.close();
            socket.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static String encryptPassword(String password, String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}