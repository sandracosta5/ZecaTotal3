/*
 * Class that has the info from the card to be displayed to the robot
 * Each card is composed of a code, an address and an emotion
 */
package robosteps.demo;

/**
 * @author Sandra Costa - scosta@dei.uminho.pt
 */

public class Card implements java.io.Serializable{
    private String code;
    private String address;
    private Emotion emotion;

    public Card(){
        code = ""; 
        address = ""; 
        emotion = null;
     }
    
     public Card(String cod, String add, Emotion emot){
         code = cod; 
         address = add;
         emotion = emot;
     }
     
     public Card(Card shap){
     code = shap.getCode();
     address = shap.getAddress();
     emotion = shap.getEmotion();
     }
    
     /**
      * Returns code of the Card
      * @return String
      */
    public String getCode(){
        return code;
    }
    
    /**
     * Returns address
     * @return String
     */
    public String getAddress(){
        return address;
    }
    
    /**
     * Returns emotion
     * @return Emotion
     */
    public Emotion getEmotion(){
        return emotion;
    }
    
    /**
     * Updates code
     * @param cod 
     */
    public void setCode(String cod){
        code = cod;
    }
    
    /**
     * Updates address
     * @param addr 
     */
    public void setAddress(String addr){
        address = addr;
    }
    
    /**
     * Updates emotion
     * @param emot 
     */
    public void setEmotion(Emotion emot){
        emotion = emot;
    }

    /**
     * Prints info from the card
     * @return String
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("----- Card -----\n");
        s.append("Code: " + code + "\n");
        s.append("Address: " + address + "\n");
        s.append("Emotion: " + emotion + "\n");
        s.append("----------------------------\n");
        return s.toString();
    }
    
    /**
     * Clone
     * @param s
     * @return 
     */
    public Card clone(Card s) { 
    return new Card(this);
    }
}