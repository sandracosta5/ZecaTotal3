/*
 * Session's administration. A new session is created when a child is identified by the QRCode
 */
package robosteps.sessions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import robosteps.Tasks.ActivityType;
import robosteps.demo.expdata.Child;
import robosteps.demo.utils.ReadWriteFile;

/**
 *
 * @author Sandra Costa - scosta@dei.uminho.pt
 */
public class Session implements java.io.Serializable{
    private String codeSession;
    private GregorianCalendar date;
    private GregorianCalendar startTime;
    private GregorianCalendar endTime;
    private float duration;
    private ActivityType activity;
    private Performance perform;
    
    public Session(){
        codeSession = "";
        date = new GregorianCalendar();
        startTime = new GregorianCalendar();
        endTime = new GregorianCalendar();
        duration = 0;
        activity = null;
        perform = null;
    }
        
    public Session(String codS, GregorianCalendar dat, GregorianCalendar st, GregorianCalendar et, float d, ActivityType act, Performance p){
        codeSession = codS;
        date = dat;
        startTime = st;
        endTime = et;
        duration = d;
        activity = act;
        perform = p;
    }
    
    public Session(String codS, GregorianCalendar dat, GregorianCalendar st, ActivityType act){
        codeSession = codS;
        date = dat;
        startTime = st;
        endTime = null;
        duration = 0;
        activity = act;
        perform = null;
    }
    
    public Session(Session s){
        codeSession = s.getCodeSession();
        date = s.getDate();
        startTime = s.getStartTime();
        endTime = s.getEndTime();
        duration = s.getDuration();
        activity = s.getActivity();
        perform = s.getPerform();
      }
    
    /**
     * Creates the folders for the individual Sessions inside eaxh child's folder
     * @param c 
     */
    public void createDataStructureSessions(Child c){
        String codeChild = c.getCodeChild();
        String codeSession = Integer.toString(c.getNSessions());

        File dir = new File("./ChildrenData/Child_"+codeChild+"/Sessions/");
        if(!dir.exists()){
            dir.mkdir(); 
            setCodSession(codeSession);
        }
        else
        {
            setCodSession(codeSession);
        }
        
        File info=new File("./ChildrenData/Child_"+codeChild+"/Sessions/0sessionFirstLine.csv");
       if(!info.exists()){
            try {
                //If the file doesn't exists creates it
                info.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
         
       }
        try {
            ReadWriteFile.setContents(info,sessionToCsvString1stLine());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Saves data from a Session
     * @param codeChild 
     */
    public void saveSessionData(String codeChild){
       File info=new File("./ChildrenData/Child_"+codeChild+"/Sessions/session"+codeSession+".csv");
       if(!info.exists()){
           try {
               //If the file doesn't exists creates it
               info.createNewFile();
           } catch (IOException ex){
               Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
   
       try {
           ReadWriteFile.setContents(info,sessionToCsvString());
           System.out.println("Data of the session saved");
       } catch (FileNotFoundException ex){
           Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex){
           Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
       }
    }

    /**
     * Deletes the information of a Session
     * @return 
     */
    public boolean deleteSession(){
        File dir=new File(codeSession);
        if(!dir.exists()){
            System.out.println("No directory presentes: "+codeSession);
            return false;
        }
        ReadWriteFile.recursiveDelete(dir);
        if(!dir.exists()){
             System.out.println("Session Deleted");
             return true;
        }
        return false;
    }
    
    /**
     * Builds CSV string from Session's Information
     * @return 
     */
    public String sessionToCsvString(){
        String toSave = "";
        toSave += codeSession+";";
        toSave += date.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (date.get(GregorianCalendar.MONTH) + 1) + "/" + date.get(GregorianCalendar.YEAR)+";";
        toSave += startTime.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + startTime.get(GregorianCalendar.MINUTE) +";";
        toSave += endTime.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + endTime.get(GregorianCalendar.MINUTE)+";";
        toSave += duration+";";
        toSave += activity+";";
        return toSave;
    }
    
    /**
     * Builds CSV string for the first line of the file with the information of all the sessions
     * @return 
     */
    public String sessionToCsvString1stLine(){
        String toSave = "";
        toSave += "Code;";
        toSave += "Date;";
        toSave += "StartTime;";
        toSave += "EndTime;";
        toSave += "Duration;";
        toSave += "Activity;";
        return toSave;
    }
    
    /**
     * Prints information of the session
     * @return 
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\n\n----- Sessão -----\n");
        s.append("Código: " + codeSession + "\n");
        s.append("Data: " + date.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (date.get(GregorianCalendar.MONTH) + 1) + "/" + date.get(GregorianCalendar.YEAR)+ "\n");
        s.append("Hora Início: ").append(startTime.get(GregorianCalendar.HOUR_OF_DAY)).append(":").append(startTime.get(GregorianCalendar.MINUTE)).append("\n");
        s.append("Hora Fim: ").append(endTime.get(GregorianCalendar.HOUR_OF_DAY)).append(":").append(endTime.get(GregorianCalendar.MINUTE)).append("\n");
        s.append("Duração: " + duration + " segundos\n");
        s.append("Atividade: " + activity + "\n");
        s.append("----------------------------\n");
        return s.toString();
    }
    /** Clone */
    public Session clone() { 
    return new Session(this);
    }
    
    /** Returns Sessions Code*/
    public String getCodeSession(){
        return codeSession;
    }
    
    /** Returns Session Date*/
    public GregorianCalendar getDate(){
        return date;
    }
    
    /** Returns Starting Time of the Session*/
    public GregorianCalendar getStartTime(){
        return startTime;
    }
    
    /** Returns Ending Time of the Session*/
    public GregorianCalendar getEndTime(){
        return endTime;
    }
    
    /** Returns session duration*/
    public float getDuration(){
        return duration;
    }
       
    /** Returns o nome da atividade*/
    public ActivityType getActivity(){
        return activity;
    }
    
    public Performance getPerform(){
        return perform;
    }
   
    /** Updates codeSession */
    public void setCodSession(String codS){
        codeSession = codS;
    }
    
    /** Updates date */
    public void setDate(GregorianCalendar dat){
        date = dat;
    }
    
    /** Updates startTime */
    public void setStartTime(GregorianCalendar st){
        startTime = st;
    }
    
    /** Updates endTime */
    public void setEndTime(GregorianCalendar et){
        endTime = et;
    }
    
    /** Updates session duration*/
    public void setDuration(float dur){
        duration = dur;
    }
    
    /** Updates activity */
    public void setActivity(ActivityType act){
        activity = act;
    }
    
    public void setPerform (Performance p){
        perform = p;
    }
}