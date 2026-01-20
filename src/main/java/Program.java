
import java.io.IOException;
import java.util.ArrayList;


import edu.wpi.first.math.jni.WPIMathJNI;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;


/**
 * Program
 */
public class Program {
    /**
     * The LED controller that controls the LED
     */
    public static RP4LEDController ledController;

    /**
     * The receiver from the network of the LED
     */
    public static StructNetworkReceiver receiver;

    public static void main(String[] args) throws IOException {

        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "wpimathjni", "ntcorejni");

        receiver = new StructNetworkReceiver();

        NetworkTableInstance.getDefault();
        ledController = new RP4LEDController(18, 21);

        while (true) {

            try {
                periodic();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

    //hz in which the loops run
    private static final long updateLoopTime = 20;
    private static final long mainLoopTime = (int) (1000 / 120.0);

    private static long nextUpdateLoop = 0;
    private static long nextMainLoop = 0;

    private static final ArrayList<SmartLEDPattern> patterns = new ArrayList<>();

    public static void periodic() throws InterruptedException {
        long currentTime = System.currentTimeMillis();


        if (currentTime > nextUpdateLoop) {
            nextUpdateLoop += updateLoopTime;
            updateLoop();
        }

        if (currentTime > nextMainLoop) {
            nextMainLoop += mainLoopTime;
            mainLoop(currentTime);
        }

        Thread.sleep(Math.min(nextUpdateLoop, mainLoopTime));

    }

    /**
     * handles temp patterns and applies patters
     *
     * @param currentTime current time
     */
    public static void mainLoop(long currentTime) {

        if (patterns.isEmpty()) return;

        patterns.removeIf(pattern -> pattern.done(currentTime));

        patterns.forEach(SmartLEDPattern::apply);
        ledController.render();

    }

    /**
     * runs the loop updating the pattern data
     */
    public static void updateLoop() {
        var newPatternOptional = receiver.periodic();
        if (newPatternOptional.isEmpty()) return;

        var newPatterns = newPatternOptional.get();

        for(SmartLEDPattern newPattern : newPatterns){
            if (!patterns.isEmpty() && newPattern.getTimeOut() == 0) {
                //removing overlapping patterns
                patterns.removeIf(pattern -> isWithin(newPattern.getStart(), newPattern.getEnd(), pattern.getStart(), pattern.getEnd()));
            }

            patterns.add(newPattern);
        }
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
