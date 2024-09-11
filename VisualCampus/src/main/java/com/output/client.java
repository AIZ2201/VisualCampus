package com.output;

import entity.*;
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
import java.util.Objects;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
            Student student = new Student();

            //System.out.println("operation:");
            //user.setOperation(in.nextLine());
            user.setOperation("bank_view");
            if(Objects.equals(user.getOperation(), "bank_view"))
            {
                user.setCardNumber(123/*in.nextInt()*/);
                user.setPassword("1233"/*in.nextLine()*/);

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }
            if(Objects.equals(user.getOperation(), "login_submit"))
            {
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
            }
            if(Objects.equals(user.getOperation(), "studentStatus_view"))
            {
                //student.setOperation(user.getOperation());
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
            }
            if(Objects.equals(user.getOperation(), "studentStatus_change"))
            {
                System.out.println("cardNumber:");
                user.setCardNumber(111/*in.nextInt()*/);
                in.nextLine();
                System.out.println("password:");
                user.setPassword("111"/*in.nextLine()*/);
                System.out.println("studentCardNumber:");
                student.setCardNumber(111/*in.nextInt()*/);
                in.nextLine();
                System.out.println("name:");
                student.setName("111"/*in.nextLine()*/);
                System.out.println("studentNumber:");
                student.setStudentNumber("111"/*in.nextLine()*/);
                System.out.println("gender:");
                student.setGender("FEMALE"/*in.nextLine()*/);
                System.out.println("major:");
                student.setMajor("111"/*in.nextLine()*/);
                System.out.println("school:");
                student.setSchool("111"/*in.nextLine()*/);
                System.out.println("studentStat:");
                student.setStudentStat("OFF"/*in.nextLine()*/);
                System.out.println("enrollment:");
                student.setEnrollment("2024-08-10"/*in.nextLine()*/);
                System.out.println("birthPlace:");
                student.setBirthPlace("111"/*in.nextLine()*/);
                System.out.println("politicalStat:");
                student.setPoliticalStat("PartyMember"/*in.nextLine()*/);
                user.setStudent(student);

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }
            if(Objects.equals(user.getOperation(), "studentStatus_search"))
            {
                //student.setOperation(user.getOperation());
                System.out.println("cardNumber:");
                user.setCardNumber(in.nextInt());
                in.nextLine();
                System.out.println("password:");
                user.setPassword(in.nextLine());
                System.out.println("searchText:");
                user.setSearchText(in.nextLine());

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }
            if(Objects.equals(user.getOperation(), "store_show"))
            {
                System.out.println("cardNumber:");
                user.setCardNumber(111/*in.nextInt()*/);
                in.nextLine();
                System.out.println("password:");
                user.setPassword("111"/*in.nextLine()*/);

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }
            if(Objects.equals(user.getOperation(), "store_getMyTransaction"))
            {
                System.out.println("cardNumber:");
                user.setCardNumber(111/*in.nextInt()*/);
                in.nextLine();
                System.out.println("password:");
                user.setPassword("111"/*in.nextLine()*/);

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }
            if(Objects.equals(user.getOperation(), "store_addProduct"))
            {
                System.out.println("cardNumber:");
                user.setCardNumber(111/*in.nextInt()*/);
                in.nextLine();
                System.out.println("password:");
                user.setPassword("111"/*in.nextLine()*/);
                Product product = new Product();
                product.setName("test");
                user.setProduct(product);

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }
            if(Objects.equals(user.getOperation(), "store_getAllTransaction"))
            {
                System.out.println("cardNumber:");
                user.setCardNumber(111/*in.nextInt()*/);
                in.nextLine();
                System.out.println("password:");
                user.setPassword("111"/*in.nextLine()*/);

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }
            if(Objects.equals(user.getOperation(), "studentStatus_add"))
            {
                user.setCardNumber(111/*in.nextInt()*/);
                user.setPassword("111"/*in.nextLine()*/);
                user.setCardNumber(111);  // For testing, can be replaced with user input
                user.setPassword("111");  // For testing, can be replaced with user input
                // Create and set student information
                student.setName("李华");
                student.setCardNumber(213222212);
                student.setStudentNumber("09022301");
                student.setGender("FEMALE");
                student.setMajor("计算机科学与技术");
                student.setSchool("计算机学院");
                student.setStudentStat("ON");
                student.setEnrollment("2022-09-21");
                student.setBirthPlace("江苏南京");
                student.setPoliticalStat("Masses");
                // Set student object in user
                user.setStudent(student);

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }
            if(Objects.equals(user.getOperation(), "studentStatus_delete"))
            {
                user.setCardNumber(111);  // For testing, can be replaced with user input
                user.setPassword("111");  // For testing, can be replaced with user input
                student.setCardNumber(213222212);
                user.setStudent(student);

                // 加密密码
                String encryptedPassword = encryptPassword(user.getPassword(), PUBLIC_KEY_STRING);
                user.setPassword(encryptedPassword);

                JSONObject jsonObject = JSONObject.fromObject(user);
                System.out.println(jsonObject);
                rw.write(jsonObject.toString()+"\n");
                rw.flush();
            }

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