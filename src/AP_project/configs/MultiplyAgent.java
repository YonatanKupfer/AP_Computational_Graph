package AP_project.configs;

import AP_project.graph.*;

/**
 * The MultiplyAgent class represents an agent that performs multiplication
 * between two received values and publishes the result to a topic.
 * It subscribes to two topics for receiving inputs and publishes the result
 * to another topic.
 */
public class MultiplyAgent implements Agent {
    private double x = 0; // Value from the first input topic
    private double y = 0; // Value from the second input topic
    private final String[] subs; // Topics to subscribe to
    private final String[] pubs; // Topics to publish to

    /**
     * Constructs a MultiplyAgent with the specified subscription and publication topics.
     * @param subs Array of topic names to subscribe to (expects two topics).
     * @param pubs Array of topic names to publish to (expects one topic).
     */
    public MultiplyAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        subscribeToTopics();
    }

    /**
     * Subscribes to the specified input topics and adds this agent as a publisher
     * to the specified output topic.
     */
    private void subscribeToTopics() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

        // Subscribe to the input topics (expects two topics)
        if (subs.length >= 2) {
            topicManager.getTopic(subs[0]).subscribe(this);
            topicManager.getTopic(subs[1]).subscribe(this);
        }

        // Add as a publisher to the output topic
        if (pubs.length > 0) {
            topicManager.getTopic(pubs[0]).addPublisher(this);
        }
    }

    @Override
    public String getName() {
        return "MultiplyAgent";
    }

    @Override
    public void reset() {
        x = 0; // Reset input values
        y = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

        // Update the values based on the topic
        if (topic.equals(subs[0])) {
            x = msg.asDouble;
        } else if (topic.equals(subs[1])) {
            y = msg.asDouble;
        }

        // Perform multiplication and publish the result
        if (!Double.isNaN(x) && !Double.isNaN(y) && pubs.length > 0) {
            double result = x * y;
            topicManager.getTopic(pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public void close() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

        // Unsubscribe from the input topics and remove this agent as a publisher
        if (subs.length > 0) {
            topicManager.getTopic(subs[0]).unsubscribe(this);
        }
        if (subs.length > 1) {
            topicManager.getTopic(subs[1]).unsubscribe(this);
        }
        if (pubs.length > 0) {
            topicManager.getTopic(pubs[0]).removePublisher(this);
        }
    }
}
