package core2Lesson06;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class SimpleSingleConsoleClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8189;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread clientConsoleThread;

    public static void main(String[] args) {
        new SimpleSingleConsoleClient().start();
    }

    public void start() {
        try (var socket  = new Socket(HOST, PORT)) {
            System.out.println("Connected to server");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            startConsoleThread();

            while (true) {
                var message = in.readUTF();
                if (message.equalsIgnoreCase("/quit")) {
                    out.writeUTF("/end");
                    shutdown();
                }
                System.out.println("Received: " + message);
            }

        } catch (SocketException e) {
            System.out.println("Connection to server has been lost");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown() throws IOException {
        if (clientConsoleThread.isAlive()) {
            clientConsoleThread.interrupt();
        }
        System.out.println("Server stopped");
    }

    private void startConsoleThread() {
        clientConsoleThread = new Thread(() -> {
           try(var reader = new BufferedReader(new InputStreamReader(System.in))) {
               System.out.println("Enter message for server >>>>> ");
               while (!Thread.currentThread().isInterrupted()) {
                   if (reader.ready()) {
                       var message = reader.readLine();
                       out.writeUTF(message);
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
        });

        clientConsoleThread.start();
    }

    private void waitForConnection(ServerSocket serverSocket) throws IOException {
        System.out.println("Waiting for connection...");
        var socket = serverSocket.accept(); // blocking method
        System.out.println("Client connected");

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }
}
