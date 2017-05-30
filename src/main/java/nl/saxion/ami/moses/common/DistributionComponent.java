package nl.saxion.ami.moses.common;

/**
 * Created by ebben on 05/10/16.
 */

public interface DistributionComponent extends ServiceComponent {

    /**
     * Get the IP address of the device.
     *
     * @return the IP address of the device.
     */
    public String getIpAddress();
}
