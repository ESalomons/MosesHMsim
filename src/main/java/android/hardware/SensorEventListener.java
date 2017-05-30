package android.hardware;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public interface SensorEventListener {
    public void onSensorChanged(SensorEvent event);

    void onAccuracyChanged(Sensor sensor, int accuracy);
}
