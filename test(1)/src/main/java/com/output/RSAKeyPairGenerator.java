package com.output;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAKeyPairGenerator {
    public static void main(String[] args) throws Exception {
        // 创建 KeyPairGenerator 对象，指定使用 RSA 算法
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");

        // 初始化密钥长度（通常为 2048 位）
        keyPairGen.initialize(2048);

        // 生成密钥对
        KeyPair pair = keyPairGen.generateKeyPair();

        // 获取公钥和私钥
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        // 将密钥转换为 Base64 编码的字符串，方便存储或传输
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        System.out.println("Public Key: " + publicKeyString);
        System.out.println("Private Key: " + privateKeyString);
    }
}

