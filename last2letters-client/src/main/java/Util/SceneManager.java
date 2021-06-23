package Util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class SceneManager {

    private final HashMap<String, Scene> scenes = new HashMap<>();
    private final HashMap<String, String> titles = new HashMap<>();
    private final Stage stage;
    private Scene current;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void addScene(String name, String tittle) {
        Scene p = getFromFXML(name);
        scenes.put(name, p);
        titles.put(name, tittle);
    }

    public void removeScene(String name) {
        scenes.remove(name);
    }

    public void activateScene(String name) {
        current = scenes.get(name);
        stage.setTitle(titles.get(name));
        stage.setScene(current);
    }

    private Scene getFromFXML(String name) {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/" + name));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new Scene(root);
    }
}