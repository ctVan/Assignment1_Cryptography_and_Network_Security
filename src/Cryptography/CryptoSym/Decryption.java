/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cryptography.CryptoSym;

import CryptographyInterfaces.ICryptographyDecryption;
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
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author Chi Tay Ta
 */
public class Decryption implements ICryptographyDecryption{
    private final String ALG;
    private final String MODE;
    private final int BYTE_OF_BLOCK;

    public Decryption(String alg, String mode, int byteOfBlock) {
        ALG = alg;
        BYTE_OF_BLOCK = byteOfBlock;
        MODE = mode;
    }
    @Override
    public boolean decrypt(SecretKey key, String inPath, String outPath) {
        DataInputStream in;
        FileOutputStream out;
        byte[] buffer = new byte[BYTE_OF_BLOCK];
        int c;
        long count = 0;
        File inFile, outFile;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            Cipher aesCipher = Cipher.getInstance(ALG + MODE);
            aesCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decVal;

            inFile = new File(inPath);
            outFile = new File(outPath);
            in = new DataInputStream(new FileInputStream(inFile));
            out = new FileOutputStream(outFile);
            // definitely c = 16 or c = -1, do not have case c < 16 because of padding
            while ((c = in.read(buffer)) > 0) {
                decVal = aesCipher.doFinal(buffer);
                int cc = BYTE_OF_BLOCK;
                // remove padding, have to determine the last one
                if((inFile.length() - count) == BYTE_OF_BLOCK){                
                    cc = decVal[0];
                    for(int i = 0; i < (int)cc; i++){
                        decVal[i] = decVal[i+1];
                    }
                }
                out.write(decVal, 0, cc);
                count += c;
                if (count % 10240000 == 0) {
                    //            System.out.println("count: " + Double.toString(count*1.0 / inFile.length()) + "%");
                }
                md.update(decVal, 0, cc);
            }
            in.close();
            out.close();

            byte[] mdbytes = md.digest();
            //convert the byte to hex format
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("Digest(in hex format):: " + sb.toString());
            return true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
