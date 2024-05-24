package Server;

import Client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Server.Server Class
public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            System.out.println("[SERVER] Server started successfully.");
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("[SERVER] A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}