package threads;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.nio.file.Files;


public class HTTPThread extends Thread {
    enum StatusCode {
        OK,
        CREATED,
        NO_CONTENT,
        NOT_FOUND,
        UNSUPPORTED_MEDIA_TYPE,
        INTERNAL_SERVER_ERROR;

        public String toString() {
            switch (this) {
                case OK:
                    return "200 OK";
                case CREATED:
                    return "201 Created";
                case NO_CONTENT:
                    return "204 No Content";
                case UNSUPPORTED_MEDIA_TYPE:
                    return "415 Unsupported Media Type";
                case NOT_FOUND:
                    return "404 Not Found";
                default:
                    return "500 Internal Server Error";
            }
        }

        public String toHtml() {
            String[] tokensArray = this.toString().split(" ");
            String code = tokensArray[0];

            String html = "<html>" + 
                "<head><title>" + this.toString() + "</title></head>" +
                "<body>" +
                    "<img src=\"https://http.cat/" + code + "\" alt=\"" + code + "\">" +
                "</body>" +
            "</html>";

            return html;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(HTTPThread.class.getName());
    private String name;
    private Socket socket;
    private final String CRLF = "\r\n";

    String method;
    String uri;
    String version;
    StringBuilder body;
    Map<String, String> headers;

    public HTTPThread(Level level, Socket socket) {
        this.name = "[" + socket.getInetAddress()+ "]";
        this.socket = socket;
        LOGGER.setLevel(level);

        this.method = "";
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

                    this.method = startLineTokens[0];
                    this.uri = startLineTokens[1];
                    this.version = startLineTokens[2];
    
                    break;
                }
            }
            
            LOGGER.info(this.name + " PARSE - HEADERS");

            // handle headers
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

            if (this.headers.containsKey("Content-Length")) {
                LOGGER.info(this.name + " PARSE - BODY");

                int contentLength = Integer.parseInt(this.headers.get("Content-Length"));
                int readChar;
                // handle body
                while (contentLength != 0 && (readChar = reader.read()) != -1) {
                    char c = (char) readChar;
                    this.body.append(c);
                    contentLength--;
                }
            }

            LOGGER.info(this.name + " PARSE - DONE");
    
