package Controller;

import Launch.ClientLauncher;
import Net.ApplicationLayer;
import Net.Client;
import Net.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {

    final String username = System.getProperty("user.name");

    private Client cli;

    @FXML
    private TextArea taLog;

    @FXML
    private TextField tfLog;

    @FXML
    private Button btLog;

    @FXML
    private Label lblTurn;

    @FXML
    public void initialize() {

        cli = ClientLauncher.cli;

        ClientLauncher.sm.getStage().setOnCloseRequest(e -> {
            cli.sendRequest(ApplicationLayer.QUIT);
            cli.stop();
            Platform.exit();
        });

        cli.sendRequest(ApplicationLayer.START);

        new Thread(() -> {
            AtomicBoolean running = new AtomicBoolean(true);

            while (running.get()) {

                Message m = cli.listenMessage();

                switch (m.getAl()) {
                    case TURN:
                        Platform.runLater(() -> {
                            btLog.setDisable(false);
                            tfLog.setDisable(false);
                            lblTurn.setText("It's your turn");
                        });
                        break;
                    case ACTION:
                        Platform.runLater(() -> {
                            taLog.appendText("Last Word : " + m.getMessage() + "\n");
                        });
                        break;
                    case END:
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, m.getMessage());
                            alert.initOwner(ClientLauncher.sm.getStage());
                            alert.setTitle("Info");
                            alert.setHeaderText("Game is over.");
                            alert.show();
                        });
                        running.set(false);
                        break;
                    case ERROR:
                        running.set(false);
                        break;
                }
            }
            Platform.runLater(() -> {
                ClientLauncher.sm.addScene("MenuView.fxml", "Menu");
                ClientLauncher.sm.activateScene("MenuView.fxml");
            });
        }).start();
    }

    @FXML
    public void tfLog_onEnter(ActionEvent event) {
        sendString();
    }

    public void btLog_Action(ActionEvent event) {
        sendString();
    }

    private void sendString() {
        String strSent = tfLog.getText();
        tfLog.clear();
        cli.sendRequest(ApplicationLayer.ACTION, strSent);
        lblTurn.setText("It's your opponent's turn");
        btLog.setDisable(true);
        tfLog.setDisable(true);
    }

    private void logMessage(String message) {
        Platform.runLater(() -> taLog.appendText(message));
    }

}
