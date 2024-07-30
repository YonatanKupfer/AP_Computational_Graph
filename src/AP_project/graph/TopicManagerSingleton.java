package AP_project.graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The TopicManagerSingleton class provides a singleton instance of the TopicManager.
 */
public class TopicManagerSingleton {

    /**
     * The TopicManager class manages topics in a thread-safe manner.
     */
    public static class TopicManager {
        private final ConcurrentHashMap<String, Topic> topics;

        private TopicManager() {
            this.topics = new ConcurrentHashMap<>();
        }

        /**
         * Gets a topic by name, creating it if it does not exist.
         * @param name The name of the topic.
         * @return The Topic instance.
         */
        public Topic getTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new);
        }

        /**
         * Gets all topics managed by this TopicManager.
         * @return A collection of all topics.
         */
        public Collection<Topic> getTopics() {
            return topics.values();
        }

        /**
         * Clears all topics.
         */
        public void clear() {
            topics.clear();
        }

        /**
         * Clears all topics and agents.
         * This method is redundant as it just calls clear().
         */
        public void clearTopicsAndAgents() {
            topics.clear();
        }
    }

    // Singleton instance of TopicManager
    private static final TopicManager instance = new TopicManager();

    /**
     * Gets the singleton instance of the TopicManager.
     * @return The singleton TopicManager instance.
     */
    public static TopicManager get() {
        return instance;
    }
}
