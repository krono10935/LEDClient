
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;

import java.io.IOException;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;
import org.opencv.core.Core;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.cscore.OpenCvLoader;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.jni.EigenJNI;
import edu.wpi.first.util.WPIUtilJNI;


/**
 * Program
 */
public class Program {

    public static final RP4LEDController ledController = new RP4LEDController(18, 5);

    public static void main(String[] args) throws IOException {
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        EigenJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);
        OpenCvLoader.Helper.setExtractOnStaticLoad(false);

        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "wpimathjni", "ntcorejni", Core.NATIVE_LIBRARY_NAME, "cscorejni");

        Runtime.getRuntime().addShutdownHook(new Thread(ledController::close));

        patterns.add(new SmartLEDPattern(LEDPattern.solid(Color.kAqua), 0, 5, 0));
        while(true){

            try {
                periodic();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



    }

    //hz in which the loops run
    private static final long updateLoopTime = 20;
    private static final long mainLoopTime = (int)(1000/120.0);

    private static long nextUpdateLoop = 0;
    private static long nextMainLoop = 0;

    private static ArrayList<SmartLEDPattern> patterns = new ArrayList<>();

    public static void periodic() throws InterruptedException {
        long currentTime = System.currentTimeMillis();


        if(currentTime>nextUpdateLoop){
            nextUpdateLoop+=updateLoopTime;
            updateLoop();
        }

        if(currentTime>nextMainLoop){
            nextMainLoop+=mainLoopTime;
            mainLoop(currentTime);
        }

        Thread.sleep(Math.min(nextUpdateLoop, mainLoopTime));

    }

    /**
     * handles temp patterns and applies patters
     * @param currentTime current time
     */
    public static void mainLoop(long currentTime){

        if(patterns.get(patterns.size()-1).done(currentTime)) patterns.remove(patterns.size()-1);
        
        int countLedsApplied = 0;

        for (int i = patterns.size()-1; i >= 0; i--) {
            
            if(countLedsApplied >= ledController.getLength()) break;

            countLedsApplied += patterns.get(i).getLength();
        
            patterns.get(i).apply();
        }
        


    }

    /**
     * runs the loop updating the pattern data
     */
    public static void updateLoop(){
        var newPattern = LedNetworkReciever.getInstance().periodic();
        if(newPattern.isEmpty()) return;

        //removing overlapping patterns
        for(SmartLEDPattern pattern : patterns){
            int patternStart = pattern.getStart();
            int patternEnd = pattern.getEnd();
            if(isWithin(newPattern.get().getStart(), patternStart, patternEnd)
                    || isWithin(newPattern.get().getEnd(), patternStart, patternEnd)){
                patterns.remove(pattern);
            }
        }

        patterns.add(newPattern.get());


    }

    /**'
     *
     * @param value the value to check
     * @param start the start of the range
     * @param end the end of the range
     * @return true if value is within range
     */
    private static boolean isWithin(int value, int start, int end){
        return value>=start && value<=end;
    }





}
