/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CryptographyInterfaces;

import javax.crypto.SecretKey;

/**
 *
 * @author Chi Tay Ta
 */
public interface ICryptographyEncryption {
    public boolean encrypt(SecretKey key, String inPath, String outPath);
    public SecretKey keyGenerator();
}
