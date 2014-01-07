/*
 * Admin page where the information of the children is created, updated or deleted
 */
package robosteps.demo;

/**
 * @author Sandra Costa - scosta@dei.uminho.pt
 */

import robosteps.demo.expdata.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.GregorianCalendar;
import java.io.File;
import java.io.IOException;
import robosteps.demo.utils.Appender;
import robosteps.sessions.Session;

public class Admin {
    /**
     * Administration Menu to manage children database
     * @param args 
     */
    public static void main( String[] args ){
        Scanner input = new Scanner(System.in); 
        int day, month, year;
        GregorianCalendar date;
        String data, data1;
        String codeChild = "";      
        int option = 7;
        int option2 = 7;

        while (option != 0){
            System.out.println("\nZECA's System Configuration");
            System.out.println("Select on of the following options:");
            System.out.println("1 - Add a new child");
            System.out.println("2 - View Child's Information");
            System.out.println("3 - Remove a child's information");
            System.out.println("4 - Change child's information");
            System.out.println("5 - Get Child's files of Performance and Answers");
            System.out.println("0 - End");
            
            option = input.nextInt();
  
            switch (option)
            {
                
                case 1: //1 - Adds a new child
                    Child myChild = new Child(); 
                    //Creates the folders for the Child's Info and Sessions inside of the folder ChildrenData   
                    myChild.createDataStructure();
                    //Entering Child's Data
                    System.out.print("Name: ");
                    input.nextLine(); //To discard \n
                    myChild.setName(input.next());                
                    System.out.print("\nGender (F or M): ");
                    data=input.next();
                    if ("F".equals(data) || "f".equals(data)) {myChild.setGender(Gender.FEMALE);}
                    else if ("M".equals(data) || "m".equals(data)) {myChild.setGender(Gender.MALE);}
                    else {myChild.setGender(Gender.UNKNOWN);} 
                    System.out.println("\nDate of Birth: ");
                    //Verifies if year is valid
                    do{
                    System.out.println("\nYear: ");
                    year = input.nextInt();
                    }while (checkYear(year) == false);
                    //Verifies if month is valid
                    do{
                    System.out.println("\nMonth: ");
                    month = input.nextInt();
                    }while (checkMonth(month) == false);
                    //Verifies if day is valid
                    do{
                    System.out.println("\nDay: ");
                    day = input.nextInt();
                    }while (checkDay(day, month) == false);
                    date = new GregorianCalendar (year, month-1 ,day,0,0);
                    myChild.setDateOfBirth(date);
                    System.out.println("\nSchool: ");
                    input.nextLine();
                    myChild.setSchool(input.nextLine());   
                    System.out.println("\nReinforcement Type: ");
                    System.out.println("\nA -  Movement");
                    System.out.println("\nB -  Verbal");
                    System.out.println("\nC -  Verbal&Movement");
                    data1=input.next();
                    if ("A".equals(data1) || "a".equals(data1)) {myChild.setReinforcement(Reinforcement.MOVEMENT);}
                    else if ("B".equals(data1) || "b".equals(data1)) {myChild.setReinforcement(Reinforcement.VERBAL);}
                    else {myChild.setReinforcement(Reinforcement.VERBAL_MOVEMENT);}
                    //When a new child is created, #Sessions = 0
                    myChild.setNSessions(0);  
                    //Prints info from the child
                    System.out.println(myChild.toString());
                    //Saving the Child's data into a file
                    myChild.saveChildData(); 
                    System.out.println(myChild.getNameChild() + "'s data is saved.");
                    break;
                
                case 2: //2 - View Child's Information
                    //If there are no children breaks
                    if (!ChildrenVerification()) {
                        System.out.println("There are no children to present.");
                        break;
                    }

                    //Presents existing Children
                    System.out.println("You can have access to the information of "); 
                    existingChildren();
                
                    //Repeats until getting an existing code
                    do{
                    System.out.println("\n Code of the child (number only): ");    
                    codeChild = input.next();
                    }while(childExists(Integer.parseInt(codeChild)) == false);
                    
                    //Loads the information of the child
                    Child loadChild = new Child(Integer.parseInt(codeChild)); /*loadData()*/
                    break;
                
                case 3:  //3 - Remove a child's information
                    //If there are no children breaks
                    if (!ChildrenVerification()) {
                    System.out.println("There are no children to present.");
                    break;
                    }
                    else{
                        //Presents existing Children
                        System.out.println("You can have delete "); 
                        existingChildren();
                        
                        do{
                            System.out.println("\nCode of the child: ");    
                            codeChild = input.next();
                            }while(childExists(Integer.parseInt(codeChild)) == false);
                            Child edChild=new Child(Integer.parseInt(codeChild));  
                            edChild.deleteChildData(); /*Delete the information of a Child*/
                    }
                    break;
                    
            case 4: //4 - Change child's information
                option2=7;
                //If there are no children breaks
                if (!ChildrenVerification())
                    break;
                else{
                    //Presents existing Children
                    System.out.println("You can have access to the information of "); 
                    existingChildren();
                    do{
                        System.out.println("\nCode of the child: ");    
                        codeChild = input.next();
                    }while(childExists(Integer.parseInt(codeChild)) == false);
                    Child edChild=new Child(Integer.parseInt(codeChild));
                    System.out.println("\nWhich information do you wish to change? ");

                    while (option2 != 0)
                    {
                        //Getting Child's Data
                        System.out.print("\n1 - Name");
                        System.out.print("\n2 - Gender");
                        System.out.print("\n3 - Date of Birth");
                        System.out.print("\n4 - School");
                        System.out.print("\n5 - Reinforcement Type");
                        System.out.println("\n0 - Go back");

                        option2 = input.nextInt();
                        switch (option2)
                        {
                            case 1:
                                System.out.print("\nNew Name: ");
                                edChild.setName(input.nextLine());
                                break;
                            case 2:
                                System.out.print("\nGender (F or M): ");
                                data=input.next();
                                if ("F".equals(data) || "f".equals(data)) {edChild.setGender(Gender.FEMALE);}
                                else if ("M".equals(data) || "m".equals(data)) {edChild.setGender(Gender.MALE);}
                                else {edChild.setGender(Gender.UNKNOWN);} 
                                break;
                            case 3:
                                System.out.println("\nDate of Birth: ");
                                do{
                                System.out.println("\nYear: ");
                                year = input.nextInt();
                                }while (checkYear(year) == false);
                                do{
                                System.out.println("\nMonth: ");
                                month = input.nextInt();
                                }while (checkMonth(month) == false);
                                do{
                                System.out.println("\nDay: ");
                                day = input.nextInt();
                                }while (checkDay(day, month) == false);

                                date = new GregorianCalendar (year, month-1 ,day,0,0);
                                edChild.setDateOfBirth(date);
                                break;
                            case 4:
                                System.out.print("\nNew School: ");
                                input.nextLine();
                                edChild.setSchool(input.nextLine());  
                                break;
                            case 5:
                                System.out.println("\nReinforcement Type: ");
                                System.out.println("\nA -  Movement");
                                System.out.println("\nB -  Verbal");
                                System.out.println("\nC -  Verbal&Movement");
                                data1=input.next();                               
                                
                                if ("A".equals(data1) || "a".equals(data1)) {edChild.setReinforcement(Reinforcement.MOVEMENT);}
                                else if ("B".equals(data1) || "b".equals(data1)) {edChild.setReinforcement(Reinforcement.VERBAL);}
                                else {edChild.setReinforcement(Reinforcement.VERBAL_MOVEMENT);}
                                
                                break;
                            default: break;
                        }
                        //Saves the modified data, keeps previous data
                        edChild.saveChildData();
                    }
                }
                break;
            
            case 5:
                String in = "";
                //If there are no children breaks
                if (!ChildrenVerification())
                    break;
                else{
                    //Presents existing Children
                    System.out.println("You can have access to the information of "); 
                    existingChildren();
                    do{
                        System.out.println("\nCode of the child: ");    
                        in = input.next();
                        if(Integer.parseInt(in)<10) codeChild = "0"+in;
                        
                    }while(childExists(Integer.parseInt(codeChild)) == false);
                    
                    String add1 = "ChildrenData/Child_"+codeChild+"/Sessions/";
                    String add2 = "ChildrenData/Child_"+codeChild+"/Performances/";
                    String add3 = "ChildrenData/Child_"+codeChild+"/Answers/";
                    String namefile1 = "TotalSessionsChild"+codeChild+".csv";
                    String namefile2 = "TotalPerformancesChild"+codeChild+".csv";
                    String namefile3 = "TotalAnswersChild"+codeChild+".csv";
                    
                    //Class that appends all the created files
                    Appender ap1 = new Appender (add1, namefile1);
                    Appender ap2 = new Appender (add2, namefile2);
                    Appender ap3 = new Appender (add3, namefile3);
                }
                break;
            default: break;
            }
        }
    }

