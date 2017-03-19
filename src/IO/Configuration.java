/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IO;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ctVan
 */
public class Configuration {

    public String keyAES;
    public String keyDES;
    public String publicKeyRSA;     // public key of sender, use to encrypt
    public String privateKeyRSA;    // private key of reciever, use to decrypt

    public Configuration() {
        keyAES = "";
        keyDES = "";
        privateKeyRSA = "";
        publicKeyRSA = "";
    }

    public void read() {
        Properties config = new Properties();
        File input = new File("./configuration/config.properties");

        try {
            FileReader reader = new FileReader(input);         
            // load configuration file
            config.load(reader);
            keyAES = config.getProperty("key_aes");
            keyDES = config.getProperty("key_des");
            publicKeyRSA = config.getProperty("public_key_rsa");
            privateKeyRSA = config.getProperty("private_key_rsa");

        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write() {
        FileWriter writer = null;
        try {
            Properties config = new Properties();
            config.setProperty("key_aes", keyAES);
            config.setProperty("key_des", keyDES);
            config.setProperty("public_key_rsa", publicKeyRSA);
            config.setProperty("private_key_rsa", privateKeyRSA);
            writer = new FileWriter(new File("./configuration/config.properties"));
            config.store(writer, "no comment");
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
