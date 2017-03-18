/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattingservice;

import Serialization.EncryptType;
import Serialization.Message;
import Serialization.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFileChooser;

/**
 *
 * @author ctVan
 */
public final class Server extends javax.swing.JFrame implements ActionListener {

    static ServerSocket serverSocket;
    static Socket server;
    static int PORT = 8888;
    static String HOSTNAME = "localhost";

    public Server() throws IOException {
        initComponents();
        initServer();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TxtArea = new javax.swing.JTextArea();
        sendFileBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        SendtxtArea = new javax.swing.JTextArea();
        SendBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TxtArea.setColumns(20);
        TxtArea.setRows(5);
        jScrollPane1.setViewportView(TxtArea);

        sendFileBtn.setText("Send File");

        SendtxtArea.setColumns(20);
        SendtxtArea.setRows(5);
        jScrollPane2.setViewportView(SendtxtArea);

        SendBtn.setText("Send");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SendBtn)
                    .addComponent(sendFileBtn))
                .addGap(17, 17, 17))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(sendFileBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SendBtn))))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @throws java.net.UnknownHostException
     */
    public void initServer() throws UnknownHostException, IOException {
        this.setVisible(true);
        this.setTitle("Server");
        sendFileBtn.addActionListener(this);
        SendBtn.addActionListener(this);
        serverSocket = new ServerSocket(PORT, 1, InetAddress.getByName(HOSTNAME));
        server = serverSocket.accept();
        while (true) {
            try {
                byte[] sizeArr = new byte[4];
                DataInputStream dis = new DataInputStream(server.getInputStream());
                dis.read(sizeArr, 0, 4);
                Message msg = new Message();
                int lenMsg = msg.byte2int(sizeArr);
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

                        System.out.println("\n" + "Receive file from Tay:" + msg.fileName);
                        TxtArea.setText(TxtArea.getText() + "\n" + "Receive file from Tay:" + msg.fileName);
                        // save to file
                        IO.printout(msg.data, file.getAbsolutePath());
                    }
                } else if (msg.msgType == MessageType.MSG) {
                    // receive data from server
                    String str = new String(msg.data);
                    TxtArea.setText(TxtArea.getText() + "\n" + "Tay: " + str);
                }

            } catch (IOException e1) {
            }
        }
    }

    public static void main(String args[]) throws IOException {
        Server server1 = new Server();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SendBtn;
    private javax.swing.JTextArea SendtxtArea;
    private javax.swing.JTextArea TxtArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton sendFileBtn;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() == SendBtn) && (!SendtxtArea.getText().equals(""))) {
            // send message
            TxtArea.setText(TxtArea.getText() + "\n" + "Van: " + SendtxtArea.getText());

            Message msg = new Message();
            msg.data = SendtxtArea.getText().getBytes();
            msg.encryptType = EncryptType.AES;
            msg.msgType = MessageType.MSG;
            int len = msg.messageSize();
            byte[] str = msg.serialize(len);
            try {
                DataOutputStream dos = new DataOutputStream(
                        server.getOutputStream());
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
                            server.getOutputStream());
                    dos.write(str);
                    TxtArea.setText(TxtArea.getText() + "\n" + "Send file to Tay: " + msg.fileName);
                } catch (IOException e1) {

                } finally {
                    SendtxtArea.setText("");
                }
            }
        }
    }
}
