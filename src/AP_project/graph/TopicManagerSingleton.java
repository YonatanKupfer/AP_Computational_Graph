//package AP_project.test;
package AP_project.graph;


import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManagerSingleton {

    public static class TopicManager{
        private final ConcurrentHashMap<String, Topic> topics;

        private TopicManager(){
            this.topics = new ConcurrentHashMap<>();
        }

        public Topic getTopic(String name){
            return topics.computeIfAbsent(name, Topic::new);
        }

        public Collection<Topic> getTopics(){
            return topics.values();
        }

        public void clear(){
            topics.clear();
        }

        public void clearTopicsAndAgents() {
            topics.clear();
        }
    }

    private static final TopicManager instance = new TopicManager();

    public static TopicManager get(){
        return instance;
    }

    
}
