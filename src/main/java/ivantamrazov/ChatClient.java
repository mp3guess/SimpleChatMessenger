package ivantamrazov;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Slf4j
public class ChatClient {

    public static void connectToChat(int port, String username) {
        try {
            String hostname = "localhost";
            Socket socket = new Socket(hostname, port);
            new ReadThread(socket).start();
            new WriteThread(socket, username).start();
        } catch (IOException ex) {
            log.error("Error connecting to the server: {}", ex.getMessage());
        }
    }

    static class ReadThread extends Thread {
        private BufferedReader reader;
        private Socket socket;

        public ReadThread(Socket socket) {
            this.socket = socket;
            try {
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
            } catch (IOException e) {
                log.error("Error reading from server: {}", e.getMessage());
            }
        }

        public void run() {
            while (true) {
                try {
                    String response = reader.readLine();
                    if (response != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    log.warn("Disconnected from server.");
                    break;
                }
            }
        }
    }

    static class WriteThread extends Thread {
        private PrintWriter writer;
        private Socket socket;
        private String username;

        public WriteThread(Socket socket, String username) {
            this.socket = socket;
            this.username = username;
            try {
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
            } catch (IOException e) {
                log.error("Error writing to server: {}", e.getMessage());
            }
        }

        public void run() {
            Scanner scanner = new Scanner(System.in);
            writer.println(username);
            log.info("Connected as: {}", username);

            String text;
            while (true) {
                text = scanner.nextLine();
                String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                log.info("Sending message: [{}] {}", username, text);
                System.out.println(timestamp + " [" + username + "]: " + text);
                writer.println(text);
            }
        }
    }
}