package AP_project.configs;

import AP_project.graph.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Graph class represents a collection of nodes and their connections.
 * It extends {@code ArrayList<Node>} and provides methods to create the graph
 * from topics and check for cycles.
 * * <p>
 *  * This class uses the default constructor provided by {@code ArrayList<Node>}
 *  * and adds functionality for managing nodes and their connections.
 *  * </p>
 */
public class Graph extends ArrayList<Node> {
    private final HashMap<String, Node> nodeMap = new HashMap<>();

    /**
     * Default constructor for the Graph class.
     * <p>
     * Initializes an empty graph using the default constructor of {@code ArrayList<Node>}.
     * </p>
     */
    public Graph() {
        super();
    }

    /**
     * Checks if the graph contains any cycles.
     * @return true if the graph has cycles, false otherwise.
     */
    public boolean hasCycles() {
        for (Node node : this) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates the graph from the topics managed by the TopicManager.
     * For each topic, a node is created. Edges are added from topics
     * to subscribers and from publishers to topics.
     */
    public void createFromTopics() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

        for (Topic topic : topicManager.getTopics()) {
            // Create a node for the topic
            Node topicNode = getOrAddNode("T" + topic.getName());
            System.out.println("Created node for topic: " + topicNode.getName());

            // Add edges from the topic node to each subscriber
            for (Agent agent : topic.getSubscribers()) {
                Node agentNode = getOrAddNode("A" + agent.getName());
                topicNode.addEdge(agentNode);
                System.out.println("Added edge from " + topicNode.getName() + " to " + agentNode.getName());
            }

            // Add edges from each publisher to the topic node
            for (Agent agent : topic.getPublishers()) {
                Node agentNode = getOrAddNode("A" + agent.getName());
                agentNode.addEdge(topicNode);
                System.out.println("Added edge from " + agentNode.getName() + " to " + topicNode.getName());
            }
        }
    }

    /**
     * Retrieves a node by name or creates a new one if it does not exist.
     * @param name The name of the node.
     * @return The existing or newly created node.
     */
    public Node getOrAddNode(String name) {
        return nodeMap.computeIfAbsent(name, k -> {
            Node node = new Node(name);
            this.add(node);
            System.out.println("Created node: " + name);
            return node;
        });
    }
}
