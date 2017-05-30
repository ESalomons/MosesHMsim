package nl.saxion.ami.moses.common;

import android.util.Log;
import com.typesafe.config.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Singleton configuration class that provides access to the overall configuration.
 */
public class Configuration {

    private static Configuration instance; // the singleton instance of this class
    public static String KEY_COMPONENTS_SECTION = "";

    private Properties properties;

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private Configuration() {

        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/assets/healthstatusmonitoring.conf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public double getDouble(String s, double defaultValue) {
        return Double.parseDouble(getProperty(s));
    }

    private String getProperty(String s) {
        String[] split = s.split("\\.");
        String val = properties.getProperty(split[split.length-1]);
        return val;
    }

    public long getLong(String s, int i) {
        return Long.parseLong(getProperty(s));
    }

    public boolean getBoolean(String s, boolean b) {
        return Boolean.parseBoolean(getProperty(s));
    }

    public String getString(String s, Object o) {
        return getProperty(s);
    }

    public int getInteger(String s, int defaultValue) {
        return Integer.parseInt(getProperty(s));
    }

    public int getUserId() {
        return 112;
    }
}
