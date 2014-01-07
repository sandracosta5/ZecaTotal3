package friendularity.test.camera.r50;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static friendularity.test.camera.r50.ImageMonitor.addressQRCode;
import static friendularity.test.camera.r50.ImageMonitor.rgb_min;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jflux.api.core.Listener;
import org.robokind.api.vision.ImageEvent;
import org.robokind.impl.vision.PortableImageUtils;


/**
 * @author Sandra Costa - scosta@dei.uminho.pt
 */

public class ImageMonitor extends JPanel implements Listener<ImageEvent> {
    private Image myImage;
    public IplImage ipl, ipl2;
    public ImageDemo imgDemo;
    //HSV Code for Yellow
    static CvScalar rgb_min = cvScalar(20, 100, 100, 0);
    static CvScalar rgb_max = cvScalar(30, 255, 255, 0);
    int posX = 0;
    int posY = 0;
    public static String addressQRCode = "";
    //String in case the QR Code is not detected, do not use ERROR (most often used in the world)
    String ERROR_CASE = "QR Code não reconhecido";
    public boolean processContinues = true;
    BinaryBitmap binaryBitmap=null;
    IplImage detectThrs;
    CvMoments moments = new CvMoments(); 
    double mom10 = 0;
    double mom01 = 0;
    double area = 0;
    IplImage imgHSV;
    IplImage imgThreshedBlur; 
    Result result = null;
    String res ="no";
    
    public ImageMonitor() {
        myImage = null;
        }

    @Override
    /**
     * Event to start the camera
     */
    public void handleEvent(ImageEvent t) {
        myImage = PortableImageUtils.unpackImage(t);
        //variable that pauses the camera
        processContinues = true; 
         
        if(myImage != null){
            //gets RGB image from BGR input
            ipl = getConvertedImage(myImage); 
            myImage=ipl.getBufferedImage();
            //Gets Threshold Image - Only yellow appears as white
            detectThrs = getThresholdImage(ipl); 
            //Calculation of the moments of the shape (getting its position, and area)
            
            cvMoments(detectThrs, moments, 1);
            mom10 = cvGetSpatialMoment(moments, 1, 0);   
            mom01 = cvGetSpatialMoment(moments, 0, 1);
            area = cvGetCentralMoment(moments, 0, 0);
            posX = (int) (mom10 / area);
            posY = (int) (mom01 / area);
            
            //Only if there is valid position                    
            //It detected there are a yellow shape, and its area it is bigger than 1000
            if (posX > 0 && posY > 0 && area > 500 && processContinues == true) {
                //When detects a yellow area, saves that image
                cvSaveImage("keep.png",ipl);
                //identifies the QR Code
                addressQRCode = QRReader("./keep.png");
                
                //QR Code is valid        
                if(!"QR Code não reconhecido".equals(addressQRCode)){
                    ImageDemo.setID(addressQRCode);
                    //Pauses the camera
                    processSTOPS();
                    }  
                         
                }
        }
        repaint();
        
        
        try {
            ((JFrame)getTopLevelAncestor()).setSize(t.getWidth(), t.getHeight());
        } catch(Exception e) {
        }
        //ipl.release();
        //myImage.flush();
        //myImage2.flush();
    }
    
    
    @Override
    /**
     * Shows the image in the window
     */
    public void paint(Graphics g) {
        if(myImage != null){
            g.drawImage(myImage, 0, 0, getWidth(), getHeight(), null);
        }
    } 
    
    /**
     * Resumes the camera
     */
    public void processOK(){
        processContinues = true;    
    }
    
    /**
     * Pauses the camera
     */
    public void processSTOPS(){
        processContinues = false;
    }
   
    /**
     * Tranformation of the image BGR from the robot to RGB
     * @param myimage - image BGR
     * @return ipl - image RGB
     */
    private IplImage getConvertedImage(Image myimage){
        BufferedImage bufferedImage = (BufferedImage) myimage;
        BufferedImage convertedImg = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        convertedImg.getGraphics().drawImage(bufferedImage, 0, 0, null);
        ipl2 = IplImage.createFrom(convertedImg);
        cvCvtColor(ipl2,ipl2 , CV_BGR2RGB);
        return ipl2;
    }
    
    /**
     * Gets the yellow part of the image
     * @param img - image from the video
     * @return imgThreshedBlur - yellow part ofthe video
     */
    private IplImage getThresholdImage(IplImage img){
    //converts the image in HSV
    imgHSV = cvCreateImage(cvGetSize(img), 8, 3);
    cvCvtColor(img, imgHSV, CV_BGR2HSV);
    imgThreshedBlur = cvCreateImage(cvGetSize(img), 8, 1);
    //does the Threshold between a min and a max value of yellow 
    cvInRangeS(imgHSV, rgb_min, rgb_max, imgThreshedBlur);
    //Smooths the image
    cvSmooth (imgThreshedBlur, imgThreshedBlur, CV_BLUR, 15);
    //Dilates the image
    cvDilate(imgThreshedBlur, imgThreshedBlur, null,6);
    return imgThreshedBlur;
}

    /**
     * Reads the QR Code 
     * @param add - String with the address of the image
     * @return String with the result of the reading
     */
    private String QRReader (String add){
              
        try{
            binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(add)))));
            result = new MultiFormatReader().decode(binaryBitmap);
            res = result.getText();
            return res;
        }catch(Exception ex){
            //ex.printStackTrace();
        }
        return ERROR_CASE;
    }
}
