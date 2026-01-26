import com.diozero.ws281xj.rpiws281x.WS281x;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;

import java.util.ArrayList;

public class RP4LEDController extends WS281x implements LEDReader, LEDWriter  {
    /**
     * The patterns on this LED strip
     */
    private final ArrayList<SmartLEDPattern> patterns = new ArrayList<>();

    /**
     * The receiver of this LED strip
     */
    private final StructNetworkReceiver receiver;

    /**
     * The ID of the LED controller, this is also the gpio pin used.
     */
    public final int id;

    /**
     * creates an LED controller
     * @param gpioNum the id of the GPIO pin used for the LED comms
     * @param ledCount The number of pixels connected to the pi
     */
    public RP4LEDController(int gpioNum, int ledCount) {
        super(gpioNum, 255, ledCount);

        id = gpioNum;

        receiver = new StructNetworkReceiver(this);
    }

    @Override
    public int getLength() {
        return this.getNumPixels();
    }

    @Override
    public int getRed(int i) {
        return getGreenComponent(i);
    }

    @Override
    public int getGreen(int i) {
        return getRedComponent(i);
    }

    @Override
    public int getBlue(int i) {
        return getBlueComponent(i);
    }

    @Override
    public void setRGB(int i, int i1, int i2, int i3) {
        super.setPixelColourRGB(i, i2, i1, i3);
    }

    /**
     *
     * @param start first LED to include
     * @param end last LED to include
     * @return a view of the LED strip
     */
    public AddressableLEDBufferView createView(int start, int end){
        return new AddressableLEDBufferView(this, start, end);
    }

    /**
     * runs the main loop of the LED
     * This updates moving patterns and removes timed out patterns
     * @param currentTime The current time in milliseconds
     */
    public void mainLoop(long currentTime){
        if (patterns.isEmpty()) return;

        patterns.removeIf(pattern -> pattern.done(currentTime));

        patterns.forEach(SmartLEDPattern::apply);
        render();
    }

    /**
     * runs the update loop of the LED.
     * This receives updates from the netwroktables for new patterns.
     */
    public void updateLoop(){
        var output = receiver.periodic();
        if(output.isEmpty()) return;

        var patterns = output.get();

        for(SmartLEDPattern pattern : patterns) addPattern(pattern);
    }


    /**
     * adds a pattern to the LED controller
     * @param pattern the pattern to add
     */
    private void addPattern(SmartLEDPattern pattern){
        if(!patterns.isEmpty() && pattern.getTimeOut() == 0)
            patterns.removeIf(oldPattern ->
                    isWithin(pattern.getStart(), pattern.getEnd(), oldPattern.getStart(), oldPattern.getEnd()));

        patterns.add(pattern);
    }

    /**
     * '
     * Checks if the new LED range is within the old LED range.
     *
     * @param newStart the start of the new LED range
     * @param newEnd   the end of the new LED range
     * @param oldStart the start of the old LED range
     * @param oldEnd   the end of the old LED range
     * @return true if value is within range
     */
    private static boolean isWithin(int newStart, int newEnd, int oldStart, int oldEnd) {
        return (newStart >= oldStart && newStart <= oldEnd) || (newEnd >= oldStart && newEnd <= oldEnd);
    }
}
