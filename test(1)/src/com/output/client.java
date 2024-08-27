package com.output;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import net.sf.json.JSONObject;

public class client {
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
}