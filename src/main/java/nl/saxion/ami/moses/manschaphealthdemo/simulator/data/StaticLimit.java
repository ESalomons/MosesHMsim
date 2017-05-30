package nl.saxion.ami.moses.manschaphealthdemo.simulator.data;

import nl.saxion.ami.moses.manschaphealthdemo.simulator.data.AspectData;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.data.HealthAspect;

/**
 * Represents an unchanging/ static object
 *
 * Made specifically immutable
 * Created by Jasper on 6/15/2016.
 */
public final class StaticLimit implements AspectData {

    private final double VALUE;
    private final HealthAspect ASPECT;

    public StaticLimit(HealthAspect aspect, double value){
        this.VALUE = value;
        this.ASPECT = aspect;
    }

    /**
     * Returns the aspect belonging to this object
     *
     * @return
     */
    @Override
    public HealthAspect getAspect() {
        return ASPECT;
    }

    /**
     * Returns the unchanging number of this object
     * @return
     */
    @Override
    public double poll() {
        return VALUE;
    }
}
