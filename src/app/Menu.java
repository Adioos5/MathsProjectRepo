package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;



public class Menu {

    private Pane root;

    public Pane createRoot(){
        root = new Pane();

        root.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm()
        );

        ImageView img = new ImageView(new Image("img/menu-wallpaper.png"));

        img.setFitWidth(1200);
        img.setFitHeight(600);

        Text appTitle = new Text("Smartboard");
        appTitle.setId("appTitle");
        appTitle.setFill(Color.WHITE);
        appTitle.setFont(Font.font("Monospaced",100));
        appTitle.setX(350);
        appTitle.setY(150);

        Text sideTxt = new Text("Granice ciągów liczbowych");
        sideTxt.setFill(Color.WHITE);
        sideTxt.setFont(Font.font("Verdana",30));
        sideTxt.setX(395);
        sideTxt.setY(200);

        Button boardButton = new Button("Tablica");
        boardButton.setId("boardButton");
        boardButton.setTranslateX(525);
        boardButton.setTranslateY(280);
        boardButton.setPrefWidth(150);
        boardButton.setPrefHeight(75);
        boardButton.setOnMouseClicked(e->{
            root.getChildren().clear();
            new Board(root,this).launchTheBoard();
        });

        Button creditsButton = new Button("Twórcy");
        creditsButton.setId("creditsButton");
        creditsButton.setTranslateX(525);
        creditsButton.setTranslateY(400);
        creditsButton.setPrefWidth(150);
        creditsButton.setPrefHeight(75);

        root.getChildren().add(img);
        root.getChildren().add(appTitle);
        root.getChildren().add(sideTxt);
        root.getChildren().add(boardButton);
        root.getChildren().add(creditsButton);

        return root;
    }

    public void reloadMenu(){
        root.getChildren().clear();

        Text appTitle = new Text("Smartboard");
        appTitle.setId("appTitle");
        appTitle.setFill(Color.WHITE);
        appTitle.setFont(Font.font("Monospaced",100));
        appTitle.setX(350);
        appTitle.setY(150);

        Text sideTxt = new Text("Granice ciągów liczbowych");
        sideTxt.setFill(Color.WHITE);
        sideTxt.setFont(Font.font("Verdana",30));
        sideTxt.setX(395);
        sideTxt.setY(200);

        ImageView img = new ImageView(new Image("img/menu-wallpaper.png"));

        img.setFitWidth(1200);
        img.setFitHeight(600);

        Button boardButton = new Button("Tablica");
        boardButton.setId("boardButton");
        boardButton.setTranslateX(525);
        boardButton.setTranslateY(280);
        boardButton.setPrefWidth(150);
        boardButton.setPrefHeight(75);
        boardButton.setOnMouseClicked(e->{
            root.getChildren().clear();
            new Board(root,this).launchTheBoard();
        });

        Button creditsButton = new Button("Twórcy");
        creditsButton.setId("creditsButton");
        creditsButton.setTranslateX(525);
        creditsButton.setTranslateY(400);
        creditsButton.setPrefWidth(150);
        creditsButton.setPrefHeight(75);

        root.getChildren().add(img);
        root.getChildren().add(appTitle);
        root.getChildren().add(sideTxt);
        root.getChildren().add(boardButton);
        root.getChildren().add(creditsButton);
    }
}
