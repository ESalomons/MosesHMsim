package nl.saxion.ami.moses.manschaphealthdemo.simulator.data;

/**
 * Enumeration of the bioharness instance properties
 *
 * These values are used to find elements inside of the json config file AND setting the
 * values in the builder object at the ZephyrSimulator
 *
 *
 * Created by Jasper on 6/14/2016.
 */
public enum HealthAspect {
    USER_ID,
    /*
     * Heart aspects
     */
    HEART_RATE,HEART_RATE_CONFIDENCE,HEART_RATE_VARIABILITY,
    ECG_AMPLITUDE,ECG_NOISE,
    /*
     * Respiratory aspects
     */
    RESPIRATION_RATE,BREATHING_WAVE_AMPLITUDE,BREATHING_WAVE_NOISE,
    BREATHING_RATE_CONFIDENCE,REMAINING_AIR_IN_MINUTES,REMAINING_AIR_PRESSURE,
    /*
     * Temperature aspects
     */
    SKIN_TEMPERATURE,CORE_TEMPERATURE,AMBIENT_TEMPERATURE,
    /*
     * Movement, directional aspects , positional
     */
    PEAK_ACCELERATION,ACTIVITY,POSTURE,
    VERTICAL_AXIS_ACCELERATION_MIN,VERTICAL_AXIS_ACCELERATION_PEAK,
    LATERAL_AXIS_ACCELERATION_MIN,LATERAL_AXIS_ACCELERATION_PEAK,
    SAGITTAL_AXIS_ACCELERATION_MIN, SAGITTAL_AXIS_ACCELERATION_PEAK,
    /*
     * System aspects
     */
    BATTERY_LEVEL,BATTERY_VOLTAGE,DEVICE_INTERNAL_TEMP,RSSI,TX_POWER,
    LINK_QUALITY,SYSTEM_CONFIDENCE,GSR,ROG,STATUS_INFO;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
