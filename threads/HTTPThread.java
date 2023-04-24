package threads;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.nio.file.Files;


public class HTTPThread extends Thread {
    enum HTTPMethod {
        GET, POST, PUT, DELETE, HEAD, OPTIONS
    }

    enum StatusCode {
        OK,
        CREATED,
        NOT_FOUND,
        UNSUPPORTED_MEDIA_TYPE,
        INTERNAL_SERVER_ERROR;

        public String toString() {
            switch (this) {
                case OK:
                    return "200 OK";
                case CREATED:
                    return "201 Created";
                case UNSUPPORTED_MEDIA_TYPE:
                    return "415 Unsupported Media Type";
                case NOT_FOUND:
                    return "404 Not Found";
                case INTERNAL_SERVER_ERROR:
                    return "500 Internal Server Error";
                default:
                    return "500 Internal Server Error";
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger(HTTPThread.class.getName());
    private String name;
    private Socket socket;
    private final String CRLF = "\r\n";

    HTTPMethod method;
    String uri;
    String version;
    StringBuilder body;
    Map<String, String> headers;

    public HTTPThread(Level level, Socket socket) {
        this.name = "[" + socket.getInetAddress()+ "]";
        this.socket = socket;
        LOGGER.setLevel(level);

        this.method = null;
        this.uri = "";
        this.version = "";
        this.headers = new HashMap<>();
        this.body = new StringBuilder();

        LOGGER.info(this.name + " CONNECTED...");
    }

    public void run() {
        try {
            LOGGER.info(this.name + " PARSE - STARTLINE");
            InputStream is = this.socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    
            String line;

            // handle startline
            while ((line = reader.readLine()) != null) {
                if (line.equals(CRLF)) {   // keep going until you find data
                    continue;
                } else {
                    String[] startLineTokens = line.split(" ");
    
                    if (startLineTokens.length != 3) {
                        LOGGER.severe(this.name + " INVALID REQUEST");
                        return;
                    }

                    this.method = HTTPMethod.valueOf(startLineTokens[0]);
                    this.uri = startLineTokens[1];
                    this.version = startLineTokens[2];
    
                    break;
                }
            }
            
            LOGGER.info(this.name + " PARSE - HEADERS");
            // // handle headers
            while ((line = reader.readLine()) != null) {
                int separatorIndex = line.indexOf(":");

                if (line.length() == 0) {
                    break;
                } else {
                    this.headers.put(
                        line.substring(0, separatorIndex).trim(),
                        line.substring(separatorIndex + 1).trim()
                    );
                }
            }

            LOGGER.info(this.name + " PARSE - BODY");
            int contentLength = Integer.parseInt(this.headers.get("Content-Length"));
            int readChar;
            // handle body
            while (contentLength != 0 && (readChar = reader.read()) != -1) {
                char c = (char) readChar;
                this.body.append(c);
                contentLength--;
            }

            LOGGER.info(this.name + " PARSE - DONE");
    
            switch (this.method) {
                case GET:
                    get();
                    break;
                case POST:
                    post();
                    break;
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
                    throw new Error("Invalid HTTP Method: " + this.method);
            }
            this.socket.close();

            LOGGER.info(this.name + " GOODBYE!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void get() {
        LOGGER.info(this.name + " GET");
        Map<String, String> responseHeaders = new HashMap<>();
        String contentType = this.headers.get("Content-Type");
        File file;
        byte[] fileBytes;

        try {
            switch (contentType) {
                case "text/plain":
                    file = new File("./texts" + this.uri);
                    fileBytes = Files.readAllBytes(file.toPath());

                    responseHeaders.put("Content-Type", contentType);
                    responseHeaders.put("Content-Length", Integer.toString(fileBytes.length));

                    respond(StatusCode.OK, fileBytes, responseHeaders);
                    break;
                case "image/jpeg":
                    file = new File("./images" + this.uri);
                    fileBytes = Files.readAllBytes(file.toPath());

                    responseHeaders.put("Content-Type", contentType);
                    responseHeaders.put("Content-Length", Integer.toString(fileBytes.length));

                    respond(StatusCode.OK, fileBytes, responseHeaders);
                    break;
                default:
                    String response = "Unsupported Media Type: " + contentType;
    
                    responseHeaders.put("Content-Type", contentType);
                    responseHeaders.put("Content-Length", Integer.toString(response.length()));
    
                    respond(StatusCode.UNSUPPORTED_MEDIA_TYPE, response.getBytes(), responseHeaders);
                    break;
            }
        } catch (FileNotFoundException e) {
            String response = "File not found: " + this.uri;

            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", Integer.toString(response.length()));

            respond(StatusCode.NOT_FOUND, response.getBytes(), responseHeaders);
        } catch (IOException e) {
            String response = "Internal Server Error: " + e.getMessage();

            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", Integer.toString(response.length()));

            respond(StatusCode.INTERNAL_SERVER_ERROR, response.getBytes(), responseHeaders);
        }
    }

    private void post() {
        LOGGER.info(this.name + " POST");

        Map<String, String> responseHeaders = new HashMap<>();

        if (!this.headers.get("Content-Type").equals("text/plain")) {   // no support for other types
            String response = "Unsupported Media Type: " + this.headers.get("Content-Type");

            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", Integer.toString(response.length()));

            respond(StatusCode.UNSUPPORTED_MEDIA_TYPE, response.getBytes(), responseHeaders);
            return;
        }

        File file;
        byte[] fileBytes;

        try {
            file = new File("./texts" + this.uri);
            fileBytes = Files.readAllBytes(file.toPath());

            byte[] newBytes = (this.body.toString() + '\n').getBytes();
            byte[] combined = new byte[fileBytes.length + newBytes.length];

            System.arraycopy(fileBytes, 0, combined, 0, fileBytes.length);
            System.arraycopy(newBytes, 0, combined, fileBytes.length, newBytes.length);

            FileWriter fw = new FileWriter(file);
            fw.write(new String(combined));

            fw.close();

            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", Integer.toString(combined.length));

            respond(StatusCode.OK, combined, responseHeaders);
        } catch (FileNotFoundException e) {
            String response = "File not found: " + this.uri;

            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", Integer.toString(response.length()));

            respond(StatusCode.NOT_FOUND, response.getBytes(), responseHeaders);
        } catch (IOException e) {
            String response = e.getMessage();

            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", Integer.toString(response.length()));

            respond(StatusCode.INTERNAL_SERVER_ERROR, response.getBytes(), responseHeaders);
        }
    }

    public void respond(StatusCode code, byte[] payload, Map<String, String> responseHeaders) {
        try {
            StringBuilder response = new StringBuilder();
            // handle start-line
            response.append("HTTP/1.1 " + code.toString());
            response.append(CRLF);

            // handle response headers
            if (responseHeaders != null) {
                for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
                    response.append(entry.getKey() + ": " + entry.getValue());
                    response.append(CRLF);
                }
        
                response.append(CRLF);
            }

            // send response
            OutputStream os = this.socket.getOutputStream();
            os.write(response.toString().getBytes());
            os.write(payload);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
