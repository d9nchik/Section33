package sample.exercise1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoanServer extends Application {

    private final TextArea ta = new TextArea();// Text area for displaying contents

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("Server"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        new Thread(() -> {
            try {
                ExecutorService executor = Executors.newCachedThreadPool();
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(() -> ta.appendText("Server started at " + new Date() + "\n"));
                while (true) {
                    // Listen for a connection request
                    final Socket accept = serverSocket.accept();
                    executor.execute(new HandleConnection(accept));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class HandleConnection implements Runnable {
        private final Socket socket;

        public HandleConnection(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            InetAddress inetAddress = socket.getInetAddress();
            ta.appendText("Client's host name is " +
                    inetAddress.getHostName() + "\n");
            ta.appendText("Client's IP Address is " +
                    inetAddress.getHostAddress() + "\n");
            try {

                socket.setSoTimeout(300_000);//timeout after 5 minute
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputFromServer = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    double amount = inputFromClient.readDouble();
                    Platform.runLater(() -> ta.appendText(inetAddress.getHostAddress() + ": amount=" + amount + "\n"));
                    int years = inputFromClient.readInt();
                    Platform.runLater(() -> ta.appendText(inetAddress.getHostAddress() + ": years=" + years + "\n"));
                    double annualRate = inputFromClient.readDouble();
                    Platform.runLater(() -> ta.appendText(inetAddress.getHostAddress() + ": annualRate=" + annualRate + "\n"));
                    final double result = amount * Math.pow(1 + annualRate / 1200, 12 * years);
                    outputFromServer.writeDouble(result);
                    Platform.runLater(() -> ta.appendText(inetAddress.getHostAddress() + ": result=" + result + "\n"));
                    Thread.yield();
                }

            } catch (SocketTimeoutException ex) {
                Platform.runLater(() -> ta.appendText("Client with IP Address " + inetAddress.getHostAddress() +
                        "disconnected due to timeout\n"));
            } catch (EOFException e) {
                Platform.runLater(() -> ta.appendText("Client with IP Address " + inetAddress.getHostAddress() +
                        "disconnected\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
