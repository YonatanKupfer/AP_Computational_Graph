package AP_project.server;

import AP_project.servlets.Servlet;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class MyHTTPServer extends Thread implements HTTPServer {

    private final int port;
    private final int nThreads;
    private final ExecutorService threadPool;
    private final Map<String, Map<String, Servlet>> servlets = new HashMap<>();
    private volatile boolean running = true;
    private ServerSocket serverSocket;

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
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(reader);
            System.out.println("Handling request: " + requestInfo.getHttpCommand() + " " + requestInfo.getUri());
            Servlet servlet = findServlet(requestInfo);
            if (servlet != null) {
                System.out.println("Found servlet for request: " + requestInfo.getHttpCommand() + " " + requestInfo.getUri());
                servlet.handle(requestInfo, outputStream);
                System.out.println("Request handled successfully.");
            } else {
                String response = "HTTP/1.1 404 Not Found\n\n";
                outputStream.write(response.getBytes());
                outputStream.flush();
                System.out.println("Servlet not found for request: " + requestInfo.getHttpCommand() + " " + requestInfo.getUri());
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

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
