/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import Logica.FileLoaderThread;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 *
 * @author charli83
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private Button bOpenPaht;
    @FXML
    private TextField tfCollectionName;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }    
    
     @FXML
    private void handleButtonAction(ActionEvent event) {
         DirectoryChooser directoryChooser = new DirectoryChooser();
         File selectedDirectory = directoryChooser.showDialog(bOpenPaht.getScene().getWindow());
         //System.out.println(selectedDirectory.toPath().toString());
         if(selectedDirectory != null){
            String documentName = tfCollectionName.getText();
            FileLoaderThread loader = new FileLoaderThread(selectedDirectory,documentName);
            Thread thread = new Thread(loader);
            thread.start();
         }
    }
    
    
}
