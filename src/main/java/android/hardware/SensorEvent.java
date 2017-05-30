package android.hardware;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class SensorEvent {
    public Sensor sensor;
    public long[] values;
    public double accuracy;
    public long timestamp;
}
