package com.baidupanapi.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * Created by xm on 15/6/11.
 */
public class Base64Util {

    public static void main(String[] args) throws IOException {
        InputStream inputStream = new FileInputStream(new File("/home/xm/Tencent Files/402276694/FileRecv/apiclient_cert.p12"));
        String base64Str = streamToBase64Str(inputStream);
        System.out.println(base64Str);
        InputStream newInputStream = base64StrToStream(base64Str);
        byte[] b = streamToByte(newInputStream);
        for(int i=0;i<b.length;++i)
        {
            if(b[i]<0){
                b[i]+=256;
            }
        }
        OutputStream out = new FileOutputStream("/home/xm/apiclient_cert.p12.new");
        out.write(b);
        out.flush();
        out.close();
    }

    public static byte[] streamToByte(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] b = new byte[1024];
        while ((len = input.read(b, 0, b.length)) != -1) {
            baos.write(b, 0, len);
        }
        byte[] buffer =  baos.toByteArray();
        return buffer;
    }

    public static InputStream byteTostream(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static String streamToBase64Str(InputStream inputStream) throws IOException{
        byte[] output = streamToByte(inputStream);
        BASE64Encoder encoder = new BASE64Encoder();
        String outstr = encoder.encode(output);
        return outstr;
    }

    public static void saveBase64strToFile(String base64Str,String filePath) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b = decoder.decodeBuffer(base64Str);
        for(int i=0;i<b.length;++i)
        {
            if(b[i]<0){
                b[i]+=256;
            }
        }
        OutputStream out = new FileOutputStream(filePath);
        out.write(b);
        out.flush();
        out.close();
    }

    public static InputStream base64StrToStream(String base64Str) throws IOException {
        if (base64Str == null){
            throw new NullPointerException();
        }
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b = decoder.decodeBuffer(base64Str);
        return byteTostream(b);
    }

    public static BASE64Decoder getDecoder(){
        return new BASE64Decoder();
    }


    public static BASE64Encoder getEncoder(){
        return new BASE64Encoder();
    }
}
