package nl.saxion.ami.moses.manschaphealthdemo.simulator;

import android.util.Log;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.InformationSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.models.Firefighter;
import nl.saxion.ami.moses.healthstatusmonitoring.models.sensors.BioHarnessData;
import nl.saxion.ami.moses.healthstatusmonitoring.util.Clock;
import sim.SimClock;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

// Static import for easy use of enums

/**

 */
public class ReplayFileScenario extends ZephyrSimulator {
    private static ReplayFileScenario instance;

    private SimClock clock;
    private String LOG_TAG = ReplayFileScenario.class.getSimpleName();
    private InformationSystem system;

    private ArrayList<TimeHR> fileEntries;
    private long timeBetweenUpdates = getTimeBetweenAnalyses();
    private String description = "";


    /**
     * Constructs the simulator using the default file name
     */
    private ReplayFileScenario() {
        clock = (SimClock) Clock.getClock();
        system = InformationSystem.getInstance();
    }

    public void readFile(String filename) {
        String logfilename = filename.split("csv")[0] + "txt";
        String[] splits = logfilename.split("/");
        logfilename = splits[splits.length - 1];
        Log.setOutput("simResults/" + logfilename);

        description = filename;
        fileEntries = new ArrayList<>();
        try (Scanner in = new Scanner(new File(filename))) {
            in.nextLine(); // read header
            while (in.hasNext()) {
                String[] splitLine = in.nextLine().split(",");
                if (splitLine.length >= 10) {
                    try {
                        fileEntries.add(new TimeHR(1, Integer.parseInt(splitLine[9])));
                    } catch (NumberFormatException nfe) {
                        // nothing
                    }
                }

            }

        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
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
            Log.closeOutput();
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ReplayFileScenario getInstance() {
        if (instance == null) {
            instance = new ReplayFileScenario();
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
