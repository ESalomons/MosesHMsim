package nl.saxion.ami.moses.common;

/**
 * Callback interface for components.
 */
public interface ServiceComponentCallbacks {
    /**
     * Indicates that a component has started successfully.
     * This callback will be invoked on the main thread.
     *
     * @param componentName the name of the component
     */
    void onComponentStarted(String componentName);

    /**
     * Indicates that a component has failed to start.
     * This callback will be invoked on the main thread.
     *
     * @param componentName the name of the component
     * @param message the error message
     * @param throwable the throwable that caused the startup failure
     */
    void onComponentFailedToStart(String componentName, String message, Throwable throwable);
}
