package com.hybe.larva.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Aes256 {

    public static void main(String[] args) {

        final String originText = UUID.randomUUID().toString().replace("-", "");
        System.out.println("originText=" + originText);

        Aes256 aes = new Aes256();
        final byte[] secretKey = aes.genSecretKey();

        String encText = aes.encrypt(secretKey, originText);
        System.out.println("encText=" + encText);

        String decText = aes.decrypt(secretKey, encText);
        System.out.println("decText=" + decText);

        System.out.println("result: " + originText.equals(decText));
    }

    public static byte[] genSecretKey() {
        final byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public static String encrypt(byte[] keyBytes, String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            int blockSize = cipher.getBlockSize();
            IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOfRange(keyBytes, 0, blockSize));
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = cipher.doFinal(plainTextBytes);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(byte[] keyBytes, String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            int blockSize = cipher.getBlockSize();
            IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOfRange(keyBytes, 0, blockSize));

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] cipherTextBytes = Base64.getDecoder().decode(cipherText.getBytes(StandardCharsets.UTF_8));
            byte[] decrypted = cipher.doFinal(cipherTextBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
