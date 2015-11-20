package com.baidupanapi.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by xm on 15/8/19.
 */

public class RSAUtils {


    /** *//**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117; //1024bit = 128byte  128-11 = 117 ; 2048bit -> 245


    public static void main(String[] args) throws Exception {
        //PrivateKey privateKey = readPrivateKey();
        String message = "S23uwrfNKZXHHVq8uYZZMZUhmiUYIMxYQNDpgLc7T98MvGyeplSJ6wq/6QL5ACtsZzdPqXzziCbzKiesO9D+UDL0PdTliVrS7WJWXah5GoOVsEIQ/TiCtxs1zV17dsjL908panVHBqu1TaJNuDV7YIM3riFV8ml/bS+RYl+jOZc=";
        System.out.println("- decrypt rsa encrypted base64 message: "+message);
        // hello ~,  encrypted and encoded with Base64:

        //或者用下面的简略方式
        //System.out.println(decryptByPrivateKey(message));

        //加密
//        System.out.println(encryptByPublicKey("你好"));
    }

    private static String decryptByPrivateKey(PrivateKey privateKey, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(data);

        return new String(decryptedData);
    }

    private static byte[] getEncryptedDataFromBase64(String base64Text) throws IOException {
        return Base64Util.getDecoder().decodeBuffer(base64Text);
    }


    private static String getBase64FromEncryptedData(byte[] encryptedData) throws IOException {
        return Base64Util.getEncoder().encodeBuffer(encryptedData).replace("\n","");
    }

    public static PublicKey readPublicKey(String privateKeyDataString) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKeyData = Base64Util.getDecoder().decodeBuffer(privateKeyDataString
                .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                .replace("\n", ""));
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedKeyData));
    }

//
//    private static PrivateKey readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        byte[] privateKeyData = Files.readAllBytes(
//                Paths.get(BaseData.PRIVATE_KEY_FILE_Path));
//
//        byte[] decodedKeyData = Base64Util.getDecoder().decodeBuffer(new String(privateKeyData)
//                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
//                .replace("\n", ""));
//
//        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decodedKeyData));
//    }

    /** *//**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param stringToEncrypt 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return 加密数据
     * @throws Exception
     */
    private static byte[] encryptToByteByPublicKey(PublicKey publicKey, String stringToEncrypt)
            throws Exception {
        byte[] data = stringToEncrypt.getBytes();
        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA"); //Android -> "RSA/None/PKCS1Padding" ; Java -> "RSA"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


//    public static String decryptByPrivateKey(String encryptedDataInBase64) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
////        if(privateKey==null){
////            privateKey = readPrivateKey();
////        }
//        System.out.println("privateKey==null"+(privateKey==null));
//        return decryptByPrivateKey(privateKey, getEncryptedDataFromBase64(encryptedDataInBase64));
//    }


    public static String encryptToBase64StringByPublicKey(PublicKey publicKey, String stringToEncrypt) throws Exception {
        System.out.println("publicKey==null"+(publicKey==null));
        return getBase64FromEncryptedData(encryptToByteByPublicKey(publicKey,stringToEncrypt));
        //return decryptByPrivateKey(privateKey, getEncryptedDataFromBase64(encryptedDataInBase64));
    }
}
