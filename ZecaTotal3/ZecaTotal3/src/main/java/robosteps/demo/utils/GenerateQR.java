/*
 * Generates the QR Code for each child created
 */
package robosteps.demo.utils;

/**
 * @author Sandra Costa - scosta@dei.uminho.pt
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
 
public class GenerateQR {
    /**
     * Generates the QR Code for each child created
     * @param number code of the child
     */
    public void newQR (String number){
        ByteArrayOutputStream out = QRCode.from(number).to(ImageType.PNG).stream();
        try {
            FileOutputStream fout = new FileOutputStream(new File("./ChildrenData/Child_"+number+"/Child_"+number+".jpg"));
            fout.write(out.toByteArray());
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}