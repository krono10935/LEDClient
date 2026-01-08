
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;

import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Core;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.cscore.OpenCvLoader;
import edu.wpi.first.math.jni.EigenJNI;
import edu.wpi.first.util.WPIUtilJNI;
import org.opencv.core.Mat;

/**
 * Program
 */
public class Program {

    public static final RP4LEDController ledController = new RP4LEDController(18, 23, 24);

    public static void main(String[] args) throws IOException {
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        EigenJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);
        OpenCvLoader.Helper.setExtractOnStaticLoad(false);

        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "wpimathjni", "ntcorejni", Core.NATIVE_LIBRARY_NAME, "cscorejni");


        while(true){
            try {
                periodic();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }


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

    public static void mainLoop(long currentTime){
        if(patterns.get(patterns.size()-1).done(currentTime)) patterns.remove(patterns.size()-1);

        patterns.forEach(SmartLEDPattern::apply);


    }

    public static void updateLoop(){
        var newPattern = LedNetworkReciever.getInstance().periodic(ledController);
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

    private static boolean isWithin(int value, int start, int end){
        return value>=start && value<=end;
    }




}
