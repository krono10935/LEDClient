import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * factory to create led patterns
 */
public class PatternsFactory {

    private static final PatternsFactory instance = new PatternsFactory();

    /**
     * parses nt data to create a led pattern
     * uses reflection to call the correct method
     * @param patternName name of pattern
     * @param primaryColor the primary color
     * @param secondaryColor the secondary color
     * @param hz hz of the pattern
     * @return the pattern only if it exists in the factory
     */
    public static Optional<LEDPattern> fromNtData(String patternName, Color primaryColor, Color secondaryColor, int hz, double brightness){


        for(Method method: PatternsFactory.class.getDeclaredMethods()){
            if(method.getName().equals(patternName)){
                try {
                    return Optional.of((LEDPattern)method.invoke(instance, primaryColor, secondaryColor, hz, brightness));
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        }

        return Optional.empty();

    }

    public LEDPattern rainbow(Color primaryColor, Color secondaryColor, int hz, double brightness){
        return LEDPattern.rainbow(255,255).scrollAtRelativeSpeed(Units.Hertz.of(hz)).atBrightness(Units.Value.of(brightness));
    }

    public LEDPattern solid(Color primaryColor, Color secondaryColor, int hz, double brightness){
        return LEDPattern.solid(primaryColor).atBrightness(Units.Value.of(brightness));
    }


    /**
     *
     * @param color Color in {r,g,b} format
     * @return a wpilib color object
     */
    public static Color doubleArrayToColor(double[] color){
        return new Color(color[0], color[1], color[2]);
    }

    private PatternsFactory(){}

}
