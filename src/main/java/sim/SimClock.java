package sim;

import nl.saxion.ami.moses.healthstatusmonitoring.util.Clock;

/**
 * @author Etto Salomons
 *         created on 18/05/17.
 */
public class SimClock extends Clock{
    private long time;

    @Override
    public long getTime(){
        return time;
    }

    public void setTime(long time){
        this.time = time;
    }

    public void advanceTime(long delta){
        time += delta;
    }
}
