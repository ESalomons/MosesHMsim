package android.util;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class Log {
    private static PrintWriter outFile;

    public static void d(String logTag, String s) {
        System.out.println("D/" + logTag + " " + s);
        if(outFile != null){
            outFile.println("D/" + logTag + " " + s);
        }
    }

    public static void w(String logTag, String s) {
        System.out.println("W/" + logTag + " " + s);
        if(outFile != null){
            outFile.println("W/" + logTag + " " + s);
        }
    }

    public static void e(String logTag, String s, IOException e) {
        System.out.println("E/" + logTag + " " + s);
        if(outFile != null){
            outFile.println("E/" + logTag + " " + s);
        }
    }

    public static void e(String logTag, String s) {
        System.out.println("E/" + logTag + " " + s);
        if(outFile != null){
            outFile.println("E/" + logTag + " " + s);
        }
    }

    public static void i(String logTag, String s) {
        System.out.println("I/" + logTag + " " + s);
        if(outFile != null){
            outFile.println("I/" + logTag + " " + s);
        }
    }

    public static void setOutput(String filename) {
        try {
            outFile = new PrintWriter(filename);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    public static void closeOutput(){
        if(outFile != null){
            outFile.close();
            outFile = null;
        }
    }
}
