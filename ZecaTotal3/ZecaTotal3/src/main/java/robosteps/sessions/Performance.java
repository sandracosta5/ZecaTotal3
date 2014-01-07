/*
 * Performance of the children: get, analyse, and save them
 */
package robosteps.sessions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import robosteps.demo.utils.ReadWriteFile;

/**
 *
 * @author Sandra Costa - scosta@dei.uminho.pt
 */
public class Performance {
    private int numRightAnswers;
    private int numWrongAnswers;
    private TreeMap <String, Answer> answers;
    
    public Performance(){
        numRightAnswers = 0;
        numWrongAnswers = 0;
        answers = new TreeMap <String, Answer> ();
    }
    
    public Performance (int numR, int numW, Map <String, Answer> ans){
        numRightAnswers = numR;
        numWrongAnswers = numW;
        answers = new TreeMap <String, Answer> ();
        for (Answer a : ans.values())
            ans.put(a.getCodeAnswer(), a.clone());
    }   
    
    
    public Performance (Performance p){
        numRightAnswers = p.getNumRightAnswers();
        numWrongAnswers = p.getNumWrongAnswers();
        answers = new TreeMap <String, Answer> ();
    }
    
    /**
     * Saves data from Performance in a Session
     * @param s
     * @param codeChild
     * @param codeSession 
     */
    public void saveSessionPerformanceData(Session s, String codeChild, String codeSession){
        File dir = new File("./ChildrenData/Child_"+codeChild+"/Performances/");
        if(!dir.exists()){
            File SessionDir = new File("./ChildrenData/Child_"+codeChild+"/Performances/");
            SessionDir.mkdir(); 
        }
        
        File info=new File("./ChildrenData/Child_"+codeChild+"/Performances/performance"+codeSession+".csv");
        if(!info.exists()){
            try{
                info.createNewFile(); /*If the file doesn't exists creates it*/
            } catch (IOException ex) {
                Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try{
            ReadWriteFile.setContents(info,PerformanceToCsvString(s, codeChild, codeSession));
            System.out.println("Performance Data saved");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File info1stLine=new File("./ChildrenData/Child_"+codeChild+"/Performances/0performancesFirstLine.csv");
       if(!info1stLine.exists()){
            try {
                //If the file doesn't exists creates it
                info1stLine.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
         
       }
        try {
            ReadWriteFile.setContents(info1stLine,performanceToCsvString1stLine());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Builds CSV string from Performance's Information
     * @param s
     * @param codeChild
     * @param codeSession
     * @return 
     */
    public String PerformanceToCsvString(Session s, String codeChild, String codeSession){
        String toSave = "";
        toSave += codeChild+";";
        toSave += codeSession+";";
        toSave += s.getActivity()+";";
        toSave += numRightAnswers+";";
        toSave += numWrongAnswers+";";
        return toSave;
    }
    
    /**
     * Builds CSV string for the first line of the file with the information of all the performances
     * @return 
     */
    public String performanceToCsvString1stLine(){
        String toSave = "";
        toSave += "CodeChild;";
        toSave += "CodeSession;";
        toSave += "Activity;";
        toSave += "#RightAnswers;";
        toSave += "#WrongAnswers;";
        return toSave;
    }
    
    /**
     * Calculated performance, counter with right and worng answers
     * @param resp 
     */
    public void calculatePerformance(Answer resp){
        boolean respBool = resp.answerClassification();
        if(respBool == true)
            numRightAnswers++;
        else 
            numWrongAnswers++;
    }
    
    /**
     * Inserts a answer in the TreeMap
     * @param a 
     */
    public void insertAnswer(Answer a){
        answers.put(a.getCodeAnswer(),a.clone());
    }
    
    /**
     * Removes answer of the TreeMap
     * @param cod 
     */
    public void removeAnswer (String cod){
        answers.remove(cod);
    }
    
    /**
     * Returns the number of answer of the child
     * @return 
     */
    public int numAnswers(){
        return answers.size();
    }

    /**
     * Verifies if a answer exists
     * @param cod
     * @return 
     */
    public boolean answerExists(String cod){return answers.keySet().contains(cod);
    }

    /**
     * Returns information of a answer with a code, verifying if the code exists
     * @param cod
     * @return 
     */
    public Answer getFileAnswer (String cod){
        if(!this.answerExists(cod)) return null;
        else return answers.get(cod).clone();
    }
    
    public int getNumRightAnswers(){
        return numRightAnswers;
    }
    
    public int getNumWrongAnswers(){
        return numWrongAnswers;
    }
    public Map<String, Answer> getAnswers(){ 
        TreeMap<String, Answer> proc = new TreeMap<String, Answer> ();
        for(Answer a : answers.values())  proc.put(a.getCodeAnswer(), a.clone()); 
        return proc;
    }
    
    public void setNumRightAnswers (int numR){
        numRightAnswers = numR;
    }
    public void setNumWrongAnswers (int numW){
        numWrongAnswers = numW;
    }
    
    /**
     * Prints performance's information
     * @return 
     */
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("----- Performance -----\r\n");
        s.append("# Respostas Certas: " + numRightAnswers + "\r\n");
        s.append("# Respostas Erradas: " + numWrongAnswers + "\r\n");
        s.append("\r\nAnswers: " + answers + "\r\n");
        s.append("----------------------------\r\n");
        return s.toString();
    }
    
    /**
     * Clone
     * @param p
     * @return 
     */
    public Performance clone(Performance p) { 
      return new Performance(this);
    }
}
