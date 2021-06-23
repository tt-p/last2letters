package Launch;

import Controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        Stage stage = primaryStage;

        Parent root = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/ServerView.fxml"));
        try {
            root = loader.load();
            ServerController controller = loader.getController();
            stage.setOnCloseRequest(event -> controller.exitApplication());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }



        stage.setTitle("Server");
        stage.setScene(new Scene(root));
        stage.show();
    }

}
