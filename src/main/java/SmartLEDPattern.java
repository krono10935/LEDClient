import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDPattern;

public class SmartLEDPattern {

    private final LEDPattern pattern;
    private final long timeOut; //ms
    private final AddressableLEDBufferView view;
    private final int start;
    private final int end;
    private final long startTime;

    public SmartLEDPattern(LEDPattern pattern, int start, int end, double timeOutSeconds){
        this.pattern = pattern;
        this.view = Program.ledController.createView(start,end);
        this.timeOut =(long)(1000*timeOutSeconds);
        this.startTime = System.currentTimeMillis();
        this.start = start;
        this.end = end;
    }



    public boolean done(long currentTime){
        if(timeOut==0) return false;
        return currentTime - startTime >= timeOut;
    }

    public void apply(){
        pattern.applyTo(view);
    }

    public int getStart(){
        return start;
    }

    public int getEnd(){
        return end;
    }






}
