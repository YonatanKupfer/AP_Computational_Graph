package AP_project.configs;

import AP_project.graph.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph extends ArrayList<Node> {
    private final HashMap<String, Node> nodeMap = new HashMap<>();

    public boolean hasCycles() {
        for (Node node : this) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    public void createFromTopics() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

        for (Topic topic : topicManager.getTopics()) {
            Node topicNode = getOrAddNode("T" + topic.getName());
            System.out.println("Created node for topic: " + topicNode.getName());

            for (Agent agent : topic.getSubscribers()) {
                Node agentNode = getOrAddNode("A" + agent.getName());
                topicNode.addEdge(agentNode);
                System.out.println("Added edge from " + topicNode.getName() + " to " + agentNode.getName());
            }

            for (Agent agent : topic.getPublishers()) {
                Node agentNode = getOrAddNode("A" + agent.getName());
                agentNode.addEdge(topicNode);
                System.out.println("Added edge from " + agentNode.getName() + " to " + topicNode.getName());
            }
        }
    }

    public Node getOrAddNode(String name) {
        return nodeMap.computeIfAbsent(name, k -> {
            Node node = new Node(name);
            this.add(node);
            System.out.println("Created node: " + name);
            return node;
        });
    }
}
