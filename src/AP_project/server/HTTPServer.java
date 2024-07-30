package AP_project.server;

import AP_project.servlets.Servlet;

/**
 * The HTTPServer interface defines the methods that an HTTP server must implement.
 */
public interface HTTPServer extends Runnable {

    /**
     * Adds a servlet to handle requests for a specific HTTP command and URI.
     * @param httpCommand The HTTP command (e.g., GET, POST).
     * @param uri The URI for which the servlet should handle requests.
     * @param s The servlet to handle the requests.
     */
    void addServlet(String httpCommand, String uri, Servlet s);

    /**
     * Removes a servlet that handles requests for a specific HTTP command and URI.
     * @param httpCommand The HTTP command (e.g., GET, POST).
     * @param uri The URI for which the servlet should be removed.
     */
    void removeServlet(String httpCommand, String uri);

    /**
     * Starts the HTTP server.
     */
    void start();

    /**
     * Closes the HTTP server.
     */
    void close();
}
