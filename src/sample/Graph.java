package sample;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import sample.Nim.Move;

import java.io.Serializable;
import java.util.ArrayList;

public class Graph implements Serializable{
    private static String choice;
    private static String mode;
    private static Group group;
    private static boolean srcFound;
    private ArrayList<Vertex> vertexList;
    private ArrayList<Edge> edgList;
    private ArrayList<Vertex> tmp;

    private static Move coup;
    private boolean click;
    private Edge checkEdge;

    public Graph(){
        this.choice =  "";
        this.mode = "";
        this.vertexList = new ArrayList<>();
        this.edgList = new ArrayList<>();
        this.tmp = new ArrayList<>();
        this.click = false;
        this.coup = new Move(-1,-1);
    }

    /**
     * Vérifie si un clic à eu lieu sur une des arêtes
     * */
    public boolean isClick(){
        return click;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public Group getGroup(){
        return(group);
    }

    public void setGroup(Group group){
        this.group = group;
    }

    /**
     * Retourner quelle arête a été choisi (Mode Gen)
     * */
    public Edge getCheckEdge(){
        return checkEdge;
    }

    /**
     * Retourner quelle arête a été choisi (Mode Nim)
     * */
    public Move getCoup(){
        return this.coup;
    }

    public int getVertexIndex(Vertex v){
        return (vertexList.indexOf(v));
    }

    public ArrayList<Edge> getEdgList(){
        return (this.edgList);
    }

    public ArrayList<Vertex> getVertexList(){
        return (this.vertexList);
    }

    public void setChoice(String choice){
        this.choice = choice;
    }

    public void setMode(String mode){
        this.mode = mode;
    }

    /**
     * Création des sommets
     * */
    public void addVertex(Vertex v){
        vertexList.add(v);
    }

    /**
     * Gestion des évènements liés à un sommet
     * */
    public void addVertexEvent(Vertex v){
        boolean curbe = false;
        v.getVertex().setOnMouseClicked(e->{
            if(choice.equals("BtnErase")){
                removeVertex(v);
            }else if(choice.equals("BtnEdge")){
                createEdgeProcess(v);
            }
        });
    }

    /**
     * Création des arêtes
     * */
    public void createEdgeProcess(Vertex v){
        tmp.add(v);
        if(tmp.size()==1){
            DropShadow dropShadow = new DropShadow();
            dropShadow.setBlurType(BlurType.GAUSSIAN);
            dropShadow.setColor(Color.ROSYBROWN);
            tmp.get(0).getVertex().setEffect(dropShadow);
        }
        if(tmp.size() == 2){
            Edge edge =new Edge(tmp.get(0),tmp.get(1));
            tmp.get(0).addNeighbour(tmp.get(1));
            tmp.get(1).addNeighbour(tmp.get(0));
            edgList.add(edge);
            if(tmp.get(0)==tmp.get(1)){
                edge.drawEdge(this,1);
                tmp.get(0).getVertex().toFront();
            }else{
                edge.drawEdge(this,2);
                tmp.get(0).getVertex().toFront();
                tmp.get(1).getVertex().toFront();
            }
            tmp.get(0).getVertex().setEffect(null);
            tmp.clear();
        }
    }

    /**
     * Gestion des évènements liés à une arête
     * */
    public void addLineEvent(Path path,Edge edge){
        path.setOnMouseClicked(e->{
            switch (choice){
                case "Game":
                    if(mode.equals("Gen")){
                        checkEdge = edge;
                    }
                    else if(mode.equals("Nim")) {
                        coup.setIndex(edge.getIndexTige());
                        coup.setSize(edge.getNumero());
                    }
                    click = true;
                    break;
                case "BtnErase":
                    check(edge);
                    //removeEdge(edge);
                default:
                    changeColor(edge);

            }
        });
        path.setOnMouseDragged(e->{
            if(choice.equals("BtnEdge")){
                edge.move(e.getX(),e.getY());
            }
        });
    }

    public void removeVertex(Vertex v){
        Circle c = v.getVertex();
        c.setOnMouseClicked(null);
        ArrayList<Edge> tmpEdge = new ArrayList<>();
        for(Edge edge: this.getEdgList()){
            if(edge.getV1()== v || edge.getV2()== v){
                tmpEdge.add(edge);
            }
        }
        for(Edge edge: tmpEdge){
            edge.getPath().setOnMouseClicked(null);
            group.getChildren().remove(edge.getPath());
            this.getEdgList().remove(edge);
        }
        tmpEdge.clear();
        removeFadingOut(v.getVertex());
        vertexList.remove(v);
    }

    public void removeEdge(Edge edge){
        edge.getPath().setOnMouseClicked(null);
        removeFadingOut(edge.getPath());
        edgList.remove(edge);
    }

    public void removeFadingOut(Node node) {

        FadeTransition transition = new FadeTransition(Duration.millis(250), node);
        transition.setFromValue(node.getOpacity());
        transition.setToValue(0);
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setOnFinished(finishHim -> {
            group.getChildren().remove(node);
        });
        transition.play();

    }

    /**
     * Méthode permettant le changement de couleur d'une arête
     * */
    public void changeColor(Edge edge){
        Path path = edge.getPath();
        if(path.getStroke() == Color.GREEN){
            path.setStroke(Color.RED);
        }else if(path.getStroke() == Color.RED){
            path.setStroke(Color.BLUE);
        }else{
            path.setStroke(Color.GREEN);
        }
    }

    public void deleteNimEdge(Move move){
        int size = move.getSize();//le nb de reste
        int index = move.getIndex();
        for (int i = edgList.size() - 1; i >= 0; i--) {
            if (edgList.get(i).getIndexTige() == index && edgList.get(i).getNumero()>=size) {
                //remove path
                edgList.get(i).getPath().setOnMouseClicked(null);
                removeFadingOut(edgList.get(i).getPath());
                //remove v2
                removeFadingOut(edgList.get(i).getV2().getVertex());
                vertexList.remove(edgList.get(i).getV2());
                //if the vertex connected to the land, remove v1 too
                if(size==0){
                    removeFadingOut(edgList.get(i).getV1().getVertex());
                    vertexList.remove(edgList.get(i).getV1());
                }
                //remove edge
                edgList.remove(edgList.get(i));
            }
        }

    }
    /***
     * Test de collision entre sommets
     * */
    public boolean collision(Vertex v){
        boolean res = false;
        Circle c1,c2 = v.getVertex();
        double dx,dy,d;

        for(int i = 0; i < vertexList.size();i++){
            c1 = vertexList.get(i).getVertex();
            dx = c1.getCenterX()-c2.getCenterX();
            dy = c1.getCenterY() -c2.getCenterY();
            d = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));

            if(d < (c1.getRadius()+c2.getRadius())){
                res = true;
                break;
            }
        }

