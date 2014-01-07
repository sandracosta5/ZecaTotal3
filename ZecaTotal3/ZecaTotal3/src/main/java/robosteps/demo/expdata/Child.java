/*
 * Class with the data from the children performing the experiments
 * A new child can be created only with his/her name, gender, age, and school
 * Data regarding his/her performance during the experiments is updated automatically.
 */
package robosteps.demo.expdata;
import robosteps.sessions.Session;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import robosteps.demo.utils.ReadWriteFile;
import java.util.TreeMap;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.qpid.util.FileUtils;
import robosteps.Tasks.ActivityType;
import robosteps.demo.utils.GenerateQR;

/**
 * @author Sandra Costa - scosta@dei.uminho.pt
 */

public class Child implements java.io.Serializable {
    private int childNum=0;
    //String for the return value of the qr codeChild and data folder
    private String codeChild;
    private String name; 
    private Gender gender;
    private GregorianCalendar dateOfBirth;
    private String school;
    //preferred type of reinforcement (movement, voice, voice+movement) 
    private Reinforcement reinforcement; 
    //To be updated with number of existing session files*
    private int nSessions; 
    //CodeSession, Session
    private TreeMap <String, Session> childSessions; 
    /*Why using TreeMap? Because they are ordered finite one-to-one correspondences*/
    
    GregorianCalendar now = new GregorianCalendar(); 
    ActivityType act = ActivityType.RECOGNIZE;
    float dur = 10;
    
    public Child(){
        codeChild = ""; 
        name = ""; 
        gender = null;
        dateOfBirth = new GregorianCalendar();
        school = ""; 
        reinforcement = null;
        nSessions = 0;
        childSessions = new TreeMap <String, Session> ();
    }
     
    public Child(String cod, String nam, Gender gend, GregorianCalendar dateOfBir, String sch, Reinforcement reinf){ 
        codeChild = cod;
        name = nam; 
        gender = gend;
        dateOfBirth = dateOfBir;
        school = sch; 
        reinforcement = reinf;
        nSessions = 0;
        childSessions = new TreeMap <String, Session> ();
    }
    
    public Child(String cod, String nam, Gender gend, GregorianCalendar dateOfBir, String sch, Reinforcement reinf, int nSess, Map<String, Session> childS){ 
        codeChild = cod; 
        name = nam; 
        gender = gend;
        dateOfBirth = dateOfBir;
        school = sch; 
        reinforcement = reinf;
        nSessions = nSess;
        childSessions = new TreeMap <String, Session> ();
        for(Session s : childS.values())
            childS.put(s.getCodeSession(), s.clone());
    }
    
    public Child(int num){
        childNum=num;
        loadChildData();
     }

    public Child(Child kid){
        codeChild = kid.getCodeChild(); 
        name = kid.getNameChild();
        gender = kid.getGenderChild();
        dateOfBirth = kid.getDateOfBirthChild();
        school = kid.getSchoolChild();
        reinforcement = kid.getReinforcementChild();
        nSessions = kid.getNSessions();
        childSessions = new TreeMap <String, Session> ();
    }     

    /**
     * Creates the folders to the Child's Info and Sessions inside the folder ChildrenData
     */
    public void createDataStructure(){
       GenerateQR QRC = new GenerateQR();
       //Folder with all the data from the children and sessions
        File dir = new File("./ChildrenData/");
        String[] dirChildren = dir.list(); 
        int nb = 1; 
        //number of children directories listed
        int nbFold = dirChildren.length-1;
        String Snum = "";
        if(nbFold >= 0){
            //last child number in string
            Snum = dirChildren[nbFold].split("_")[1];   
            nb = Integer.parseInt(Snum)+1;
            
        }
        String fname = "./ChildrenData/Child_";
        //Child folder name concatenation
        if(nb<10) Snum = "0"+Integer.toString(nb);
        fname += Snum;
        //Creates a directory of name fname
        File childdir = new File(fname);
        boolean result = childdir.mkdir();
        if(result){
            dirChildren = dir.list(); 
        }                
        //Child codeChild is path to child folder
        //setCode(fname);
        setCode(Snum);
        //Creating a QR Code 
        QRC.newQR(Snum);

    }
    
    /**
     * Gets number of children as a string
     * @return String
     */
    public String getChildNumString(){
    String snum=null;
        if(childNum<10){snum="0";}
        snum+=Integer.toString(childNum);
        return snum;        
    }

    /**
     * Loads the child' information
     */
    public void loadChildData(){
        //Only loads data if there is children
        if(childNum<0){
            System.out.println("There is no children in the database. Returning to main menu.");
            return; 
        }
        //Selects child
        String localNum = Integer.toString(childNum); 
        if(childNum<10)localNum = "0"+localNum; 
        //Get info from the file.csv
        File info = new File("./ChildrenData/Child_"+localNum+"/infos.csv");
        int i = 0;
        //Gets the first argument of the file (first element before first ;)
        String myString = ReadWriteFile.getContents(info).get(i);
        //mydata is an array of string with the info from the file separated by ;
        String[] mydata = myString.split(";");
        
        codeChild = mydata[i++];
        name = mydata[i++];
        String[] myDate = mydata[i++].split("/");
        dateOfBirth = new GregorianCalendar(Integer.parseInt(myDate[2]),Integer.parseInt(myDate[1])-1,Integer.parseInt(myDate[0]));
        String myGender = mydata[i++];
        gender = Gender.UNKNOWN;
        if(myGender.equalsIgnoreCase("FEMALE")){
            gender = Gender.FEMALE;
        }
        else{
            gender = Gender.MALE;
        }
        school = mydata[i++];
        reinforcement = Reinforcement.MOVEMENT;
        String theReinf = mydata[i++];
        if(theReinf.equalsIgnoreCase("VERBAL"))
            reinforcement = Reinforcement.VERBAL;
        if(theReinf.equalsIgnoreCase("VERBAL_MOVEMENT"))
            reinforcement = Reinforcement.VERBAL_MOVEMENT;
        nSessions =  Integer.parseInt(mydata[i++]); 
        //Prints child's information
        System.out.println(toString());
    }
    
