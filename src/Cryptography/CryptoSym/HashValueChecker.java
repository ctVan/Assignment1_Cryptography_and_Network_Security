/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cryptography.CryptoSym;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chi Tay Ta
 */
public class HashValueChecker {
    private DataInputStream in;
    private Integer BYTE_OF_BLOCK;
    private byte[] buffer;    
    private MessageDigest md;
    private String hashFunction;
    private StringBuilder sb;
    
    public HashValueChecker(Integer byteOfBlock, String hashFunction)
    {
        this.BYTE_OF_BLOCK = byteOfBlock;
        buffer = new byte[BYTE_OF_BLOCK];
        this.hashFunction = hashFunction;
        try {
            md = MessageDigest.getInstance(hashFunction);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HashValueChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public StringBuilder getStringBuilderHashValue()
    {
        return sb;
    }
    
    public String getHashValue(File inputFile)
    {
        int c;
        try {            
            in = new DataInputStream(new FileInputStream(inputFile));
            
            while ((c = in.read(buffer)) > 0) {
                md.update(buffer, 0, c);
            }
            byte[] mdbytes = md.digest();
            //convert the byte to hex format
            sb = new StringBuilder("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
            
        }   catch (IOException ex) {
            Logger.getLogger(HashValueChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
