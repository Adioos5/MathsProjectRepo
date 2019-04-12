package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Menu {

    private Pane root;
    private Rectangle rect;


    public Pane showMenu(){
        root = new Pane();

        root.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm()
        );

        rect = new Rectangle(100,100, Color.BLACK);
        rect.setOnMouseClicked(e->{
            root.getChildren().clear();
            new Board(root,this).createRoot();
        });
        root.getChildren().add(rect);

        return root;
    }

    public void reloadMenu(){
        root.getChildren().clear();
        rect = new Rectangle(100,100, Color.BLACK);
        rect.setOnMouseClicked(e->{
            root.getChildren().clear();
            new Board(root,this).createRoot();
        });
        root.getChildren().add(rect);
    }
}