    /**
     * Builds CSV string from Child's Information
     * @return String
     */
    public String childToCsvString(){
        String toSave = "";
        toSave += codeChild+";";
        toSave += name+";";
        toSave += dateOfBirth.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (dateOfBirth.get(GregorianCalendar.MONTH) + 1) + "/" + dateOfBirth.get(GregorianCalendar.YEAR)+";";
        toSave += gender+";";
        toSave += school+";";
        toSave += reinforcement+";";
        toSave += nSessions+";";
        toSave += childSessions+";";
        return toSave;
    }

    /**
     * Deletes the information of a Child
     * @return true if success
     */
    public boolean deleteChildData(){
        File dir=new File(codeChild); 
        if(!dir.exists()){
            System.out.println("No directory present: "+codeChild);
            return false;
        }
        ReadWriteFile.recursiveDelete(dir);
        if(!dir.exists()){
            System.out.println("Child Deleted");
            return true;
        }
        return false;
    }
    
    /**
     * Saves data from a Child
     */
    public void saveChildData(){
        File info=new File("./ChildrenData/Child_"+codeChild+"/infos.csv");
        if(!info.exists()){
            try {
                //If the file doesn't exists creates it
                info.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        File dat=new File("./ChildrenData/Child_"+codeChild+"/child.txt");             
        if(!dat.exists()){
            try {
                dat.createNewFile(); /*If the file doesn't exists creates it*/
            } catch (IOException ex) {
                Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Write in the files (.csv and .txt)
        try {
            ReadWriteFile.setContents(info, childToCsvString());
            ReadWriteFile.setContents(dat, this.toString());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Returns codeChild*/
    public String getCodeChild(){
        return codeChild;
    }
    
    /** Returns name*/
    public String getNameChild(){
        return name;
    }
    
    /** Returns gender*/
    public Gender getGenderChild(){
        return gender;
    }
    
    /** Returns age*/
    public GregorianCalendar getDateOfBirthChild(){
        return (GregorianCalendar) dateOfBirth.clone();
    }
    
    /** Returns school*/
    public String getSchoolChild(){
        return school;
    }
    
    /** Returns reinforcement type*/
    public Reinforcement getReinforcementChild(){
        return reinforcement;
    }
    
    /** Returns total number of sessions of the child*/
    public int getNSessions(){
        return nSessions;
    }
    
    /** Returns a copy of the Child's Sessions  */
    public Map<String, Session> getChildSessions(){
        TreeMap<String, Session> proc = new TreeMap<String, Session> ();
        for(Session s : childSessions.values())  proc.put(s.getCodeSession(), s.clone()); 
        return proc;
    }
    
    /** Inserts a session in the TreeMap*/
    public void insertSession(Session s){
        childSessions.put(s.getCodeSession(),s.clone());
    }
    
    /** Removes Session of the TreeMap*/
    public void removeSession (String cod){
        childSessions.remove(cod);
    }
    
    /** Returns the number of sessions of the child*/
    public int numSessions(){
        return childSessions.size();
    }
    
    /** Verifies if a session exists*/
    public boolean sessionExists(String cod){
        return childSessions.keySet().contains(cod);
    }

    /**Returns information of a session with a code, verifying if the code exists*/
    public Session getFileSession (String cod){
        if(!this.sessionExists(cod)) return null;
        else return childSessions.get(cod).clone();
    }

    /**
     * Prints info from the Child
     * @return String
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("----- Child -----\r\n");
        s.append("Code: " + codeChild + "\r\n");
        s.append("Name: " + name + "\r\n");
        s.append("Gender: " + gender + "\r\n");
        s.append("Date of Birth: " + dateOfBirth.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (dateOfBirth.get(GregorianCalendar.MONTH) + 1) + "/" + dateOfBirth.get(GregorianCalendar.YEAR) + "\r\n");
        s.append("School: " + school + "\r\n");
        s.append("Reinforcement: " + reinforcement + "\r\n");
        s.append("Total # Sessions: " + nSessions + "\r\n");
        s.append("----------------------------\r\n");
        return s.toString();
    }
    
    /** Clone */
    public Child clone(Child c) { 
      return new Child(this);
    }
    
    /** Updates codeChild*/
    public void setCode(String cod){
        codeChild = cod;
    }
    
    /** Updates name*/
    public void setName(String nam){
        name = nam;
    }
    
    /** Updates gender*/
    public void setGender(Gender gend){
        gender = gend;
    }
   
    /** Updates age*/
    public void setDateOfBirth(GregorianCalendar dateOfBir){
        dateOfBirth = dateOfBir;
    }
     
    /** Updates school*/
    public void setSchool(String scho){
        school = scho;
    }
      
    /** Updates reinforcement*/
    public void setReinforcement(Reinforcement reinf){
        reinforcement = reinf;
    }
       
    /** Updates nSessions*/
    public void setNSessions(int nS){
        nSessions = nS;
    }    
}
