package com.example.mailapp1.config;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String SECRET_KEY = loadSecretKey();
    private static String loadSecretKey() {
        try (InputStream input = EncryptionUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return "";
            }
            prop.load(input);
            return prop.getProperty("jwt.secret.key");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String encrypt(String plainText) throws Exception {
        byte[] keyBytes = getKeyBytes();
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText) throws Exception {
        byte[] keyBytes = getKeyBytes();
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static byte[] getKeyBytes() {
        // SECRET_KEY'i uygun uzunluğa dönüştür
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        int keyLength = 32; // 256-bit AES anahtar uzunluğu
        if (keyBytes.length < keyLength) {
            byte[] extendedKeyBytes = new byte[keyLength];
            System.arraycopy(keyBytes, 0, extendedKeyBytes, 0, keyBytes.length);
            keyBytes = extendedKeyBytes;
        } else if (keyBytes.length > keyLength) {
            byte[] truncatedKeyBytes = new byte[keyLength];
            System.arraycopy(keyBytes, 0, truncatedKeyBytes, 0, keyLength);
            keyBytes = truncatedKeyBytes;
        }
        return keyBytes;
    }
}