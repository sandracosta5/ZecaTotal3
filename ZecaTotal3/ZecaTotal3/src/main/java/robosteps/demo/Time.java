/*
 * Timer to control the duration of the sessions, and response waiting time
 */
package robosteps.demo;

/**
 * @author Sandra Costa - scosta@dei.uminho.pt
 */

import java.util.Timer;
import java.util.TimerTask;

public class Time {
    public final static int ONE_SECOND = 1000;
    Timer timer;
    boolean flag = false;

    public Time(int seconds) {
        timer = new Timer();
        timer.schedule(new Time.TimeIsUp(), seconds*1000);    
    }

    class TimeIsUp extends TimerTask {
        public void run() {
            flag = true;
            System.out.println("Time's up!");  
            timer.cancel(); //Terminate the timer thread  
        }
    }

    public static void main(String args[]) {
    }
}