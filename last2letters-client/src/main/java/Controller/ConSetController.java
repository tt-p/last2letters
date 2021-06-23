package Controller;

import Launch.ClientLauncher;
import Net.ApplicationLayer;
import Net.Client;
import Net.TCP_Client;
import Net.UDP_Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class ConSetController {

    final String username = System.getProperty("user.name");

    private Client cli;

    @FXML
    private RadioButton rBtnTCP;

    @FXML
    private RadioButton rBtnUDP;

    @FXML
    private TextField tfIp;

    @FXML
    private TextField tfPort;

    @FXML
    private TextField tfUser;

    @FXML
    private Button btCon;

    @FXML
    public void initialize() {

    }

    public void btCon_Action(ActionEvent event) {
        String strIp = tfIp.getText();
        String strPort = tfPort.getText();

        cli = rBtnTCP.isSelected() ?
                new TCP_Client(strIp, Integer.parseInt(strPort)) : new UDP_Client(strIp, Integer.parseInt(strPort));
        cli.start();
        ApplicationLayer res = cli.sendAndReceiveRequest(ApplicationLayer.REGISTER, tfUser.getText());
        if (res == ApplicationLayer.CONNECTED) {
            ClientLauncher.cli = cli;
            ClientLauncher.sm.addScene("MenuView.fxml", "Menu");
            ClientLauncher.sm.activateScene("MenuView.fxml");
        }
    }

}
