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
import static cryptographynetworksecurity.FXMLChatController.server;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JFileChooser;

/**
 * FXML Controller class
 *
 * @author Chi Tay Ta
 */
public class FXMLChatClientController implements Initializable, ActionListener {
    static Socket client;
    static int PORT = 9991;
    static String HOSTNAME = "localhost";
    @FXML
    private TextArea messageTextArea;
    @FXML
    private Button sendButton;
    @FXML
    private TextArea enterTextArea;
    @FXML
    private Button sendFileButton;
    @FXML
    private Button startButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleSendButtonClick(MouseEvent event) throws IOException {
        if ((!enterTextArea.getText().equals(""))) {
            // send message
            messageTextArea.appendText("\n" + "Tay: " + enterTextArea.getText());

            Message msg = new Message();
            msg.data = enterTextArea.getText().getBytes();
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
                enterTextArea.setText("");
            }
        }    
    }

    @FXML
    private void handleSendFileButtonClick(MouseEvent event) throws IOException {
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
    private void handleStartButtonClick(MouseEvent event) {
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    initClient();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLChatClientController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();
    }
    
        private void initClient() throws UnknownHostException, IOException {
            
        client = new Socket(HOSTNAME, PORT);
        Platform.runLater(() -> {
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

                System.out.println(msg.msgType);
                     
                if (msg.msgType == MessageType.FILE) {
                    
                    System.out.println("\n" + "Receive file from Van:" + msg.fileName);
                    messageTextArea.appendText("\n" + "Receive file from Van:" + msg.fileName);
                    
                    //Alert alertSave = new Alert(AlertType.CONFIRMATION);
                    //alertSave.setTitle("Confirmation");
                    //alertSave.setHeaderText("Save file");
                    //alertSave.setContentText("Do you want to save this encrypted file?");
                    
                    //Optional<ButtonType> resultSave = alertSave.showAndWait();
                    //if (resultSave.get() == ButtonType.OK){
                    
                        DirectoryChooser dirChooser = new DirectoryChooser();
                        dirChooser.setTitle("Choose folder");

                        File chosenDirectory = dirChooser.showDialog(null);
                        if (chosenDirectory != null){
                        try {
                            File file = new File(chosenDirectory.getName()+"\\" + msg.fileName);
                            System.out.println(file.getAbsolutePath());
                            // save to file
                            IO.printout(msg.data, file.getAbsolutePath());

                            //Alert alert = new Alert(AlertType.CONFIRMATION);
                            //alert.setTitle("Confirmation");
                            //alert.setHeaderText("Decrypt file");
                            //alert.setContentText("Do you want to decrypt this file?");

                            //Optional<ButtonType> result = alert.showAndWait();
                            //if (result.get() == ButtonType.OK){
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            
                            FXMLDocumentController controller =  fxmlLoader.<FXMLDocumentController>getController();
                            
                            controller.initDataToDecrypt(msg, chosenDirectory);
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.setTitle("Decrypt file");
                            stage.setScene(new Scene(root1));
                            stage.show();
                            //} else {
                            // ... user chose CANCEL or closed the dialog
                        } catch (IOException ex) {
                            Logger.getLogger(FXMLChatClientController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                            }    
                        //}
                    //}
                    //}
                } else if (msg.msgType == MessageType.MSG) {
                    // in case exchange key of AES or DES
                    if (msg.encryptType == EncryptType.RSA) {
                        String key = new String(msg.data);
                        // save to file config, set variable
                    } else {
                        // receive data from server
                        String str = new String(msg.data);
                        messageTextArea.appendText("\n" + "Van: " + str);
                    }
                }
                
            } catch (IOException e1) {
            }
        }
                });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
}
