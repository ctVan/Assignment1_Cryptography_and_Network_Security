/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattingservice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ctVan
 */
public class IO {

    static void printout(byte[] data, String filename) {
        DataOutputStream out;
        try {
            out = new DataOutputStream(new FileOutputStream(filename));
            out.write(data);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static byte[] readin(String filename) {
        File file = new File(filename);
        byte[] result = new byte[(int) file.length()];
        DataInputStream in;
        try {
            in = new DataInputStream(new FileInputStream(file));
            in.read(result);
            return result;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
