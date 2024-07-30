
package AP_project;

import AP_project.server.HTTPServer;
import AP_project.server.MyHTTPServer;
import AP_project.servlets.ConfLoader;
import AP_project.servlets.TopicDisplayer;
import AP_project.servlets.HtmlLoader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        HTTPServer server = new MyHTTPServer(8080, 5);

        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader("src"));
        server.addServlet("GET", "/app/", new HtmlLoader("src/html_files"));

        server.start();

        System.in.read();
        server.close();
    }
}