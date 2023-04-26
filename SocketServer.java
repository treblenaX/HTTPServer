import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import threads.*;

public class SocketServer {
    private static final Logger LOGGER = Logger.getLogger(SocketServer.class.getName());
    private static int PORT = 80;

    public static void main(String[] args) {
        String flag = (args.length > 0) ? args[0].toLowerCase() : "";

        switch (flag) {
            case "-i":
                LOGGER.setLevel(Level.INFO);
                printLogLevel();
                break;
            default:
                LOGGER.setLevel(Level.SEVERE);
                break;
        }

        LOGGER.info("CREATED - ExecutorService");
        ExecutorService executor = Executors.newFixedThreadPool(5);

        try {
            boolean isRunning = true;
            ServerSocket serverSocket = new ServerSocket(PORT);

            LOGGER.info("EXECUTE - TCPThread");
            do {
                Socket client = serverSocket.accept();
                executor.execute(new HTTPThread(LOGGER.getLevel(), client));
            } while (isRunning);

            serverSocket.close();
        } catch (IOException e) {

        }
    }

    private static void printLogLevel() {
        System.out.println("Log level: " + LOGGER.getLevel());
    }
}