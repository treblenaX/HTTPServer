package threads;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class HTTPThread extends Thread {
    enum HTTPMethod {
        GET, POST, PUT, DELETE, HEAD, OPTIONS
    }

    private static final Logger LOGGER = Logger.getLogger(HTTPThread.class.getName());
    private String name;
    private Socket socket;
    private final String CRLF = System.getProperty("line.separator").toString();

    HTTPMethod method;
    String uri;
    String version;
    Map<String, String> headers;

    public HTTPThread(Level level, Socket socket) {
        this.name = "[" + socket.getInetAddress()+ "]";
        this.socket = socket;
        LOGGER.setLevel(level);

        this.method = null;
        this.uri = "";
        this.version = "";
        this.headers = new HashMap<>();

        LOGGER.info(this.name + " CONNECTED...");
    }

    public void run() {
        try {
            parseClientRequest(this.socket);
            String payload = handleClientRequest();
            sendResponse(this.socket, payload);
            LOGGER.info(this.name + " CLOSING SOCKET!");
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseClientRequest(Socket socket) {
        try {
            LOGGER.info(this.name + " PARSING CLIENT REQUEST...");
            InputStream input = socket.getInputStream();
            String inputLine;
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // startline data
            boolean handleStartLine = true;

            // handle startline
            while (handleStartLine && (inputLine = reader.readLine()) != null) {
                if (inputLine.equals(CRLF)) {   // keep going until you find data
                    continue;
                } else {
                    String[] startLineTokens = inputLine.split(" ");

                    if (startLineTokens.length != 3) {
                        LOGGER.severe(this.name + " INVALID REQUEST");
                        return;
                    }
                    this.method = HTTPMethod.valueOf(startLineTokens[0]);
                    this.uri = startLineTokens[1];
                    this.version = startLineTokens[2];

                    handleStartLine = false;
                }
            }

            // handle headers
            boolean handleHeaders = true;
            
            while (handleHeaders && (inputLine = reader.readLine()) != null) {
                String[] headerTokens = inputLine.split(":");

                if (inputLine.equals(CRLF) || headerTokens.length != 2) {
                    handleHeaders = false;
                } else {
                    this.headers.put(headerTokens[0].trim(), headerTokens[1].trim());
                }
            }
            LOGGER.info(this.name + " DONE CLIENT REQUEST PARSE...");
        } catch (IOException e) {
            LOGGER.severe(this.name + " " + e.toString());
        }
    }

    private String handleClientRequest() {
        LOGGER.info(this.name + " HANDLING CLIENT REQUEST...");
        String response = "";

        try {
            switch (this.method) {
                case GET:
                    response = handleGET();
                    break;
                // case POST:
                //     response = handlePOST();
                //     break;
                // case PUT:
                //     response = handlePUT();
                //     break;
                // case DELETE:
                //     response = handleDELETE();
                //     break;
                // case HEAD:
                //     response = handleHEAD();
                //     break;
                // case OPTIONS:
                //     response = handleOPTIONS();
                //     break;
                default:
                    response = "HTTP/1.1 405 Method Not Allowed" + CRLF + CRLF;
                    break;
            }
        } catch (Exception e) {
            response = "HTTP/1.1 500 Internal Server Error" + CRLF + CRLF;
        }

        return response;
    }

    private String handleGET() {
        LOGGER.info(this.name + " processing GET request...");
        String response = "";
        String contentType = this.headers.get("Content-Type");

        try {
            switch (contentType) {
                case "text/plain":     // text/plain
                    File file = new File("." + this.uri);
                    System.out.println(file.getAbsolutePath());
                    Scanner scanner = new Scanner(file);


                    response += scanner.nextLine();

                    while (scanner.hasNextLine()) {
                        response += '\n';
                        response += scanner.nextLine();
                    }
                    break;
                default:
                    response = "HTTP/1.1 415 Unsupported Media Type" + CRLF + CRLF;
                    break;
            }
        } catch (Exception e) {
            System.out.println(e);
            response = "HTTP/1.1 500 Internal Server Error" + CRLF + CRLF;
        }

        return response;
    }

    private void sendResponse(Socket socket, String payload) {
        try {
            StringBuilder response = new StringBuilder();

            // handle start-line
            response.append("HTTP/1.1 200 OK" + CRLF);

            // handle response headers
            response.append("Content-Type: " + this.headers.get("Content-Type"));
            response.append("Content-Length: " + payload.length() + CRLF + CRLF);

            // handle response payload
            response.append(payload);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.print(response.toString());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe(this.name + " " + e.toString());
        }
    }
}
