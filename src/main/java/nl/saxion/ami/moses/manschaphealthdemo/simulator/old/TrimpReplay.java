package nl.saxion.ami.moses.manschaphealthdemo.simulator.old;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.InformationSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.models.Firefighter;
import nl.saxion.ami.moses.healthstatusmonitoring.models.HRReserve;
import nl.saxion.ami.moses.healthstatusmonitoring.models.TRIMP;
import nl.saxion.ami.moses.healthstatusmonitoring.models.sensors.BioHarnessData;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.ZephyrSimulator;

// Static import for easy use of enums

/**

 */
public class TrimpReplay extends ZephyrSimulator {
    private static TrimpReplay instance;
    private static final String DEFAULT_FILE = "replayScenarios/brandweerHigher.csv";
    private ArrayList<Integer> avgHrs = new ArrayList<>();
    private double trimpTime = 0.5; //minutes
    private String LOG_TAG = TrimpReplay.class.getSimpleName();
    /*
     * Constant references to the system and context
     */
    private InformationSystem system;
    private final Context context;
    /*
     * The summary package builder, we'll only need one builder.
     */
    private final BioHarnessData.Builder builder;
    /*
     * Thread on which this simulator will run
     * Mutable since someone could stop the simulator
     */
    private Thread thread;
    /*
     * Flag to start/stop the thread -- Non atomic,but unneeded since there is no present
     * race condition
     */
    private volatile boolean flag;
    /*
     * File location/name
     */
    private String fileName;

    private int heartRate = 42;

    /*
     * Initialise constants
     */ {
        builder = new BioHarnessData.Builder();
        thread = new Thread(this);
    }

    /**
     * Constructs the simulator using the default file name
     */
    private TrimpReplay(Context context) {
        super(context);
        this.context = context;
        system = InformationSystem.getInstance();
        this.fileName = DEFAULT_FILE;
        initBuilder();
        readFile();
    }



    private void initBuilder() {
        builder.setBatteryLevel(75);
        builder.setRespirationRate(18);
        builder.setEstimatedCoreTemperature(37.8);
    }

    public void readFile() {
        ArrayList<TimeHR> fileEntries = new ArrayList<>();
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
        try (Scanner in = new Scanner(context.getAssets().open(DEFAULT_FILE))) {
            in.nextLine(); // read header
            while (in.hasNext()) {
                String[] splitLine = in.nextLine().split(";");
                fileEntries.add(new TimeHR(
                        parser.parse(splitLine[0]),
                        Integer.parseInt(splitLine[1])));
            }
        }  catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        calculateAvgHrs(fileEntries);
    }

    private void calculateAvgHrs(ArrayList<TimeHR> fileEntries) {
        if (fileEntries.size() >= 2) {
            long firstTime = fileEntries.get(0).time.getTime();
            long secondTime = fileEntries.get(1).time.getTime();
            long timeBetweenEntries = secondTime - firstTime;

            long trimpTimeL = (long) (trimpTime * 60000);

            long startTime = firstTime - timeBetweenEntries;
            int nrHrs = 0;
            int hrSum = 0;
            for (TimeHR fileEntry : fileEntries) {
                long fileTime = fileEntry.time.getTime();
                nrHrs++;
                hrSum += fileEntry.hr;
                if (fileTime - startTime >= trimpTimeL) {
                    avgHrs.add(hrSum / nrHrs);
                    nrHrs = 0;
                    hrSum = 0;
                    startTime = fileTime;
                }
            }
            if (hrSum > 0) {
                avgHrs.add(hrSum / nrHrs);
            }

        }
        Log.d(LOG_TAG, avgHrs.toString());
    }

    /**
     * Generates a new value from the current scenario
     *
     * @return BioHarnessData
     */
    public BioHarnessData generate() {
        builder.setHeartRate((int) (Math.random() * 30) + 65);
        return builder.build();
    }


    private TRIMP pushNextTrimp() {
        if (!avgHrs.isEmpty()) {
            InformationSystem is = InformationSystem.getInstance();
            Firefighter firefighter = is.getFirefighter();
            int avgHr = avgHrs.remove(0);
            HRReserve hrReserve = new HRReserve(firefighter.getHeartRateMax(), firefighter
                    .getHeartRateRest(), avgHr);
            TRIMP trimp = new TRIMP(hrReserve.getValue(), (long) (60000 * trimpTime),
                    firefighter.isFemale());
            is.push(trimp);
            return trimp;
        }
        return null;
    }

    /**
     * Gives the information system frequent updates containing zephyr dummy data
     */
    public void run() {
//        String LOG_TAG = ExhaustionModule.class.getSimpleName();
        try {
            Log.d(LOG_TAG, "Trimp time: " + trimpTime + " minutes");
            Firefighter firefighter = InformationSystem.getInstance().getFirefighter();
            Log.d(LOG_TAG, String.format("Firefighter: id: %d, age: %d, sex: %s, HRmax: %d, " +
                            "HRRest: %d",
                    firefighter.getId(), firefighter.getAge(),
                    firefighter.isFemale() ? "female" : "male",
                    firefighter.getHeartRateMax(),
                    firefighter.getHeartRateRest()));
            while (flag) {
                if (system.informationSourceExist(TRIMP.class)) {
                    TRIMP trimp = pushNextTrimp();
                    if (trimp != null) {
                        Log.d(LOG_TAG, String.format("push TRIMP intensity: %.2f, value: %.2f",
                                trimp.getIntensity(), trimp.getValue()));
                    } else {
                        Log.d(LOG_TAG, "TRIMP simulation finished");
                    }
                }
                Thread.sleep(1000); // Frequency of updates
            }
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
        flag = true;
    }

    /**
     * Stops the simulator
     */
    public void stopSimulator() throws InterruptedException {
        flag = false;
    }


    public static TrimpReplay getInstance(Context context) {
        if (instance == null) {
            instance = new TrimpReplay(context);
        }
        return instance;
    }

    private class TimeHR {
        public Date time;
        public int hr;

        public TimeHR(Date time, int hr) {
            this.time = time;
            this.hr = hr;
        }
    }
}
