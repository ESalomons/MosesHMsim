package nl.saxion.ami.moses.manschaphealthdemo.simulator.data;

/**
 * Class containing the necessary constants describing health aspects
 *
 * Constant declaration
 * MIN               : Element of json, takes the minimum limit
 * MAX               : Element of json, takes the maximum limit
 * CHANGE            : Element of json, takes the max variability
 *
 * Add any elements you want
 *
 * Created by Jasper on 6/15/2016.
 */
public enum AspectElement {
    /*
     * Used for Dynamic Limit
     */
    MIN,MAX,CHANGE,
    /*
     * Used for static limit
     */
    VALUE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
