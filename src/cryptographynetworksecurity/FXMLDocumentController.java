/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptographynetworksecurity;

import Cryptography.CryptoSym.Decryption;
import Cryptography.CryptoSym.Encryption;
import Cryptography.CryptoSym.HashValueChecker;
import Cryptography.CryptoSym.RSADecryption;
import Cryptography.CryptoSym.RSAEncryption;
import Serialization.EncryptType;
import Serialization.Message;
import Serialization.MessageType;
import Utils.CustomChoiceDialog;
import Utils.IO;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author user
 */
public class FXMLDocumentController implements Initializable {
    //public static Message msg;
    private HashValueChecker hashValueChecker;
    
    Boolean isFolder; 
    private File chosenEncryptFile;
    private File chosenEncryptDir;
    private File chosenDecryptFile;
    
    private File chosenPublicKeyFile;
    private File chosenPrivateKeyFile;
    
    private File originalFile;
    private File decryptedFile;
    
    private RSAEncryption RSAEncrypt;
    private RSADecryption RSADecrypt;
    
    private Encryption DESEncryption;
    private Encryption AESEncryption;
    private Decryption AESDecryption;
    private Decryption DESDecryption;
    
    private SecretKey secretKey;
    private byte[] encryptedSecretKey;
    
    private String    cryptographyAlgorithmEncrypt;
    private String    cryptographyAlgorithmDecrypt;
    
    
    
    @FXML
    private TextField chooseFileFolderEncryptTextField;
    @FXML
    private Button chooseFileFolderEncryptButton;
    @FXML
    private Button generateSecretKeyEncryptButton;
    @FXML
    private Button encryptButton;
    @FXML
    private Button decryptButton;
    @FXML
    private Button chooseFileFolderDecryptButton;
    @FXML
    private Button enterSecretKeyDecryptButton;
    @FXML
    private TextField generateSecreteKeyEncryptTextField;
    @FXML
    private TextField chooseFileFolderDecryptTextField;
    @FXML
    private TextField enterSecretKeyDecryptTextField;
    @FXML
    private TextField RSAPublicKeyEncryptTextField;
    @FXML
    private Button encryptSecretKeyButton;
    @FXML
    private Button RSAPublicKeyEncryptButton;
    @FXML
    private TextField RSAPrivateKeyDecryptTextField;
    @FXML
    private Button RSAPrivateKeyDecryptButton;
    @FXML
    private Button decryptSecretKeyButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button checkHashButton;
    @FXML
    private Button generateSecretKeyEncryptButton1;
    @FXML
    private TextField originalFileTextField;
    @FXML
    private TextArea hashValueOriginalFileTextArea;
    @FXML
    private Button originalFileButton;
    @FXML
    private TextArea hashValueDecryptedFileTextArea;
    @FXML
    private TextField decryptedFileTextField;
    @FXML
    private Button decryptedFileButton;
    @FXML
    private TextField statusTextField;
    
    public Socket socket;
    public Message messageToDecrypt;
    
    public void initData(Socket socket)
    {
        this.socket = socket;
    }
    
    public void initDataToDecrypt(Message msg, File dirChooser)
    {
        this.messageToDecrypt = msg;
        this.cryptographyAlgorithmDecrypt = msg.encryptType == EncryptType.AES? "AES" : "DES";
        this.encryptedSecretKey = Base64.getDecoder().decode(msg.key);
        this.decryptedFile = dirChooser;
    }

