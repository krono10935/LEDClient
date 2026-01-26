import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructArraySubscriber;

import java.util.Optional;

public class StructNetworkReceiver {
    /**
     * If the receiver should connect to the robot or a test pc
     */
    private static final boolean isReal = true;
    /**
     * If the client has started, as it is shared for all the receivers
     */
    private static boolean initialized = false;

    /**
     * The subscriber of the struct array
     */
    private final StructArraySubscriber<LedState> sub;
    /**
     * The publisher of the struct array, used to clear the data on the nt once it is read
     */
    private final StructArrayPublisher<LedState> pub;

    /**
     * The timestamp of the last update received, used to check if there was new data
     */
    private long timeStamp = 0;

    /**
     * The LED controller this receiver applies to
     */
    private final RP4LEDController ledController;

    /**
     * Creates a new network receiver for the LED state struct
     * @param ledController The LED controller this receiver applies to
     */
    public StructNetworkReceiver(RP4LEDController ledController){
        NetworkTableInstance nt = NetworkTableInstance.getDefault();

        this.ledController = ledController;

        if(!initialized){
            if (isReal) nt.setServerTeam(10935);
            else nt.setServer("192.168.1.239");

            nt.startClient4("LED go brrrrrrrr :)");

            initialized = true;
        }

        var topic = nt.getTable("Led").getStructArrayTopic("Strip" + ledController.id, LedState.struct);
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

        System.out.println("recived LED command");

        if(array.length == 0) return Optional.empty();

        pub.set(new LedState[0]);

        SmartLEDPattern[] patternArray = new SmartLEDPattern[array.length];

        for(int i = 0; i < array.length; i++){
            int start = array[i].start();
            int end = array[i].end();
            patternArray[i] = array[i].toSmartLedPattern(ledController.createView(start, end));
        }

        return Optional.of(patternArray);
    }
}
