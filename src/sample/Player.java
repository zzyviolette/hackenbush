package sample;

import javafx.scene.paint.Color;
import sample.Graph;

public abstract class Player{
    private String name;
    private Color color;

    public Player(String name,Color color){
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void pause(long time){
        try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
        }
    }

    public abstract boolean actionDone(Graph graph, String mode);

    public Color getColor() {
        return color;
    }

}
