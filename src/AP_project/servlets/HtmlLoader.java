package AP_project.servlets;

import AP_project.server.RequestParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HtmlLoader implements Servlet {
    private final String htmlFolder;

    public HtmlLoader(String htmlFolder) {
        this.htmlFolder = htmlFolder;
    }

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        String uri = ri.getUri();
        System.out.println("Requested URI: " + uri);

        if (uri.startsWith("/app/")) {
            String fileName = uri.substring("/app/".length());
            if (fileName.isEmpty() || fileName.equals("/")) {
                fileName = "index.html";
            }
            Path filePath = Paths.get(htmlFolder, fileName);
            System.out.println("Resolved file path: " + filePath.toAbsolutePath());

            if (Files.exists(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                String contentType = getContentType(fileName);
                String header = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\nContent-Length: " + content.length + "\r\n\r\n";
                toClient.write(header.getBytes());
                toClient.write(content);
                System.out.println("Served file: " + fileName);
            } else {
                sendNotFoundResponse(toClient);
                System.out.println("File not found: " + filePath.toAbsolutePath());
            }
        } else {
            sendNotFoundResponse(toClient);
            System.out.println("URI does not start with /app/: " + uri);
        }
        toClient.flush();
        toClient.close();
    }

    /**
     * Determines the content type based on the file extension.
     * @param fileName The name of the file.
     * @return The content type as a string.
     */
    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else {
            return "text/plain";
        }
    }

    /**
     * Sends a 404 Not Found response to the client.
     * @param toClient The OutputStream to write the response to.
     * @throws IOException If an I/O error occurs.
     */
    private void sendNotFoundResponse(OutputStream toClient) throws IOException {
        String notFound = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<html><body><h1>404 Not Found</h1></body></html>";
        toClient.write(notFound.getBytes());
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }
}
