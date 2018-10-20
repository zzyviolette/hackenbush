package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sample.History;

public class MenuController extends Stage{
    @FXML private BorderPane menuBorderPane;
    @FXML private Label configLabel;

    @FXML private void configuration(ActionEvent event){
        String page = "fxml/Config.fxml";
        Node node= (Node) event.getSource();
        String mode = ((Button)node).getText();
        ConfigController configController = new ConfigController();
        configController.setMode(mode);
        new History().addPage("Add",page,(Node)event.getSource(),null);
    }
}