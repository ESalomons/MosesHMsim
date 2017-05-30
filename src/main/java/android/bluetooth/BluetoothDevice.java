package android.bluetooth;

import java.util.UUID;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class BluetoothDevice {
    public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) {
        return new BluetoothSocket();
    }
}
