/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robosteps.demo.utils;

import java.util.Scanner;
import robosteps.demo.App;

/**
 *
 * @author Utilizador
 */
public class KeypadListener implements Runnable {
     Scanner input; 
     App mainApp=null;
     boolean running;
     
     public KeypadListener(){
      input = new Scanner(System.in);      
     }
     
     public void stopKeypad(){
         running=false;
     }
     
     public void setApp(App _app){
         mainApp=_app;
                 
     }
     
     public void  getNext(){
         String s=input.next();
         System.out.println("key got:"+s);
        mainApp.processExternalEvent(s);
     }

    @Override
    public void run() {
        running=true;
        System.out.println("Started keypad thingy");
        while(running){
            getNext();
        }        
        System.out.println("Exiting Keypad listener");
    }
}
