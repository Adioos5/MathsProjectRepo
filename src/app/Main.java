package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(new Menu().createRoot()));
        stage.setTitle("Wykresy ciągów");
        stage.setWidth(1200);
        stage.setHeight(600);
        stage.setResizable(false);

        stage.show();
    }
}
