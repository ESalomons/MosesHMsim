package nl.saxion.ami.moses.common.datamodel;

/**
 * Created by ebben on 15/11/16.
 */

public class SCBAStatus extends DistributableData {

    private int userId;
    private int remainingAirPressure;
    private int remainingAirInMinutes;

    public int getUserId() {
        return userId;
    }

    public int getRemainingAirInMinutes() {
        return remainingAirInMinutes;
    }

    public int getRemainingAirPressure() {
        return remainingAirPressure;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setRemainingAirInMinutes(int remainingAirInMinutes) {
        this.remainingAirInMinutes = remainingAirInMinutes;
    }

    public void setRemainingAirPressure(int remainingAirPressure) {
        this.remainingAirPressure = remainingAirPressure;
    }
}
