package nl.saxion.ami.moses.manschaphealthdemo.simulator.modules;

import android.content.Context;

import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.exhaustion
        .MediumTermIntensiveModule;


/**
 * Change ExhaustionModule slightly for use in simulator
 * @author Etto Salomons
 *         created on 06/04/17.
 */

public class ExhaustionMidtermSim extends MediumTermIntensiveModule {

    @Override
    public boolean startModule(Context context) {
        boolean result = super.startModule(context);
        // noodzakelijke voorwaarde, omdat de tijd afhankelijk is van het ingelezen bestand.
        lastPolled = 0;
        return result;
    }
}
