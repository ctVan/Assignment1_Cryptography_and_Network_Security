/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cryptography.CryptoSym;

import CryptographyInterfaces.ICryptographyEncryption;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author Chi Tay Ta
 */
public class Encryption implements ICryptographyEncryption{
    private final String ALG;
    private final String MODE;
    private final int BYTE_OF_BLOCK;

    public Encryption(String alg, String mode, int byteOfBlock) {
        ALG = alg;
        BYTE_OF_BLOCK = byteOfBlock;
        MODE = mode;
    }
        @Override
        public boolean encrypt(SecretKey key, String inPath, String outPath) {
        DataInputStream in;
        FileOutputStream out;
        byte[] buffer = new byte[BYTE_OF_BLOCK];
        int c;
        long count = 0;
        File inFile, outFile;

        try {
            // for check sum using MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // init cipher generator
            Cipher aesCipher = Cipher.getInstance(ALG + MODE);
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal;

            // read block of byte and encrypt
            inFile = new File(inPath);
            outFile = new File(outPath);
            in = new DataInputStream(new FileInputStream(inFile));
            out = new FileOutputStream(outFile);
            while ((c = in.read(buffer)) > 0) {
                md.update(buffer, 0, c);

                // add padding for the last one
                if (c < BYTE_OF_BLOCK) {               
                    for (int i = c; i >= 1; i--) {
                        buffer[i] = buffer[i - 1];
                    }
                     buffer[0] = (byte) c;
                    for (int i = c+1; i < buffer.length; i++) {
                        buffer[i] = 0;
                    }
                }

                encVal = aesCipher.doFinal(buffer);
                //        String encryptedValue = new BASE64Encoder().encode(encVal);
                out.write(encVal, 0, buffer.length);
                count += c;
                if (count % 10240000 == 0) {
                    //        System.out.println("count: " + Double.toString(count*100.0 / inFile.length()) + "%");
                }
            }

            byte[] mdbytes = md.digest();
            //convert the byte to hex format
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("Digest(in hex format):: " + sb.toString());
            out.close();
            in.close();
            
            return true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
        
    @Override
    public SecretKey keyGenerator() {
        KeyGenerator keygenerator;
        try {
            keygenerator = KeyGenerator.getInstance(ALG);
            return keygenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
