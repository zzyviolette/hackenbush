package sample;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import sample.Nim.Move;
import sample.controller.GameController;


import java.util.ArrayList;
import java.util.Random;

public class Ai extends Player {
    private static Nim nim;
    private static Move coup;
    private int [] heap;

    public Ai(String name, Color color, int []heap, Nim nim){
        super(name,color);
        this.heap = heap;
        this.nim = nim;
    }

    @Override
    public boolean actionDone(Graph graph, String mode){
        super.pause((long) GameController.pause * 20);
        Color opponentColor = (this.getColor() == Color.RED) ? Color.BLUE : Color.RED;
        if(mode.equals("Nim")){
            coup = nim.nextMove(heap);
            Platform.runLater(()-> {
                graph.deleteNimEdge(coup);
            });
            Nim.applyMove(coup, heap);
        }else if(mode.equals("Gen")){

            ArrayList<Edge> tmpEdgeList = new ArrayList<>();
            for(Edge edge : graph.getEdgList()){
                if(!edge.getPath().getStroke().equals(opponentColor)){
                    tmpEdgeList.add(edge);
                }
            }
            Random random = new Random();
            int index = random.nextInt(tmpEdgeList.size());
            Platform.runLater(()-> {
                graph.check(graph.getEdgList().get(index));
            });
            if((graph.numberOfColor(opponentColor) + graph.numberOfColor(Color.GREEN))==0){
                coup = Move.EMPTY;
            }
            tmpEdgeList.clear();
        }
        return true;
    }
}
