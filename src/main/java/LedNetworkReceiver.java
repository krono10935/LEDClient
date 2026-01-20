import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.Optional;


/**
 * receiver to get the pattern data from the network tables
 */
public class LedNetworkReceiver {

    private static final boolean isReal = false;
    final NetworkTableEntry ledLineIDEntry;
    final NetworkTableEntry patternEntry;
    final NetworkTableEntry mainColorEntry;
    final NetworkTableEntry secondaryColorEntry;
    final NetworkTableEntry hzEntry;
    final NetworkTableEntry rangeEntry;
    final NetworkTableEntry hasChangeEntry;
    final NetworkTableEntry timeOutEntry;
    final NetworkTableEntry brightness;

    public LedNetworkReceiver() {
        NetworkTableInstance nt = NetworkTableInstance.getDefault();

        if (isReal) nt.setServerTeam(10935);
        else nt.setServer("192.168.1.239");

        nt.startClient4("LED go brrrrrrrr :)");

        NetworkTable table = nt.getTable("Led");
        ledLineIDEntry = table.getEntry("id");
        patternEntry = table.getEntry("pattern");
        mainColorEntry = table.getEntry("mainColor");
        secondaryColorEntry = table.getEntry("secondaryColor");
        hzEntry = table.getEntry("hz");
        rangeEntry = table.getEntry("range");
        hasChangeEntry = table.getEntry("hasChange");
        timeOutEntry = table.getEntry("timeout");
        brightness = table.getEntry("brightness");
    }

    /**
     * '
     * periodic function to get pattern data from the network tables
     *
     * @return returns the new pattern if there is one
     */
    public Optional<SmartLEDPattern> periodic() {
        if (!hasChangeEntry.getBoolean(false)) return Optional.empty();

        try {
            var primaryColor = PatternsFactory.doubleArrayToColor(mainColorEntry.getDoubleArray(new double[]{0, 0, 0}));
            var secondaryColor = PatternsFactory.doubleArrayToColor(secondaryColorEntry.getDoubleArray(new double[]{0, 0, 0}));
            var pattern = PatternsFactory.fromNtData(patternEntry.getString("solid_black"),
                    primaryColor, secondaryColor, (int) hzEntry.getDouble(0), brightness.getDouble(1));

            if (pattern.isEmpty()) return Optional.empty();

            var range = (rangeEntry.getDoubleArray(new double[]{0, 0}));

            SmartLEDPattern smartPattern = new SmartLEDPattern(pattern.get(),
                    (int) range[0], (int) range[1], timeOutEntry.getDouble(0));


            return Optional.of(smartPattern);
        } finally {
            hasChangeEntry.setBoolean(false);
        }

    }


}
