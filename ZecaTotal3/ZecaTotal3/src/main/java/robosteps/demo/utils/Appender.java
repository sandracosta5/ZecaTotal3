package robosteps.demo.utils;

import java.io.*;

/**
 * Class that allows only listings of cvs files when reading a specific directory
 * @author Sandra Costa - scosta@dei.uminho.pt
 */
public class Appender {
    // directory containing csv files
    private File directory; 
    // all cvs files in a given directory
    private File[] files;     
    private String fileName = "";
    
    public Appender(String address, String nameFile) {
        // initialize console to start prompting user for input
        String dir = address;
        this.directory = new File(dir);
        
        // check that user's input points to a directory that exists 
        // in the file system
        if (this.directory.exists() && this.directory.isDirectory()) {
            // check that the directory is both readable and writable
            if (!this.directory.canRead() && !this.directory.canWrite()){
                System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("Error:\nApplication will now terminate because:");
                System.out.println("Directory: " + this.directory.getAbsolutePath());
                System.out.println("Either; NOT WRITABLE or NOT READABLE");
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++\n");
                System.exit(-100);
            }
            // read directory and check for any csv files
            this.readSelectedDirectory();
            // read all csv files and append data to fileName
            this.readAndAppend(nameFile);
        }
        else {
            System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Error:\nApplication will now terminate because:");
            System.out.println("Directory: " + this.directory.getAbsolutePath());
            System.out.println("Does not exist!!!!");
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.exit(-100);
        }
    }
    
    public void readSelectedDirectory() {
        // inform the user of the working directory
        System.out.println("Now Working in: " + this.directory.getAbsolutePath());
        // get a list of all csv files in a given directory
        // Note: use of CVSFilter declared below this class
        this.files = this.directory.listFiles(new CVSFilter());
        // if a list of csv files exist, continue...otherwise terminate application
        if (files.length<2){
            System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Application will now terminate as the number of ");
            System.out.println("files to read is zero or below 2 files");
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        }
    }
    
    private void readAndAppend(String nameFile) {
        fileName = nameFile;
        File fileOut = new File(this.directory, fileName);
        // if file exists...delete it...so it is ready for a fresh input
        if (fileOut.exists()){
            fileOut.delete(); 
        }
        // now create it 
        try {
            fileOut.createNewFile();
            System.out.println("File: '" + fileOut.getAbsolutePath() + "' created successfully ;)" );
        }
        catch (IOException err ) {
            err.printStackTrace(System.err);
        }
        
        // initialize buffer and start reading data
        BufferedWriter  out = null;
        try {
            out = new BufferedWriter(new FileWriter(fileOut,true));
            for(File input : this.files) {
                if (fileName.equalsIgnoreCase(input.getName())){
                    continue;
                }
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new FileReader(input));
                    System.out.println("++ Now reading: '" + input.getName() + "'");
                    String txt = null;
                    while ((txt=in.readLine())!=null) {
                        out.write(txt);
                        out.newLine();
                        out.flush();
                    }
                    in.close();
                    System.out.println("-- Finished Writing: '" + input.getName() + "'");
                }
                catch (IOException e ) {
                    e.printStackTrace(System.err);
                }
            }
        }
        catch (FileNotFoundException err ) {
            err.printStackTrace(System.err);
        }
        catch (IOException err ) {
            err.printStackTrace(System.err);
        }
        finally {
            try {
                if(out!=null) {
                    out.close();
                }
            }
            catch (IOException ignored ) {
                //ignored.printStackTrace(System.err);
            }
        } 
    }
    
} 

class CVSFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".csv"));
    } 
} 