package AP_project.configs;

import AP_project.graph.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a node in a graph. Each node can have edges to other nodes
 * and can store a message.
 */
public class Node {
    private String name; // The name of the node
    private List<Node> edges; // List of nodes this node has edges to
    private Message msg; // Message associated with this node

    /**
     * Constructs a Node with the specified name.
     * @param name The name of the node
     */
    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    /**
     * Gets the name of the node.
     * @return The name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the node.
     * @param name The new name of the node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of nodes connected by edges from this node.
     * @return List of adjacent nodes
     */
    public List<Node> getEdges() {
        return edges;
    }

    /**
     * Sets the list of nodes connected by edges from this node.
     * @param edges The new list of adjacent nodes
     */
    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    /**
     * Gets the message associated with this node.
     * @return The message associated with this node
     */
    public Message getMsg() {
        return msg;
    }

    /**
     * Sets the message associated with this node.
     * @param msg The new message associated with this node
     */
    public void setMsg(Message msg) {
        this.msg = msg;
    }

    /**
     * Adds an edge from this node to another node.
     * @param node The node to which an edge will be added
     */
    public void addEdge(Node node) {
        edges.add(node);
    }

    /**
     * Checks if the graph containing this node has cycles.
     * @return True if a cycle is detected, false otherwise
     */
    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        return hasCycles(this, visited);
    }

    /**
     * Helper method to detect cycles using Depth-First Search (DFS).
     * @param node The current node being visited
     * @param visited Set of visited nodes to detect cycles
     * @return True if a cycle is detected, false otherwise
     */
    private boolean hasCycles(Node node, Set<Node> visited) {
        if (visited.contains(node)) {
            return true; // Cycle detected
        }
        visited.add(node);

        for (Node adjacent : node.getEdges()) {
            if (hasCycles(adjacent, visited)) {
                return true;
            }
        }
        visited.remove(node);
        return false; // No cycle found
    }
}
