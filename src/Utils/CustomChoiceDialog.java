/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ChoiceDialog;

/**
 *
 * @author Chi Tay Ta
 */
public class CustomChoiceDialog {
    private String title;
    private String headerText;
    private String contentText;
    private List<String> choices = new ArrayList<>();
    private Optional<String> result;
    
    public CustomChoiceDialog(String title, String headerText, String contentText, List<String> options)
    {
        this.title = title;
        this.headerText = headerText;
        this.contentText = contentText;
        for (int i = 0; i < options.size(); i++)
            choices.add(options.get(i));
    }
    
    public void showAndWait(){
        
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);

        // Traditional way to get the response value.
        result = dialog.showAndWait();
    }
    public String getResult(){
        if (result.isPresent())
            return result.get();
        else
            return null;
    }
}
