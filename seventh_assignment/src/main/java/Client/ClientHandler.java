package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static ArrayList<String> chatHistory = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private boolean isOnline;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            isOnline = false;
            broadcastMessage("[SERVER] " + clientUsername + " has entered the server.");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    @Override
    public void run() {
        String request;
        while (!socket.isClosed()) {
            try {
                bufferedWriter.write("--Menu--\n~Chat\n~Download\n~Exit");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                request = bufferedReader.readLine();
                switch (request.toLowerCase()) {
                    case "chat":
                        chat();
                        break;
                    case "download":
                        downloadFiles();
                        break;
                    case "exit":
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        // ToDo debug
                        break;
                    default:
                        bufferedWriter.write("[SERVER] Invalid input: please choose from the menu");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }

        }
    }

    private void downloadFiles() {
    }


    public void chat() {

        try {
            isOnline = true;
            bufferedWriter.write("[SERVER] You have entered the group chat.");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String messageFromClient;
        showChatHistory(chatHistory.size());
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient.equalsIgnoreCase("exit")) {
                    isOnline = false;
                    bufferedWriter.write("[SERVER] You have left the group chat.");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                }
                broadcastMessage(clientUsername + ": " + messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }


    private void showChatHistory(int size) {
        for (String msg : chatHistory) {
            if (chatHistory.indexOf(msg) < chatHistory.size() - size) {
                continue;
            }
            try {
                bufferedWriter.write(msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        chatHistory.add(messageToSend);
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername) && clientHandler.isOnline) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("[SERVER] " + clientUsername + " has left the chat.");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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
}
