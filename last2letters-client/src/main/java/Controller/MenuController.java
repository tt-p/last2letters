package Controller;

import Launch.ClientLauncher;
import Net.ApplicationLayer;
import Net.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

public class MenuController {

    private Client cli;

    @FXML
    public void initialize() {
        cli = ClientLauncher.cli;

        ClientLauncher.sm.getStage().setOnCloseRequest(e -> {
            cli.sendAndReceiveRequest(ApplicationLayer.QUIT);
            cli.stop();
            Platform.exit();
        });
    }

    @FXML
    public void btPlay_Action(ActionEvent event) {
        ApplicationLayer res1 = cli.sendAndReceiveRequest(ApplicationLayer.PLAY);
        if (res1 == ApplicationLayer.OK) {
            ButtonType cancel = new ButtonType("Cancel");
            final Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Please wait players to join.", cancel);
            alert1.initModality(Modality.APPLICATION_MODAL);
            alert1.initOwner(ClientLauncher.sm.getStage());
            alert1.setTitle("Info");
            alert1.setHeaderText("Waiting for players.");
            alert1.show();

            if (cancel == alert1.getResult()) {
                //client.sendRequest(ApplicationLayer.CANCEL);
                //return;
            }

            new Thread(() -> {

                ApplicationLayer res2 = cli.listen();

                Platform.runLater(() -> {
                    if (res2 == ApplicationLayer.OK) {
                        alert1.close();
                        ButtonType ok = new ButtonType("Ok");
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "Please click Ok to start.", ok);
                        alert2.initOwner(ClientLauncher.sm.getStage());
                        alert2.setHeaderText("Player Found.");
                        alert2.showAndWait();

                        if (ok == alert2.getResult()) {
                            ClientLauncher.sm.addScene("GameView.fxml", "Game");
                            ClientLauncher.sm.activateScene("GameView.fxml");
                        } else {
                            cli.sendRequest(ApplicationLayer.LEAVE);
                        }
                    }
                });
            }).start();
        }
    }

    @FXML
    public void btQuit_Action() {
        cli.sendRequest(ApplicationLayer.QUIT);
        Platform.exit();
    }

}