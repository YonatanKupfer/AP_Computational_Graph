package AP_project.servlets;

import AP_project.server.RequestParser;
import AP_project.graph.Message;
import AP_project.graph.Topic;
import AP_project.graph.TopicManagerSingleton;
import AP_project.configs.Graph;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import AP_project.view.HtmlGraphWriter;

public class TopicDisplayer implements Servlet {

    public static Map<String, String> topicToCurrentMessage = new HashMap<>();
    private String configDescription;

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        System.out.println("Handling /publish request...");
        Map<String, String> parameters = ri.getParameters();
        String topicName = parameters.get("topicName");
        String messageContent = parameters.get("message");

        System.out.println("Received parameters - topic: " + topicName + ", message: " + messageContent);

        if (topicName == null || messageContent == null) {
            invalidParameterResponse(toClient, topicName);
            return;
        }

        Collection<Topic> allTopics = TopicManagerSingleton.get().getTopics();
        boolean found = false;

        // Look for this topic
        for (Topic topic : allTopics) {
            if (topic.name.equals(topicName)) {
                found = true;
                break;
            }
        }

        if (!found) {
            // If invalid topic asked for
            invalidParameterResponse(toClient, topicName);
            return;
        }

        // Publish the message to the topic using TopicManagerSingleton
        System.out.println("Publishing message to topic...");
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        Topic topic = topicManager.getTopic(topicName);
        System.out.println("Topic fetched: " + topic.getName());
        topic.publish(new Message(messageContent));
        System.out.println("Message published to topic: " + topicName);

        // Update the message in the map
        topicToCurrentMessage.put(topicName, messageContent);

        // Update all topics' values
        for (Topic topic1 : allTopics) {
            // If it's a source node and not seen this topic yet
            if (topic1.getPublishers().isEmpty() && !topicToCurrentMessage.containsKey(topic1.name)) {
                topicToCurrentMessage.put(topic1.name, "");
            } else {
                topicToCurrentMessage.put(topic1.name, topic1.getResult());
            }
        }

        // Generate the description if not already set
        if (configDescription == null) {
            configDescription = generateDescriptionFromConfig("src/config_files/uploadedConfig.txt");
        }

        // Create and update the graph
        Graph graph = new Graph();
        graph.createFromTopics();

        List<String> graphHtmlLines = HtmlGraphWriter.getGraphHtml(graph, topicToCurrentMessage);
        List<String> tableHtmlLines = HtmlGraphWriter.getTableHtml(graph, topicToCurrentMessage, configDescription);
        String graphHtmlResponse = String.join("\n", graphHtmlLines);
        String tableHtmlResponse = String.join("\n", tableHtmlLines);

        writeToFile("src/html_files/graph.html", graphHtmlResponse);
        writeToFile("src/html_files/table.html", tableHtmlResponse);

        // Send the response indicating success
        String successResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<html><body><h1>Message published and graph updated</h1>" +
                "<script>" +
                "window.parent.document.getElementById('graphFrame').src = 'graph.html';" +
                "window.parent.document.getElementById('tableFrame').src = 'table.html';" +
                "</script>" +
                "</body></html>";

        // Send the response
        System.out.println("Sending response...");
        toClient.write(successResponse.getBytes(StandardCharsets.UTF_8));
        toClient.flush();
        toClient.close();
        System.out.println("Response sent for /publish request.");
    }

    /**
     * Sends an error response indicating an invalid parameter.
     */
    private void invalidParameterResponse(OutputStream toClient, String topicName) throws IOException {
        String errorResponse = "<html><body><div>Invalid Topic: " + topicName + "</div></body></html>";
        byte[] responseBytes = errorResponse.getBytes();

        // Setting HTTP response headers manually
        String headers = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + responseBytes.length + "\r\n" +
                "Connection: close\r\n\r\n";

        toClient.write(headers.getBytes());
        toClient.write(responseBytes);
        toClient.flush();
    }

    /**
     * Writes content to a file.
     */
    private void writeToFile(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a description from the configuration file.
     */
    private String generateDescriptionFromConfig(String configFilePath) throws IOException {
        StringBuilder description = new StringBuilder();
        String configContent = new String(Files.readAllBytes(Paths.get(configFilePath)), StandardCharsets.UTF_8);
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

    @Override
    public void close() throws IOException {
        System.out.println("Closing TopicDisplayer servlet...");
    }
}