    public FXMLDocumentController() {
        this.isFolder = false;
        cryptographyAlgorithmEncrypt = "AES";
        cryptographyAlgorithmDecrypt = "AES";
        DESEncryption = new Encryption("DES","/ECB/NoPadding", 16);
        AESEncryption = new Encryption("AES","/ECB/NoPadding", 16);
        
        DESDecryption = new Decryption("DES","/ECB/NoPadding", 16);
        AESDecryption = new Decryption("AES","/ECB/NoPadding", 16);
        
        RSAEncrypt = new RSAEncryption();
        RSADecrypt = new RSADecryption();        
        
        hashValueChecker = new HashValueChecker(16, "MD5");
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleCancelButtonClick(MouseEvent event) {
        Stage stage = (Stage) this.cancelButton.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void handleEncryptButtonClick(MouseEvent event) {
        switch (cryptographyAlgorithmEncrypt)
        {
            case "AES":
                AESEncryption.encrypt(secretKey, chosenEncryptFile.getAbsolutePath(), "D:\\CryptographyProject\\Encryption\\AESencrypt-" + chosenEncryptFile.getName());  
                break;
            case "DES":
                DESEncryption.encrypt(secretKey, chosenEncryptFile.getAbsolutePath(), "D:\\CryptographyProject\\Encryption\\DESencrypt-" + chosenEncryptFile.getName());
                break;            
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Completed!");
        alert.setHeaderText(null);
        alert.setContentText("Encrypt file/folder successfully! Please encrypt secret key on next step to send file/folder.");
        
        alert.show();
    }

    @FXML
    private void handleDecryptButtonClick(MouseEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose folder to save...");
        File chosenDirectory = dirChooser.showDialog(null);
        if (chosenDirectory != null){
            switch (cryptographyAlgorithmDecrypt)
            {
                case "AES":
                    AESDecryption.decrypt(secretKey, chosenDecryptFile.getAbsolutePath(), chosenDirectory + messageToDecrypt.fileName);
                    break;
                case "DES":
                    DESDecryption.decrypt(secretKey, chosenDecryptFile.getAbsolutePath(), chosenDirectory + messageToDecrypt.fileName);
                    break;            
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Completed!");
            alert.setHeaderText(null);
            alert.setContentText("Decrypt file/folder successfully! ");

            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleChooseFileFolderEncryptButtonClick(MouseEvent event) {
        List<String> listItems = Arrays.asList("File","Folder");
        CustomChoiceDialog choiceDialog = new CustomChoiceDialog("Choose File/Folder","Please choose file or folder","Choose:",listItems);
        choiceDialog.showAndWait();
        String choice = choiceDialog.getResult();
        
        if (choice == null)
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Cancel choose file/folder ");
            alert.setHeaderText(null);
            alert.setContentText("Please choose file or folder type!");

            alert.showAndWait();
            return;
        }
        else
        {
            if ("File".equals(choice))
                isFolder = false;
            else
                isFolder = true;
        }
        
        Boolean isFileFolderNull = false;
        if (!isFolder)
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose file");

            chosenEncryptFile = fileChooser.showOpenDialog(null);
            if (chosenEncryptFile != null)
            {
                System.out.print("OK");
                this.chooseFileFolderEncryptTextField.setText(chosenEncryptFile.getAbsolutePath());
                return;
            }
            else
            {
                isFileFolderNull = true;
            }
        }
        else
        {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Choose folder");

            chosenEncryptDir = dirChooser.showDialog(null);
            if (chosenEncryptDir != null)
            {
                chooseFileFolderEncryptTextField.setText(chosenEncryptDir.getAbsolutePath());
                return;
            }
            else
            {
                isFileFolderNull = true;
            }
        }
        
        if (isFileFolderNull)
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No file or folder");
            alert.setHeaderText(null);
            alert.setContentText("You didn't choose any file or folder!");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleChooseFileFolderDecryptButtonClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");

        chosenDecryptFile = fileChooser.showOpenDialog(null);
        if (chosenDecryptFile != null)
        {
            chooseFileFolderDecryptTextField.setText(chosenDecryptFile.getAbsolutePath());
            return;
        }
        else
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No file or folder");
            alert.setHeaderText(null);
            alert.setContentText("You didn't choose any file!");

            alert.showAndWait();
        }
    }    
    
    @FXML
    private void handleGenerateSecretKeyEncryptButtonClick(MouseEvent event) {
        List<String> listAlgorithm = Arrays.asList("AES","DES");
        CustomChoiceDialog choiceDialog = new CustomChoiceDialog("Secret Key","Please choose an algorithm!","Choose algorithm:",listAlgorithm);
        choiceDialog.showAndWait();
        String choice = choiceDialog.getResult();
        
        if (choice == null)
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Cancel algorithm selection ");
            alert.setHeaderText(null);
            alert.setContentText("Please choose an algorithm!");

            alert.showAndWait();
            return;
        }
        else
        {
            cryptographyAlgorithmEncrypt = choice;
        }
        
        switch (cryptographyAlgorithmEncrypt)
        {
            case "AES":
                secretKey = AESEncryption.keyGenerator();
                break;
            case "DES":
                secretKey = DESEncryption.keyGenerator();
                break;
        }
        
        this.generateSecreteKeyEncryptTextField.setText(secretKey.toString());        
    }


    @FXML
    private void handleEnterKeySecretKeyDecryptButtonClick(MouseEvent event)
    {
        List<String> listAlgorithm = Arrays.asList("AES","DES");
        CustomChoiceDialog choiceDialog = new CustomChoiceDialog("Secret Key","Please choose an algorithm!","Choose algorithm:",listAlgorithm);
        choiceDialog.showAndWait();
        String choice = choiceDialog.getResult();
        
        if (choice == null)
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Cancel algorithm selection ");
            alert.setHeaderText(null);
            alert.setContentText("Please choose an algorithm!");

            alert.showAndWait();
            return;
        }
        
        cryptographyAlgorithmDecrypt = choice;
        
        enterSecretKeyDecryptTextField.setText(encryptedSecretKey.toString());
        
    }

    @FXML
    private void handleEncryptSecretKeyButtonClick(MouseEvent event) {
        try {
            encryptedSecretKey = RSAEncrypt.encryption(secretKey.getEncoded(), chosenPublicKeyFile, "D:\\CryptographyProject\\Encryption\\Key\\secretkey");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Completed!");
        alert.setHeaderText(null);
        alert.setContentText("Encrypt secret key successfully! ");
        alert.show();
    }

    @FXML
    private void handleRSAPublickKeyEncryptButtonClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose public key");

        chosenPublicKeyFile = fileChooser.showOpenDialog(null);
        if (chosenPublicKeyFile != null)
        {
            RSAPublicKeyEncryptTextField.setText(chosenPublicKeyFile.getAbsolutePath());
        }
        else
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No file or folder");
            alert.setHeaderText(null);
            alert.setContentText("You didn't choose any public key!");

            alert.showAndWait();
        }   
    }

