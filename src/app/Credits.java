package app;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Credits {

    private Pane root;
    private Menu menu;
    private Text text;
    private Button backToMenuButton;
    private AnimationTimer timer;

    public Credits(Pane root, Menu menu){
        this.root = root;
        this.menu = menu;
    }

    public void launchCredits(){
        text = new Text("Adrian Cieśla IIa :D");
        text.setFont(Font.font("Verdana",30));
        text.setX(450);
        text.setY(-50);
        text.setFill(Color.WHITE);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                text.setY(text.getY()+5);
            }
        };

        backToMenuButton = new Button("X");
        backToMenuButton.setId("backToMenuButton");
        backToMenuButton.setTranslateX(10);
        backToMenuButton.setTranslateY(10);
        backToMenuButton.setPrefWidth(10);
        backToMenuButton.setPrefHeight(10);
        backToMenuButton.setOnMouseClicked(e->{
            menu.reloadMenu();
        });

        root.getChildren().add(new Rectangle(1200,600, Color.BLACK));
        root.getChildren().add(text);
        root.getChildren().add(backToMenuButton);

        timer.start();
    }

}
