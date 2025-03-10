package bcs.aicrm.broadcns.auth.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/***********************************************************************************************
* Program Name : 암복호화유틸(AES256Crypt.java)
* Creator      : sukim
* Create Date  : 2021.12.20
* Description  : 암복호화유틸
* Modify Desc  :
* -----------------------------------------------------------------------------------------------
* Date         | Updater        | Remark
* -----------------------------------------------------------------------------------------------
* 2021.12.20     sukim            최초생성
************************************************************************************************/
public class AES256Crypt {
    public static String alg = "AES/CBC/PKCS5Padding";             //취약점이 많으므로 CBC모드 사용 추천
    private static final String key = "budEhPc8Q5nTFWIZUmDMhg%3D%3D3046"; //256비트(32바이트), 192비트(24바이트), 128비트(16바이트)
    private static final String iv = key.substring(0, 16);                //16byte(AES는 128비트(16바이트)단위로 암호화 하기때문

    public static String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(iv.getBytes()));

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        cipher.init(Cipher.DECRYPT_MODE,  new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(iv.getBytes()));

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }
}