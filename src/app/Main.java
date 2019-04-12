package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(new Menu().createRoot()));
        stage.setTitle("Smartboard - Granice ciągów liczbowych");
        stage.getIcons().add(new Image("img/icon.png"));
        stage.setWidth(1200);
        stage.setHeight(600);
        stage.setResizable(false);

        stage.show();
    }
}
