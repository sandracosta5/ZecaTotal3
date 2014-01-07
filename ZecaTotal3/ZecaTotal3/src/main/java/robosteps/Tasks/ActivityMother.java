/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robosteps.Tasks;

import com.robosteps.api.core.RsRobot;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import robosteps.demo.App;
import robosteps.demo.Card;
import robosteps.demo.Emotion;
import robosteps.demo.expdata.Child;
import robosteps.sessions.Answer;
import robosteps.sessions.Performance;
import robosteps.sessions.Session;

/**
 *
 * @author Utilizador
 */
public class ActivityMother implements Runnable{
    
    
  
    public static enum type{RECOGNIZE,STORY,IMITATION};      
    public boolean started;
    public String name = "mother";
    public int state=-1; //running 0, paused 1, stopped 2
    public final int STOPPED=2;
    public final int PAUSED=1;
    public final int RUNNING=0;
    public boolean listen=false;
    public RsRobot myRobot;
    Child child;
    String childCode;
    Session session;
    String sessionCode;
    Performance perf;
    Answer answ;    
    App myapp = null;
    GregorianCalendar later;
    float duration;
    Object pauseMonitor;
     Card Fear = new Card ("Fear", "V9/afraidHeadGesture", Emotion.AFRAID);
    Card Anger = new Card ("Anger", "V9/angryHeadGesture", Emotion.ANGRY);
    Card Joy = new Card ("Joy", "V9/happyHeadGesture", Emotion.HAPPY);
    Card Sadness = new Card ("Sadness", "V9/sadHeadGesture", Emotion.SAD);
    Card Surprise = new Card ("Surprise", "V9/surprisedHeadGesture", Emotion.SURPRISED);
    public  ArrayList emoCards;
    
    public ActivityMother(Child chil, Session sess){
  
        child = chil;
        childCode = child.getCodeChild();
        session = sess;
        sessionCode = session.getCodeSession();
         //Creates answer and performance
        answ = new Answer ();
        perf = new Performance ();
        emoCards=new ArrayList<Card>(5);
        emoCards.add(0,null);
        emoCards.add(1, Fear);
        emoCards.add(2, Anger);
        emoCards.add(3,Joy);
        emoCards.add(4,Sadness);
        emoCards.add(5,Surprise);                
    }
    
    public void run() {
        
        while(state==RUNNING)      processActivity();
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void startActivity(){
        Thread thread=new Thread(this);
        thread.start();
                
    }
    
    public void processAnswer(String answerString){
    
    }
    /**
     * to override in actuaL ACTIVITIES
     * in there put the actual sequence 
     * of action, events and responses
     */
    public void processActivity(){
    
    }
   /*
    * should stop or kill activity safely
    * save data
    * exit activity
    * TODO: set 
    */
    public void interruptActivity(){
       
        state=PAUSED;
    }
   
    public void waitForIt(){
         while(state!=RUNNING){
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(ActivityMother.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
    }
    
    public void resumeActivity(){
        
        System.out.println("Resuming");
        state=RUNNING;
   }
   
   
   public void saveActivity(GregorianCalendar initTimeSession){
   
            //Defines time the session ended
            later = new GregorianCalendar(); 
            //Save this time as endTime
            session.setEndTime(later); 
            //Calculates duration of the session
            duration = later.getTimeInMillis() - initTimeSession.getTimeInMillis();       
            duration = duration/1000;
            session.setDuration(duration);
            //Saves data from the child's performance
            perf.saveSessionPerformanceData(session, childCode, sessionCode);
            //Saves data from the child's session
            session.saveSessionData(childCode);
   
   }
   
   public void setApp(App ap){
       myapp=ap;
       setRobot(myapp.getRobot());
       
   }
    public void setRobot(RsRobot theRobot){
    
        myRobot=theRobot;
        answ.setRobot(myRobot);
    }
}

