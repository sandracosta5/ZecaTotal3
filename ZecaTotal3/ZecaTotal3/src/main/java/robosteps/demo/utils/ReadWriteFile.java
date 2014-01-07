/*
Class to Read and Write the files created to each child and to the files od the sessions
 */
package robosteps.demo.utils;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sandra Costa - scosta@dei.uminho.pt
 */
public class ReadWriteFile {   
    static public List<String> getContents(File aFile){
        StringBuilder contents = new StringBuilder();
        List <String> toReturn= new ArrayList<String >();
        
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input =  new BufferedReader(new FileReader(aFile));
            try {
                String line = null;
                while (( line = input.readLine()) != null){
                toReturn.add(line);
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return toReturn;
    }

  /**
  * Change the contents of text file in its entirety, overwriting any
  * existing text.
  *
  * This style of implementation throws all exceptions to the caller.
  *
  * @param aFile is an existing file which can be written to.
  * @throws IllegalArgumentException if param does not comply.
  * @throws FileNotFoundException if the file does not exist.
  * @throws IOException if problem encountered during write.
  */
    static public void setContents(File aFile, String aContents) throws FileNotFoundException, IOException{
        if (aFile == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!aFile.exists()) {
            throw new FileNotFoundException ("File does not exist: " + aFile);
        }
        if (!aFile.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + aFile);
        }
        if (!aFile.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + aFile);
        }

        //use buffering
        Writer output = new BufferedWriter(new FileWriter(aFile));
        try {
            //FileWriter always assumes default encoding is OK!
            output.write( aContents );
        }
        finally {
            output.close();
        }
    }
    
    /**
     * Deletes file
     * @param rootDir 
     */
    static public void  recursiveDelete(File rootDir){
    recursiveDelete(rootDir, true);
    }
    
    /**
     * Deletes file
     * @param rootDir
     * @param deleteRoot 
     */
    static public void recursiveDelete(File rootDir, boolean deleteRoot){
        File[] childDirs = rootDir.listFiles();
        for(int i = 0; i < childDirs.length; i++){
            if(childDirs[i].isFile()){
                childDirs[i].delete();
            }
            else {
                recursiveDelete(childDirs[i], deleteRoot);
                childDirs[i].delete();
            }
        }
        if(deleteRoot){
            rootDir.delete();
        }
    }
  
    /**
     * Appends content to the file
     * @param filename
     * @param aContent
     * @throws FileNotFoundException 
     */
    public static void append(String filename, String aContent) throws FileNotFoundException{
        
        try{
            FileWriter fileWritter = new FileWriter(filename,true);
            fileWritter.write(aContent+"\n");
            fileWritter.close();
        }
        catch(IOException ioe){
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
}