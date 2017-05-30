package android.content;

import android.hardware.SensorManager;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class Context {
    public static final int SENSOR_SERVICE = 1;

    public Assets getAssets() {
        return new Assets();
    }

    public Object getSystemService(int sensorService) {
        return new SensorManager();
    }

    public Intent registerReceiver(Object o, IntentFilter ifilter) {
        return new Intent();
    }
}
