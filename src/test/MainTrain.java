//package test;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class MainTrain { // RequestParser
//
//
//    private static void testParseRequest() {
//        // Test data
//        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
//                            "Host: example.com\n" +
//                            "Content-Length: 5\n"+
//                            "\n" +
//                            "filename=\"hello_world.txt\"\n"+
//                            "\n" +
//                            "hello world!\n"+
//                            "\n" ;
//
//        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
//        try {
//            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);
//
//            // Test HTTP command
//            if (!requestInfo.getHttpCommand().equals("GET")) {
//                System.out.println("HTTP command test failed (-5)");
//            }
//
//            // Test URI
//            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
//                System.out.println("URI test failed (-5)");
//            }
//
//            // Test URI segments
//            String[] expectedUriSegments = {"api", "resource"};
//            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
//                System.out.println("URI segments test failed (-5)");
//                for(String s : requestInfo.getUriSegments()){
//                    System.out.println(s);
//                }
//            }
//            // Test parameters
//            Map<String, String> expectedParams = new HashMap<>();
//            expectedParams.put("id", "123");
//            expectedParams.put("name", "test");
//            expectedParams.put("filename","\"hello_world.txt\"");
//            if (!requestInfo.getParameters().equals(expectedParams)) {
//                System.out.println("Parameters test failed (-5)");
//            }
//
//            // Test content
////            byte[] expectedContent = "hello world!\n".getBytes();
////            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
////                System.out.println("Content test failed (-5)");
////            }
//
//
//            // Test content
//            byte[] expectedContent = "hello world!\n".getBytes();
//            byte[] actualContent = requestInfo.getContent();
//
//            if (!Arrays.equals(actualContent, expectedContent)) {
//                System.out.println("Content test failed (-5)");
//                System.out.println("Expected content: " + new String(expectedContent));
//                System.out.println("Actual content: " + new String(actualContent));
//                System.out.println("Expected content bytes: " + Arrays.toString(expectedContent));
//                System.out.println("Actual content bytes: " + Arrays.toString(actualContent));
//            }
//
//
//            input.close();
//        } catch (IOException e) {
//            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
//        }
//    }
//
//    private static class MockServlet implements Servlet {
//        private final String name;
//
//        public MockServlet(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
//            String response = name + " handled " + ri.getHttpCommand() + " " + ri.getUri();
//            toClient.write(("HTTP/1.1 200 OK\nContent-Length: " + response.length() + "\n\n" + response).getBytes());
//            toClient.flush();
//            toClient.close(); // Ensure the output stream is properly closed
//        }
//
//        @Override
//        public void close() throws IOException {
//            // No cleanup necessary for mock
//        }
//    }
//    public static void testServer() throws Exception{
//        // Initialize server
//        HTTPServer server = new MyHTTPServer(8080, 5);
//        server.addServlet("GET", "/publish", new MockServlet("TopicDisplayer"));
//        server.addServlet("POST", "/upload", new MockServlet("ConfLoader"));
//        server.addServlet("GET", "/app/", new MockServlet("HtmlLoader"));
//
//        // Start server in a background thread
//        server.start();
//
//        // Allow server to start
//        Thread.sleep(1000);
//
//        // Simulate client requests
//        testClientRequest("GET /publish HTTP/1.1\nHost: localhost\n\n", "TopicDisplayer handled GET /publish");
//        testClientRequest("POST /upload HTTP/1.1\nHost: localhost\n\n", "ConfLoader handled POST /upload");
//        testClientRequest("GET /app/index.html HTTP/1.1\nHost: localhost\n\n", "HtmlLoader handled GET /app/index.html");
//
//        // Stop server
//        server.close();
//    }
//
//    private static void testClientRequest(String request, String expectedResponse) throws IOException {
//        System.out.println("Sending request: " + request);
//        Socket socket = new Socket("localhost", 8080);
//        socket.getOutputStream().write(request.getBytes());
//
//        // Write the request to the server
//        socket.getOutputStream().write(request.getBytes());
//        socket.getOutputStream().flush();
//        socket.shutdownOutput(); // Properly signal the end of the request
//
//        BufferedReader responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        StringBuilder responseBuilder = new StringBuilder();
//        String line;
//        while ((line = responseReader.readLine()) != null) {
//            responseBuilder.append(line).append("\n"); // Append newlines to maintain response format
//        }
//
//
//        String response = responseBuilder.toString().trim(); // Trim to remove trailing newlines
//        System.out.println("Received response: " + response);
//        if (!response.contains(expectedResponse)) {
//            System.out.println("Request test failed (-20)");
//            System.out.println("Expected response: " + expectedResponse);
//            System.out.println("Actual response: " + response);
//        }
//
//        socket.close();
//    }
//
//    public static void main(String[] args) {
//        testParseRequest(); // 40 points
//        try{
//            testServer(); // 60
//        }catch(Exception e){
//            System.out.println("your server throwed an exception (-60)");
//        }
//        System.out.println("done");
//    }
//
//}
