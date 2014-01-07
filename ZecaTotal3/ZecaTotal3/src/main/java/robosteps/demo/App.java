/*Relatório: Este programa como está é o teste piloto para a atividade recognize.
 * Guarda info sobre crianças, sessoes, performance e respostas, e cria ficheiro final com a junção de vários documentos
 * não permite a repetição de emoções através de cartão e pode acontecer de a mesma expressão ser mostrada duas vezes seguidas
 * Único problema é a msg de erro quando fala o reforço. Algum tipo de erro, por voltar a chamar IP (settings()), mas que não consegui resolver
 * 
 * Cartão Repeat, STOP e Start OK
 * A mesma expressão ser mostrada duas vezes seguidas - OK
 * Código da criança passou a ser 02 em vez de String com endereço/Child_02
 */

package robosteps.demo;
import org.robokind.api.animation.Animation;
import com.robosteps.api.core.*;
import robosteps.Tasks.ActivityType;
import friendularity.test.camera.r50.ImageDemo;
import friendularity.test.camera.r50.ImageMonitor;
import java.io.File;
import robosteps.demo.expdata.Child;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import robosteps.Tasks.ActivityMother;
import robosteps.Tasks.Recognize;
import robosteps.Tasks.Story;
import static robosteps.demo.App.childExists;
import static robosteps.demo.App.isNumeric;
import robosteps.sessions.Answer;
import robosteps.sessions.Performance;
import robosteps.sessions.Session;
import robosteps.demo.utils.KeypadListener;
/**
 * @author Sandra Costa - scosta@dei.uminho.pt
 */

public class App implements Runnable{
    private static RsRobot myRobot;
    public ImageDemo iKey;
    public Thread tThread;     
    ImageMonitor monit = new ImageMonitor();
    String childCode = "";
    String instructionCode = "";
    String emotionCode = "";
    String activityCode = "";
    Admin admin = new Admin ();
    Child child = new Child ();
    Animation smile = Robosteps.loadAnimation("V9/happyHead");
    Animation blink = Robosteps.loadAnimation("V9/blink");
    Animation greeting = Robosteps.loadAnimation("V9/01");
    Animation anim1 = Robosteps.loadAnimation("V9/02");
    Animation anim2 = Robosteps.loadAnimation("V9/03");
    Animation anim3 = Robosteps.loadAnimation("V9/04");
    Animation neutral = Robosteps.loadAnimation("V9/neutralHeadGesture");
    public ActivityMother runningActivity=null; 
    Card emotionCard = new Card();
    public ActivityType currentActivityType = null;
    Recognize recog = null;
    Story stor = null;
    KeypadListener keypad=null;
    private boolean emoCodeWait;
    /**
     * Creates a Thread so we can pause and interrupt it
     */
    public App(){ 
    	tThread = new Thread(this);  
        tThread.start();
    }
    
      /**
     * Main, IP is defined
     * @param args 
     */
    public static void main( String[] args ){
     
        App myapp = new App();
    }
    /**
     * Notifies all Threads
     */
    public synchronized void wakeUp(){
        synchronized (this) {
            notifyAll();
        }	
    } 
    
    /**
     * Sets the App to gather Emotion Code from camera or keypad
     */
    public void awaitEmotionCode(){
    
        emoCodeWait=true;
        
    }
    /**
     * Gets QR Code
     * @return 
     */
    public synchronized String getQRCode(){
        ImageDemo.ID = "";
        monit.processOK();
        
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*
         * Getting camera answer
         */
        String toreturn=iKey.getID();
        System.out.println("Camera says: "+toreturn);
        if(toreturn.equalsIgnoreCase("no")){
            toreturn=emotionCode;
        }
        emotionCode="no";
        System.out.println("returning: "+toreturn);
        return toreturn;   
    }
 /*
     * Propagate Event from keyboard or Camera
     */
    public synchronized void processExternalEvent(String myEvent){      
        
        System.out.println("App got event:" +myEvent);        
        /**
         * Handling running activities
         */
        if(runningActivity!=null){
        /**
         * Pausing and resuming an actgivity which is running
         */
            if(myEvent.equals("-")){   
                if(runningActivity.state==runningActivity.RUNNING){
                    runningActivity.interruptActivity();
                }else{
                    runningActivity.resumeActivity();
                }
                return;
            }
            /**
            * Got emocode from keypad
            */
           if("12345".toLowerCase().contains(myEvent.toLowerCase())){
               /*
                * In case keypad is hit without recognition
                */
               if(recog==null) return;
               
               emotionCode=((Card)(recog.emoCards.get(Integer.parseInt(myEvent)))).getCode();
               System.out.println("Keypad got emotion "+ emotionCode);
               wakeUp();
               return;
           }
           /**
            * Repeat key handled as emotionCode BAD
            */
           if("*".equals(myEvent)){
               emotionCode=myEvent;
               wakeUp();
               return;
           }
        }
        System.out.println("Null activity or no events to process");
        return;
    }
 
  
    
