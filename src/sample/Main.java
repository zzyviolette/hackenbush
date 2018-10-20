package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.History;
import sample.KeyBoardEvent;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    public static void setView(URL url,Node node,String fxmlFile,Parent r){
        try {
            Stage stage=(Stage) node.getScene().getWindow();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(r == null ?root:r);
            (new KeyBoardEvent()).add_Keypress_event(scene,fxmlFile);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        String page = "fxml/Menu.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(page));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        new History().addPage("Start",page,root,null);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
