package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Client.Client Class
public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (!socket.isClosed()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String msgFromGroupChat = bufferedReader.readLine();
                    System.out.println(msgFromGroupChat);
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);

                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if ((bufferedWriter != null)) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("[SERVER] Enter your username to enter the server: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localHost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}