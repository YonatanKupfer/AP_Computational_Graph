////package AP_project.test;
//package AP_project.server;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class RequestParser {
//
//    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
//		// implement
//        String httpCommand = null;
//        String uri = null;
//        String[] uriSegments = null;
//        Map<String, String> parameters = new HashMap<>();
//        byte[] content = null;
//
//        // Read the request line
//        String requestLine = reader.readLine();
//        System.out.println("Request line: " + requestLine);
//        if (requestLine != null && !requestLine.isEmpty()) {
//            String[] requestParts = requestLine.split(" ");
//            if (requestParts.length >= 3) {
//                httpCommand = requestParts[0];
//                uri = requestParts[1];
//                String httpVersion = requestParts[2]; // Not used but can be stored if needed
//                System.out.println("Parsed request - Command: " + httpCommand + ", URI: " + uri + ", Version: " + httpVersion);
//            }
//
//            // Parse the URI and parameters
//            String[] uriParts = uri.split("\\?");
//            uriSegments = uriParts[0].substring(1).split("/");
//            if (uriParts.length > 1) {
//                String[] paramPairs = uriParts[1].split("&");
//                for (String pair : paramPairs) {
//                    String[] keyValue = pair.split("=");
//                    if (keyValue.length == 2) {
//                        parameters.put(keyValue[0], keyValue[1]);
//                    }
//                }
//            }
//        }
//
//        // Read the headers
//        String line;
//        int contentLength = 0;
//        while ((line = reader.readLine()) != null && !line.isEmpty()) {
//            String[] headerParts = line.split(": ");
//            if (headerParts.length == 2) {
//                String headerName = headerParts[0];
//                String headerValue = headerParts[1];
//                if (headerName.equalsIgnoreCase("Content-Length")) {
////                    contentLength = Integer.parseInt(headerValue);
//                    // ignoring
//                    contentLength = 1;
//                }
//            }
//        }
//
//        // Read the parameters after headers
//        while ((line = reader.readLine()) != null && !line.isEmpty()) {
//            String[] keyValue = line.split("=");
//            if (keyValue.length == 2) {
//                parameters.put(keyValue[0], keyValue[1]);
//            }
//        }
//
//        // Read the content
////        if (contentLength > 0) {
////            char[] contentChars = new char[contentLength];
////            reader.read(contentChars, 0, contentLength);
////            content = new String(contentChars).getBytes();
////        }
//
//        // Read the content until the next empty line
//        StringBuilder contentBuilder = new StringBuilder();
//        while ((line = reader.readLine()) != null && !line.isEmpty()) {
//            contentBuilder.append(line).append("\n");
//        }
//        content = contentBuilder.toString().getBytes();
//
//        System.out.println("Parsed parameters: " + parameters);
//
//        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
//    }
//
//
//	// RequestInfo given internal class
//    public static class RequestInfo {
//        private final String httpCommand;
//        private final String uri;
//        private final String[] uriSegments;
//        private final Map<String, String> parameters;
//        private final byte[] content;
//
//        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
//            this.httpCommand = httpCommand;
//            this.uri = uri;
//            this.uriSegments = uriSegments;
//            this.parameters = parameters;
//            this.content = content;
//        }
//
//        public String getHttpCommand() {
//            return httpCommand;
//        }
//
//        public String getUri() {
//            return uri;
//        }
//
//        public String[] getUriSegments() {
//            return uriSegments;
//        }
//
//        public Map<String, String> getParameters() {
//            return parameters;
//        }
//
//        public byte[] getContent() {
//            return content;
//        }
//    }
//}

package AP_project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

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

    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final Map<String, String> headers;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, Map<String, String> headers, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.headers = headers;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public byte[] getContent() {
            return content;
        }
    }
}

