package AP_project.graph;

/**
 * The Agent interface defines the methods that an agent must implement.
 */
public interface Agent {

    /**
     * Gets the name of the agent.
     * @return The name of the agent.
     */
    String getName();

    /**
     * Resets the state of the agent.
     */
    void reset();

    /**
     * Callback method to handle messages from a topic.
     * @param topic The topic from which the message is received.
     * @param msg The message received from the topic.
     */
    void callback(String topic, Message msg);

    /**
     * Closes the agent and releases any resources held by it.
     */
    void close();
}
