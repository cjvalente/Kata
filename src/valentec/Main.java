package valentec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("setUp.fxml"));
        Parent root = loader.load();
        stage.setTitle("Welcome to CJ's Quiz!");
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }
}

