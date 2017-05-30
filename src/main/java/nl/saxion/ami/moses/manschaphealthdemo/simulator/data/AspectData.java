package nl.saxion.ami.moses.manschaphealthdemo.simulator.data;

import org.json.JSONException;
import org.json.JSONObject;

import static nl.saxion.ami.moses.manschaphealthdemo.simulator.data.AspectElement.*;

/**
 * Interface for data objects used in the simulator
 * Created by Jasper on 6/15/2016.
 */
public interface AspectData {



    /**
     * Returns the aspect belong to this limit object
     *
     * @return Health aspect
     */
    HealthAspect getAspect();

    /**
     * Generates a new number to be used in the simulator
     *
     * @return new number
     */
    double poll();
}
