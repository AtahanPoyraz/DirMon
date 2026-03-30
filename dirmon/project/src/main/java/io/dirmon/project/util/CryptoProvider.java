package io.dirmon.project.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoProvider {
    private static final String ALGO = "AES";

    public static byte[] encrypt(byte[] input, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    public static byte[] decrypt(byte[] input, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }
}