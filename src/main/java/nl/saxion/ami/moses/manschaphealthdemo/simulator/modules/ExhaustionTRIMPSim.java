package nl.saxion.ami.moses.manschaphealthdemo.simulator.modules;

import android.content.Context;
import android.util.Log;

import java.util.List;

import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.InformationSource;
import nl.saxion.ami.moses.healthstatusmonitoring.models.TRIMP;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.exhaustion.LongTermTRIMPModule;


/**
 * Change ExhaustionModule slightly for use in simulator
 * @author Etto Salomons
 *         created on 06/04/17.
 */

public class ExhaustionTRIMPSim extends LongTermTRIMPModule {

    private static final String LOG_TAG = LongTermTRIMPModule.class.getSimpleName();

    @Override
    protected double getAvgTrimp(long historyTime, InformationSource<TRIMP> infoSource) {
        long currentTime = infoSource.getMostRecentTimestamp();
        List<TRIMP> trimpList = infoSource.getDataSince(currentTime - historyTime);
        double totalTrimp = 0;
        long totalTrimpTime = 0l;
        for (TRIMP trimp : trimpList) {
            totalTrimp += trimp.getIntensity() * trimp.getTime();
            totalTrimpTime += trimp.getTime();
        }
        Log.d(LOG_TAG, String.format("totalTrimp: %.2f, totalTime: %d, avg: %.2f", totalTrimp,
                totalTrimpTime, totalTrimp / totalTrimpTime));
        return totalTrimp / totalTrimpTime;
    }

    @Override
    public boolean startModule(Context context) {
        boolean result = super.startModule(context);
        // noodzakelijke voorwaarde, omdat de tijd afhankelijk is van het ingelezen bestand.
        lastPolled = 0;
        return result;
    }
}
