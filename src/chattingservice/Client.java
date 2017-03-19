/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattingservice;

import IO.IO;
import Serialization.EncryptType;
import Serialization.Message;
import Serialization.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFileChooser;

/**
 *
 * @author ctVan
 */
public class Client extends javax.swing.JFrame implements ActionListener {

    static Socket client;
    static int PORT = 9991;
    static String HOSTNAME = "localhost";

    public Client() throws IOException {
        initComponents();
        initClient();
    }

    public void initClient() throws UnknownHostException, IOException {
        this.setVisible(true);
        this.setTitle("Client");
        SendBtn.addActionListener(this);
        sendFileBtn.addActionListener(this);
        client = new Socket(HOSTNAME, PORT);
        while (true) {
            try {
                byte[] sizeArr = new byte[4];
                DataInputStream dis = new DataInputStream(client.getInputStream());
                dis.read(sizeArr, 0, 4);
                System.err.print("available: " + Integer.toString(dis.available()));
                Message msg = new Message();
                int lenMsg = msg.byte2int(sizeArr);
                System.err.println("size: " + Integer.toString(lenMsg));
                if (lenMsg < 0) {
                    continue;
                }

                byte[] rec = new byte[lenMsg];

                int byteRead = 0, offset = 0;
                do {
                    byteRead = dis.read(rec, offset, lenMsg - offset);
                    offset += byteRead;
                } while (byteRead > 0);
                System.err.println("total byte read: " + Integer.toString(offset));
                msg.deserialize(rec, rec.length);

                if (msg.msgType == MessageType.FILE) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(msg.fileName));
                    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();

                        System.out.println("\n" + "Receive file from Van:" + msg.fileName);
                        TxtArea.setText(TxtArea.getText() + "\n" + "Receive file from Van:" + msg.fileName);
                        // save to file
                        IO.printout(msg.data, file.getAbsolutePath());
                    }
                } else if (msg.msgType == MessageType.MSG) {
                    // in case exchange key of AES or DES
                    if (msg.encryptType == EncryptType.RSA) {
                        String key = new String(msg.data);
                        // save to file config, set variable
                    } else {
                        // receive data from server
                        String str = new String(msg.data);
                        TxtArea.setText(TxtArea.getText() + "\n" + "Van: " + str);
                    }
                }

            } catch (IOException e1) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TxtArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        SendtxtArea = new javax.swing.JTextArea();
        SendBtn = new javax.swing.JButton();
        sendFileBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TxtArea.setColumns(20);
        TxtArea.setRows(5);
        jScrollPane1.setViewportView(TxtArea);

        SendtxtArea.setColumns(20);
        SendtxtArea.setRows(5);
        jScrollPane2.setViewportView(SendtxtArea);

        SendBtn.setText("Send");

        sendFileBtn.setText("Send File");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sendFileBtn)
                            .addComponent(SendBtn)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SendBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendFileBtn)))
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() == SendBtn) && (!SendtxtArea.getText().equals(""))) {
            // send message
            TxtArea.setText(TxtArea.getText() + "\n" + "Tay: " + SendtxtArea.getText());

            Message msg = new Message();
            msg.data = SendtxtArea.getText().getBytes();
            msg.encryptType = EncryptType.AES;
            msg.msgType = MessageType.MSG;
            int len = msg.messageSize();
            byte[] str = msg.serialize(len);
            try {
                DataOutputStream dos = new DataOutputStream(
                        client.getOutputStream());
                dos.write(str);
            } catch (IOException e1) {
            } finally {
                SendtxtArea.setText("");
            }
        } else if (e.getSource() == sendFileBtn) {
            // choose file and load to Ram
            JFileChooser fc = new JFileChooser();
            int returnValue = fc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                String inFile = selectedFile.getAbsolutePath();
                byte[] data = IO.readin(inFile);
                Message msg = new Message();
                msg.data = data;
                msg.encryptType = EncryptType.AES;
                msg.msgType = MessageType.FILE;
                msg.fileName = selectedFile.getName();
                int len = msg.messageSize();
                byte[] str = msg.serialize(len);
                try {
                    DataOutputStream dos = new DataOutputStream(
                            client.getOutputStream());
                    dos.write(str);
                } catch (IOException e1) {

                } finally {
                    TxtArea.setText(TxtArea.getText() + "\n" + "Send file to Van: " + msg.fileName);
                    SendtxtArea.setText("");
                }
            }
        }
    }

    public static void main(String args[]) throws IOException {
        new Client();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SendBtn;
    private javax.swing.JTextArea SendtxtArea;
    private javax.swing.JTextArea TxtArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton sendFileBtn;
    // End of variables declaration//GEN-END:variables
}