    /**
     * Verifies if there are any children 
     * @return true if there is children, false if not
     */
    public static boolean ChildrenVerification(){
        File dir = new File("./ChildrenData/");
        String[] dirChildren = dir.list(); 
        //Verify if there are children in the file
        if(dirChildren.length<1){
            for(int i=0; i<dirChildren.length;i++) System.out.println("Directories are:  "+ dirChildren[i]);
            return false;
        }
        return true;
    }
    
    /**
     * Presents Existing Children
     */
    public static void existingChildren(){
        File dir = new File("./ChildrenData/");
        String[] dirChildren = dir.list(); 
        //Children in the file
        for(int i=0; i<dirChildren.length;i++) System.out.println(dirChildren[i]);              
    }
    
    /**
     * Checks if a child exists using her code
     * @param code - int
     * @return true is the child exists, false if not
     */
    public static boolean childExists(int code){
        boolean check = false;
        File dir = new File("./ChildrenData/");
        String[] dirChildren = dir.list(); 
        //Children in the file
        for(int i=0; i<dirChildren.length;i++){
            if(code == i+1){
                // child exists
                check = true; 
                break;
            }    
            else
                // child does not exists
                check = false;
        }
        return check;
    }          
    
    /**
     * Verifies if the year is valid
     * @param y - int
     * @return true if valid, false if not
     */
    public static boolean checkYear (int y){
        GregorianCalendar gregorianCalendar = new GregorianCalendar();    
        //Current Year
        String ye = String.valueOf(gregorianCalendar.get(GregorianCalendar.YEAR));    
        if(y > Integer.parseInt(ye) || y < 1900){
            System.out.println("Invalid Year. Insert a value between 1900 and  " + ye + ".");
            return false;
        }
        else return true;
    }
    
    /**
     * Verifies if the month is valid
     * @param m
     * @return true if valid, false if not
     */
    public static boolean checkMonth (int m){
        if(m < 0 || m > 12){
            System.out.println("Invalid Month. Insert a value between 1 and 12");
            return false;
        }
        else return true;
    }
    
    /**
     * Verifies if the day is valid
     * @param d
     * @param m
     * @return true if valid, false if not
     */
    public static boolean checkDay (int d, int m){
        if(m == 2)
            if(d < 0 || d > 28){
            System.out.println("February does no have more than 28 days. Insert a value between 1 and 28.");
            return false;
            } 
        
        if(m == 1 || m == 3 || m == 5 || m == 8 || m == 10 || m == 12 )
            if(d < 0 || d > 31){
                System.out.println("Insert a value between 1 and 31.");
                return false;
            }        
        
        if(m == 4 || m == 6 || m == 7 || m == 9 || m == 11)
            if(d < 0 || d > 30)
            {
                System.out.println("Insert a value between 1 and 30.");
                return false; 
            }
        return true;
    }
}