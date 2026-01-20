import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDPattern;

/**
 * a pattern with a start and end index and an optional timeout
 */
public class SmartLEDPattern {

    private final LEDPattern pattern;
    private final long timeOut; //ms
    private final AddressableLEDBufferView view;
    private final int start;
    private final int end;
    private final long startTime;


    /**'
     * creates a new pattern
     * @param pattern the pattern to apply
     * @param start start index
     * @param end end index
     * @param timeOutSeconds timeout in seconds (0 if no timeout)
     */
    public SmartLEDPattern(LEDPattern pattern, int start, int end, double timeOutSeconds){
        this.pattern = pattern;
        this.view = Program.ledController.createView(start,end);
        this.timeOut =(long)(1000*timeOutSeconds);
        this.startTime = System.currentTimeMillis();
        this.start = start;
        this.end = end;
    }


    /**
     * @param currentTime current time
     * @return if the pattern has timed out
     */
    public boolean done(long currentTime){
        if(timeOut==0) return false;
        return currentTime - startTime >= timeOut;
    }

    /**
     * apply the pattern, should be called periodically
     */
    public void apply(){
        pattern.applyTo(view);
    }

    /**
     * @return the starting index of the LED pattern
     */
    public int getStart(){
        return start;
    }

    /**
     * @return the end index of the LED pattern
     */
    public int getEnd(){
        return end;
    }

    /**
     * @return how much time the pattern needs to be alive in micro seconds
     */
    public long getTimeOut(){
        return timeOut;
    }

}
