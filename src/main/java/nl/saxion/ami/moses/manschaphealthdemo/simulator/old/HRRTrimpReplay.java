package nl.saxion.ami.moses.manschaphealthdemo.simulator.old;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.AnalysisSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.InformationSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.models.Firefighter;
import nl.saxion.ami.moses.healthstatusmonitoring.models.HRReserve;
import nl.saxion.ami.moses.healthstatusmonitoring.models.TRIMP;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.aggregators.HRRModule;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.aggregators.TRIMPModule;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.ZephyrSimulator;

// Static import for easy use of enums

/**

 */
public class HRRTrimpReplay extends ZephyrSimulator {
    private static HRRTrimpReplay instance;
    //private static final String DEFAULT_FILE = "replayScenarios/2017_03_29-06_36_49_Summary.csv";
    private static final String DEFAULT_FILE = "replayScenarios/brandweerman.csv";
    private static final boolean isZephyrFile = false;
    private String LOG_TAG = HRRTrimpReplay.class.getSimpleName();
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
    private long lastTrimpTimestamp = 0l;
    private static long TRIMP_INTERVAL = 30000;
    private int sumHrs = 0;
    private int nrHrs = 0;

    /*
     * Initialise constants
     */ {
        thread = new Thread(this);
    }

    /**
     * Constructs the simulator using the default file name
     */
    private HRRTrimpReplay(Context context) {
        super(context);
        this.context = context;
        system = InformationSystem.getInstance();

        readFile();
        if (!fileEntries.isEmpty()) {
            lastTrimpTimestamp = fileEntries.get(0).time.getTime();
        }
    }


    public void readFile() {
        fileEntries = new ArrayList<>();

        try (Scanner in = new Scanner(context.getAssets().open(DEFAULT_FILE))) {
            if (!isZephyrFile) {
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
                in.nextLine(); // read header
                while (in.hasNext()) {
                    String[] splitLine = in.nextLine().split(";");
                    fileEntries.add(new TimeHR(
                            parser.parse(splitLine[0]),
                            Integer.parseInt(splitLine[1])));
                }
            } else {
                // 29/03/2017 06:36:49.409
                SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
                int colNr = 0;
                int hrCol = 0;
                int timeCol = 0;
                for (String s : in.nextLine().split(",")) {
                    if (s.equals("HR")) {
                        hrCol = colNr;
                    } else if (s.equals("Time")) {
                        timeCol = colNr;
                    }
                    colNr++;
                }

                while (in.hasNext()) {
                    String[] splitLine = in.nextLine().split(",");
                    fileEntries.add(new TimeHR(
                            parser.parse(splitLine[timeCol]),
                            Integer.parseInt(splitLine[hrCol])));
                }

            }
        }  catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }


    private boolean pushNextValues() {
        if (!fileEntries.isEmpty()) {
            InformationSystem is = InformationSystem.getInstance();
            Firefighter firefighter = is.getFirefighter();
            TimeHR timeHr = fileEntries.remove(0);
            HRReserve hrReserve = new HRReserve(firefighter.getHeartRateMax(), firefighter
                    .getHeartRateRest(), timeHr.hr);


            long timestamp = timeHr.time.getTime();
            is.addDataPoint(timestamp - previousTimestamp, hrReserve);

            Log.d(LOG_TAG, String.format(Locale.US, "Replay push HR: %d " + hrReserve.toString(),
                    timeHr.hr));

            // TRIMP calculation
            sumHrs += timeHr.hr;
            nrHrs++;

            if (timestamp - lastTrimpTimestamp >= TRIMP_INTERVAL) {
                TRIMP trimp = new TRIMP(hrReserve.getValue(), TRIMP_INTERVAL,
                        firefighter.isFemale());
                is.addDataPoint(timestamp - previousTimestamp, trimp);
                Log.d(LOG_TAG, String.format("Replay push TRIMP intensity: %.2f",
                        trimp.getIntensity()));
                sumHrs = 0;
                nrHrs = 0;
                lastTrimpTimestamp = timestamp;
            }
            previousTimestamp = timestamp;
            return true;
        }
        return false;
    }

    public void run() {
//        String LOG_TAG = ExhaustionModule.class.getSimpleName();
        AnalysisSystem.getInstance().unregisterModule(HRRModule.class);
        AnalysisSystem.getInstance().unregisterModule(TRIMPModule.class);
//        boolean unregistered = AnalysisSystem.getInstance().unregisterModule(ExhaustionModule
//                .class);
        system.registerInformationSource(HRReserve.class);
        system.registerInformationSource(TRIMP.class);
//        AnalysisSystem.getInstance().registerModule(new ExhaustionModuleSim());
//        if (!unregistered) {
//            Log.d(LOG_TAG, "WARNING: Using original Exhaustion Module!");
//        }

        try {
            Log.d(LOG_TAG, "Starting HeartRate Reserve/Trimp Replay");
            Firefighter firefighter = InformationSystem.getInstance().getFirefighter();
            Log.d(LOG_TAG, String.format("Firefighter: id: %d, age: %d, sex: %s, HRmax: %d, " +
                            "HRRest: %d",
                    firefighter.getId(), firefighter.getAge(),
                    firefighter.isFemale() ? "female" : "male",
                    firefighter.getHeartRateMax(),
                    firefighter.getHeartRateRest()));
            boolean hasMoreValues = true;
            while (hasMoreValues) {
                if (system.informationSourceExist(HRReserve.class) && system
                        .informationSourceExist(TRIMP.class)) {
                    try {
                        hasMoreValues = pushNextValues();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
                Thread.sleep(timeBetweenUpdates); // Frequency of updates
            }
            Log.d(LOG_TAG, "HRReserve simulation finished");
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


    public static HRRTrimpReplay getInstance(Context context) {
        if (instance == null) {
            instance = new HRRTrimpReplay(context);
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

        @Override
        public String toString() {
            return time.toString() + " => " + hr;
        }
    }
}
