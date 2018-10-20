package sample;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.Serializable;
import java.util.ArrayList;


public class Vertex implements Serializable{
    private transient Circle vertex;
    private double x,y;
    private boolean selected;
    private boolean connect;
    private boolean src;
    private boolean visited;
    private ArrayList<Vertex> neighbourList;
    private boolean duplicated;


    public Vertex(double x,double y){
        this.x = x;
        this.y = y;
        this.vertex = new Circle(x,y,13);
        this.selected = false;
        this.connect = false;
        this.neighbourList = new ArrayList<Vertex>();
        this.duplicated = false;
    }

    public Vertex(double raduis,double x,double y){
        this.vertex = new Circle(x,y,raduis);
        this.neighbourList = new ArrayList<Vertex>();
        this.duplicated = false;
        this.selected = false;
    }

    public Circle getVertex(){
        return vertex;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    /***
     *
     * **/
    public void connectAll(boolean connect){
        if(!this.getConnect()){
            this.connect = connect;
            for(int i = 0; i < neighbourList.size();i++){
                neighbourList.get(i).connectAll(true);
            }
        }
    }
    public void setConnect(boolean connect){
        this.connect = connect;
    }

    public void setConnectStatus(boolean connect){
        this.connect = connect;
    }

    public boolean getConnect() {
        return connect;
    }

    public void setSrc(boolean src){
        this.src = src;
    }

    public boolean isSrc(){
        return src;
    }

    public boolean getSrc(){
        return(src);
    }

    public ArrayList<Vertex> getNeighbourList(){
        return(neighbourList);
    }

    public boolean getVisited(){
        return  visited;
    }

    public void setVisited(boolean visited){
        this.visited = visited;
    }

    public void removeNeighbour(Vertex v){
        if(neighbourList.contains(v)){
            neighbourList.remove(v);
            v.neighbourList.remove(this);
        }
    }

    public void addNeighbour(Vertex v){
        if(!neighbourList.contains(v))
            neighbourList.add(v);
    }

    public void drawVertex(Group group){
        this.vertex.getStyleClass().add("vertex");
        group.getChildren().add(vertex);
    }

    public boolean isEmpty(){

        return this.neighbourList.size()==0 ? true : false;
    }

    public boolean isDuplicated() {
        return duplicated;
    }

    public void setDuplicated(boolean isduplicated) {
        this.duplicated = isduplicated;
    }

    public void setRaduis(double raduis){
        vertex.setRadius(raduis);
    }

    public void reinitCircle(){
        this.vertex = new Circle(x,y,13);
    }
}
