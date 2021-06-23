package Launch;

import Net.Client;
import Util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientLauncher extends Application {

    public static Client cli;

    public static SceneManager sm;

    @Override
    public void start(Stage primaryStage) {

        sm = new SceneManager(primaryStage);

        sm.addScene("ConSetView.fxml", "Connection Settings");
        sm.activateScene("ConSetView.fxml");

        primaryStage.show();
    }

}
