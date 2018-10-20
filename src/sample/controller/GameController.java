package sample.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sample.*;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable{
    @FXML private Group gameG1;
    @FXML private Group gameG2;

    private static Graph graph;

    private static Nim nim;
    private static int[] heap;
    private static boolean type;//true - normal

    private static Player currentPlayer;
    private static Player player1;
    private static Player player2;
    private static Nim.Move coup;

    private static String mode;
    //HUD
    private static Label playerNameLabel;
    private static Label time;
    private static Chrono chrono;
    private static Slider gameSpeed;

    public static double pause;

    private Timeline timeline;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        if(graph != null){
            chrono = new Chrono();
            chrono.start();
            refreshHUD();
            startGame();
        }

    }

    public void initGame(String mode,Group group1,Group group2){
        this.mode = mode;
        initSlider();
        gameG1.getChildren().add(gameSpeed);
        gameG1.getChildren().addAll(group1.getChildren());
        gameG2 = group2;
        addHUD(gameG1);
    }

    public void addHUD(Group group){
        time = new Label("time");
        playerNameLabel = new Label("playerName");
        time.setId("time");
        playerNameLabel.setId("playerName");
        time.setLayoutX(700);
        playerNameLabel.setLayoutX(350);
        gameG1.getChildren().addAll(time,playerNameLabel);
    }
    /**
     * Raffraichissement du HUD
     * */
    public void refreshHUD(){
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis((1)),e->{
                    time.setText(chrono.getDureeSec());
                    playerNameLabel.setText(currentPlayer.getName());
                    playerNameLabel.setTextFill(currentPlayer.getColor());
                })
        );
        timeline.play();
    }

    public void setGraph(Graph graph){
        this.graph = graph;
    }

    public Group getGroup(){
        return(gameG1);
    }
    /**
     * Initialisation du jeu de Nim
     * */
    public void initNim(Nim nim, int[] heap, boolean type,boolean typePlayer1, boolean typePlayer2){
        this.nim = nim;
        this.heap = heap;
        initPlayer(type,typePlayer1,typePlayer2);
    }

    public void initGen(boolean type,boolean typePlayer1,boolean typePlayer2){
        initPlayer(type,typePlayer1,typePlayer2);
    }
    /**
     * Initialisation des joueurs
     * */
    public void initPlayer(boolean type,boolean typePlayer1,boolean typePlayer2){
        this.type = type;
        player1 =(typePlayer1)?new Human("HUMAN1",Color.BLUE,heap):new Ai("AI1",Color.BLUE,heap,nim);
        player2 =(typePlayer2)?new Human("HUMAN2",Color.RED,heap):new Ai("AI2",Color.RED,heap,nim);
        currentPlayer = player1;
    }
    /**
     * Changement du joueur courant
     * */
    public void switchPlayer(){
        if((currentPlayer.getName()).equals(player1.getName()))
            currentPlayer = player2;
        else
            currentPlayer = player1;

    }
    /***
     * Lancement du jeu
     * */
    public void startGame(){
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                boolean stop = false;
                while(!stop){
                    if(0 < pause){
                        if(currentPlayer.actionDone(graph,mode)){
                            if(graph.win(mode)){
                                showWinner();
                                stop = true;
                            }else {
                                switchPlayer();
                            }
                        }
                    }else{
                        //System.out.println("Pause : ");
                        //alerte();
                    }
                }
                return null;
            }
        };

        new Thread(task).start();


    }
    public void showWinner(){

        String s = getWinmessage();
        Platform.runLater(()-> {
            BorderPane pane = new BorderPane();
            pane.setLayoutX(0);
            pane.setLayoutY(70);

            Image img = new Image(getClass().getResourceAsStream("../img/winner.png"));
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(400);
            imgView.setFitHeight(250);

            Text text = new Text(s);
            pane.setAlignment(text, Pos.CENTER);

            pane.setTop(text);
            pane.setCenter(imgView);
            graph.getGroup().getChildren().add(pane);

            KeyValue xValue  = new KeyValue(pane.layoutXProperty(), 350);
            KeyFrame keyFrame  = new KeyFrame(Duration.millis(2000), xValue);

            timeline  = new Timeline();
            timeline.setCycleCount(5);
            timeline.setAutoReverse(true);

            timeline.setOnFinished(event -> {
                graph.getGroup().getChildren().remove(pane);
                graph = null;
            });
            timeline.getKeyFrames().addAll(keyFrame);
            timeline.play();
        });


    }
    public String getWinmessage(){
        if(type){
            return ("Victoire du joueur : "+currentPlayer.getName()+" en mode normal");
        }else{
            switchPlayer();
            return ("Victoire du joueur : "+currentPlayer.getName()+" en mode misère");
        }

    }

    public static void alerte(){
        Platform.runLater(()-> {
            Pane pane = new Pane();
            pane.setLayoutX(250);
            pane.setLayoutY(150);
            Text text = new Text("Choisissez la bonne couleur");
            pane.getChildren().add(text);
            graph.getGroup().getChildren().add(pane);
            KeyFrame keyFrame  = new KeyFrame(Duration.millis(1200));
            Timeline timeline = new Timeline();
            timeline.setOnFinished(event -> {
                graph.getGroup().getChildren().remove(pane);
            });
            timeline.getKeyFrames().addAll(keyFrame);
            timeline.play();

        });
    }

    public static void initSlider(){
        gameSpeed = new Slider();
        gameSpeed.setPrefWidth(300);
        gameSpeed.setLayoutX(250);
        gameSpeed.setLayoutY(369);
        pause = 25;
        gameSpeed.setValue(pause);
        gameSpeed.setShowTickMarks(true);
        controlGameSpeedEvent(gameSpeed);
    }

    private static void controlGameSpeedEvent(Slider gameSpeed){
        gameSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            pause = newValue.intValue();
        });
    }
}