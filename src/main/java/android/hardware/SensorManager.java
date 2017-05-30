package android.hardware;

import nl.saxion.ami.moses.healthstatusmonitoring.sensors.smartphone.SensorListener;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class SensorManager {
    public static final int SENSOR_DELAY_NORMAL = 1;

    public void registerListener(SensorListener sensorListener, Sensor sens, int sensorDelayNormal) {
    }

    public void unregisterListener(SensorListener sensorListener) {
    }

    public Sensor getDefaultSensor(int id) {
        return new Sensor();
    }
}
