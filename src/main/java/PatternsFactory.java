import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;

import java.lang.reflect.Method;
import java.util.Optional;

public class PatternsFactory {

    public static Optional<LEDPattern> fromNtData(String patternName, Color primaryColor, Color secondaryColor, int hz){


        for(Method method: PatternsFactory.class.getDeclaredMethods()){
            if(method.getName().equals(patternName)){
                try {
                    method.invoke(primaryColor, secondaryColor, hz);
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        }


        return Optional.empty();
    }

    public static LEDPattern rainbow(Color primaryColor, Color secondaryColor, int hz){
        return LEDPattern.rainbow(255,255);
    }

    public static LEDPattern solid(Color primaryColor, Color secondaryColor, int hz){
        return LEDPattern.solid(primaryColor);
    }

    public static Color doubleArrayToColor(double[] color){
        return new Color(color[0], color[1], color[2]);
    }


}
