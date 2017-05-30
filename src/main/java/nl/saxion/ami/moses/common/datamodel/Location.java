package nl.saxion.ami.moses.common.datamodel;

/**
 * Created by ebben on 04/06/15.
 */
public class Location extends DistributableData {

    private int userId;
    private double latitude;
    private double longitude;
    private double altitude;
    private boolean indoor = false;
    private Integer floorNumber = null;

    public Location(int userId, double latitude, double longitude, Integer floorNumber) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floorNumber = floorNumber;
    }

    public Location() {
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public boolean isIndoor() {
        return indoor;
    }

    public void setIndoor(boolean indoor) {
        this.indoor = indoor;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }
}
