package sample;

import sample.controller.GameController;

public class Chrono {

    private long tempsDepart = 0;
    private long tempsFin = 0;
    private static String min;
    private static String second;

    public void start(){
        tempsDepart = System.currentTimeMillis()/1000;
        tempsFin = 0;
        min = "";
        second = "";
    }

    public String getDureeSec(){
        tempsFin = System.currentTimeMillis()/1000;
        long current_time =(tempsFin-tempsDepart);
        String min = (current_time/60)<10?"0"+(current_time/60):(current_time/60)+"";
        String second = current_time%60+"";
        return(min+":"+second);
    }
}
