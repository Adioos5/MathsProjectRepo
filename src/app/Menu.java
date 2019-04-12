package app;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Menu {

    private Pane root;
    private Rectangle rect;
    private Main parentClass;

    public Menu(Pane root, Main parentClass){
        this.root = root;
        this.parentClass = parentClass;
    }

    public void showMenu(){
        root.getChildren().clear();
        rect = new Rectangle(100,100, Color.BLACK);
        rect.setOnMouseClicked(e->{
            root.getChildren().clear();
            parentClass.createRoot();
        });
        root.getChildren().add(rect);
    }

}
