import com.diozero.ws281xj.rpiws281x.WS281x;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;

public class RP4LEDController extends WS281x implements LEDReader, LEDWriter  {
    public RP4LEDController(int i, int i1, int i2) {
        super(i, i1, i2);
    }

    @Override
    public int getLength() {
        return this.getNumPixels();
    }

    @Override
    public int getRed(int i) {
        return this.getRedComponent(i);
    }

    @Override
    public int getGreen(int i) {
        return getGreenComponent(i);
    }

    @Override
    public int getBlue(int i) {
        return getBlueComponent(i);
    }

    @Override
    public void setRGB(int i, int i1, int i2, int i3) {
        setPixelColourRGB(i, i1, i2, i3);
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
}
