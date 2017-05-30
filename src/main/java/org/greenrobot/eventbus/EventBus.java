package org.greenrobot.eventbus;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */
public class EventBus {
    private static EventBus bus = new EventBus();
    public static EventBus getDefault() {
        return bus;
    }

    public void post(Object poll) {
    }
}
