package com.apssouza.mytrade.common.encrypt;

import org.junit.Test;
import static org.junit.Assert.*;

public class AesPBEStringEncryptorTest {

    @Test
    public void testEncrypt_with_default_settings() throws Exception {
        var encryptBuilder = new AESPBEStringEncryptor.Builder();
        var encryptor = encryptBuilder.build("secretKey");
        String myPassEncrypted = encryptor.encrypt("myPass");
        assertEquals("myPass",encryptor.decrypt(myPassEncrypted));
    }


    @Test
    public void testEncrypt_with_custom_settings() throws Exception {
        var encryptBuilder = new AESPBEStringEncryptor.Builder();
        encryptBuilder.withSalt("test")
                .withIterationCount(2000)
                .withKeyLength(256);
        var encryptor = encryptBuilder.build("secretKey");
        String myPassEncrypted = encryptor.encrypt("myPass");
        assertEquals("myPass",encryptor.decrypt(myPassEncrypted));
    }
}