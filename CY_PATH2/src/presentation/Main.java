package presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application  {

    public void start(Stage stage) throws IOException {
        try{
            stage.setTitle("Cy_path");
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("App.fxml"));
            Parent root = fxml.load();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
