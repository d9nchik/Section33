package sample.exercise1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoanController {
    // IO streams
    private DataOutputStream toServer = null;
    private DataInputStream fromServer = null;

    @FXML
    private TextField rate, years, amount;

    @FXML
    private TextArea console;

    @FXML
    public void submit() {
        double rateValue = Double.parseDouble(rate.getText());
        int yearsValue = Integer.parseInt(years.getText());
        double amountValue = Double.parseDouble(amount.getText());

        if (toServer == null) {
            try {
                Socket socket = new Socket("127.0.0.1", 8000);
                fromServer = new DataInputStream(socket.getInputStream());
                toServer = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                console.appendText(e.getMessage() + "\n");
            }
        }

        if (toServer != null) {
            new Thread(() -> {
                try {
                    toServer.writeDouble(rateValue);
                    toServer.writeInt(yearsValue);
                    toServer.writeDouble(amountValue);
                    toServer.flush();
                    double result = fromServer.readDouble();
                    Platform.runLater(() -> console.appendText("Got value: " + result + "\n"));
                } catch (IOException e) {
                    console.appendText(e.getMessage() + "\n");
                }

            }).start();
        }
    }
}
