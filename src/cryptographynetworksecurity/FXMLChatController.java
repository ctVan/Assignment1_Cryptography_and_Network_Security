/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptographynetworksecurity;

import Serialization.EncryptType;
import Serialization.Message;
import Serialization.MessageType;
import Utils.IO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JFileChooser;

/**
 * FXML Controller class
 *
 * @author Chi Tay Ta
 */
public class FXMLChatController implements Initializable{
    static ServerSocket serverSocket;
    public static Socket server;
    static int PORT = 9991;
    static String HOSTNAME = "localhost";
    
    @FXML
    private Button sendButton;
    @FXML
    private Button sendFileButton;
    @FXML
    private TextArea messageTextArea;
    @FXML
    private TextArea enterTextArea;
    @FXML
    private Button startButton;

    public FXMLChatController()
    {
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleSendFileButtonClick(MouseEvent event) throws IOException {
        //   
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();

        FXMLDocumentController controller =  fxmlLoader.<FXMLDocumentController>getController();

        controller.initData(server);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Send file");
        stage.setScene(new Scene(root1));  
        stage.show();
            
    }

    @FXML
    private void handleSendButtonClick(MouseEvent event) {
        if ((!enterTextArea.getText().equals(""))) {
            // send message
            messageTextArea.appendText("\n" + "Van: " + enterTextArea.getText());

            Message msg = new Message();
            msg.data = enterTextArea.getText().getBytes();
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
                enterTextArea.setText("");
            }
        }
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void handleStartButtonClick(MouseEvent event) {
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    initServer();
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(FXMLChatController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();
        
//                try {
//                    initServer();
//        } catch (IOException ex) {
//            Logger.getLogger(FXMLChatController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    private void initServer() throws UnknownHostException, IOException {
        sendFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    
                    FXMLDocumentController controller =  fxmlLoader.<FXMLDocumentController>getController();
                    
                    controller.initData(server);
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Send file");
                    stage.setScene(new Scene(root1));
                    stage.show();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!enterTextArea.getText().equals("")){
                // send message
                messageTextArea.setText(messageTextArea.getText() + "\n" + "Van: " + enterTextArea.getText());

                Message msg = new Message();
                msg.data = enterTextArea.getText().getBytes();
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
                    enterTextArea.setText("");
                }
            }
            }
        });
        System.out.println("dm");     
        serverSocket = new ServerSocket(PORT, 1, InetAddress.getByName(HOSTNAME));
        System.out.println("Wait");                
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

                        Platform.runLater(() -> {                 
                   
                if (msg.msgType == MessageType.FILE) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(msg.fileName));
                    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();

                        System.out.println("\n" + "Receive file from Tay:" + msg.fileName);
                        messageTextArea.appendText("\n" + "Receive file from Tay:" + msg.fileName);
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
                        messageTextArea.appendText("\n" + "Tay: " + str);
                    }
                }
                });

            } catch (IOException e1) {
            }
        }
    }
}
