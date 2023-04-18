package client;

import java.io.*;
import java.net.Socket;

public class Client extends Thread {
    private final Main main;

    private final Socket socket;

    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;

    public Client(Main main, String address, int port) {
        this.main = main;

        try {
            this.socket = new Socket(address, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        String message;

        while (true) {
            try {
                if ((message = socketReader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            main.addMessage(message);

            System.out.println(message);
        }
    }

    public void sendMessage(String message) {
        socketWriter.println(message);
    }
}
