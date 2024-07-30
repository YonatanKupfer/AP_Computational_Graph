package AP_project.servlets;

import AP_project.configs.GenericConfig;
import AP_project.configs.Graph;
import AP_project.configs.Node;
import AP_project.graph.TopicManagerSingleton;
import AP_project.server.RequestParser;
import AP_project.view.HtmlGraphWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The ConfLoader class is responsible for loading configuration files,
 * processing them to generate graphs and tables, and sending appropriate
 * responses to clients. It implements the Servlet interface for handling
 * HTTP requests.
 */
public class ConfLoader implements Servlet {
    private final String baseDir;
    /**
     * A set that holds the names of loaded topics.
     */
    public static Set<String> loadedTopics = new HashSet<>();

    /**
     * Constructs a ConfLoader instance with the specified base directory.
     *
     * @param baseDir The base directory where configuration files are located.
     */
    public ConfLoader(String baseDir) {
        this.baseDir = baseDir;
        clearStateAndWriteEmptyHtmlFiles();
    }

    /**
     * Clears previous state and writes empty HTML files for the graph and table.
     */
    private void clearStateAndWriteEmptyHtmlFiles() {
        // Clear previous state
        TopicManagerSingleton.get().clear();
        loadedTopics.clear();
        TopicDisplayer.topicToCurrentMessage.clear();

        // Write empty HTML files
        try {
            String emptyGraphHtml = generateEmptyGraphHtml();
            String emptyTableHtml = generateEmptyTableHtml();

            writeToFile("src/html_files/graph.html", emptyGraphHtml);
            writeToFile("src/html_files/table.html", emptyTableHtml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates an empty graph HTML structure.
     */
    private String generateEmptyGraphHtml() {
        return "<html><head><title>Graph Visualization</title></head><body><svg width=\"960\" height=\"600\"></svg></body></html>";
    }

    /**
     * Generates an empty table HTML structure.
     */
    private String generateEmptyTableHtml() {
        return "<html><head><title>Node Values</title></head><body><h1>Node Values</h1><table><thead><tr><th>Node</th><th>Value</th></tr></thead><tbody></tbody></table></body></html>";
    }

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        String contentType = ri.getHeaders().get("Content-Type");
        if (contentType == null || !contentType.contains("multipart/form-data")) {
            sendErrorResponse(toClient, "Invalid content type");
            return;
        }

        String boundary = contentType.split("boundary=")[1];
        if (boundary == null) {
            sendErrorResponse(toClient, "Boundary not found in content type");
            return;
        }
        boundary = "--" + boundary;

        byte[] content = ri.getContent();
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
        BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream, StandardCharsets.UTF_8));

        StringBuilder fileContent = new StringBuilder();
        String line;
        boolean filePart = false;
        while ((line = reader.readLine()) != null) {
            if (line.equals(boundary)) {
                filePart = true;
                continue;
            } else if (line.equals(boundary + "--")) {
                break;
            }

            if (filePart) {
                if (line.startsWith("Content-Disposition:") || line.isEmpty()) {
                    continue;
                } else {
                    fileContent.append(line).append("\n");
                }
            }
        }

        String fileName = baseDir + "/config_files/uploadedConfig.txt";
        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileContent.toString().getBytes(StandardCharsets.UTF_8));
        }

        System.out.println("Configuration file content:");
        System.out.println(fileContent);

        // Clear previous configuration state
        TopicManagerSingleton.get().clear();
        loadedTopics.clear();
        TopicDisplayer.topicToCurrentMessage.clear();

        // Create and load configuration
        GenericConfig config = new GenericConfig();
        config.setConfFile(fileName);
        config.create();

        // Create graph from topics
        Graph graph = new Graph();
        graph.createFromTopics();

        String description = generateDescription(fileContent.toString());

        System.out.println("Graph nodes and edges:");
        for (Node node : graph) {
            if (node.getName().startsWith("T")) {
                loadedTopics.add(node.getName().substring(1)); // Storing topic names without 'T' prefix
            }
            System.out.println("Node: " + node.getName() + ", Edges: " + node.getEdges().size());
            for (Node edge : node.getEdges()) {
                System.out.println("  Edge to: " + edge.getName());
            }
        }

        // Generate HTML content for graph and table
        List<String> graphHtmlLines = HtmlGraphWriter.getGraphHtml(graph, TopicDisplayer.topicToCurrentMessage);
        List<String> tableHtmlLines = HtmlGraphWriter.getTableHtml(graph, TopicDisplayer.topicToCurrentMessage, description);

        String graphHtmlResponse = String.join("\n", graphHtmlLines);
        String tableHtmlResponse = String.join("\n", tableHtmlLines);

        // Write the HTML files
        writeToFile("src/html_files/graph.html", graphHtmlResponse);
        writeToFile("src/html_files/table.html", tableHtmlResponse);

        sendSuccessResponse(toClient, "Configuration and graph successfully loaded.");
    }

    /**
     * Sends an error response to the client.
     */
    private void sendErrorResponse(OutputStream toClient, String message) throws IOException {
        String response = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<html><body><h1>400 Bad Request</h1><p>" + message + "</p></body></html>";
        toClient.write(response.getBytes(StandardCharsets.UTF_8));
        toClient.flush();
        toClient.close();
    }

    /**
     * Sends a success response to the client.
     */
    private void sendSuccessResponse(OutputStream toClient, String message) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<html><body><h1>" + message + "</h1>" +
                "<script>" +
                "window.parent.document.getElementById('graphFrame').src = 'graph.html';" +
                "window.parent.document.getElementById('tableFrame').src = 'table.html';" +
                "</script>" +
                "</body></html>";
        toClient.write(response.getBytes(StandardCharsets.UTF_8));
        toClient.flush();
        toClient.close();
    }

    /**
     * Generates a description of the configuration from the content.
     */
    private String generateDescription(String configContent) {
        StringBuilder description = new StringBuilder();
        String[] lines = configContent.split("\n");

        Map<String, String> operationMap = new HashMap<>();
        operationMap.put("AP_project.configs.PlusAgent", "+");
        operationMap.put("AP_project.configs.MultiplyAgent", "×");
        operationMap.put("AP_project.configs.SquareAgent", "^2");
        operationMap.put("AP_project.configs.SquareRootAgent", "√");
        operationMap.put("AP_project.configs.MinusAgent", "−");
        operationMap.put("AP_project.configs.DivideAgent", "÷");

        List<String> operations = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                if (operationMap.containsKey(line)) {
                    String operation = operationMap.get(line);
                    String inputs = lines[i + 1].trim();
                    String output = lines[i + 2].trim();
                    if (operation.equals("^2")) {
                        operations.add(inputs + " " + operation + " = " + output);
                    } else if (operation.equals("√")) {
                        operations.add(operation + inputs + " = " + output);
                    } else {
                        String[] inputArray = inputs.split(",");
                        operations.add(inputArray[0] + " " + operation + " " + inputArray[1] + " = " + output);
                    }
                }
            }
        }

        description.append("This configuration performs the following steps:<br><br>");
        for (int i = 0; i < operations.size(); i++) {
            description.append(i + 1).append(". ").append(operations.get(i)).append("<br><br>");
        }

        return description.toString().trim();
    }

    /**
     * Writes content to a file.
     */
    private void writeToFile(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws IOException {
        // Implement any necessary cleanup logic
    }
}
