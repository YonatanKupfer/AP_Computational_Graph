package AP_project.configs;

import AP_project.graph.*;

/**
 * An agent that computes the square of a value received from a subscribed topic.
 * Publishes the result to a specified topic.
 */
public class SquareAgent implements Agent {
    private double value = 0; // Holds the value received from the subscription
    private final String[] subs; // Topics to subscribe to
    private final String[] pubs; // Topics to publish results to

    /**
     * Constructs a SquareAgent with specified subscriptions and publications.
     * @param subs Topics to subscribe to
     * @param pubs Topics to publish results to
     */
    public SquareAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        subscribeToTopics();
    }

    /**
     * Subscribes to the specified topics and sets up publishing.
     */
    private void subscribeToTopics() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        if (subs.length >= 1) {
            topicManager.getTopic(subs[0]).subscribe(this);
        }
        if (pubs.length > 0) {
            topicManager.getTopic(pubs[0]).addPublisher(this);
        }
    }

    @Override
    public String getName() {
        return "SquareAgent";
    }

    @Override
    public void reset() {
        value = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        value = msg.asDouble;
        if (!Double.isNaN(value) && pubs.length > 0) {
            double result = value * value; // Compute the square of the value
            topicManager.getTopic(pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public void close() {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        if (subs.length > 0) {
            tm.getTopic(subs[0]).unsubscribe(this);
        }
        if (pubs.length > 0) {
            tm.getTopic(pubs[0]).removePublisher(this);
        }
    }
}
