
import java.io.IOException;
import java.util.ArrayList;


import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.jni.WPIMathJNI;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;


/**
 * Program
 */
public class Program {
    public static RP4LEDController ledController;
    public static LedNetworkReceiver receiver;

    public static void main(String[] args) throws IOException {
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "wpimathjni", "ntcorejni");



        receiver = new LedNetworkReceiver();

        NetworkTableInstance.getDefault();
        ledController = new RP4LEDController(18, 21);


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

    private static final ArrayList<SmartLEDPattern> patterns = new ArrayList<>();

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

//        if(patterns.get(patterns.size()-1).done(currentTime)) patterns.remove(patterns.size()-1);
        
        int countLedsApplied = 0;

        for (int i = patterns.size()-1; i >= 0; i--) {

            if(countLedsApplied >= ledController.getLength()) break;

            countLedsApplied += patterns.get(i).getLength();

            patterns.get(i).apply();
        }
        ledController.render();

    }

    /**
     * runs the loop updating the pattern data
     */
    public static void updateLoop(){
        var newPattern = receiver.periodic();
        if(newPattern.isEmpty()) return;

        if(!patterns.isEmpty()){
            //removing overlapping patterns
        for(SmartLEDPattern pattern : patterns){
            int patternStart = pattern.getStart();
            int patternEnd = pattern.getEnd();
            if(isWithin(newPattern.get().getStart(), patternStart, patternEnd)
                    || isWithin(newPattern.get().getEnd(), patternStart, patternEnd)){
                patterns.remove(pattern);
            }
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
