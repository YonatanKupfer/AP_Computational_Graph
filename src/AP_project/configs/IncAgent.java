//package AP_project.test;
package AP_project.configs;

import AP_project.graph.*;

public class IncAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;

    public IncAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        subscribeToTopics();
    }

    private void subscribeToTopics() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        if(subs.length > 0){
            topicManager.getTopic(subs[0]).subscribe(this);
        }
        if (pubs.length >0) {
            topicManager.getTopic(pubs[0]).addPublisher(this);
        }

    }

    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {

    }

    @Override
    public void callback(String topic, Message msg) {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        if(!Double.isNaN(msg.asDouble) && pubs.length > 0){
            double result = msg.asDouble + 1;
            topicManager.getTopic(pubs[0]).publish(new Message(result));
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
