package Controller;

import Launch.ServerLauncher;
import Net.Server;
import Net.TransportLayer;
import Util.LogEvent;
import Util.LogEventListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ServerController implements LogEventListener {

    final String username = System.getProperty("user.name");

    private Server server;

    @FXML
    private BorderPane bPane;

    @FXML
    private TextArea taLog;

    @FXML
    private TextField tfLog;

    @FXML
    private Button btLog;

    @FXML
    public void initialize() {
        server = getServer();
        server.addLogListener(this);
        server.start();
    }

    @FXML
    public void exitApplication() {
        server.stop();
    }

    public void btLog_Action(ActionEvent event) {
        String str = tfLog.getText();
        tfLog.clear();
        taLog.appendText(username + ": " + str + "\n");
    }

    @Override
    public void HandleLog(LogEvent le) {
        logMessage(le.getMessage());
    }

    private void logMessage(String message) {
        Platform.runLater(() -> taLog.appendText(message));
    }

    private Server getServer() {
        Stage alertBox = new Stage();
        alertBox.initModality(Modality.APPLICATION_MODAL);
        alertBox.setTitle("Server Configurations");
        alertBox.setMinWidth(350);

        alertBox.setOnCloseRequest(e -> {
            System.exit(0);
        });

        ToggleGroup tlGroup = new ToggleGroup();
        RadioButton rBtnTCP = new RadioButton("TCP");
        rBtnTCP.setToggleGroup(tlGroup);
        RadioButton rBtnUDP = new RadioButton("UDP");
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(rBtnTCP, rBtnUDP);
        rBtnUDP.setSelected(true);
        rBtnUDP.setToggleGroup(tlGroup);
        TextField tfPort = new TextField("9090");

        Label lbl1 = new Label("Protocol :", hbox);
        lbl1.setContentDisplay(ContentDisplay.RIGHT);
        Label lbl2 = new Label("Port :", tfPort);
        lbl2.setContentDisplay(ContentDisplay.RIGHT);

        AtomicReference<TransportLayer> tl = new AtomicReference<>();
        AtomicInteger port = new AtomicInteger();
        Button okBtn = new Button("OK");
        okBtn.setPrefWidth(60);
        okBtn.setOnAction(e -> {
            if (rBtnTCP.isSelected()) {
                tl.set(TransportLayer.TCP);
            } else {
                tl.set(TransportLayer.UDP);
            }
            port.set(Integer.parseInt(tfPort.getText()));
            alertBox.close();
        });

        VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(30));
        vBox.getChildren().addAll(lbl1, lbl2, okBtn);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);
        alertBox.setScene(scene);
        alertBox.showAndWait();

        return new Server(port.get(), tl.get());
    }

}
