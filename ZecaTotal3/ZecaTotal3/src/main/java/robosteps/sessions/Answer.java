/*
 * Answers provided by the children: get, analyse, and save them
 */
package robosteps.sessions;

import com.robosteps.api.core.Robosteps;
import com.robosteps.api.core.RsRobot;
import com.robosteps.api.core.UserSettings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.robokind.api.animation.Animation;
import robosteps.demo.Card;
import robosteps.demo.Emotion;
import robosteps.demo.expdata.Reinforcement;
import robosteps.demo.utils.ReadWriteFile;
import robosteps.demo.App;

/**
 *
 * @author Sandra Costa - scosta@dei.uminho.pt
 */
public class Answer {
    private String codeAnswer;
    private String input;
    private String output;
    private boolean rightOrWrong;
    private GregorianCalendar promptTime;
    private GregorianCalendar respTime;
    private float timeResponse;
    public RsRobot myRobot;  
    Animation wrongAnswer = Robosteps.loadAnimation("V9/wrongAnswer");
    Animation rightAnswer01 = Robosteps.loadAnimation("V9/rightAnswer01");
    Animation rightAnswer02 = Robosteps.loadAnimation("V9/rightAnswer02");
    Animation rightAnswer03 = Robosteps.loadAnimation("V9/rightAnswer03");
    Animation rightAnswer04 = Robosteps.loadAnimation("V9/rightAnswer04");
    Animation rightAnswer05 = Robosteps.loadAnimation("V9/rightAnswer05");
    Animation smile = Robosteps.loadAnimation("V9/happyHead");
    Animation blink = Robosteps.loadAnimation("V9/blink");   
    
    public Answer(){
        codeAnswer = "";
        input = "";
        output = "";
        rightOrWrong = false;
        promptTime = null;
        respTime = null;
        timeResponse = 0;
    }
    
    public Answer (String cod){
        codeAnswer = cod;
        input = "";
        output = "";
        rightOrWrong = false;
        promptTime = null;
        respTime = null;
        timeResponse = 0;
    }
    
    public Answer (String cod, String in, String out){
        codeAnswer = cod;
        input = in;
        output = out;
        rightOrWrong = false;
        promptTime = null;
        respTime = null;
        timeResponse = 0;
    }
    
    public Answer (String cod, String in, String out, boolean rw, float timeR){
        codeAnswer = cod;
        input = in;
        output = out;
        rightOrWrong = rw;
        promptTime = null;
        respTime = null;
        timeResponse = timeR;
    }
    
    public Answer (Answer a){
        codeAnswer = a.getCodeAnswer();
        input = a.getInput();
        output = a.getOutput();
        promptTime = a.getPromptTime();
        respTime = a.getRespTime();
        rightOrWrong = a.getRightOrWrong();
        timeResponse = a.getTimeResponse();
    }
    
    public void setRobot(RsRobot rob)
    {
        myRobot=rob;
    }    
    
    /**
     * Builds CSV string from Answers' Information
     * @param codeChild
     * @param codeSession
     * @return 
     */
    public String AnswerToCsvString(String codeChild, String codeSession){
        String toSave = "";
        toSave += codeChild+";";
        toSave += codeSession+";";
        toSave += codeAnswer+";";
        toSave += input+";";
        toSave += output+";";
        toSave += rightOrWrong+";";
        toSave += timeResponse+";";
        return toSave;
    }
    
    /**
     * Builds CSV string for the first line of the file with the information of all the answers
     * @return 
     */
    public String answerToCsvString1stLine(){
        String toSave = "";
        toSave += "CodeChild;";
        toSave += "CodeSession;";
        toSave += "CodeAnswer;";
        toSave += "Input;";
        toSave += "Output;";
        toSave += "Right?;";
        toSave += "ResponseTime;";
        return toSave;
    }
    
    /**
     * Calculates the seconds between one moment and the other
     * @param init - when the prompt was given (by the robot)
     * @param fin - when the response was given (By the child)
     */
    public void calculateResponseTime(GregorianCalendar init, GregorianCalendar fin){
        //Gets duration
        float dur = fin.getTimeInMillis() - init.getTimeInMillis();     
        dur = dur/1000;
        setTimeResponse(dur);
    }

    /**
     * Updates the value of the variable rightOrWrong, according to the answer
     * @return true is the answer was right
     */
    public boolean answerClassification(){
        if(input.equals(output))
            rightOrWrong = true;
        else
            rightOrWrong = false;
        return rightOrWrong;
    }
    
