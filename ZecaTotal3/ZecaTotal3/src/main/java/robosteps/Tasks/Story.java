package robosteps.Tasks;

import au.com.bytecode.opencsv.CSVReader;
import com.robosteps.api.core.Robosteps;
import com.robosteps.api.core.RsRobot;
import com.robosteps.api.core.UserSettings;
import friendularity.test.camera.r50.ImageMonitor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

public class Story extends ActivityMother{
    
    /**
     * Inheritance from ActivityMother
     * @param chil - Child child; String childCode;
     * @param newSess - Session session; String sessionCode;
     * @param perf - Performance perf;
     * @param answ - Answer answ;
     */
    public Story(Child chil, Session newSess){
    
            super(chil, newSess);  
             
    }
    
    public static final int NUM_STORIES = 15;
    private static RsRobot myRobot;  
    public String emotionCode = "";
    boolean firstTime = true;
    int repeat = 0;
    boolean proceed = true;
    Card emotionCard = new Card();
    Animation blink = Robosteps.loadAnimation("V9/blink");
    Animation emotionOut = Robosteps.loadAnimation("V9/04");
    ImageMonitor monit = new ImageMonitor();
    String storyText = "";
    String emotionStory = "";
    Animation smile = Robosteps.loadAnimation("V9/happyHead");    
    Animation anim2 = Robosteps.loadAnimation("V9/02");
    Animation anim3 = Robosteps.loadAnimation("V9/03");
    Animation storyEmotionShow = null;
    
    @Override
    /**
     * Activity telling Stories:  The robot tells a storyEmotionShow and at the end it shows a facial expression and the corresponding gesture, and the child has to identify the emotion
     */
    public void processActivity(){
        //settings();
        //Gets Reinforcement of the child
        state=RUNNING;
        Reinforcement myChildReinforcement = child.getReinforcementChild();
        //Defines code of the answer
        answ.setCodeAnswer(Integer.toString(perf.numAnswers()));
        //Time of the prompt
        GregorianCalendar prompt = new GregorianCalendar();
        Robosteps.sleep(3000);
        try {
            //Robot choose a random storyText
            randStory();
        } catch (IOException ex) {
            Logger.getLogger(Story.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        //Algorithm:
        //Story (neutral) + "How I feel?" + Child answers +
        //1) Right answer: "You're right, I felt sad"  or 2) wrong answer: "Nope,actually I felt sad" 
        //+ Show emotion
        
        do{
        //Inits storyEmotionShow
        myRobot.playAnimation(smile);
        Robosteps.sleep(5000);
               
        myRobot.speak("\\spd=80\\");
        //Tells the story
        myRobot.speak(storyText);
        //anim 2 and anim3 = Zeca's movements while telling the storyEmotionShow (each anim takes 5 seg)
        myRobot.playAnimation(anim2);
        Robosteps.sleep(6000);
        myRobot.playAnimation(anim3);
        Robosteps.sleep(6000);    
        myRobot.speak("\\spd=100\\");
        myRobot.speak("Como me senti no final desta história?");
        
        //Receives String form QR Code
        emotionCode = myapp.getQRCode();
        }while("REPEAT".equals(emotionCode));
        
        
        
         //The emotion shown by the robot is saved as input
        answ.setInput(emotionCard.getCode());
        //Defines answer from the child
        answ.setOutput(emotionCode);
                //Time of the answer was shown
        GregorianCalendar promptAnswered = new GregorianCalendar(); 
        //Calculates the time between the prompt and the answer  
        answ.calculateResponseTime (prompt, promptAnswered);     
        myRobot.speak("Nesta história, eu senti-me "+getStringEmotion(emotionCard.getCode())+".");
        //Showing the emotion inside the story    
        storyEmotionShow = Robosteps.loadAnimation(emotionCard.getAddress());
        myRobot.playAnimation(storyEmotionShow);
        Robosteps.sleep(2000);
        //Verifies if the answer is right or wrong (IF Stop Card is show it does not show wrong reinforcement
        if(!"STOP".equals(emotionCode))answ.matching(myChildReinforcement); 
        
        
        
        //Insert answer in the TreeMap
        perf.insertAnswer(answ); 
        //Calculates if the answer is wrong or right, and updates counter
        perf.calculatePerformance(answ);
        //Increments codeAnswer
        int c = Integer.parseInt(answ.getCodeAnswer());
        c++;
        answ.setCodeAnswer(Integer.toString(c));
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
     * Randomizes the storyText to be presented to the child
     * Prevents repeating the same stories twice in a row
     * @return String with address of the emotion
     */     
     public void randStory() throws IOException{
        try {
            int j = 0;
            int i = 0;

            do{
                j = randNum(NUM_STORIES);
            }while (j == repeat);
            
            String storiesFile = "./stories.csv";
            
           CSVReader csvReader = new CSVReader(new FileReader(storiesFile), ';');
        String[] row = null;
        while((row = csvReader.readNext()) != null) {
            
            if (Integer.parseInt(row[0]) == j){
                repeat = Integer.parseInt(row[0]);
                emotionStory = row[1];
                storyText = row[2];
                
                if("Fear".equals(emotionStory)) {emotionCard = Fear;}
                else if ("Anger".equals(emotionStory)) {emotionCard = Anger;}
                else if ("Joy".equals(emotionStory)) {emotionCard = Joy;}
                else if ("Sadness".equals(emotionStory)) {emotionCard = Sadness;}
                else if ("Surprise".equals(emotionStory)) {emotionCard = Surprise;}
            }
        }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Story.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
      /**
     * Getting String for the robot to say according to the emotion conveyed in the story
     * @param emot - Emotion conveyed in the story
     * @return 
     */
    public String getStringEmotion(String emot){
        String stringEmotion = "";
        Card c = new Card ();
        if("Fear".equals(emot)) stringEmotion = "com medo";
        else if("Anger".equals(emot)) stringEmotion = "zangado";
        else if("Joy".equals(emot)) stringEmotion = "alegre";
        else if("Sadness".equals(emot)) stringEmotion = "triste";
        else if("Surprise".equals(emot)) stringEmotion = "surpreso";
        return stringEmotion;
    }
}
