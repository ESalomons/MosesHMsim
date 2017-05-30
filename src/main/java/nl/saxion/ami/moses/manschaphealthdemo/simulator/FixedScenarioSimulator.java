package nl.saxion.ami.moses.manschaphealthdemo.simulator;

import android.util.Log;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.InformationSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.models.Firefighter;
import nl.saxion.ami.moses.healthstatusmonitoring.models.sensors.BioHarnessData;
import nl.saxion.ami.moses.healthstatusmonitoring.util.Clock;
import sim.SimClock;

import java.util.ArrayList;
import java.util.Locale;

// Static import for easy use of enums

/**

 */
public class FixedScenarioSimulator extends ZephyrSimulator {
    private static FixedScenarioSimulator instance;

    private SimClock clock;
    private String LOG_TAG = FixedScenarioSimulator.class.getSimpleName();
    private InformationSystem system;

    private ArrayList<TimeHR> fileEntries;
    private long timeBetweenUpdates = getTimeBetweenAnalyses();
    private String description = "";


    /**
     * Constructs the simulator using the default file name
     */
    private FixedScenarioSimulator() {
        clock = (SimClock) Clock.getClock();
        system = InformationSystem.getInstance();
    }

    public void createScenarioIndicator4() {
        description = "Indicator 4";
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

    public void createScenarioIndicator5() {
        description = "Indicator 5";
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
            TimeHR timeHr = fileEntries.remove(0);

            builder.setHeartRate(timeHr.hr);
            BioHarnessData datapoint = builder.build();
            is.push(datapoint);

            Log.d(LOG_TAG, String.format(Locale.US, "Replay push %s - hr: " + timeHr.hr,
                    getTimeString((int) (clock.getTime() / 1000))));
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        try {
            Log.d(LOG_TAG, "###########################################");
            Log.d(LOG_TAG, "#");
            Log.d(LOG_TAG, "# Starting scenario: " + description);
            Log.d(LOG_TAG, "#");
            Log.d(LOG_TAG, "###########################################");
            Firefighter firefighter = InformationSystem.getInstance().getFirefighter();
            Log.d(LOG_TAG, String.format("Firefighter: id: %d, age: %d, sex: %s, HRmax: %d, " +
                            "HRRest: %d",
                    firefighter.getId(), firefighter.getAge(),
                    firefighter.isFemale() ? "female" : "male",
                    firefighter.getHeartRateMax(),
                    firefighter.getHeartRateRest()));
            boolean hasMoreValues = true;
            while (hasMoreValues) {
                if (system.informationSourceExist(BioHarnessData.class)) {
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
            Log.d(LOG_TAG, "###########################################");
            Log.d(LOG_TAG, "#");
            Log.d(LOG_TAG, "# Simulation finished: " + description);
            Log.d(LOG_TAG, "#");
            Log.d(LOG_TAG, "###########################################");
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static FixedScenarioSimulator getInstance() {
        if (instance == null) {
            instance = new FixedScenarioSimulator();
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
