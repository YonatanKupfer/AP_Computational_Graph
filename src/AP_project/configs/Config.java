package AP_project.configs;

/**
 * The Config interface defines the methods that a configuration class must implement.
 */
public interface Config {

    /**
     * Creates the configuration.
     */
    void create();

    /**
     * Gets the name of the configuration.
     * @return The name of the configuration.
     */
    String getName();

    /**
     * Gets the version of the configuration.
     * @return The version of the configuration.
     */
    int getVersion();

    /**
     * Closes the configuration and releases any resources held by it.
     */
    void close();
}
