//package AP_project.test;
package AP_project.configs;

import AP_project.graph.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    // Constructor that initializes the node with a name
    public Node(String name) {
        this.name = name;
        edges = new ArrayList<Node>();
        //edges = new ArrayList<>();  new AI
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public void addEdge(Node node) {
        if (edges == null) {
            edges = new ArrayList<>();
        }
        edges.add(node);
    }

    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        return hasCycles(this, visited);
    }

    private boolean hasCycles(Node node, Set<Node> visited) {
        if (visited.contains(node)) {
            return true; // cycle
        }
        visited.add(node);

        for (Node adjacent : node.getEdges()) {
            if (hasCycles(adjacent, visited)){
                return true;
            }
        }
        visited.remove(node);
        return false; // No cycles found
    }

}