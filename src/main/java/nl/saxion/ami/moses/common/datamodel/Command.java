package nl.saxion.ami.moses.common.datamodel;

/**
 * Created by ebben on 20/08/15.
 */
public class Command extends DistributableData {

    public static final String COMMAND_CLEAR = "clear";

    private int userId;
    private String command;
    private Location location;

    public Command(int userId, String command, Location location) {
        this.userId = userId;
        this.command = command;
        this.location = location;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
