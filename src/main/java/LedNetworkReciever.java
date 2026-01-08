import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.Optional;

public class LedNetworkReciever {

    private static final  boolean isReal = false;
    final NetworkTableEntry ledLineIDEntry;
    final NetworkTableEntry patternEntry;
    final NetworkTableEntry mainColorEntry;
    final NetworkTableEntry secondaryColorEntry;
    final NetworkTableEntry hzEntry;
    final NetworkTableEntry rangeEntry;
    final NetworkTableEntry hasChangeEntry;
    final NetworkTableEntry timeOutEntry;

    private static LedNetworkReciever instance;

    public static LedNetworkReciever getInstance(){
        if(instance==null){
            instance = new LedNetworkReciever();
        }
        return instance;
    }

    private LedNetworkReciever (){
        NetworkTableInstance nt = NetworkTableInstance.getDefault();

        if(isReal) nt.setServerTeam(10935);
        else nt.setServer("127.0.0.1");

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


    }

    public Optional<SmartLEDPattern> periodic(RP4LEDController ledController){
        if(!hasChangeEntry.getBoolean(false)) return Optional.empty();
        var primaryColor = PatternsFactory.doubleArrayToColor(mainColorEntry.getDoubleArray(new double[]{0,0,0}));
        var secondaryColor = PatternsFactory.doubleArrayToColor(secondaryColorEntry.getDoubleArray(new double[]{0,0,0}));
        var pattern = PatternsFactory.fromNtData(patternEntry.getString("none"),
                primaryColor, secondaryColor, (int)hzEntry.getDouble(0) );

        if(pattern.isEmpty()) return Optional.empty();


        var range = (rangeEntry.getDoubleArray(new double[]{0,0}));

        SmartLEDPattern smartPattern = new SmartLEDPattern(pattern.get(),
                (int)range[0],(int)range[1], timeOutEntry.getDouble(0));

        return Optional.of(smartPattern);

    }




}
