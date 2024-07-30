package AP_project.configs;

import AP_project.graph.*;

public class DivideAgent implements Agent {
    private double x = 0;
    private double y = 0;
    private final String[] subs;
    private final String[] pubs;

    public DivideAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        subscribeToTopics();
    }

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
        return "DivideAgent";
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
            if (y != 0) {
                double result = x / y;
                topicManager.getTopic(pubs[0]).publish(new Message(result));
            } else {
                topicManager.getTopic(pubs[0]).publish(new Message(Double.NaN)); // Handle division by zero
            }
        }
    }

    @Override
    public void close() {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(subs[0]).unsubscribe(this);
        tm.getTopic(subs[1]).unsubscribe(this);
        tm.getTopic(pubs[0]).removePublisher(this);
    }
}
