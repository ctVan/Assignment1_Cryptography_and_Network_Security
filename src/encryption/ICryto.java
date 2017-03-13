/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import javax.crypto.SecretKey;

/**
 *
 * @author ctVan
 */
public interface ICryto {
    public boolean encrypt(SecretKey key, String inPath, String outPath);
    public boolean decrypt(SecretKey key, String inPath, String outPath);
    public SecretKey keyGenerator();
}
