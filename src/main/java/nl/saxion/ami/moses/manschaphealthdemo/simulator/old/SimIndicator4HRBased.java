package nl.saxion.ami.moses.manschaphealthdemo.simulator.old;

import android.content.Context;
import android.util.Log;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.AnalysisSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.InformationSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.models.Firefighter;
import nl.saxion.ami.moses.healthstatusmonitoring.models.HRReserve;
import nl.saxion.ami.moses.healthstatusmonitoring.models.sensors.BioHarnessData;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.aggregators.HRRModule;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.exhaustion.MediumTermIntensiveModule;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.exhaustion.ShortTermVeryIntensiveModule;
import nl.saxion.ami.moses.healthstatusmonitoring.util.Clock;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.ZephyrSimulator;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.modules.ExhaustionMidtermSim;
import sim.SimClock;

import java.util.ArrayList;
import java.util.Locale;

// Static import for easy use of enums

/**

 */
public class SimIndicator4HRBased extends ZephyrSimulator {
    private static SimIndicator4HRBased instance;

    private static final boolean isZephyrFile = false;
    private SimClock clock;
    private String LOG_TAG = SimIndicator4HRBased.class.getSimpleName();
    /*
     * Constant references to the system and context
     */
    private InformationSystem system;
    private final Context context;

    /*
     * Thread on which this simulator will run
     * Mutable since someone could stop the simulator
     */
    private Thread thread;

    private ArrayList<TimeHR> fileEntries;
    private long previousTimestamp = 0l;
    private long timeBetweenUpdates = 30;
    private int nrHrs = 0;

    /*
     * Initialise constants
     */ {
        thread = new Thread(this);
    }

    /**
     * Constructs the simulator using the default file name
     */
    private SimIndicator4HRBased(Context context) {
        super(context);
        this.context = context;
        clock = (SimClock) Clock.getClock();
        system = InformationSystem.getInstance();
    }

    private void readFile() {
        /*
        Scenario:
            Tijd	Duur  	HRR  Verwacht
            0     	30	    <0.9  G
            30    	310		>0.9  G
            5.30   			-    O
            5.40  	5		<0.9  O
            5.45  	5		>0.9  O
            5.50  	70		<0.9  O
            6.00   			-    G
            7.00   	425		>0.9 G
            12.00 			-     O
            14.00 			-     R
            14.05 	5		<0.9  R
            14.10 	5		>0.9  R
            14.15 	25		<0.9  R
            14.25 			-     O
            14.35 			-     G
            15.00 Eind
         */
        // HRR >0.9 => HR = 181
        // HRR < 0.9 => HR = 160

        int HRHi = 181;
        int HRLo = 160;

        fileEntries = new ArrayList<>();

        addEntries(fileEntries, 30, HRLo);
        addEntries(fileEntries, 310, HRHi);
        addEntries(fileEntries, 5, HRLo);
        addEntries(fileEntries, 5, HRHi);
        addEntries(fileEntries, 70, HRLo);
        addEntries(fileEntries, 425, HRHi);
        addEntries(fileEntries, 5, HRLo);
        addEntries(fileEntries, 5, HRHi);
        addEntries(fileEntries, 25, HRLo);
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
            TimeHR timeHr = fileEntries.remove(0);

            builder.setHeartRate(timeHr.hr);
            BioHarnessData datapoint = builder.build();
            is.push(datapoint);

            Log.d(LOG_TAG, String.format(Locale.US, "Replay (%d) push hr: " + timeHr.hr));
            return true;
        }
        return false;
    }

    public void run() {
        readFile();
        AnalysisSystem.getInstance().unregisterModule(HRRModule.class);
        boolean unregistered = AnalysisSystem.getInstance().unregisterModule
                (MediumTermIntensiveModule.class);
        AnalysisSystem.getInstance().unregisterModule(ShortTermVeryIntensiveModule.class);
        system.registerInformationSource(HRReserve.class);
        AnalysisSystem.getInstance().registerModule(new ExhaustionMidtermSim());
        if (!unregistered) {
            Log.d(LOG_TAG, "WARNING: Using original MediumTermIntensiveModule Module!");
        }

        try {
            Log.d(LOG_TAG, "Starting MediumTermIntensiveModule scenario");
            Firefighter firefighter = InformationSystem.getInstance().getFirefighter();
            Log.d(LOG_TAG, String.format("Firefighter: id: %d, age: %d, sex: %s, HRmax: %d, " +
                            "HRRest: %d",
                    firefighter.getId(), firefighter.getAge(),
                    firefighter.isFemale() ? "female" : "male",
                    firefighter.getHeartRateMax(),
                    firefighter.getHeartRateRest()));
            boolean hasMoreValues = true;
            while (hasMoreValues) {
                if (system.informationSourceExist(HRReserve.class)) {
                    try {
                        hasMoreValues = pushNextValues();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, e.getMessage());
                        System.exit(0);
                    }
                }
                Thread.sleep(timeBetweenUpdates); // Frequency of updates
            }
            Log.d(LOG_TAG, "MediumTermIntensiveModule simulation finished");
            thread = new Thread(this);

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


    public static SimIndicator4HRBased getInstance(Context context) {
        if (instance == null) {
            instance = new SimIndicator4HRBased(context);
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
