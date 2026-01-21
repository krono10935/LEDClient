
import java.io.IOException;
import java.util.ArrayList;


import edu.wpi.first.math.jni.WPIMathJNI;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;


/**
 * Program
 */
public class Program {

    /**
     * The array of the LED controllers
     */
    private static final ArrayList<RP4LEDController> ledStrips = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Loads the wpilib native libraries
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "wpimathjni", "ntcorejni");

        ledStrips.add(new RP4LEDController(18, 21));

        while (true) {
            try {
                periodic();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * How much time should one update loop take in milliseconds
     */
    private static final long updateLoopTime = 20;
    /**
     * How much time should one main loop take in milliseconds
     */
    private static final long mainLoopTime = (int) (1000 / 120.0);

    /**
     * The next time that the update loop needs to run
     */
    private static long nextUpdateLoop = 0;
    /**
     * The next time that the main loop needs to run
     */
    private static long nextMainLoop = 0;

    /**
     * Runs all the LED controllers
     * @throws InterruptedException throws if the program is interrupted
     */
    public static void periodic() throws InterruptedException {
        long currentTime = System.currentTimeMillis();

        // Checks if it's due to the run update loop
        if (currentTime > nextUpdateLoop) {
            nextUpdateLoop += updateLoopTime;
            ledStrips.forEach(RP4LEDController::updateLoop);
        }

        // Cheks if it's due to run the main loop
        if (currentTime > nextMainLoop) {
            nextMainLoop += mainLoopTime;
            ledStrips.forEach(strip -> strip.mainLoop(currentTime));
        }

        // Sleeps until the closest next loop
        Thread.sleep(Math.min(nextUpdateLoop, mainLoopTime));

    }
}
