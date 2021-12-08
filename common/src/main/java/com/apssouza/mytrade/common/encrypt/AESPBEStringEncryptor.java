package com.apssouza.mytrade.common.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class AESPBEStringEncryptor {

    private final SecretKey secretKeySpec;
    private final IvParameterSpec iv;

    public AESPBEStringEncryptor(final SecretKey secretKeySpec, IvParameterSpec iv) {
        this.secretKeySpec = secretKeySpec;
        this.iv = iv;
    }

    public String encrypt(String dataToEncrypt) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        byte[] cipherText = pbeCipher.doFinal(dataToEncrypt.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String dataToDecrypt) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        byte[] decode = Base64.getDecoder().decode(dataToDecrypt);
        byte[] plainText = cipher.doFinal(decode);
        return new String(plainText);
    }


    static class Builder {
        private String secretKeyFactory = "PBKDF2WithHmacSHA512";
        private int iterationCount = 40000;
        private int keyLength = 128;
        private String salt = "rand888s88";
        private IvParameterSpec iv = generateIv();

        public Builder withSecretKeyFactory(String factory) {
            this.secretKeyFactory = factory;
            return this;
        }

        public Builder withIterationCount(int iterationCount) {
            this.iterationCount = iterationCount;
            return this;
        }

        public Builder withKeyLength(int length) {
            this.keyLength = length;
            return this;
        }

        public Builder withSalt(String salt) {
            this.salt = salt;
            return this;
        }

        public Builder withIv(IvParameterSpec iv) {
            this.iv = iv;
            return this;
        }

        private  IvParameterSpec generateIv() {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            return new IvParameterSpec(iv);
        }

        public AESPBEStringEncryptor build(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretKeyFactory);
            PBEKeySpec keySpec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), iterationCount, keyLength);
            SecretKey keyTmp = keyFactory.generateSecret(keySpec);
            return new AESPBEStringEncryptor(new SecretKeySpec(keyTmp.getEncoded(), "AES"), iv);
        }
    }
}