package sample;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.io.Serializable;

public class Edge implements Serializable{
    private Vertex v1;
    private Vertex v2;
    private transient Path path;
    private boolean selected;
    private int numero;
    private int indexTige;
    private boolean isLine;
    private int type;
    private MoveTo moveTo;
    private QuadCurveTo quadTo;


    public Edge(Vertex v1,Vertex v2){
        this.v1 = v1;
        this.v2 = v2;
        this.selected = false;
        this.path = new Path();
        this.path.setStrokeWidth(4);
        this.path.setStroke(Color.GREEN);
        this.isLine = true;
    }

    public Edge(Vertex v1,Vertex v2, int indexTige, int numero){
        this.v1 = v1;
        this.v2 = v2;
        this.selected = false;
        this.path = new Path();
        this.path.setStrokeWidth(4);
        this.path.setStroke(Color.GREEN);
        this.indexTige = indexTige;
        this.numero = numero;
    }

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }

    public Path getPath(){
        return(this.path);
    }

    public QuadCurveTo getQuadTo() {
        return quadTo;
    }

    public int getType(){
        return type;
    }

    public int getNumero() {
        return numero;
    }

    public int getIndexTige() {
        return indexTige;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isLine() {

        return isLine;
    }

    public void setLine(boolean isLine){
        this.isLine = isLine;
    }

    public void drawEdge(Graph graph, int type){
        this.type = type;
        this.moveTo = new MoveTo();
        Group group = graph.getGroup();
        switch (type) {
            case 1:
                loopEdge();
                break;
            case 2:
                singleEdge();
                break;
        }
        graph.addLineEvent(path,this);
        group.getChildren().add(path);

    }

    public void loopEdge(){
        setConnect();
        moveTo.xProperty().bind((this.v1.getVertex().centerXProperty()).subtract(this.v1.getVertex().radiusProperty()));
        moveTo.yProperty().bind(this.v1.getVertex().centerYProperty());
        quadTo = new QuadCurveTo();
        quadTo.xProperty().bind(this.v2.getVertex().centerXProperty().add(this.v2.getVertex().radiusProperty()));
        quadTo.yProperty().bind(this.v2.getVertex().centerYProperty());
        quadTo.setControlX(this.v1.getVertex().getCenterX());
        quadTo.setControlY(this.v1.getVertex().getCenterY()/2-10);
        path.getElements().addAll(moveTo,quadTo);
        isLine = false;

    }

    public void singleEdge(){
        setConnect();
        moveTo.xProperty().bind(this.v1.getVertex().centerXProperty());
        moveTo.yProperty().bind(this.v1.getVertex().centerYProperty());
        quadTo = new QuadCurveTo();
        quadTo.xProperty().bind(this.v2.getVertex().centerXProperty());
        quadTo.yProperty().bind(this.v2.getVertex().centerYProperty());
        quadTo.setControlX((this.v2.getVertex().getCenterX()+this.getV1().getVertex().getCenterX())/2);
        quadTo.setControlY((this.v2.getVertex().getCenterY()+this.getV1().getVertex().getCenterY())/2);
        path.getElements().addAll(moveTo,quadTo);

    }

    public void setConnect(){
        if(v1.getConnect())
            v2.connectAll(true);
        if(v2.getConnect())
            v1.connectAll(true);
    }

    public void move(double x, double y){
        quadTo.setControlX(x);
        quadTo.setControlY(y);
        if(x==(moveTo.getX()+quadTo.getX())/2 && y==(moveTo.getY()+quadTo.getY())/2){
            isLine = true;
        }else {
            isLine = false;
        }
    }


    public void reinitPath(){
        this.path = new Path();
        this.path.setStrokeWidth(4);
        this.path.setStroke(Color.GREEN);
        this.path.getElements().addAll(moveTo,quadTo);
    }

}
