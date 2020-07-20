package sample.exercise9;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatServerController {
    @FXML
    private TextArea clientText, serverText;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    @FXML
    public void initialize() {
        new Thread(() -> {
            try {
                ServerSocket socketServer = new ServerSocket(5050);
                Socket socket = socketServer.accept();
                inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                new Thread(this::updater).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            try {
                outputStream.writeUTF(serverText.getText());
                outputStream.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void updater() {
        try {
            while (true) {
                clientText.setText(inputStream.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
