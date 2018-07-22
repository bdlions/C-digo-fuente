import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created with IntelliJ IDEA.
 * User: poorna
 * Date: 1/7/14
 * Time: 11:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Runner {

    public static void main(String[] args) {

        Key key;
        KeyGenerator generator = null;
        try {
            generator = KeyGenerator.getInstance("DES");
            generator.init(new SecureRandom("TESTTEST".getBytes("utf-8")));
            key = generator.generateKey();

            Cipher cipher1 = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher1.init(Cipher.ENCRYPT_MODE, key);
            Cipher cipher2 = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher2.init(Cipher.DECRYPT_MODE, key);



            byte[] stringBytes = "testmessage".getBytes("utf-8");

            // encrypt using the cypher
            byte[] raw = cipher1.doFinal(stringBytes);

            // converts to base64 for easier display.
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(raw);


            System.out.println(base64);

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] base641 = decoder.decodeBuffer(base64);
            System.out.println(new String(cipher2.doFinal(base641), "utf-8"));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BadPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


//        while (true) {
//            String separator = System.getProperty("file.separator");
//            String classpath = System.getProperty("java.class.path");
//            String path = System.getProperty("java.home")
//                    + separator + "bin" + separator + "java";
//            ProcessBuilder processBuilder =
//                    new ProcessBuilder(path, "-cp",
//                            classpath,
//                            "activemq.ui.MainNewUI");
//
//            Process process = null;
//            try {
//                process = processBuilder.start();
//            } catch (IOException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//            if (process != null) {
//                try {
//                    int i = process.waitFor();
//                    if(i ==0)
//                        System.exit(-1);
//                } catch (InterruptedException e) {
//                    //
//                }
//                process.destroy();
//            }
//
////            try {
//////                Thread.sleep(6 * 60 * 1000);
//////                Thread.sleep(30 * 1000);
////            } catch (InterruptedException e) {
////                //
////            }
//
//        }
    }
}
