package android.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class BluetoothSocket {
    public void close() throws IOException {
    }

    public void connect() throws IOException {
    }

    public InputStream getInputStream() {
        return System.in;
    }

    public OutputStream getOutputStream() {
        return System.out;
    }
}
