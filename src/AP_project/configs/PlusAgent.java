package AP_project.configs;

import AP_project.graph.*;

/**
 * An agent that performs addition on values received from subscribed topics.
 * Publishes the result to a specified topic.
 */
public class PlusAgent implements Agent {
    private double x = 0; // Holds the value from the first subscription
    private double y = 0; // Holds the value from the second subscription
    private final String[] subs; // Topics to subscribe to
    private final String[] pubs; // Topics to publish to

    /**
     * Constructs a PlusAgent with specified subscriptions and publications.
     * @param subs Topics to subscribe to
     * @param pubs Topics to publish results to
     */
    public PlusAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        subscribeToTopics();
    }

    /**
     * Subscribes to the specified topics and sets up publishing.
     */
    private void subscribeToTopics() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        if (subs.length >= 2) {
            topicManager.getTopic(subs[0]).subscribe(this);
            topicManager.getTopic(subs[1]).subscribe(this);
        }
        if (pubs.length > 0) {
            topicManager.getTopic(pubs[0]).addPublisher(this);
        }
    }

    @Override
    public String getName() {
        return "PlusAgent";
    }

    @Override
    public void reset() {
        x = 0;
        y = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        if (topic.equals(subs[0])) {
            x = msg.asDouble;
        } else if (topic.equals(subs[1])) {
            y = msg.asDouble;
        }
        if (!Double.isNaN(x) && !Double.isNaN(y) && pubs.length > 0) {
            double result = x + y;
            topicManager.getTopic(pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public void close() {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        if (subs.length > 0) {
            tm.getTopic(subs[0]).unsubscribe(this);
        }
        if (subs.length > 1) {
            tm.getTopic(subs[1]).unsubscribe(this);
        }
        if (pubs.length > 0) {
            tm.getTopic(pubs[0]).removePublisher(this);
        }
    }
}
