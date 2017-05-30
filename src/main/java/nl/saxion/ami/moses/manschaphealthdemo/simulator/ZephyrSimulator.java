package nl.saxion.ami.moses.manschaphealthdemo.simulator;

import android.content.Context;
import nl.saxion.ami.moses.healthstatusmonitoring.models.sensors.BioHarnessData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class ZephyrSimulator implements Runnable {

    protected Context context;
    private Properties properties;
    protected final BioHarnessData.Builder builder;
    /*
     * Thread on which this simulator will run
     * Mutable since someone could stop the simulator
     */
    private Thread thread;

    /*
     * Initialise constants
     */ {
        builder = new BioHarnessData.Builder();
        thread = new Thread(this);
    }

    /**
     * Constructs the simulator using the default file name
     */
    protected ZephyrSimulator(Context context) {
        this.context = context;
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/assets/healthstatusmonitoring.conf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected ZephyrSimulator() {
        this(null);
    }

    public int getTimeBetweenAnalyses() {
        return Integer.parseInt(properties.getProperty("timeBetweenAnalyses"));
    }

    /**
     * Generates a new value from the current scenario
     *
     * @return BioHarnessData
     */
    public BioHarnessData generate() {
        return builder.build();
    }

    /**
     * Gives the information system frequent updates containing zephyr dummy data
     */
    public abstract void run();

    /**
     * Starts the simulator
     */
    public void startSimulator() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public static String getTimeString(int time) {
        int seconds = time % 60;
        time = time / 60;
        int minutes = time % 60;
        int hrs = time / 60;
        return String.format("%02d:%02d:%02d", hrs, minutes, seconds);
    }

}
