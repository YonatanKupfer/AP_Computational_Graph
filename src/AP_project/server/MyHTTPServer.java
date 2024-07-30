package AP_project.server;

import AP_project.servlets.Servlet;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

/**
 * MyHTTPServer is an implementation of an HTTP server that handles HTTP requests
 * and manages multiple servlets for different HTTP commands and URIs.
 * It extends Thread to run the server in a separate thread and implements
 * the HTTPServer interface.
 */
public class MyHTTPServer extends Thread implements HTTPServer {

    private final int port;
    private final int nThreads;
    private final ExecutorService threadPool;
    private final Map<String, Map<String, Servlet>> servlets = new HashMap<>();
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    /**
     * Constructs a MyHTTPServer instance with a specified port and number of threads.
     * @param port The port on which the server will listen for incoming connections.
     * @param nThreads The number of threads to use for handling client connections.
     */
    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void addServlet(String httpCommand, String uri, Servlet s) {
        servlets.computeIfAbsent(httpCommand, k -> new HashMap<>()).put(uri, s);
        System.out.println("Added servlet for " + httpCommand + " " + uri);
    }

    @Override
    public void removeServlet(String httpCommand, String uri) {
        Map<String, Servlet> commandMap = servlets.get(httpCommand);
        if (commandMap != null) {
            commandMap.remove(uri);
            System.out.println("Removed servlet for " + httpCommand + " " + uri);
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Accepted connection from " + clientSocket.getInetAddress());

                    threadPool.execute(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (!running) {
                        break;
                    }
                    System.out.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        System.out.println("Handling client connection...");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // Parse the request
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(reader);
            System.out.println("Handling request: " + requestInfo.getHttpCommand() + " " + requestInfo.getUri());

            // Find the appropriate servlet
            Servlet servlet = findServlet(requestInfo);
            if (servlet != null) {
                System.out.println("Found servlet for request: " + requestInfo.getHttpCommand() + " " + requestInfo.getUri());
                servlet.handle(requestInfo, outputStream);
                System.out.println("Request handled successfully.");
            } else {
                sendNotFoundResponse(outputStream);
                System.out.println("Servlet not found for request: " + requestInfo.getHttpCommand() + " " + requestInfo.getUri());
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    /**
     * Searches for a servlet that matches the request's HTTP command and URI.
     * @param requestInfo The request information.
     * @return The matching servlet, or null if no match is found.
     */
    private Servlet findServlet(RequestParser.RequestInfo requestInfo) {
        System.out.println("Searching for servlet for command: " + requestInfo.getHttpCommand() + " and URI: " + requestInfo.getUri());
        Map<String, Servlet> commandMap = servlets.get(requestInfo.getHttpCommand());
        if (commandMap == null) {
            System.out.println("No servlets registered for command: " + requestInfo.getHttpCommand());
            return null;
        }
        // Match servlet by prefix
        for (String registeredUri : commandMap.keySet()) {
            if (requestInfo.getUri().startsWith(registeredUri.replace("*", ""))) {
                return commandMap.get(registeredUri);
            }
        }
        System.out.println("No servlet found for URI: " + requestInfo.getUri());
        return null;
    }

    /**
     * Sends a 404 Not Found response to the client.
     * @param outputStream The OutputStream to write the response to.
     * @throws IOException If an I/O error occurs.
     */
    private void sendNotFoundResponse(OutputStream outputStream) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<html><body><h1>404 Not Found</h1></body></html>";
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    @Override
    public void close() {
        running = false;
        threadPool.shutdown();
        try {
            serverSocket.close();
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }
}
