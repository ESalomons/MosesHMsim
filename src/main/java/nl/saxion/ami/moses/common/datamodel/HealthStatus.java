package nl.saxion.ami.moses.common.datamodel;

/**
 * Sample data class for health status.
 */
public class HealthStatus extends DistributableData {

    public enum Status {
        RED, ORANGE, GREEN, GRAY
    }

    private String explanation;
    private Status status;
    private int userId;

    public HealthStatus(int userId, Status status) {
        this(userId,status, "No extra explanation is set");
    }

    public HealthStatus(int userId, Status status, String explanation) {
        this.userId = userId;
        this.status = status;
        this.explanation = explanation;
    }

    public HealthStatus() {
        // Default no-arg constructor
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Returns the RED, ORANGE, GREEN or GRAY status
     * @return
     */
    public Status getStatus() {
        return status;
    }

    public String getExplanation() { return explanation; }

    public int getUserId(){
        return userId;
    }

    @Override
    public String toString() {
        return status + " " + explanation;
    }
}