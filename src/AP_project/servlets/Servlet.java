//package AP_project.test;
package AP_project.servlets;


import AP_project.server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;

public interface Servlet {
    void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException;
    void close() throws IOException;
}
