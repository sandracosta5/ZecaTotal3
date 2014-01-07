package friendularity.test.camera.r50;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.robokind.api.vision.config.CameraServiceConfig;
import org.robokind.api.vision.messaging.RemoteImageServiceClient;
import org.robokind.client.basic.Robokind;
import org.robokind.client.basic.UserSettings;
import com.google.zxing.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.*;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import robosteps.demo.*;


/**
 * Camera service
 */
public class ImageDemo extends JFrame implements Runnable{
    
    public volatile boolean runit;
    public volatile boolean running;
    public Thread t;
    int ii = 0;
    JPanel jp = new JPanel();
    
    public static String addressQRCode = "";
    public static String ID = "";
    ImageMonitor monitor = new ImageMonitor();
    IplImage img;
    public static App myapp;
    
    /**
     * Inits Thread
     * @param app Main
     */
    public ImageDemo(App app) {
      myapp = app;
      init();
   }
    
    /**
     * Starts Thread
     */
    public void init(){
        t = new Thread(this);
        runit = false;
        running = false;
        t.start();    	   
   }
    
    /**
     * Gets ID from QR Code
     * @return String
     */
    public String getID(){
        String toreturn="no";
        System.out.println("ID is: "+ID+" returning: "+toreturn);
        
        if("JoyAngerFearSadnessSurprise".toLowerCase().contains(ID.toLowerCase())){
            toreturn=ID;           
            System.out.println("ID is: "+ID+" returning: "+toreturn);
        }
        if(ID.isEmpty()){
            toreturn= "no";
        }
        System.out.println("ID is: "+ID+" returning: "+toreturn);
        return toreturn;
    }
    
    /**
     * Sets ID of a QR Code
     * @param r String
     */
    public static void setID(String r){
        ID = r;
        if(myapp!=null)  ImageDemo.myapp.wakeUp();
    }
    
    /**
     * Starts the camera service
     */
    public ImageDemo() {
        //IP Address of the Robot to access the cameras
        String IP = "192.168.1.108";
        UserSettings.setCameraAddress(IP);
        //Right Eye = 0; Left Eye = 1;
        UserSettings.setCameraId("0");
        RemoteImageServiceClient<CameraServiceConfig> images = Robokind.connectCameraService();
        ImageMonitor monitor = new ImageMonitor();
        add(monitor);
        images.addImageListener(monitor);
    }
    
    /**
     * Main
     * @param args 
     */
    public static void main(String[] args) {
        ImageDemo cot = new ImageDemo();
        Thread th = new Thread(cot);
        th.start();
    }
    
    @Override
    /**
     * Run
     */
    public void run() {
        runit=true;
        ImageDemo frame = new ImageDemo();
        frame.setVisible(true);   
    }
}
