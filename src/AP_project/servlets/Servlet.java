package AP_project.servlets;

import AP_project.server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The Servlet interface defines the methods that a servlet must implement.
 */
public interface Servlet {
    /**
     * Handles an HTTP request and sends a response.
     * @param ri The request information.
     * @param toClient The output stream to write the response to.
     * @throws IOException If an I/O error occurs.
     */
    void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Closes any resources associated with the servlet.
     * @throws IOException If an I/O error occurs.
     */
    void close() throws IOException;
}
