package AP_project.configs;

import AP_project.graph.*;

/**
 * The IncAgent class represents an agent that increments a received value
 * and publishes the result to a topic. It subscribes to a single topic and
 * publishes to another.
 */
public class IncAgent implements Agent {
    private final String[] subs; // Topics to subscribe to
    private final String[] pubs; // Topics to publish to

    /**
     * Constructs an IncAgent with the specified subscription and publication topics.
     * @param subs Array of topic names to subscribe to.
     * @param pubs Array of topic names to publish to.
     */
    public IncAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        subscribeToTopics();
    }

    /**
     * Subscribes to the specified topics and adds this agent as a publisher
     * to the specified topics.
     */
    private void subscribeToTopics() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

        if (subs.length > 0) {
            topicManager.getTopic(subs[0]).subscribe(this);
        }

        if (pubs.length > 0) {
            topicManager.getTopic(pubs[0]).addPublisher(this);
        }
    }

    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {
        // No state to reset for IncAgent
    }

    @Override
    public void callback(String topic, Message msg) {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

        // Increment the message value and publish the result
        if (!Double.isNaN(msg.asDouble) && pubs.length > 0) {
            double result = msg.asDouble + 1;
            topicManager.getTopic(pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public void close() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

        // Unsubscribe from the topics and remove this agent as a publisher
        if (subs.length > 0) {
            topicManager.getTopic(subs[0]).unsubscribe(this);
        }
        if (pubs.length > 0) {
            topicManager.getTopic(pubs[0]).removePublisher(this);
        }
    }
}
