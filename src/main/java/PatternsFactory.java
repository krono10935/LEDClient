import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Dimensionless;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * factory to create led patterns
 */
public class PatternsFactory {

    /**
     * The instance used for reflection
     */
    private static final PatternsFactory instance = new PatternsFactory();

    /**
     * The new primary color and secondary color
     */
    private Color primaryColor, secondaryColor;

    /**
     * how fast should the LED pattern move
     */
    private int hz;

    /**
     * The brightness of the LED pattern (0-1)
     */
    private Dimensionless brightness;

    private final BooleanSupplier rslStatus;

    /**
     * parses nt data to create a LED pattern
     * uses reflection to call the correct method
     *
     * @param patternName    name of pattern
     * @param primaryColor   the primary color
     * @param secondaryColor the secondary color
     * @param hz             hz of the pattern
     * @param brightness     how bright should the LED be (0-1)
     * @return the pattern only if it exists in the factory
     */
    public static Optional<LEDPattern> fromNtData(String patternName, Color primaryColor, Color secondaryColor, int hz, double brightness) {
        for (Method method : PatternsFactory.class.getDeclaredMethods()) {
            if (method.getName().equals(patternName)) {
                setNewPatternParams(primaryColor, secondaryColor, hz, brightness);
                try {
                    return Optional.of((LEDPattern) method.invoke(instance));
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        }

        return Optional.empty();

    }

    /**
     * Updates the new params for the new pattern.
     * used that the functions of the patterns wouldn't need to ask for the params.
     *
     * @param primaryColor   the new primary color of the pattern
     * @param secondaryColor the new secondary color of the pattern
     * @param hz             the new refresh rate of the pattern
     * @param brightness     the new brightness of the pattern
     */
    private static void setNewPatternParams(Color primaryColor, Color secondaryColor, int hz, double brightness) {
        instance.primaryColor = primaryColor;
        instance.secondaryColor = secondaryColor;
        instance.hz = hz;
        instance.brightness = Units.Value.of(brightness);
    }

    /**
     * Creates a rainbow pattern
     *
     * @return a rainbow pattern
     */
    public LEDPattern rainbow() {
        return LEDPattern.rainbow(255, 255).scrollAtRelativeSpeed(Units.Hertz.of(hz)).atBrightness(brightness);
    }

    /**
     * Creates a solid pattern
     *
     * @return a solid pattern
     */
    public LEDPattern solid() {
        return LEDPattern.solid(primaryColor).atBrightness(brightness);
    }

    /**
     * Creates a blinking pattern
     *
     * @return a blinking pattern
     */
    public LEDPattern blink() {
        return LEDPattern.solid(primaryColor).blink(Units.Seconds.of(1.0 / hz)).atBrightness(brightness);
    }

    public LEDPattern rsl_blink(){
        return LEDPattern.solid(primaryColor).synchronizedBlink(rslStatus).atBrightness(brightness)
                .overlayOn(LEDPattern.solid(secondaryColor).atBrightness(brightness));
    }

    /**
     * an empty led pattern, used for when there is no string attached
     * @return a turned off pattern
     */
    public LEDPattern solid_black() {
        return LEDPattern.kOff;
    }


    /**
     *
     * @param color Color in {r,g,b} format
     * @return a wpilib color object
     */
    public static Color doubleArrayToColor(double[] color) {
        return new Color(color[0], color[1], color[2]);
    }

    private PatternsFactory() {
        var entry = NetworkTableInstance.getDefault().getTable("Led").getEntry("RslStatus");
        rslStatus = () -> entry.getBoolean(false);
    }

}
