package core2Lesson06;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleSingleConsoleServer {
    private static final int PORT = 8189;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread serverConsoleThread;

    public static void main(String[] args) {
        new SimpleSingleConsoleServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started");
            waitForConnection(serverSocket);

            startConsoleThread();

            while (true) {
                var message = in.readUTF();
                if (message.startsWith("/end")) {
                    shutdown();
                    break;
                }
                System.out.println("Received: " + message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                shutdown();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void shutdown() throws IOException {
        if (serverConsoleThread.isAlive()) {
            serverConsoleThread.interrupt();
        }
        if (socket != null) {
            socket.close();
        }
        System.out.println("Server stopped");
    }

    private void startConsoleThread() {
        serverConsoleThread = new Thread(() -> {
           try(var reader = new BufferedReader(new InputStreamReader(System.in))) {
               System.out.println("Enter message for client >>>>> ");
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

        serverConsoleThread.start();
    }

    private void waitForConnection(ServerSocket serverSocket) throws IOException {
        System.out.println("Waiting for connection...");
        var socket = serverSocket.accept(); // blocking method
        System.out.println("Client connected");

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }
}
