/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cryptography.CryptoSym;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Chi Tay Ta
 */
public class RSAEncryption {
    private byte[] publicKeyByteArray;
    private Cipher cipher;
    private FileOutputStream out;
    public RSAEncryption()
    {
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(RSAEncryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public byte[] encryption(byte[] secretKey, File publicKeyFile, String outFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException{        
        Path keyLocation = Paths.get(publicKeyFile.getAbsolutePath());
        publicKeyByteArray = Files.readAllBytes(keyLocation);
        PublicKey RSApublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyByteArray));
        
        try {
            cipher.init(Cipher.ENCRYPT_MODE, RSApublicKey);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(RSAEncryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        byte[] encrypted = cipher.doFinal(secretKey);
        out = new FileOutputStream(outFile);
        
        out.write(encrypted);
        
        out.close();
        
        return encrypted;
    }
}
