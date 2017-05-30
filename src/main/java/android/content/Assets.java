package android.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class Assets {
    public InputStream open(String firefightersFileLocation) {
        try {
            return new FileInputStream(new File("src/main/assets/firefighters.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
