package ivantamrazov;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Slf4j
public class ChatServer {
    private static final Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void startServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Chat started on port " + port);

        new Thread(ChatServer::handleServerMessages).start();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clientHandlers.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    private static void handleServerMessages() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for this chat:");
        String serverUsername = scanner.nextLine();

        while (true) {
            String message = scanner.nextLine();
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String fullMessage = timestamp + " [" + serverUsername + "]: " + message;
            System.out.println(fullMessage);
            broadcast(fullMessage, null);
        }
    }

    static void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeUser) {
                client.sendMessage(message);
            }
        }
    }

    static void logEvent(String event) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        System.out.println(timestamp + " - " + event);
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);


                this.username = in.readLine();
                logEvent(username + " has joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    String fullMessage = timestamp + " [" + username + "]: " + message;

                    logEvent(username + " sent a message: " + message);
                    System.out.println(fullMessage);
                    ChatServer.broadcast(fullMessage, this);
                }
            } catch (IOException e) {
                logEvent("Error with user " + username + ": " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    logEvent(username + " has left the chat.");
                } catch (IOException e) {
                    logEvent("Error closing socket for " + username + ": " + e.getMessage());
                }
            }
        }

        void sendMessage(String message) {
            out.println(message);
        }
    }
}