    /**
     * Thread. Calls startZeca()
     */
    public void run(){
    	startZeca();
    }
    
    /**
     * Verifies if a child exists using a code (String) coming from the QR Code 
     * @param code
     * @return true if the child exists
     */
    public static boolean childExists(String code){
         
        boolean check = false;
        File dir = new File("./ChildrenData/");
        String[] dirChildren = dir.list(); 

        //Children in the file
        for(int i=0; i<dirChildren.length;i++) {
            if(Integer.parseInt(code) == i+1){
                check = true; // child exists
                break;
            }      
            else 
                check = false; // child does not exists
        }

        return check;
    }

    /**
     * Main method
     */
    public void startZeca( ){
        Scanner input = new Scanner(System.in); 
        //Gets camera
        iKey = new ImageDemo(this);
        //to get if it is a number
        boolean num= false;
        //connects the robot
        
        String IP = "192.168.1.108";
        UserSettings.setRobotId("myRobot");
        UserSettings.setRobotAddress(IP);
        UserSettings.setSpeechAddress(IP);
        UserSettings.setAnimationAddress(IP); 
                
        myRobot = Robosteps.connectRobot();
        myRobot.speak("Olá Sandra! Vamos começar a trabalhar.");
        myRobot.playAnimation(anim3);
        
        //Participant's identification using QR CODE (Verifies if the child is is the system)
        do{
            myRobot.speak("Insere o código do participante.");
            Robosteps.sleep(3000);
            //childCode = getQRCode();
            childCode = input.next();
            //Checks if the QR Code shows a Child's Code (eg. 02)
            num = isNumeric(childCode);
            //Asks again if the QR Code does not correspond to a Child's Code or the child does not exist
        }while((num == false) || (!childExists(childCode) ) );

        
        //Accessing Child's Data
        Child myChild = new Child(Integer.parseInt(childCode));
        String myChildName = myChild.getNameChild();
        //Gets the total # of sessions this child did already
        int myChildNSessions = myChild.getNSessions();
        myRobot.speak("O participante chama-se " + myChildName + ".");
        Robosteps.sleep(500);
       
        //Choosing the Activity
        myRobot.speak("Que jogo vamos jogar hoje? ");
        Robosteps.sleep(500);
        
        int op = 2;// 0 is stop, 7 recognise, 8 is storytelling , 9 imitation
        
        String instrucaoJogo = "";
        String nomeAtividade = "";
        
        do{
            //activityCode= getQRCode();
            op = input.nextInt();
            
        }while(op != 7 && op != 8 && op != 9);
        
        if(op == 7){
            currentActivityType = ActivityType.RECOGNIZE;
            nomeAtividade = "Reconhecer";
            instrucaoJogo = "Neste jogo, tens que escolher a ráquééte que mostra o que eu estou a sentir. Vamos lá.";
        }
        else if (op == 8){
            currentActivityType = ActivityType.STORY;
            nomeAtividade = "Contar estórias";
            instrucaoJogo = "Neste jogo, vais ouvir estórias. Depois tens que escolher a ráquééte que mostra o que eu estou a sentir. Vamos lá.";
        }
        else if (op == 9){
            nomeAtividade = "Imita-me";
            currentActivityType = ActivityType.IMITATION;
            instrucaoJogo = "Neste jogo, tens que copiar a minha caraç. Consegues?";
        }
                        
        myRobot.speak("Vamos jogar ao jogo " +nomeAtividade+".");
        Robosteps.sleep(3000);
        myRobot.speak("Sandra, carrega em START para começarmos.");
        Robosteps.sleep(3000);
        
        //Waits for START instruction 
        do{
            //instructionCode = getQRCode();
            instructionCode = input.next();
        }while ((!"/".equals(instructionCode)));

        
        //Start a new Session
        myRobot.playAnimation(greeting);
        myRobot.speak("Olá " + myChildName + ". Que bom te ver. Vamos jogar ao jogo "+nomeAtividade+".");
        Robosteps.sleep(3000);
        myRobot.playAnimation(anim3);
        myRobot.speak(instrucaoJogo);
        Robosteps.sleep(10000);
        
        
        //Updates number of sessions of the child
        myChildNSessions++; 
        //Sets number of sessions of the child
        myChild.setNSessions(myChildNSessions);
        //Saves child's info 
        myChild.saveChildData(); 
        //Creates a new session
        Session newSession = new Session(); 
        //Creates a new session folder, and files
        newSession.createDataStructureSessions(myChild); 
        //Defines present day and time
        GregorianCalendar now = new GregorianCalendar(); 
        //Sets present date 
        newSession.setDate(now);
        //Sets start time
        newSession.setStartTime(now);
        //Sets activity to be performed
        newSession.setActivity(currentActivityType);
        //Saves session's data 
        newSession.saveSessionData(childCode);  //Saves the inicial data to the files
        //Gets code of the session, produced automatically (lastest+1)
        String codeSession = newSession.getCodeSession();

       
        //Starts a timer (value in seconds) (300 = 5min)
        Time timerSession = new Time(300); 

        recog = new Recognize(myChild, newSession);
        stor = new Story (myChild, newSession);
        
        recog.setApp(this);
        stor.setApp(this);
        //Imitation imit = new Imitation (myChild, newSession);
        //imit.setApp(this);
        
        //When the time is up flag = true
        /*
         * Activity start and processing 
         */
        keypad=new KeypadListener();
        keypad.setApp(this);
        Thread kthread=new Thread(keypad);
        kthread.start();
                
        while((timerSession.flag == false) || (!"0".equals(recog.emotionCode))){
             if(currentActivityType == ActivityType.RECOGNIZE){
                runningActivity=recog;
                recog.processActivity();   
                if("0".equals(recog.emotionCode))timerSession.flag = true;                  
            }
            else if (currentActivityType == ActivityType.STORY){                
                runningActivity=stor;
                stor.processActivity();
                if("0".equals(stor.emotionCode))timerSession.flag = true;   
            }
            else{
                //runningActivity=imit;
                //imit.processActivity();
                 //if("0".equals(imit.emotionCode))timerSession.flag = true; 
            }
             //End of the session
        //Defines time the session ended
        GregorianCalendar later = new GregorianCalendar(); 
        //Save this time as endTime
        newSession.setEndTime(later); 
        //Calculates duration of the session
        float duration = later.getTimeInMillis() - now.getTimeInMillis();       
        duration = duration/1000;
        newSession.setDuration(duration);
        //Saves data from the child's session
        newSession.saveSessionData(childCode);
        }
              
        //Closes camera
        iKey.runit=false; 
        //Farewell
        myRobot.playAnimation(neutral);
        Robosteps.sleep(5000);
        myRobot.playAnimation(anim2);
        myRobot.speak("O nosso jogo terminou por hoje. Foi muito bom ter jogado contigo. Até breve.");
        Robosteps.sleep(5000);
        Robosteps.disconnect();
        System.exit(0);   
    }
    
      /**
      * Checks if a String is numeric
      * @param number String to tested
      * @return true if the String is numberic
      */
     public static boolean isNumeric(String number){  
         boolean isValid = false;
           String expression = "[-+]?[0-9]*\\.?[0-9]+$";  
           CharSequence inputStr = number;  
           Pattern pattern = Pattern.compile(expression);  
           Matcher matcher = pattern.matcher(inputStr);  
           if(matcher.matches()){  
              isValid = true;  
           }  
           return isValid;  
         }

    public RsRobot getRobot() {
        return myRobot;
    }
}