    /**
     * Gets a random int (between 1 and i)
     * @param i
     * @return 
     */
    public int randNum(int i){
    Random randomGenerator = new Random();
    int randomInt = randomGenerator.nextInt(i);
    //to exclude 0
    randomInt +=1; 
    return randomInt;
    }
    
    /**
     * Matches the answer with the prompt and provides reinforcement, according with the one preferred by the child - Activity Recognize and Imitation
     * @param r 
     */
    
    
    /////////////CONFIRMAR ESTA PARTE EM BAIXO
    
    public void matching(Reinforcement r){
        System.out.println("MATCHING input: "+input+" to output: "+output);
        String emocao = "";
        if(input.equals("Fear")) 
            emocao = "com medo";
        else if (input.equals("Anger")) 
            emocao = "zangado";
        else if (input.equals("Joy")) 
            emocao = "alegre";
        else if (input.equals("Sadness")) 
            emocao = "triste";
        else if (input.equals("Surprise")) 
            emocao = "surpreso";
        
        if(input.equals(output)){
            if(r == Reinforcement.VERBAL)rightAnswerVerbal(emocao);
            if(r == Reinforcement.MOVEMENT)rightAnswerMovement();
            if(r == Reinforcement.VERBAL_MOVEMENT)rightAnswerVerbalMovement(emocao);
        }
        if(!input.equals(output)){
            if(r == Reinforcement.VERBAL)wrongAnswerVerbal(emocao);
            if(r == Reinforcement.MOVEMENT)wrongAnswerMovement();
            if(r == Reinforcement.VERBAL_MOVEMENT)wrongAnswerVerbalMovement(emocao);
        }
    }

    /**
     * Function when the answer is wrong & Reinforcement = Verbal; 
     */
    public void wrongAnswerVerbal(String emocao){
       // settings();
        int j = randNum(2);
        if(j == 1) myRobot.speak("Uuuuups. Présta atenção. Eu estava a mostrar que estava "+emocao+". Vamos a outra.");
        else myRobot.speak("Ohoooooh, vamos tentar outra. Na verdade, eu estava a mostrar que estava "+emocao);
        Robosteps.sleep(5000);
        myRobot.playAnimation(blink);
    }

    /**
     * Function when the answer is wrong & Reinforcement = Movement; 
     */
    public void wrongAnswerMovement(){
      //  settings();
        myRobot.playAnimation(wrongAnswer);
        System.out.println("WRONG");
        Robosteps.sleep(5000);
        myRobot.playAnimation(blink);
    }
    
    /**
     * Function when the answer is wrong & Reinforcement = (Verbal & Movement); 
     */
    public void wrongAnswerVerbalMovement(String emocao){
      //  settings();
        int j = randNum(2);
        myRobot.playAnimation(wrongAnswer);
        if(j == 1) {myRobot.speak("Uuuuups. Présta atenção. Eu estava a mostrar que estava "+emocao+". Vamos a outra.");}
        else myRobot.speak("Ohoooooh, vamos tentar outra. Na verdade, eu estava a mostrar que estava "+emocao);
        Robosteps.sleep(5000);
        myRobot.playAnimation(blink);
    }
    
    /**
    * Function when the answer is right & Reinforcement = Verbal; 
    */
    public void rightAnswerVerbal(String emocao){
        //settings();
        int j = randNum(10);
        myRobot.playAnimation(smile);
        if(j == 1) myRobot.speak("Fantástico!");
        else if (j == 2) myRobot.speak("Boa, acertaste!");
        else if (j == 3) myRobot.speak("Boa!");
        else if (j == 4) myRobot.speak("Está correcto!");
        else if (j == 5) myRobot.speak("Está certo!");
        else if (j == 6) myRobot.speak("Bravo!");
        else if (j == 7) myRobot.speak("Muito bem!");
        else if (j == 8) myRobot.speak("UAAAAAAU! Boa!");
        else if (j == 9) myRobot.speak("Parabéns, acertaste!");
        else myRobot.speak("Boa escolha. Está certo!");
        Robosteps.sleep(5000);
        myRobot.speak("Estava a mostrar que estava "+emocao);
        Robosteps.sleep(5000);
        myRobot.playAnimation(blink);
    }

    /**
    * Function when the answer is right & Reinforcement = Movement; 
    */
    public void rightAnswerMovement(){
        int j = randNum(5);
       // settings();
        if(j == 1) myRobot.playAnimation(rightAnswer01);
        else if (j == 2) myRobot.playAnimation(rightAnswer02);
        else if (j == 3) myRobot.playAnimation(rightAnswer03);
        else if (j == 4) myRobot.playAnimation(rightAnswer04);
        else myRobot.playAnimation(rightAnswer05);
        Robosteps.sleep(5000);
        myRobot.playAnimation(blink);
    }

