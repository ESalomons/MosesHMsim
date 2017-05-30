package nl.saxion.ami.moses.manschaphealthdemo.simulator.old;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.AnalysisSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.InformationSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.models.Firefighter;
import nl.saxion.ami.moses.healthstatusmonitoring.models.HRReserve;
import nl.saxion.ami.moses.healthstatusmonitoring.models.TRIMP;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.aggregators.HRRModule;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.aggregators.TRIMPModule;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.exhaustion
        .MediumTermIntensiveModule;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.exhaustion
        .ShortTermVeryIntensiveModule;
import nl.saxion.ami.moses.healthstatusmonitoring.util.Clock;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.ZephyrSimulator;
import sim.SimClock;

// Static import for easy use of enums

/**

 */
public class SimIndicator5 extends ZephyrSimulator {
    private static SimIndicator5 instance;

    private static final boolean isZephyrFile = false;
    private String LOG_TAG = SimIndicator5.class.getSimpleName();
    /*
     * Constant references to the system and context
     */
    private InformationSystem system;

    /*
     * Thread on which this simulator will run
     * Mutable since someone could stop the simulator
     */
    private Thread thread;

    private ArrayList<TimeHR> fileEntries;
    private long timeBetweenUpdates = getTimeBetweenAnalyses();
    private int nrTrimps = 0;

    /*
     * Initialise constants
     */ {
        thread = new Thread(this);
    }

    private SimClock clock;

    /**
     * Constructs the simulator using the default file name
     */
    private SimIndicator5(Context context) {
        super(context);
        clock = (SimClock) Clock.getClock();
        system = InformationSystem.getInstance();
    }


    public void readFile() {
        /*
        Scenario:
        Tijd	s.	min.	HRR.	Verwacht
        00:00	30	00:30	<0.8	G
        00:30	600	10:00	>0.8	G
        10:30	600	10:00	<0.8	O
        20:30	600	10:00	>0.8	G
        06:30	300	05:00	>0.8	R
        11:30	60	01:00	>0.8	R
        12:30	300	05:00	<0.8	R
        17:30	300	05:00	<0.8	O
        22:30	60	01:00	<0.8	G
        23:30
         */
        // HRR >0.8 => HR = 167
        // HRR < 0.8 => HR = 160

        int HRHi = 167;
        int HRLo = 160;

        fileEntries = new ArrayList<>();

        addEntries(fileEntries, 30, HRLo);
        addEntries(fileEntries, 600, HRHi);
        addEntries(fileEntries, 180, HRHi);
        addEntries(fileEntries, 600, HRLo);
        addEntries(fileEntries, 600, HRHi);
        addEntries(fileEntries, 300, HRHi);
        addEntries(fileEntries, 60, HRHi);
        addEntries(fileEntries, 300, HRLo);
        addEntries(fileEntries, 300, HRLo);
        addEntries(fileEntries, 60, HRLo);
    }

    private void addEntries(ArrayList<TimeHR> fileEntries, int seconds, int hrVal) {
        for (int time = 0; time < seconds; time++) {
            fileEntries.add(new TimeHR(1, hrVal));
        }
    }


    private boolean pushNextValues() {
        if (!fileEntries.isEmpty()) {
            clock.advanceTime(1000);
            InformationSystem is = InformationSystem.getInstance();
            Firefighter firefighter = is.getFirefighter();
            TimeHR timeHr = fileEntries.remove(0);
            HRReserve hrReserve = new HRReserve(firefighter.getHeartRateMax(), firefighter
                    .getHeartRateRest(), timeHr.hr);
            TRIMP trimp = new TRIMP(hrReserve.getValue(), timeHr.time * 1000, firefighter
                    .isFemale());

            long timestamp = 1000 * timeHr.time;
            is.addDataPoint(timestamp, trimp);

            nrTrimps++;
            Log.d(LOG_TAG, String.format(Locale.US, "Replay (%d) push TRIMP: " + trimp
                            .toString(),
                    nrTrimps));

            return true;
        }
        return false;
    }

    public void run() {
        readFile();
        AnalysisSystem.getInstance().unregisterModule(HRRModule.class);
        AnalysisSystem.getInstance().unregisterModule(TRIMPModule.class);

        AnalysisSystem.getInstance().unregisterModule
                (MediumTermIntensiveModule.class);
        AnalysisSystem.getInstance().unregisterModule(ShortTermVeryIntensiveModule.class);

//        boolean unregistered = AnalysisSystem.getInstance().unregisterModule(LongTermTRIMPModule
//                .class);
        system.registerInformationSource(HRReserve.class);
        system.registerInformationSource(TRIMP.class);
//        AnalysisSystem.getInstance().registerModule(new ExhaustionTRIMPSim());
//        if (!unregistered) {
//            Log.d(LOG_TAG, "WARNING: Using original LongTermTRIMPModule Module!");
//        }

        try {
            Log.d(LOG_TAG, "Starting LongTermTRIMPModule scenario");
            Firefighter firefighter = InformationSystem.getInstance().getFirefighter();
            Log.d(LOG_TAG, String.format("Firefighter: id: %d, age: %d, sex: %s, HRmax: %d, " +
                            "HRRest: %d",
                    firefighter.getId(), firefighter.getAge(),
                    firefighter.isFemale() ? "female" : "male",
                    firefighter.getHeartRateMax(),
                    firefighter.getHeartRateRest()));
            boolean hasMoreValues = true;
            while (hasMoreValues) {
                if (system.informationSourceExist(TRIMP.class)) {
                    try {
                        hasMoreValues = pushNextValues();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
                Thread.sleep(timeBetweenUpdates); // Frequency of updates
            }
            Log.d(LOG_TAG, "LongTermTRIMPModule simulation finished");
            System.exit(0);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the simulator
     */
    public void startSimulator() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }


    public static SimIndicator5 getInstance(Context context) {
        if (instance == null) {
            instance = new SimIndicator5(context);
        }
        return instance;
    }

    private class TimeHR {
        public int time; // seconds
        public int hr;

        public TimeHR(int time, int hr) {
            this.time = time;
            this.hr = hr;
        }

        @Override
        public String toString() {
            return time + " => " + hr;
        }
    }
}
