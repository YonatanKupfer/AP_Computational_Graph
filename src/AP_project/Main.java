package AP_project;

import AP_project.server.HTTPServer;
import AP_project.server.MyHTTPServer;
import AP_project.servlets.ConfLoader;
import AP_project.servlets.TopicDisplayer;
import AP_project.servlets.HtmlLoader;

import java.io.IOException;

/**
 * The Main class serves as the entry point for the application.
 * It sets up and starts an HTTP server, configures servlets to handle
 * different types of requests, and waits for user input to stop the server.
 */
public class Main {
    /**
     * The main method initializes the server, sets up servlets, and starts the server.
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while starting or stopping the server.
     */
    public static void main(String[] args) throws IOException {
        // Create a new instance of MyHTTPServer with port 8080 and 5 threads
        HTTPServer server = new MyHTTPServer(8080, 5);

        // Add servlet to handle GET requests for /publish
        server.addServlet("GET", "/publish", new TopicDisplayer());

        // Add servlet to handle POST requests for /upload with a configuration loader
        server.addServlet("POST", "/upload", new ConfLoader("src"));

        // Add servlet to handle GET requests for /app/ to load HTML files
        server.addServlet("GET", "/app/", new HtmlLoader("src/html_files"));

        // Start the server
        server.start();

        // Wait for user input to terminate the server
        System.in.read();

        // Close the server
        server.close();
    }
}
