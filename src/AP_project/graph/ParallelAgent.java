package AP_project.graph;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The ParallelAgent class wraps an Agent to handle messages in parallel using a separate worker thread.
 */
public class ParallelAgent implements Agent {
    private final Agent agent;
    private final BlockingQueue<TopicMessage> queue;
    private final Thread workerThread;
    private volatile boolean active;

    /**
     * Constructs a ParallelAgent with the specified agent and queue capacity.
     * @param agent The agent to be wrapped.
     * @param capacity The capacity of the message queue.
     */
    public ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.queue = new LinkedBlockingQueue<>(capacity);
        this.active = true;
        this.workerThread = new Thread(this::processMessages);
        this.workerThread.start();
    }

    /**
     * Processes messages from the queue in a separate thread.
     */
    private void processMessages() {
        while (active) {
            try {
                TopicMessage topicMessage = queue.take();
                agent.callback(topicMessage.getTopic(), topicMessage.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        agent.reset();
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
        agent.close();
    }

    /**
     * A class to hold topic and message pairs.
     */
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
