package sample.exercise4;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class CountClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Text text = new Text();
        try {
            Socket socket = new Socket("localhost", 5050);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            text.setText("You are visitor " + inputStream.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final StackPane pane = new StackPane(text);
        pane.getStylesheets().add("/sample/exercise4/text.css");
        primaryStage.setScene(new Scene(pane, 300, 100));
        primaryStage.setTitle("Exercise 4");
        primaryStage.show();
    }
}
