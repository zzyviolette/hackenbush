package sample;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.controller.ConfigController;
import sample.History;

public class KeyBoardEvent {

    public boolean isPageHistEvent(KeyEvent e){
        return(e.getCode() == KeyCode.F && e.isControlDown() ||
                e.getCode() == KeyCode.B && e.isControlDown());
    }

    public void add_Keypress_event(Scene scene,String page){

        scene.addEventFilter(KeyEvent.KEY_PRESSED,
                e->{
                    switch(page){
                        case "fxml/Menu.fxml":
                            menuBoardEventManager(e,scene);
                            break;
                        case "fxml/Config.fxml":
                            configBoardEventManager(e,scene);
                            break;
                        case "fxml/Game.fxml":
                            gameBoardEventManager(e,scene);
                            break;
                    }

                });
    }

    public void pageHistManager(KeyEvent e,Scene scene){
        int n =2;
        int cpt = History.getPages_cpt();

        boolean crlt_backward = e.getCode() == KeyCode.B && e.isControlDown();
        boolean crlt_forward  = e.getCode() == KeyCode.F && e.isControlDown();

        boolean backward = 0 < cpt;
        boolean forward = 0 <= cpt && cpt < n && cpt+1 < History.getPageHistSize();

        if(crlt_backward  && backward){
            History.setPages_cpt(cpt-1);
            new History().addPage("Set","backward",scene.getRoot(),null);
        }else if(crlt_forward && forward){
            History.setPages_cpt(cpt+1);
            new History().addPage("Set","forward",scene.getRoot(),null);
        }

    }

    public void menuBoardEventManager(KeyEvent e,Scene scene){
        if(isPageHistEvent(e)){
            pageHistManager(e,scene);
        }else{

        }
    }

    public void configBoardEventManager(KeyEvent e,Scene scene){
        if(isPageHistEvent(e)){
            pageHistManager(e,scene);
        }else{
            ConfigController c = new ConfigController();
            double rotateAngle = 15;
            switch (e.getCode().toString()){
                case "UP":
                    c.zoom("IN");break;
                case "DOWN":
                    c.zoom("OUT");break;
                case "LEFT":
                    c.setRotate(rotateAngle);break;
                case "RIGHT":
                    c.setRotate(-rotateAngle);break;
            }

        }
    }

    public void gameBoardEventManager(KeyEvent e,Scene scene){
        if(isPageHistEvent(e)){
            pageHistManager(e,scene);
        }else{
        }
    }
}
