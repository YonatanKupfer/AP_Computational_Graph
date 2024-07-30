package AP_project.configs;

import AP_project.graph.*;

public class SquareRootAgent implements Agent {
    private double value = 0;
    private final String[] subs;
    private final String[] pubs;

    public SquareRootAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        subscribeToTopics();
    }

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
        return "SquareRootAgent";
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
            double result = Math.sqrt(value);
            topicManager.getTopic(pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public void close() {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(subs[0]).unsubscribe(this);
        tm.getTopic(pubs[0]).removePublisher(this);
    }
}
