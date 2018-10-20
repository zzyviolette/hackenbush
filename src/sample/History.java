package sample;

import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class History{
    private static ArrayList<String> pageshist = new ArrayList<>();
    private static ArrayList<Vertex> gamehist = new ArrayList<>();
    private static int pages_cpt = 0;

    public static ArrayList<String> getPageshist(){
        return pageshist;
    }

    public static int getPageHistSize(){
        return (pageshist.size());
    }

    public static int getPages_cpt(){
        return pages_cpt;
    }

    public static void setPages_cpt(int cpt){
        pages_cpt = cpt;
    }

    public void addPage(String mode, String page, Node node,Parent r){
        if(mode.equals("Start")){
            pageshist.add(page);
        }else if(!pageshist .contains(page) && mode.equals("Add")){
            pageshist.add(page);
            pages_cpt++;
        }else if(pageshist.contains(page) && mode.equals("Add")){
            pages_cpt++;
        }
        URL url = getClass().getResource(pageshist.get(pages_cpt));
        Main.setView(url, node,pageshist.get(pages_cpt),r);
    }

    public static void addGameHist(Vertex v){
        System.out.println("Test hist : "+v.getNeighbourList().size());
        gamehist.add(v);
    }

    public static void serialize(Graph g, File file){
        try {
            ObjectOutputStream oos =  new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(g) ;
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Graph deserialize(File file){
        Graph g = null;
        try {
            ObjectInputStream ois =  new ObjectInputStream(new FileInputStream(file));
            g = (Graph) ois.readObject() ;
            ois.close();
        } catch (IOException |ClassNotFoundException e) {
            e.printStackTrace();
        }
        return g;
    }
}
