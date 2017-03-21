/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cryptography.CryptoSym;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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
public class RSADecryption {
    private byte[] privateKeyByteArray;
    private Cipher cipher;
    private FileOutputStream out;
    
    public RSADecryption()
    {
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(RSADecryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
       private byte[] blockCipher(byte[] bytes, int mode) throws IllegalBlockSizeException, BadPaddingException, IOException{
	// string initialize 2 buffers.
	// scrambled will hold intermediate results
	byte[] scrambled = new byte[0];

	byte[] toReturn = new byte[0];
	int length = (mode == Cipher.ENCRYPT_MODE)? 100 : 128;

	byte[] buffer = new byte[length];

        

	for (int i=0; i< bytes.length; i++){

		if ((i > 0) && (i % length == 0)){
			//execute the operation
			scrambled = cipher.doFinal(buffer);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                        outputStream.write(scrambled);
                        outputStream.write(toReturn);
			toReturn = outputStream.toByteArray();
			int newlength = length;

			if (i + length > bytes.length) {
				 newlength = bytes.length - i;
			}
			// clean the buffer array
			buffer = new byte[newlength];
		}
		// copy byte into our buffer.
		buffer[i%length] = bytes[i];
	}

	scrambled = cipher.doFinal(buffer);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(scrambled);
        outputStream.write(toReturn);
	toReturn = outputStream.toByteArray();

	return toReturn;
}
    
    public byte[] decryptData(byte[] encryptedSecretKey, File privateKeyFile, String outFile)
    { 
        try {
        Path keyLocation = Paths.get(privateKeyFile.getAbsolutePath());
        privateKeyByteArray = Files.readAllBytes(keyLocation);

        KeyPair keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PrivateKey RSAprivateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyByteArray));
        
        cipher.init(Cipher.DECRYPT_MODE, RSAprivateKey);
        //byte[] decrypted = cipher.doFinal(encryptedSecretKey);
        byte[] decrypted = blockCipher(encryptedSecretKey, Cipher.DECRYPT_MODE);
        out = new FileOutputStream(outFile);
        
        out.write(decrypted);
        
        out.close();
        
        return decrypted;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
            Logger.getLogger(RSADecryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
