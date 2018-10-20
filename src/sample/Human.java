package sample;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import sample.Nim.Move;
import sample.controller.GameController;

public class Human extends Player {
    private static Move coup;
    private int [] heap;

    public Human(String name,Color color,int []heap) {
        super(name,color);
        this.heap = heap;
    }

    @Override
    public boolean actionDone(Graph graph, String mode){
        Color opponentColor = (this.getColor() == Color.RED) ? Color.BLUE : Color.RED;
        boolean wait = false;
        while(!wait){
            if(GameController.pause == 0)
                return false;
            if(mode.equals("Nim") && graph.isClick()){
                wait = true;
                graph.setClick(false);
            }else if(mode.equals("Gen") && graph.isClick()) {
//                System.out.println("click");
                if (!graph.getCheckEdge().getPath().getStroke().equals(opponentColor)) {
                    wait = true;
                } else if (graph.getCheckEdge().getPath().getStroke().equals(opponentColor)) {
                    GameController.alerte();
                }
                graph.setClick(false);
            }else{
                try {
                    Thread.sleep((long) GameController.pause * 40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(mode.equals("Nim")){
            coup = graph.getCoup();
            Platform.runLater(()->{
                graph.deleteNimEdge(coup);
            });
            Nim.applyMove(coup,heap);
        }else if(mode.equals("Gen")){
            Platform.runLater(()->{
                graph.check(graph.getCheckEdge());
            });
            if((graph.numberOfColor(opponentColor) + graph.numberOfColor(Color.GREEN))==0){
                coup = Move.EMPTY;
            }
        }
        return true;
    }

}
