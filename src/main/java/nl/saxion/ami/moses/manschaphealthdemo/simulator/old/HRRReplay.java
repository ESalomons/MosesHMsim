package nl.saxion.ami.moses.manschaphealthdemo.simulator.old;

import android.content.Context;
import android.util.Log;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.AnalysisSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.InformationSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.models.Firefighter;
import nl.saxion.ami.moses.healthstatusmonitoring.models.HRReserve;
import nl.saxion.ami.moses.healthstatusmonitoring.models.sensors.BioHarnessData;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.aggregators.HRRModule;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.ZephyrSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

// Static import for easy use of enums

/**

 */
public class HRRReplay extends ZephyrSimulator {
    private static HRRReplay instance;
    //private static final String DEFAULT_FILE = "replayScenarios/2017_03_29-06_36_49_Summary.csv";
    private static final String DEFAULT_FILE = "replayScenarios/brandweerman.csv";
    private static final boolean isZephyrFile = false;
    private String LOG_TAG = HRRReplay.class.getSimpleName();
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

    private ArrayList<TimeHR> fileEntries;
    private long previousTimestamp = 0l;
    private long timeBetweenUpdates = 30;

    /*
     * Initialise constants
     */ {
        builder = new BioHarnessData.Builder();
        thread = new Thread(this);
    }

    /**
     * Constructs the simulator using the default file name
     */
    private HRRReplay(Context context) {
        super(context);
        this.context = context;
        system = InformationSystem.getInstance();
        this.fileName = DEFAULT_FILE;
        initBuilder();
        readFile();
        AnalysisSystem.getInstance().unregisterModule(HRRModule.class);
        system.registerInformationSource(HRReserve.class);
    }


    private void initBuilder() {
        builder.setBatteryLevel(75);
        builder.setRespirationRate(18);
        builder.setEstimatedCoreTemperature(37.8);
    }

    public void readFile() {
        fileEntries = new ArrayList<>();

        try (Scanner in = new Scanner(new File((DEFAULT_FILE)))) {
            if (!isZephyrFile) {
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
                in.nextLine(); // read header
                while (in.hasNext()) {
                    String[] splitLine = in.nextLine().split(";");
                    fileEntries.add(new TimeHR(
                            parser.parse(splitLine[0]),
                            Integer.parseInt(splitLine[1])));
                }
                Log.d(LOG_TAG,fileEntries.toString());
            } else {
                // 29/03/2017 06:36:49.409
                SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
                int colNr = 0;
                int hrCol = 0;
                int timeCol = 0;
                for(String s: in.nextLine().split(",")){
                    if(s.equals("HR")){
                        hrCol = colNr;
                    } else if (s.equals("Time")){
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
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }


    private HRReserve pushNextHRReserve() {
        if (!fileEntries.isEmpty()) {
            InformationSystem is = InformationSystem.getInstance();
            Firefighter firefighter = is.getFirefighter();
            TimeHR timeHr = fileEntries.remove(0);
            HRReserve hrReserve = new HRReserve(firefighter.getHeartRateMax(), firefighter
                    .getHeartRateRest(), timeHr.hr);

            long timestamp = timeHr.time.getTime();
            is.addDataPoint(timestamp - previousTimestamp, hrReserve);
            previousTimestamp = timestamp;
            Log.d(LOG_TAG, String.format(Locale.US, "Replay push HR: %d " + hrReserve.toString(),
                    timeHr.hr));
            return hrReserve;
        }
        return null;
    }

    public void run() {
//        String LOG_TAG = ExhaustionModule.class.getSimpleName();
        try {
            Log.d(LOG_TAG, "Starting HeartRateReserve Replay");
            Firefighter firefighter = InformationSystem.getInstance().getFirefighter();
            Log.d(LOG_TAG, String.format("Firefighter: id: %d, age: %d, sex: %s, HRmax: %d, " +
                            "HRRest: %d",
                    firefighter.getId(), firefighter.getAge(),
                    firefighter.isFemale() ? "female" : "male",
                    firefighter.getHeartRateMax(),
                    firefighter.getHeartRateRest()));
            while (flag) {
                if (system.informationSourceExist(HRReserve.class)) {
                    HRReserve hrReserve = pushNextHRReserve();
                    if (hrReserve == null) {
                        Log.d(LOG_TAG, "HRReserve simulation finished");
                    }
                }
                Thread.sleep(timeBetweenUpdates); // Frequency of updates
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


    public static HRRReplay getInstance(Context context) {
        if (instance == null) {
            instance = new HRRReplay(context);
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
        public String toString(){
            return time.toString() + " => " + hr;
        }
    }
}