            switch (this.method) {
                case "GET":
                    get();
                    break;
                case "POST":
                    post();
                    break;
                case "PUT":
                    put();
                    break;
                case "DELETE":
                    delete();
                    break;
                case "HEAD":
                    head();
                    break;
                case "OPTIONS":
                    options();
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

        byte[] outputBytes = null;
        StatusCode code = null;
        String contentType = "";

        try {
            File file = new File("./public" + this.uri);

            if (!file.exists()) throw new FileNotFoundException();

            outputBytes = Files.readAllBytes(file.toPath());
            contentType = Files.probeContentType(file.toPath());
            code = StatusCode.OK;
        } catch (FileNotFoundException e) {
            outputBytes = (StatusCode.NOT_FOUND.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.NOT_FOUND;
        } catch (IOException e) {
            outputBytes = (StatusCode.INTERNAL_SERVER_ERROR.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.INTERNAL_SERVER_ERROR;
        }
    
        responseHeaders.put("Content-Type", contentType);
        responseHeaders.put("Content-Length", Integer.toString(outputBytes.length));

        LOGGER.info(this.name + " - " + code.toString());
        respond(code, outputBytes, responseHeaders);
    }

    private void post() {
        LOGGER.info(this.name + " POST");

        Map<String, String> responseHeaders = new HashMap<>();

        byte[] fileBytes = null;
        byte[] outputBytes = null;
        StatusCode code = null;
        String contentType = "";

        try {
            File file = new File("./public" + this.uri);

            if (!file.exists()) throw new FileNotFoundException();

            fileBytes = Files.readAllBytes(file.toPath());
            contentType = Files.probeContentType(file.toPath());
            code = StatusCode.OK;

            // check if the contentType is supported
            if (!contentType.equals("text/plain")) { throw new Exception("Unsupported Media Type"); }

            byte[] bodyBytes = ('\n' + this.body.toString()).getBytes();
            outputBytes = new byte[fileBytes.length + bodyBytes.length];

            System.arraycopy(fileBytes, 0, outputBytes, 0, fileBytes.length);
            System.arraycopy(bodyBytes, 0, outputBytes, fileBytes.length, bodyBytes.length);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(outputBytes);
            fos.close();
        } catch (FileNotFoundException e) {
            outputBytes = (StatusCode.NOT_FOUND.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.NOT_FOUND;
        } catch (IOException e) {
            outputBytes = (StatusCode.INTERNAL_SERVER_ERROR.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.INTERNAL_SERVER_ERROR;
        } catch (Exception e) {
            outputBytes = (StatusCode.UNSUPPORTED_MEDIA_TYPE.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.UNSUPPORTED_MEDIA_TYPE;
        }
    
        responseHeaders.put("Content-Type", contentType);
        responseHeaders.put("Content-Length", Integer.toString(outputBytes.length));

        LOGGER.info(this.name + " - " + code.toString());
        respond(code, outputBytes, responseHeaders);
    }

    private void put() {
        LOGGER.info(this.name + " PUT");

        Map<String, String> responseHeaders = new HashMap<>();

        byte[] outputBytes = null;
        StatusCode code = null;
        String contentType = "";

        try {
            File file = new File("./public" + this.uri);
            
            code = StatusCode.OK;

            if (!file.exists()) {   // CREATE - file does not exist
                file.createNewFile();
                code = StatusCode.CREATED;
            }

            outputBytes = this.body.toString().getBytes();
            contentType = Files.probeContentType(file.toPath());

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(outputBytes);
            fos.close();

        } catch (IOException e) {
            outputBytes = (StatusCode.INTERNAL_SERVER_ERROR.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.INTERNAL_SERVER_ERROR;
        } catch (Exception e) {
            outputBytes = (StatusCode.UNSUPPORTED_MEDIA_TYPE.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.UNSUPPORTED_MEDIA_TYPE;
        }
    
        responseHeaders.put("Content-Type", contentType);
        responseHeaders.put("Content-Length", Integer.toString(outputBytes.length));

        LOGGER.info(this.name + " - " + code.toString());
        respond(code, outputBytes, responseHeaders);
    }

    private void delete() {
        LOGGER.info(this.name + " DELETE");

        Map<String, String> responseHeaders = new HashMap<>();
        StatusCode code = null;
        File file = new File("./public" + this.uri);

        byte[] outputBytes = null;
        String contentType = "";

        try {
            if (file.exists()) {
                outputBytes = String.valueOf(file.delete()).getBytes();
                contentType = "text/plain";
                code = StatusCode.OK;
            } else {
                throw new Exception("File not found");
            }
        } catch (Exception e) {
            outputBytes = (StatusCode.NOT_FOUND.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.NOT_FOUND;
        }
    
        responseHeaders.put("Content-Type", contentType);
        responseHeaders.put("Content-Length", Integer.toString(outputBytes.length));

        LOGGER.info(this.name + " - " + code.toString());
        respond(code, outputBytes, responseHeaders);
    }

    private void head() {
        LOGGER.info(this.name + " HEAD");

        Map<String, String> responseHeaders = new HashMap<>();

        byte[] fileBytes = null;
        StatusCode code = null;
        String contentType = "";

        try {
            File file = new File("./public" + this.uri);
            fileBytes = Files.readAllBytes(file.toPath());
            contentType = Files.probeContentType(file.toPath());
            code = StatusCode.OK;
        } catch (FileNotFoundException e) {
            fileBytes = (StatusCode.NOT_FOUND.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.NOT_FOUND;
        } catch (IOException e) {
            fileBytes = (StatusCode.INTERNAL_SERVER_ERROR.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.INTERNAL_SERVER_ERROR;
        }
    
        responseHeaders.put("Content-Type", contentType);
        responseHeaders.put("Content-Length", Integer.toString(fileBytes.length));

        LOGGER.info(this.name + " - " + code.toString());
        respond(code, null, responseHeaders);
    }

    private void options() {
        LOGGER.info(this.name + " OPTIONS");

        // GET, PUT is universal, POST if text file, 

        Map<String, String> responseHeaders = new HashMap<>();

        StatusCode code = null;
        byte[] outputBytes = null;
        String contentType = "";

        try {
            File file = new File("./public" + this.uri);

            contentType = Files.probeContentType(file.toPath());

            switch (contentType) {
                case "text/plain":
                    responseHeaders.put("Allow", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
                    break;
                default:
                    responseHeaders.put("Allow", "GET, PUT, DELETE, HEAD, OPTIONS");
                    break;
            }

            contentType = Files.probeContentType(file.toPath());
            code = StatusCode.OK;
        } catch (FileNotFoundException e) {
            outputBytes = (StatusCode.NOT_FOUND.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.NOT_FOUND;
        } catch (IOException e) {
            outputBytes = (StatusCode.INTERNAL_SERVER_ERROR.toHtml()).getBytes();
            contentType = "text/html";
            code = StatusCode.INTERNAL_SERVER_ERROR;
        }
        responseHeaders.put("Content-Length", "0");

        LOGGER.info(this.name + " - " + StatusCode.OK.toString());

        respond(code, (outputBytes != null) ? outputBytes : null, responseHeaders);
    }

    private void respond(StatusCode code, byte[] payload, Map<String, String> responseHeaders) {
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
            if (payload != null) os.write(payload);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
