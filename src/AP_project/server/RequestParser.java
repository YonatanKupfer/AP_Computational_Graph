package AP_project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to parse HTTP requests.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from the given BufferedReader.
     * @param reader The BufferedReader to read the request from.
     * @return A RequestInfo object containing parsed request details.
     * @throws IOException If an I/O error occurs.
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String httpCommand = null;
        String uri = null;
        String[] uriSegments = null;
        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        byte[] content = null;

        // Read the request line
        String requestLine = reader.readLine();
        System.out.println("Request line: " + requestLine);
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length >= 3) {
                httpCommand = requestParts[0];
                uri = requestParts[1];
                String httpVersion = requestParts[2];
                System.out.println("Parsed request - Command: " + httpCommand + ", URI: " + uri + ", Version: " + httpVersion);
            }

            // Parse the URI and parameters
            String[] uriParts = uri.split("\\?");
            uriSegments = uriParts[0].substring(1).split("/");
            if (uriParts.length > 1) {
                String[] paramPairs = uriParts[1].split("&");
                for (String pair : paramPairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        parameters.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }

        // Read the headers
        String line;
        int contentLength = 0;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                String headerName = headerParts[0];
                String headerValue = headerParts[1];
                headers.put(headerName, headerValue);
                if (headerName.equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(headerValue);
                }
            }
        }

        // Read the content
        if (contentLength > 0) {
            char[] contentChars = new char[contentLength];
            reader.read(contentChars, 0, contentLength);
            content = new String(contentChars).getBytes();
        }

        System.out.println("Parsed parameters: " + parameters);
        System.out.println("Parsed headers: " + headers);

        return new RequestInfo(httpCommand, uri, uriSegments, parameters, headers, content);
    }

    /**
     * A class to store parsed HTTP request information.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final Map<String, String> headers;
        private final byte[] content;

        /**
         * Constructs a RequestInfo object with the specified details.
         * @param httpCommand The HTTP command (e.g., GET, POST).
         * @param uri The URI of the request.
         * @param uriSegments The segments of the URI path.
         * @param parameters The request parameters.
         * @param headers The request headers.
         * @param content The request content.
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, Map<String, String> headers, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.headers = headers;
            this.content = content;
        }

        /**
         * Returns the HTTP command.
         * @return The HTTP command.
         */
        public String getHttpCommand() {
            return httpCommand;
        }

        /**
         * Returns the URI of the request.
         * @return The URI.
         */
        public String getUri() {
            return uri;
        }

        /**
         * Returns the segments of the URI path.
         * @return The URI segments.
         */
        public String[] getUriSegments() {
            return uriSegments;
        }

        /**
         * Returns the request parameters.
         * @return The parameters as a map.
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * Returns the request headers.
         * @return The headers as a map.
         */
        public Map<String, String> getHeaders() {
            return headers;
        }

        /**
         * Returns the request content.
         * @return The content as a byte array.
         */
        public byte[] getContent() {
            return content;
        }
    }
}