        return res;
    }
    /**
     * Nettoyage de configPane avant le début du jeu
     * */
    public void  clearConfigPane(){
        clearNoConnectEdge();
        clearNoConnectVertex();

    }

    public void clearNoConnectVertex(){
        Vertex v;
        ArrayList<Vertex> removeList = new ArrayList<>();
        for(int i = 0; i < vertexList.size();i++){
            v = vertexList.get(i);
            if(!v.getConnect())
                removeList.add(v);

        }

        for(int j = 0; j < removeList.size();j++){
            v = removeList.get(j);
            removeVertex(v);
        }

    }

    public void clearNoConnectEdge(){
        Vertex v1,v2;
        Edge edge;
        ArrayList<Edge> removeList = new ArrayList<>();

        for(int i = 0; i < edgList.size();i++){
            edge = edgList.get(i);
            v1 = edge.getV1();
            v2 = edge.getV2();
            if(!v1.getConnect() && !v2.getConnect())
                removeList.add(edge);

        }

        for(int j = 0; j < removeList.size();j++){
            edge = removeList.get(j);
            v1 = edge.getV1();
            v2 = edge.getV2();
            removeEdge(edge);
            removeVertex(v1);
            removeVertex(v2);
        }
    }
    /**
     * Gestion de multiArêtes
     * */
    public boolean ismultPath(Edge edge){
        int cpt = 0;
        Vertex v1 = edge.getV1();
        Vertex v2 = edge.getV2();

        for(int i = 0; i < edgList.size();i++){
            Edge edge1 = edgList.get(i);
            if(edge1.getV1()==v1 && edge1.getV2() == v2||
                    edge1.getV1()==v2 && edge1.getV2() == v1){
                cpt++;
            }
        }
        if(cpt == 1){
            return false;
        }else if(1 < cpt){
            removeEdge(edge);
        }
        return true;
    }
    /**
     * Détection de l'arête qui a été selectionnée
     * puis élimination des voisins non connecté au sol
     * */
    public void check(Edge edge){
        if(!ismultPath(edge)){
            Vertex v1 = edge.getV1();
            Vertex v2 = edge.getV2();
            v1.removeNeighbour(v2);
            removeEdge(edge);
            srcFound = false;
            update(v1);
            srcFound = false;
            update(v2);
            for(int i = 0;i < tmp.size();i++){
                v1 = tmp.get(i);
                v1.setVisited(false);
                if(!v1.getConnect()){
                    if(choice.equals("Game")){
                        cutlink(v1,v1.getNeighbourList());
                        removeVertex(v1);
                    }
                }
                if(v1.isSrc() && v1.getNeighbourList().size() == 0){
                    if(choice.equals("Game"))
                        removeVertex(v1);
                }
            }
        }
        tmp.clear();
    }
    /**
     * Marquage de chaque sommet connecté à l'arête selectionné
     * */
    public void update(Vertex v){
        Vertex neighbour;
        ArrayList<Vertex> neighbourList;

        if(!v.getVisited() && !srcFound){
            v.setVisited(true);
            v.setConnectStatus(false);
            tmp.add(v);
            neighbourList = v.getNeighbourList();
            for(int i = 0; i < neighbourList.size();i++){
                neighbour = neighbourList.get(i);
                if(!neighbour.getVisited())
                    update(neighbour);

            }
        }
        if(v.getSrc()){
            v.connectAll(true);
            srcFound = true;
        }

    }
    /**
     * Suppression des sommets non connectés au sol
     * */
    public void cutlink(Vertex v,ArrayList<Vertex> neighbourList){
        Edge edge;
        ArrayList<Edge> removeList = new ArrayList<>();

        for(int i = 0; i < neighbourList.size();i++){
            for(int j = 0; j < edgList.size();j++){
                edge = edgList.get(j);
                if(edge.getV1() == v && edge.getV2() == neighbourList.get(i)||
                        edge.getV1() == neighbourList.get(i) && edge.getV2() == v)
                    removeList.add(edge);
            }
        }

        for(int k = 0; k < removeList.size();k++){
            edge = removeList.get(k);
            removeEdge(edge);
        }
    }

    /**
     * Méthode de victoire
     * */
    public boolean win(String mode){
        try{
            Thread.sleep(2);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return(edgList.size()==0 || coup == Move.EMPTY);

    }

    /**
     * Nombre de edges de ce couleur qui restent dans le graph
     * */

    public int numberOfColor(Color color){
        int count=0;
        for(Edge edge : this.getEdgList()){
            if(edge.getPath().getStroke() == color){
                count++;
            }
        }
        return count;
    }
    /**
     * Importation du graphe g
     * **/
    public void importG(Graph g){
        importVertex(g);
        importEdge(g);
    }
    /**
     * Importation des sommets
     * */
    public void importVertex(Graph g){
        for(int i = 0; i < g.vertexList.size();i++){
            Vertex v = g.vertexList.get(i);
            v.reinitCircle();
            this.addVertex(v);
            v.drawVertex(group);
            this.addVertexEvent(v);
        }
    }
    /***
     * Importation des arêtes
     * */
    public void importEdge(Graph g){
        for(int i = 0; i < g.edgList.size();i++){
            Edge edge = g.edgList.get(i);
            edge.reinitPath();
            edge.drawEdge(this,edge.getType());
        }
    }

}
