//package AP_project.test;
package AP_project.graph;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ParallelAgent implements Agent{
    private final Agent agent;
    private final BlockingQueue<TopicMessage> queue;
    private Thread workerThread;
    private volatile boolean active;


    public ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.queue = new LinkedBlockingQueue<>(capacity);
        this.active = true;
        this.workerThread = new Thread(this::processMessages);
        this.workerThread.start();
    }

    private void processMessages() {
        while(active) {
            try {
                TopicMessage topicMessage = queue.take();
                agent.callback(topicMessage.getTopic(), topicMessage.getMessage());
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void reset() {

    }

    @Override
    public void callback(String topic, Message msg) {
        try {
            queue.put(new TopicMessage(topic, msg)); // This will block if the queue is full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        active = false;
        workerThread.interrupt();
    }

    private static class TopicMessage {
        private final String topic;
        private final Message message;

        public TopicMessage(String topic, Message message) {
            this.topic = topic;
            this.message = message;
        }

        public String getTopic() {
            return topic;
        }

        public Message getMessage() {
            return message;
        }
    }
}
