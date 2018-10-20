package sample.controller;


import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.*;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;


public class ConfigController implements Initializable{
    @FXML private VBox configTopVBox;
    @FXML private MenuBar menuBar;
    @FXML private RadioButton HUMAN,HUMAN1;
    @FXML private RadioButton NORMAL;
    @FXML private TilePane configLeftTilePane;
    @FXML private Pane configPane;
    @FXML private Group configG1;
    @FXML private Group configG2;
    @FXML private Group configG3;
    @FXML private TextField configTextfield1;
    @FXML private TextField configTextfield2;

    private static String mode;
    private double oldX, oldY;//translation
    private double dupOldX, dupOldY;//memoriser l'ancien position (duplicated)
    private boolean selectFlag;
    private static Graph graph;
    private static String choice;
    private static Circle select;
    private static  Rotate rotate;
    private static double startX,startY,endX,endY;//la position de selection
    private static Nim nim;
    private static int[] heap;

    ArrayList<Vertex> dupVertexList = new ArrayList<Vertex>();
    ArrayList<Edge> dupEdgList = new ArrayList<Edge>();

    @FXML private void explore(ActionEvent event){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("fxml", "*.fxml"));
        MenuItem menuItem = (MenuItem)event.getSource();
        Stage stage =(Stage)menuBar.getScene().getWindow();
        File file;
        switch(menuItem.getText()){
            case "Open":
                file =  chooser.showOpenDialog(stage);
                Graph g = History.deserialize(file);
                graph.importG(g);
                break;
            case "Save":
                file = chooser.showSaveDialog(stage);
                if(file != null)
                    History.serialize(graph,file);
                break;
            case "Quit":
                exit(menuItem);
                break;
        }
    }

    public void saveFile(String content,File file){
        try{
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML private void start(ActionEvent event){
        Parent root = SetGameParams();
        if(root==null){
            alertPlayer();
        }else{
            String page = "fxml/Game.fxml";
            Node node= (Node) event.getSource();
            configTopVBox.getChildren().remove(menuBar);
            new History().addPage("Add",page,(Node)event.getSource(),root);
        }
    }
    /***
     * Configuration avant le début du jeu
     * */
    public Parent SetGameParams(){
        Parent root = null;
        clearNoconnect();
        select.setRadius(0);
        if(mode.equals("Gen")&&graph.getEdgList().size()==0){
            return root;
        }else {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../fxml/Game.fxml"));
                root = loader.load();
                GameController gameController = loader.getController();
                graph.setGroup(gameController.getGroup());
                switch (mode) {
                    case "Nim":
                        generateNim(gameController);
                        break;
                    case "Gen":
                        gameController.initGen(this.NORMAL.isSelected(),this.HUMAN.isSelected(),this.HUMAN1.isSelected());
                        break;
                }
                gameController.setGraph(graph);
                gameController.initGame(mode,configG1,configG2);
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return root;
    }
    /***
     * Nettoyage avant le commencement du jeu
     * */
    public void clearNoconnect(){
        if(mode.equals("Gen"))
            graph.clearConfigPane();
        graph.setChoice("Game");
        graph.setMode(mode);
    }

    /**
     * Génération du jeu de Nim
     * **/
    public void generateNim(GameController gameController){
        int nbr_tiges = Integer.parseInt(configTextfield1.getText());
        int len_max   = Integer.parseInt(configTextfield2.getText());
        editNim(nbr_tiges,len_max,this.NORMAL.isSelected());
        gameController.initNim(nim,heap,this.NORMAL.isSelected(),this.HUMAN.isSelected(),this.HUMAN1.isSelected());
    }

    /**
     * Génération Nim
     * */
    public void editNim(int nbTige, int maxSize, boolean type){
        double xInterval,yInterval;
        Random random = new Random();
        nim = new Nim(type);
        heap = nim.generateRandomNim(nbTige,maxSize);
        Line line = (Line)configG2.getChildren().get(0);
        xInterval = line.getEndX()/(heap.length+1);

        for(int i = 0; i < heap.length; i++){
            yInterval = line.getLayoutY()/(heap[i]+5);
            for(int j =0; j<heap[i]+1;j++){
                Vertex v = new Vertex(xInterval*(i+1) + random.nextInt(40) - 20,
                        line.getLayoutY()-j*(yInterval)-(heap[i]-j)*10 );

                v.setRaduis(6);
                graph.addVertex(v);
                v.drawVertex(configG1);

                if(j!=0){
                    int index = graph.getVertexIndex(v)-1;

                    Edge edge = new Edge(graph.getVertexList().get(index),v,i,j-1);
                    graph.getEdgList().add(edge);
                    edge.drawEdge(graph,2);
                }else{
                    v.getVertex().setCenterX(xInterval*(i+1));
                    v.getVertex().setCenterY(line.getLayoutY() - 10);
                }
            }
        }
    }

    @FXML private void makeChoice(ActionEvent event){
        choice = ((Button)event.getSource()).getId();
        graph.setChoice(choice);
        drawShape();
    }
    @FXML private void drawShape(){
        switch(choice){
            case "BtnVertex":
                select.setRadius(0);
                drawVertex();break;
            case "BtnEdge":
                select.setRadius(0);break;
            case "BtnSelect":
                select.setRadius(0);
                drawSelect();break;
            case "BtnRotation":
                doRotation(); break;
            case "BtnErase":
                select.setRadius(0);
                break;

        }
    }

    public void drawVertex(){
        configPane.setOnMouseClicked(e->{

            if(choice.equals("BtnVertex")){
                Vertex v = new Vertex(e.getX(),e.getY());
                if(insidePane(v) && !graph.collision(v)){
                    graph.addVertex(v);
                    v.drawVertex(configG1);
                    graph.addVertexEvent(v);
                }else{
                    alertPlayer();
                }
            }
        });

    }
    /**
     * Vérification des bornes, concernant les limites de la zone de dessin
     * */
    public boolean insidePane(Vertex v){
        double connect_lim = 322;
        double pane_north = 29;
        double pane_south = 329;
        double pane_west = 5;
        double pane_east = 725;

        Circle c = v.getVertex();
        double centerX = c.getCenterX();
        double centerY = c.getCenterY();
        double radius = c.getRadius();

        double v_south = centerY-radius;
        double v_north = centerY+radius;
        double v_west = centerX-radius;
        double v_east = centerX+radius;

        if(connect_lim <= v_south){
            v.connectAll(true);
            v.setSrc(true);
        }

        return ((v_south < pane_south) &&
                (pane_north < v_north) &&
                (pane_west < v_west) &&
                (v_east < pane_east));
    }

    /**
     * Méthode permettant d'avertir le joueur par rapport aux bornes de dessin
     * **/
    public void alertPlayer(){

        final Animation animation = new Transition() {


            {
                setCycleDuration(Duration.millis(1000));
                setCycleCount(2);
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                Color vColor = new Color(1, 0, 0, 1 - frac);
                configPane.setBorder(new Border(new BorderStroke(vColor,
                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            }
        };
        animation.play();
        animation.setOnFinished(event -> {
            configPane.setBorder(new Border(new BorderStroke(Color.rgb(0,191,255),
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        });
    }

    /***
     * Sélection d'une zone
     * */
    private void drawSelect(){
        doSelectPrevious();
        selectFlag = true;
        setSelectStartPosition();
        addDrawSelectEvent();
    }
    /***
     * Identification de chaque élément contenu dans la sélection
     * */
    private void doSelectPrevious(){
        for(Vertex vertex : graph.getVertexList()){
            if(vertex.getSelected()){
                vertex.setSelected(false);
            }
        }
        for(Edge edge : graph.getEdgList()){
            if(edge.isSelected()){
                edge.setSelected(false);
            }
        }
        for(Vertex vertex : graph.getVertexList()){
            if(vertex.isDuplicated()){
                vertex.setDuplicated(false);
            }
        }
        dupVertexList.clear();
        dupEdgList.clear();

    }
    /***
     * Initialisation de la position initiale de la sélection
     * */
    public void setSelectStartPosition(){
        configPane.setOnMousePressed(e->{
            if(choice.equals("BtnSelect")){
                startX = endX = e.getX();
                startY = endY = e.getY();
            }
        });
    }
    /**
     *
     * */
    public void addDrawSelectEvent(){
        configPane.setOnMouseDragged(e->{
            if(choice.equals("BtnSelect")){
                endX = e.getX();
                endY = e.getY();
                double centerX = (startX + endX) / 2;
                double centerY = (startY + endY) / 2;
                double radius = Math.sqrt((startX - endX) * (startX - endX) +
                        (startX - endX) * (startX - endX)) / 2;

                if(centerY - radius < 0 ){ centerY = radius;}
                if(centerY + radius > 350){ centerY = 350 - radius;}
                if(centerX - radius < 0){ centerX = radius;}
                if(centerX + radius > 725){ centerX = 725 - radius;}

                select.setCenterX(centerX);
                select.setCenterY(centerY);
                select.setRadius(radius);
                //modifier la position pour la prochaine fois duplicated
                getSelectElement();
                dupOldX = select.getCenterX();
                dupOldY = select.getCenterY();

            }

        });
    }

    public void  addMoveSelectEvent(){
        select.setOnMousePressed(e->{
            if(choice.equals("BtnTranslation")||choice.equals("BtnDuplicated")) {
                oldX = e.getX();
                oldY = e.getY();
                dupOldX = select.getCenterX();
                dupOldY = select.getCenterY();

            }
        });

        select.setOnMouseDragged(e->{
            double x = e.getX();
            double y = e.getY();
            double deltaX = x - oldX;
            double deltaY = y - oldY;

            double centerX = select.getCenterX();
            double centerY = select.getCenterY();
            double radius = select.getRadius();

            if(centerY + deltaY + radius > 350){
                deltaY = 350 - centerY - radius;
            }
            if(centerY + deltaY - radius < 0){
                deltaY = radius - centerY;
            }
            if(centerX + deltaX + radius > 725){
                deltaX = 725 - centerX - radius;
            }
            if(centerX + deltaX - radius < 0){
                deltaX = radius - centerX;
            }
            oldX = x;
            oldY = y;

            if(choice.equals("BtnTranslation")) {

                doTranslatation(deltaX, deltaY);

            }
            else if(choice.equals("BtnDuplicated")){

                select.setCenterX(select.getCenterX() + deltaX);
                select.setCenterY(select.getCenterY() + deltaY);

            }

        });

        select.setOnMouseReleased(e->{
                    if(choice.equals("BtnDuplicated")){

                        doDuplicated(select.getCenterX() - dupOldX,select.getCenterY() - dupOldY);

                    }
                }
        );


    }
    /**
     * Initialisation d'une rotation
     * */
    public void doRotation(){
        if(0.0 < select.getRadius()){
            rotate = new Rotate();
            rotate.pivotXProperty().bind(select.centerXProperty());
            rotate.pivotYProperty().bind(select.centerYProperty());
            select.getTransforms().add(rotate);
            getSelectElement();
            dupOldX = select.getCenterX();
            dupOldY = select.getCenterY();
        }
    }
    /****
     * Rotation d'une zone sélectionnée
     * */
    public void setRotate(double angle) {
        if(0.0 < select.getRadius() && choice.equals("BtnRotation")){
            double anglerad = angle * (Math.PI / 180);
            double xu, yu, xv, yv, xm, ym;
            for (Vertex vertex : graph.getVertexList()){
                if (vertex.getSelected()) {
                    xu = vertex.getVertex().getCenterX() - select.getCenterX();
                    yu = vertex.getVertex().getCenterY() - select.getCenterY();
                    xv = (Math.cos(anglerad) * xu) - (Math.sin(anglerad) * yu);
                    yv = (Math.sin(anglerad) * xu) + (Math.cos(anglerad) * yu);
                    xm = select.getCenterX() + xv;
                    ym = select.getCenterY() + yv;
                    vertex.getVertex().setCenterX(xm);
                    vertex.getVertex().setCenterY(ym);
                }
            }
            //zzy
            adjustControl();
            rotate.setAngle(rotate.getAngle() + angle);
        }
    }
    /***
     * distance = deltaX = newSelect - oldSelect
     * */
    public void doDuplicated(double deltaX, double deltaY){
        rotate.setAngle(0);
        doDuplicatedAllVertex(deltaX,deltaY);
        dupVertexList.clear();
        for(Edge edge : graph.getEdgList()){
            if(edge.isSelected()){
                if(edge.getV1().getSelected()&&edge.getV2().getSelected()){
                    duplicatedEdge(edge,deltaX,deltaY);
                }else{//one vertex is selected, move just this vertex
                    if(edge.getV1().getSelected()&&!edge.getV1().isDuplicated()){
                        addDuplicatedVertex(deltaX,deltaY,edge.getV1(),true);
                    }
                    if(edge.getV2().getSelected()&&!edge.getV2().isDuplicated()){
                        addDuplicatedVertex(deltaX,deltaY,edge.getV2(),true);
                    }
                }

            }
        }
        adjustControlCopy(deltaX,deltaY);
        select.setCenterX(0);
        select.setCenterY(0);
        select.setRadius(0);

    }

    /***
     *Duplication des sommets isolés
     * */
    public void doDuplicatedAllVertex(double deltaX,double deltaY){
        for(Vertex vertex : graph.getVertexList()){
            if(vertex.getSelected()&&vertex.isEmpty()){
                //le dernier argument est false, ne peut pas appler doDuplicatedVertex, parce que ca va augmenter le size de graph.getVertexList()
                addDuplicatedVertex(deltaX,deltaY,vertex,false);
            }
        }

        for(Vertex vertex : dupVertexList){

            doDuplicatedVertex(deltaX,deltaY,vertex);
        }

    }
    /****
     * Traitement de v1 et v2 appartenant à la zone sélectionnée
     * Cond1 : v1 non dupliqué et v2 non dupliqué alors on duplique v1 et v2
     * Cond2 : v1 dupliqué et v2 dupliqué alors on cherche v1 et v2 dans la liste
     *         dupVertexList
     * Cond3 : v1 dupliqué on cherche v1 dans la liste dupVertexList, puis on crée une
     *         copie de v2
     * Cond4 : neg(cond3)
     * l'objectif étant de faire la duplication de l'arête concernée
     * */
    public void duplicatedEdge(Edge edge,double deltaX,double deltaY){
        Vertex v1,v2;
        if(!edge.getV1().isDuplicated()&&!edge.getV2().isDuplicated()){
            v1 = addDuplicatedVertex(deltaX,deltaY,edge.getV1(),true);
            v2 = addDuplicatedVertex(deltaX,deltaY,edge.getV2(),true);
        }else if(edge.getV1().isDuplicated()&&edge.getV2().isDuplicated()){
            //si ce vertex est deja duplique, nouveau edge attche l'ancien vertex(la valeur retourné)
            v1 = inDuplicatedVertexList(deltaX,deltaY,edge.getV1());
            v2 = inDuplicatedVertexList(deltaX,deltaY,edge.getV2());
        }else if(edge.getV1().isDuplicated()){
            v1 = inDuplicatedVertexList(deltaX,deltaY,edge.getV1());
            v2 = addDuplicatedVertex(deltaX,deltaY,edge.getV2(),true);
        }else{
            v1 = addDuplicatedVertex(deltaX,deltaY,edge.getV1(),true);
            v2 = inDuplicatedVertexList(deltaX,deltaY,edge.getV2());
        }
        v1.addNeighbour(v2);
        v2.addNeighbour(v1);
        doDuplicatedEdge(v1,v2,edge);
    }
    /**
     *  Recherche de vertex dans la liste dupVertexList
    * **/
    public Vertex inDuplicatedVertexList(double deltaX, double deltaY, Vertex vertex){

        for(Vertex v : dupVertexList){
            if(vertex.getVertex().getCenterX() + deltaX == v.getVertex().getCenterX() && vertex.getVertex().getCenterY() + deltaY == v.getVertex().getCenterY()){
                return v;
            }
        }
        return null;
    }
    /**
     * Création d'edge
     * **/
    public void doDuplicatedEdge(Vertex v1, Vertex v2, Edge edge){

        Edge dupEdge = new Edge(v1,v2);
        dupEdge.getPath().setStroke(edge.getPath().getStroke());
        dupEdge.setLine(edge.isLine());
        dupEdge.drawEdge(graph,edge.getType());
        v1.getVertex().toFront();
        v2.getVertex().toFront();

        QuadCurveTo quadCurveTo = edge.getQuadTo();
        QuadCurveTo dupquad = dupEdge.getQuadTo();
        double quadX = quadCurveTo.getControlX();
        double quadY = quadCurveTo.getControlY();
        dupquad.setControlX(quadX);
        dupquad.setControlY(quadY);
        dupEdgList.add(dupEdge);

    }
    /**
     * Ajout d'une copie de vertex dans une liste temporaire
     **/
    public Vertex addDuplicatedVertex(double deltaX, double deltaY, Vertex vertex, boolean type){
        //type == true appler doDuplicatedVertex() immediatement
        Vertex dupVertex = new Vertex(vertex.getVertex().getCenterX(),vertex.getVertex().getCenterY());
        dupVertexList.add(dupVertex);
        if(type){
            doDuplicatedVertex(deltaX,deltaY,dupVertex);
            vertex.setDuplicated(true);
        }
        return dupVertex;
    }
    /**
     * Ajout de la copie dans notre interface graphique (configG1)
     * **/
    public void doDuplicatedVertex(double deltaX, double deltaY, Vertex vertex){

        graph.getVertexList().add(vertex);
        vertex.drawVertex(configG1);
        graph.addVertexEvent(vertex);
        vertex.getVertex().setCenterX(vertex.getVertex().getCenterX()+deltaX);
        vertex.getVertex().setCenterY(vertex.getVertex().getCenterY()+deltaY);
    }

    public void setMode(String mode){
        this.mode = mode;
    }

    public void doTranslatation(double deltaX, double deltaY){
        rotate.setAngle(0);
        if(selectFlag){
            doSelectPrevious();
            getSelectElement();
            selectFlag = false;
        }
        select.setCenterX(select.getCenterX()+deltaX);
        select.setCenterY(select.getCenterY()+deltaY);

        for(Vertex vertex : graph.getVertexList()){
            if(vertex.getSelected()){
                vertex.getVertex().setCenterX(vertex.getVertex().getCenterX()+deltaX);
                vertex.getVertex().setCenterY(vertex.getVertex().getCenterY()+deltaY);
            }
        }
        adjustControl();

        for(Edge edge: graph.getEdgList()){
            if(edgeInSelect(edge)){
                QuadCurveTo quadCurveTo = edge.getQuadTo();
                if(!edge.isLine()){
                    quadCurveTo.setControlX(quadCurveTo.getControlX()+deltaX);
                    quadCurveTo.setControlY(quadCurveTo.getControlY()+deltaY);
                }
            }
        }

        dupOldX = select.getCenterX();
        dupOldY = select.getCenterY();
    }

    public void zoom(String mode){
        double x,y,vectorX,vectorY,distance,n=(mode.equals("IN")?5:-5);
        if(choice.equals("BtnScale")){
            getSelectElement();
            for(Vertex vertex : graph.getVertexList()){
                if(vertex.getSelected()){
                    x = vertex.getVertex().getCenterX();
                    y = vertex.getVertex().getCenterY();
                    vectorX = x - select.getCenterX();
                    vectorY = y - select.getCenterY();
                    distance = Math.sqrt((select.getCenterX() - x) * (select.getCenterX() - x) +
                                         (select.getCenterY() - y) * (select.getCenterY() - y));
                    x  =  vertex.getVertex().getCenterX()+vectorX/distance*(n);
                    y  =  vertex.getVertex().getCenterY()+vectorY/distance*(n);//new x , y
                    distance =  Math.sqrt((select.getCenterX() - x) * (select.getCenterX() - x) +
                                          (select.getCenterY() - y) * (select.getCenterY() - y));
                    if(mode.equals("IN") && (distance+vertex.getVertex().getRadius())<select.getRadius()){
                        vertex.getVertex().setCenterX(x);
                        vertex.getVertex().setCenterY(y);
                    }else if(mode.equals("OUT") && distance>25){
                        vertex.getVertex().setCenterX(x);
                        vertex.getVertex().setCenterY(y);
                    }
                }
            }
            adjustControl();
            getSelectElement();
            dupOldX = select.getCenterX();
            dupOldY = select.getCenterY();
        }
    }

    public static void getSelectElement() {
        int countVertex = 0, countEdge = 0;

        for(Vertex vertex : graph.getVertexList()){
            if(vertexInSelect(vertex)){
                countVertex++;
                vertex.setSelected(true);
            }
        }

        for(Edge edge : graph.getEdgList()){
            if(edgeInSelect(edge)){
                countEdge++;
                edge.setSelected(true);
            }
        }
    }

    private static boolean edgeInSelect(Edge edge){
        if(vertexInSelect(edge.getV1()) || vertexInSelect(edge.getV2())){
            return true;
        }
        return false;

    }

    private static boolean vertexInSelect(Vertex vertex){
        double x = vertex.getVertex().getCenterX();
        double y = vertex.getVertex().getCenterY();
        double distance = Math.sqrt((select.getCenterX() - x) * (select.getCenterX() - x) +
                (select.getCenterY() - y) * (select.getCenterY() - y));
        if(distance <= select.getRadius() - vertex.getVertex().getRadius()){
            return true;
        }
        return false;

    }

    public void addTextFiledListener(TextField textField){

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]*")||newValue.equals("0")){

                textField.setText(oldValue);

            }
        });

    }

    public void adjustControl(){
        for(Edge edge: graph.getEdgList()){
            if(edgeInSelect(edge)){
                QuadCurveTo quadCurveTo = edge.getQuadTo();
                if(edge.isLine()){
                    quadCurveTo.setControlX((edge.getV1().getVertex().getCenterX()+edge.getV2().getVertex().getCenterX())/2);
                    quadCurveTo.setControlY((edge.getV1().getVertex().getCenterY()+edge.getV2().getVertex().getCenterY())/2);
                }
            }
        }
    }
    /***
     *
     * */
    public void adjustControlCopy(double deltaX,double deltaY){
        //adapter control
        for(Edge edge: dupEdgList){
            graph.getEdgList().add(edge);
            QuadCurveTo quadCurveTo = edge.getQuadTo();
            if(!edge.isLine()){
                quadCurveTo.setControlX(quadCurveTo.getControlX()+deltaX);
                quadCurveTo.setControlY(quadCurveTo.getControlY()+deltaY);
            }else{
                quadCurveTo.setControlX((edge.getV1().getVertex().getCenterX()+edge.getV2().getVertex().getCenterX())/2);
                quadCurveTo.setControlY((edge.getV1().getVertex().getCenterY()+edge.getV2().getVertex().getCenterY())/2);
            }

        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        graph = new Graph();
        rotate = new Rotate();
        graph.setGroup(configG1);
        this.choice = "";
        this.selectFlag = false;
        this.select = new Circle();
        this.select.setId("select");
        configG1.getChildren().add(select);
        setConfigInit();
        addMoveSelectEvent();
        addTextFiledListener(configTextfield1);
        addTextFiledListener(configTextfield2);
    }

    public void setConfigInit(){
        if(mode.equals("Gen")){
            setNimComponentsVisib(false);
            setGenComponentsVisib(true);
        }else if(mode.equals("Nim")) {
            setGenComponentsVisib(false);
            setNimComponentsVisib(true);
        }
    }

    public void setGenComponentsVisib(boolean visib){
        configG1.setVisible(visib);
        configG2.setVisible(visib);
        configLeftTilePane.setVisible(visib);
    }

    public void setNimComponentsVisib(boolean visib){
        configG3.setVisible(visib);
    }

    public void exit(MenuItem menu){
        menu.setOnAction( e ->{
            Platform.exit();
        });
    }

}
