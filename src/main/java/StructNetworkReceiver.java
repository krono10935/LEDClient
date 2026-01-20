import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructArraySubscriber;

import java.util.Optional;

public class StructNetworkReceiver {
    private static final boolean isReal = false;
    final StructArraySubscriber<LedState> sub;
    final StructArrayPublisher<LedState> pub;

    private long timeStamp = 0;

    public StructNetworkReceiver(){
        NetworkTableInstance nt = NetworkTableInstance.getDefault();

        if (isReal) nt.setServerTeam(10935);
        else nt.setServer("192.168.1.239");

        nt.startClient4("LED go brrrrrrrr :)");

        var topic = nt.getTable("Led").getStructArrayTopic("states", LedState.struct);
        sub = topic.subscribe(new LedState[0]);
        pub = topic.publish();
    }

    /**
     * '
     * periodic function to get pattern data from the network tables
     *
     * @return returns the new pattern if there is one
     */
    public Optional<SmartLEDPattern[]> periodic() {
        var arrayAtomic = sub.getAtomic();

        if(arrayAtomic.timestamp == timeStamp) return Optional.empty();

        timeStamp = arrayAtomic.timestamp;

        var array = arrayAtomic.value;

        if(array.length == 0) return Optional.empty();

        pub.set(new LedState[0]);

        SmartLEDPattern[] patternArray = new SmartLEDPattern[array.length];

        for(int i = 0; i < array.length; i++){
            patternArray[i] = array[i].toSmartLedPattern();
        }

        return Optional.of(patternArray);
    }
}