    /**
    * Function when the answer is right & Reinforcement = (Verbal & Movement); 
    */
    public void rightAnswerVerbalMovement(String emocao){
       // settings();
        int j = randNum(10);
        if(j == 1) {myRobot.speak("Fantástico!"); myRobot.playAnimation(rightAnswer01);}
        else if (j == 2) {myRobot.speak("Boa, acertaste!"); myRobot.playAnimation(rightAnswer03);}
        else if (j == 3) {myRobot.speak("Boa!"); myRobot.playAnimation(rightAnswer04);}
        else if (j == 4) {myRobot.speak("Está correcto!"); myRobot.playAnimation(rightAnswer05);}
        else if (j == 5) {myRobot.speak("Está certo!"); myRobot.playAnimation(rightAnswer01);}
        else if (j == 6) {myRobot.speak("Bravo!"); myRobot.playAnimation(rightAnswer02);}
        else if (j == 7) {myRobot.speak("Muito bem!"); myRobot.playAnimation(rightAnswer03);}
        else if (j == 8) {myRobot.speak("UAAAAU! Boa!"); myRobot.playAnimation(rightAnswer04);}
        else if (j == 9) {myRobot.speak("Parabéns, acertaste!"); myRobot.playAnimation(rightAnswer05);}
        else {myRobot.speak("Boa escolha. Está certo!"); myRobot.playAnimation(rightAnswer01);}
        Robosteps.sleep(7000);
        myRobot.speak("Estava a mostrar que estava "+emocao);
        Robosteps.sleep(5000);
        myRobot.playAnimation(blink);
    }
    
   
    
    /**
     * Saves data from Answers
     * @param codeChild
     * @param codeSession
     * @throws FileNotFoundException 
     */
    public void saveAnswerData(String codeChild, String codeSession) throws FileNotFoundException{
        
        File dir = new File("./ChildrenData/Child_"+codeChild+"/Answers/");
        if(!dir.exists()){
            File SessionDir = new File("./ChildrenData/Child_"+codeChild+"/Answers/");
            SessionDir.mkdir(); 
        }
        
        String address = "./ChildrenData/Child_"+codeChild+"/Answers/answers"+codeSession+".csv";
        File info=new File(address);  
        if(!info.exists()) {
            try {info.createNewFile(); /*If the file doesn't exists creates it*/
            } catch (IOException ex) {
                Logger.getLogger(Answer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ReadWriteFile.append(address, AnswerToCsvString(codeChild, codeSession)); 
        
        File info1stLine=new File("./ChildrenData/Child_"+codeChild+"/Answers/0answersFirstLine.csv");
       if(!info1stLine.exists()){
            try {
                //If the file doesn't exists creates it
                info1stLine.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
         
       }
        try {
            ReadWriteFile.setContents(info1stLine,answerToCsvString1stLine());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public String getCodeAnswer(){
        return codeAnswer;
    }
    
    public String getInput(){
        return input;
    }
    public String getOutput(){
        return output;
    }
    public boolean getRightOrWrong(){
        return rightOrWrong;
    }
    public GregorianCalendar getPromptTime(){
        return promptTime;
    }
    public GregorianCalendar getRespTime(){
        return respTime;
    }
    public float getTimeResponse(){
        return timeResponse;
    }
    public void setCodeAnswer(String code){
        codeAnswer=code;
    }
    public void setInput(String in){
        input=in;
    }
    public void setOutput(String out){
        output=out;
        System.out.println("Setting answer output to: "+output);
    }
    public void setRightorWrong(boolean rw){
        rightOrWrong=rw;
    }
    public void setPromptTime(GregorianCalendar g){
        promptTime = g;
    }
    public void setRespTime(GregorianCalendar g){
        respTime = g;
    }
    public void setTimeResponse(float t){
        timeResponse = t;
    }
    
    /**
     * Prints info from the answer
     * @return String
     */
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("\r\n----- Answer -----\r\n");
        s.append("Code: " + codeAnswer + "\r\n");
        s.append("Input: " + input + "\r\n");
        s.append("Output: " + output + "\r\n");
        s.append("Certo?: " + rightOrWrong + "\r\n");
        s.append("Tempo de Resposta: " + timeResponse + "\r\n");
        s.append("----------------------------\r\n");
        return s.toString();
    }
    
    /**
     * Clone
     * @return 
     */
    public Answer clone() { return new Answer(this); }
}