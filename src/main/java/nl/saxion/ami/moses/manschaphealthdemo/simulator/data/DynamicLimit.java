package nl.saxion.ami.moses.manschaphealthdemo.simulator.data;

import java.util.Random;

/**
 * Hes
 * <p/>
 * Created by Jasper on 6/14/2016.
 */
public class DynamicLimit implements AspectData {

    private final static Random RANDOM = new Random(); // Random is thread safe
    private final HealthAspect ASPECT;
    private double value;
    private final double MIN;
    private final double MAX;
    private final double VARIABILITY; //Maximum change

    /**
     * Creates a new data limit which determines the new value generated
     *
     * @param aspect
     * @param min
     * @param max
     * @param variability
     */
    public DynamicLimit(HealthAspect aspect, double min, double max, double variability) {
        this.ASPECT = aspect;
        this.MIN = min;
        this.MAX = max;
        this.VARIABILITY = variability;
        this.value = (MAX + MIN) / 2; // Average initialisation
    }

    public HealthAspect getAspect() {
        return ASPECT;
    }


    /**
     * Generates a retrieves the generated value
     *
     * @return
     */
    public double poll() {
        /*
         * If there is no change,we don't need to recalculate the new value so we'll just return it instantly
         */
        if (VARIABILITY == 0) {
            return value;
        }
        this.value = value + (VARIABILITY - ((RANDOM.nextDouble() * VARIABILITY) * 2));
        /*
         * If the generated value exceeded one of the boundary's, set the previous value to the nearest boundary
         */
        if (value < MIN) {
            value = MIN;
        } else if (value > MAX) {
            value = MAX;
        }

        return value;

    }

}