    @FXML
    private void handleRSAPrivateKeyDecryptButtonClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose private key");

        chosenPrivateKeyFile = fileChooser.showOpenDialog(null);
        if (chosenPrivateKeyFile != null)
        {
            RSAPrivateKeyDecryptTextField.setText(chosenPrivateKeyFile.getAbsolutePath());
        }
        else
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No file or folder");
            alert.setHeaderText(null);
            alert.setContentText("You didn't choose any private key!");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDecryptSecretKeyButtonClick(MouseEvent event) {
        byte[] decryptedSecretKey = RSADecrypt.decryptData(encryptedSecretKey, chosenPrivateKeyFile, cryptographyAlgorithmEncrypt);
        secretKey = new SecretKeySpec(decryptedSecretKey, 0, decryptedSecretKey.length, cryptographyAlgorithmDecrypt);
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Completed!");
        alert.setHeaderText(null);
        alert.setContentText("Decrypt secret key successfully! Choose file to decrypt...");
        alert.show();
    }

    @FXML
    private void handleSendButtonClick(MouseEvent event) {
        byte[] data = IO.readin("D:\\CryptographyProject\\Encryption\\AESencrypt-" + chosenEncryptFile.getName()).getBytes();
        Message msg = new Message();
        msg.data = data;
        msg.encryptType = cryptographyAlgorithmEncrypt.equals("AES") ? EncryptType.AES:EncryptType.DES;
        msg.msgType = MessageType.FILE;
        msg.fileName = chosenEncryptFile.getName();
        msg.key = Base64.getEncoder().encodeToString(encryptedSecretKey);
        int len = msg.messageSize();
        byte[] str = msg.serialize(len);
        try {
            DataOutputStream dos = new DataOutputStream(
                    this.socket.getOutputStream());
            dos.write(str);
            //TxtArea.setText(TxtArea.getText() + "\n" + "Send file to Tay: " + msg.fileName);
        } catch (IOException e1) {

        } finally {
            //SendtxtArea.setText("");
            System.out.println("OK");
        }
    }

    @FXML
    private void handleCheckHashButton(MouseEvent event) {
        String originalFileHashValue = hashValueChecker.getHashValue(originalFile);
        String decryptedFileHashValue = hashValueChecker.getHashValue(decryptedFile);
        hashValueOriginalFileTextArea.setText(originalFileHashValue);
        hashValueDecryptedFileTextArea.setText(decryptedFileHashValue);
        
        if (originalFileHashValue.equals(decryptedFileHashValue))
        {
            this.statusTextField.setText("TRUE");
        }
        else
        {
            this.statusTextField.setText("FALSE");
        }
    }

    @FXML
    private void handleCancelHashCheckButton(MouseEvent event) {
    }

    @FXML
    private void handleOriginalFileButtonClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose original file");

        originalFile = fileChooser.showOpenDialog(null);
        if (originalFile != null)
        {
            originalFileTextField.setText(originalFile.getAbsolutePath());
            return;
        }
        else
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No file or folder");
            alert.setHeaderText(null);
            alert.setContentText("You didn't choose any file!");

            alert.showAndWait();
        }    
    }

    @FXML
    private void handleDecryptedFileButtonClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose decrypted file");

        decryptedFile = fileChooser.showOpenDialog(null);
        if (decryptedFile != null)
        {
            decryptedFileTextField.setText(decryptedFile.getAbsolutePath());
            return;
        }
        else
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No file or folder");
            alert.setHeaderText(null);
            alert.setContentText("You didn't choose any file!");

            alert.showAndWait();
        }       
    }
    
}