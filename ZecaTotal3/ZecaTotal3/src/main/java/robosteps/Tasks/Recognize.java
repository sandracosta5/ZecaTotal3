package robosteps.Tasks;

import com.robosteps.api.core.Robosteps;
import com.robosteps.api.core.RsRobot;
import com.robosteps.api.core.UserSettings;
import friendularity.test.camera.r50.ImageMonitor;
import java.io.FileNotFoundException;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.robokind.api.animation.Animation;
import robosteps.demo.App;
import robosteps.demo.Card;
import robosteps.demo.Emotion;
import robosteps.demo.expdata.Child;
import robosteps.demo.expdata.Reinforcement;
import robosteps.sessions.Answer;
import robosteps.sessions.Performance;
import robosteps.sessions.Session;

/**
 * @author Sandra Costa - scosta@dei.uminho.pt
 */

public class Recognize extends ActivityMother{
 
    
    public static final int NUM_EMOTIONS = 5;
     
    public String emotionCode = "";
    public String changeActivity = "";
    boolean firstTime = true;
    int repeat = 0;
    boolean proceed = true;
    Card emotionCard = new Card();
   
    Animation blink = Robosteps.loadAnimation("V9/blink");
    Animation emotionOut = Robosteps.loadAnimation("V9/04");
    ImageMonitor monit = new ImageMonitor();
    GregorianCalendar prompt;
    GregorianCalendar promptAnswered;
    GregorianCalendar later;
    String randEm = "";
    int c = 0;
    float duration;
 /**
     * Inheritance from ActivityMother
     * @param child - Child child; String childCode;
     * @param session - Session session; String sessionCode;
     * @param perf - Performance perf;
     * @param answ - Answer answ;
     */
   
    public Recognize(Child chil, Session newSess){
    
            super(chil, newSess);
            
    }
    @Override
    /**
     * Activity Recognize: The robot shows a facial expression and the corresponding gesture, and the child has to identify the emotion
     */
    public void processActivity(){

        state=RUNNING;
        //Gets Reinforcement of the child
        Reinforcement myChildReinforcement = child.getReinforcementChild();
        GregorianCalendar initTimeSession = session.getStartTime();
        do{
            //Defines code of the answer
            answ.setCodeAnswer(Integer.toString(perf.numAnswers()));
            //Time of the prompt
            prompt = new GregorianCalendar();
            
            waitForIt();
            
            Robosteps.sleep(3000);
            //Gets prompt
            randEm = randEmotion();
            emotionOut = Robosteps.loadAnimation(randEm);
            //The emotion shown by the robot is saved as input
            answ.setInput(emotionCard.getCode());
            waitForIt();
            //Robot shows facial expressions
            randInstruction();
            waitForIt();
            do{
                //Shows the emotion
                Robosteps.sleep(1000);
                waitForIt();
                myRobot.playAnimation(emotionOut);
                Robosteps.sleep(3000);
                waitForIt();
                //Receives String form QR Code
                emotionCode = myapp.getQRCode();
                System.out.println("Got "+ emotionCode);
            //}while("REPEAT".equals(emotionCode));
            }while("*".equals(emotionCode));

            //Defines answer from the child
            answ.setOutput(emotionCode); 
            //Time of the answer was shown
            promptAnswered = new GregorianCalendar(); 
            //Calculates the time between the prompt and the answer  
            answ.calculateResponseTime (prompt, promptAnswered);          
                                 
            //Verifies if the answer is right or wrong (IF Stop Card is show it does not show wrong reinforcement
            if(!"0".equals(emotionCode))
            {   
                waitForIt();
                answ.matching(myChildReinforcement); 
            }
            //Insert answer in the TreeMap
            perf.insertAnswer(answ); 
            //Calculates if the answer is wrong or right, and updates counter
            perf.calculatePerformance(answ);
            //Increments codeAnswer
            c = Integer.parseInt(answ.getCodeAnswer());
            c++;
            answ.setCodeAnswer(Integer.toString(c));

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

            //Saves answers in the file
            try {
                answ.saveAnswerData(childCode, sessionCode);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(!"0".equals(emotionCode));
        state=STOPPED;
   }
    
    
   
    /**
     * 
     * @param i - limit of the random method
     * @return int random
     */
    public int randNum(int i){
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(i);
        
        randomInt +=1; 
        return randomInt;
    }
    
    /**
     * Randomizes the emotion to be presented to the child
     * Prevents repeating the same expressions twice in a row
     * @return String with address of the emotion
     */
     public String randEmotion(){
        int j = 0;
        do{
            j = randNum(NUM_EMOTIONS);
        }while (j == repeat);
        String result = "";
        if(j == 1) {emotionCard = Fear; repeat = 1;}
        else if (j == 2) {emotionCard = Anger; repeat = 2;}
        else if (j == 3) {emotionCard = Joy; repeat = 3;}
        else if (j == 4) {emotionCard = Sadness; repeat = 4;}
        else if (j == 5) {emotionCard = Surprise; repeat = 5;}
        result = emotionCard.getAddress();
        return result;     
     }
     
     /**
      * Randomizes the instruction to ask the correct card to the child
      */
     public void randInstruction(){
        int j = randNum(4);
        if(firstTime == true) {myRobot.speak("Escolhe a resposta certa pegando na ráquéete."); j=0;}
        if(j == 1) myRobot.speak("Qual é a resposta certa?");
        else if (j == 2) myRobot.speak("Escolhe a resposta certa pegando na ráquéete.");
        else if (j == 3) myRobot.speak("Qual é a imagem certa?");
        else if (j == 4) myRobot.speak("Escolhe a imagem correta.");
        myRobot.playAnimation(blink);
        Robosteps.sleep(3000);
        firstTime = false;   
    }
        
}