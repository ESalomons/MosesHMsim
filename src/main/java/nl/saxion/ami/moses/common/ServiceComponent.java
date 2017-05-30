package nl.saxion.ami.moses.common;

import android.content.Context;

/**
 * Interface that all service components (i.e. all components that run within the context
 * of the foreground service) must implement to control their lifecycle. Note that, in addition to
 * these methods, all classes must have a default constructor.
 *
 * Note that all ServiceComponents can access the configuration using the
 * {@link Configuration#getInstance()} method and they should use their name to identify
 * properties belonging to the specific component.
 */
public interface ServiceComponent {

    /**
     * Returns the unique name of the service component. By convention, this is used to identify the
     * service and the properties in the configuration file. It should be written in camelCase
     * and start with a lowercase character.
     * @return the name of the service component.
     */
    String getName();

//    /**
//     * Set the name of the service component, as defined in the configuration file.
//     * @param name the name of the service component
//     */
//    void setName(String name);

    /**
     * Starts the service component. Depending on how the component is started (see
     * {@link CoreService#startComponent(ServiceComponent) and {@link CoreService#startComponentAsync(ServiceComponent, ServiceComponentCallbacks)}}
     * this method will either be invoked on the main thread (in which case it is not OK to
     * perform long running operations) or it will run on a separate thread (in which case it is OK
     * to block until all initialisation has been completed). Components should spawn a new thread
     * from within this method to do any other work besides initial start-up code.
     *
     * @param context the context to use to lookup resources et cetera
     * @return whether the service component has been started successfully
     */
    boolean start(Context context);

    /**
     * Stop the service component. This method should return as quickly as possible, to avoid
     * blocking the main (UI) thread.
     */
    void stop();
}
