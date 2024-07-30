package AP_project.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The Topic class represents a topic in the publish-subscribe model.
 * It maintains a list of subscribers and publishers, and allows publishing messages to subscribers.
 */
public class Topic {
    /** The name of the topic. */
    public String name;
    private List<Agent> subs;
    private List<Agent> pubs;
    private String result = "";

    /**
     * Constructs a Topic with the specified name.
     * @param name The name of the topic.
     */
    public Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
    }

    /**
     * Subscribes an agent to the topic.
     * @param a The agent to subscribe.
     */
    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
        }
    }

    /**
     * Unsubscribes an agent from the topic.
     * @param a The agent to unsubscribe.
     */
    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    /**
     * Publishes a message to all subscribed agents.
     * @param m The message to publish.
     */
    public void publish(Message m) {
        setResult(m.asText);
        for (Agent a : subs) {
            a.callback(this.name, m);
        }
    }

    /**
     * Adds an agent as a publisher for the topic.
     * @param a The agent to add as a publisher.
     */
    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a);
        }
    }

    /**
     * Removes an agent as a publisher for the topic.
     * @param a The agent to remove as a publisher.
     */
    public void removePublisher(Agent a) {
        pubs.remove(a);
    }

    /**
     * Gets the list of subscribed agents.
     * @return A list of subscribed agents.
     */
    public List<Agent> getSubscribers() {
        return new ArrayList<>(subs);
    }

    /**
     * Gets the list of publishers.
     * @return A list of publishers.
     */
    public List<Agent> getPublishers() {
        return new ArrayList<>(pubs);
    }

    /**
     * Gets the name of the topic.
     * @return The name of the topic.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the result of the last published message.
     * @return The result of the last published message.
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the result of the last published message.
     * @param result The result to set.
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Sets the name of the topic.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
