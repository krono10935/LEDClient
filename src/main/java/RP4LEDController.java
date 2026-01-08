import com.diozero.ws281xj.rpiws281x.WS281x;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

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

    @Override
    public Color getLED(int index) {
        return LEDReader.super.getLED(index);
    }

    @Override
    public Color8Bit getLED8Bit(int index) {
        return LEDReader.super.getLED8Bit(index);
    }

    @Override
    public void forEach(IndexedColorIterator iterator) {
        LEDReader.super.forEach(iterator);
    }

    @Override
    public void setHSV(int index, int h, int s, int v) {
        LEDWriter.super.setHSV(index, h, s, v);
    }

    @Override
    public void setLED(int index, Color color) {
        LEDWriter.super.setLED(index, color);
    }

    @Override
    public void setLED(int index, Color8Bit color) {
        LEDWriter.super.setLED(index, color);
    }

    public AddressableLEDBufferView createView(int start, int end){
        return new AddressableLEDBufferView(this, start, end);
    }
}
