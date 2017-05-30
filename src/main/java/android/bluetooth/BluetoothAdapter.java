package android.bluetooth;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class BluetoothAdapter {
    private static BluetoothAdapter bta;
    public static BluetoothAdapter getDefaultAdapter() {
        return bta;
    }

    public BluetoothDevice getRemoteDevice(String bluetoothAddress) {
        return new BluetoothDevice();
    }
}
