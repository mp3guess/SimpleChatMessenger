package ivantamrazov;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class ChatApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        log.info("Starting chat application...");

        System.out.println("Do you want to start a new chat or join an existing one?");
        System.out.println("1. Start a new chat");
        System.out.println("2. Join an existing chat");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> startNewChat();
            case 2 -> joinExistingChat();
            default -> log.warn("Invalid choice. Choose 1 or 2.");
        }
    }

    public static void startNewChat() {
        System.out.println("Enter port number for the new chat:");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        scanner.nextLine();

        try {
            log.info("Starting new chat on port {}", port);
            ChatServer.startServer(port);
        } catch (IOException e) {
            log.error("Error starting the server: {}", e.getMessage());
        }
    }

    public static void joinExistingChat() {
        System.out.println("Enter the port to connect to:");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        log.info("Connecting to chat on port {} with username {}", port, username);
        ChatClient.connectToChat(port, username);
    }
}