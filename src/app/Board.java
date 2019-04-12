
package app;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Board {

    private Pane root;

    private Menu menu;

    private Rectangle binOverlay;
    private Rectangle hamburgerOverlay;
    private Rectangle optionsPanel;
    private Rectangle deltaLine;
    private Rectangle limitLine;
    private Rectangle mLine;
    private Rectangle mRectUp;
    private Rectangle mRectDown;
    private Rectangle background;

    private Rectangle epsilonLine1;
    private Rectangle epsilonLine2;
    private Rectangle epsilonDistanceLine1;
    private Rectangle epsilonDistanceLine2;
    private Rectangle epsilonArea1;
    private Rectangle epsilonArea2;
    private Rectangle xLine;
    private Rectangle yLine;

    private Text upperText;
    private Text deltaLetter;
    private Text epsilonLetter1;
    private Text epsilonLetter2;
    private Text limitLetter;
    private Text mLetter;
    private Text xLetter;
    private Text yLetter;

    private TextArea txtArea;
    private TextArea txtArea2;
    private TextArea txtArea3;

    private ImageView bin;
    private ImageView hamburger;

    private Button drawButton;
    private Button scaleButton;
    private Button speedButton;
    private Button backToMenuButton;

    private CheckBox zbOpt;
    private CheckBox rozbPOpt;
    private CheckBox rozbMOpt;
    private CheckBox paleDesignOpt;
    private CheckBox darkDesignOpt;

    private int scale = 15;
    private int speed = 50;
    private int lastLineIndex = 0;

    private double pointSize = 5;
    private double x0 = 52;
    private double y0 = 300;
    private double xn = 1;
    private double fx = 0;
    private double currentX = x0;
    private double currentY = y0;

    private boolean isPlusM = false;
    private boolean isMinusM = false;
    private boolean blankPoint = false;
    private boolean isAddingLines = true;
    private boolean isAnimating = false;
    private boolean animationsEnabled = true;

    private AnimationTimer timer;

    private List<Node> listOfLines = new ArrayList<>();
    private List<Node> listOfPoints = new ArrayList<>();
    private List<Text> listOfNums = new ArrayList<>();
    private List<Node> optionsPanelElements = new ArrayList<>();

    private Color linesColor = Color.WHITE;
    private Color backgroundColor = Color.BLACK;
    private Color textColor = Color.WHITE;
    private Color pointColor = Color.YELLOW;

    public Board(Pane root, Menu menu){
        this.root = root;
        this.menu = menu;
    }

    public Pane createRoot(){

        //osie
        xLine = new Rectangle( x0,y0,960,2);
        yLine = new Rectangle(x0,50,2,480);

        xLine.setFill(Color.WHITE);
        yLine.setFill(Color.WHITE);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(isAddingLines) {
                    isAnimating = true;

                    if(root.getChildren().contains(txtArea)){
                        removeUI();
                    }
                    try {

                        Thread.currentThread().sleep(speed);
                        drawPoint();
                    } catch (InterruptedException e) {
                        System.err.println(e);
                    }

                }
                if(currentX>900 || currentY < 100 || currentY > 500){
                    isAddingLines = false;
                    try {

                        Thread.currentThread().sleep(1);

                        animateRemovingLines();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        //background
        background = new Rectangle(1200,600, backgroundColor);
        root.getChildren().add(background);


        //elementy okna
        hamburgerOverlay = new Rectangle(80,80);
        hamburgerOverlay.setOpacity(0);
        hamburgerOverlay.setX(1080);
        hamburgerOverlay.setY(35);

        optionsPanel = new Rectangle(900,200);
        optionsPanel.setFill(Color.DARKGRAY);
        optionsPanel.setX(150);
        optionsPanel.setY(0);

        root.setOnMouseClicked(e->{
            if(root.getChildren().contains(optionsPanel) && !new Rectangle(e.getX(),e.getY(),1,1).intersects(new Rectangle(optionsPanel.getX(),optionsPanel.getY(),optionsPanel.getWidth()+300,optionsPanel.getHeight()).getBoundsInParent())){
                removeOptionsPanel();
                addUI();
            }
        });

        root.setOnMousePressed(e->{
            if(root.getChildren().contains(optionsPanel) && !new Rectangle(e.getX(),e.getY(),1,1).intersects(new Rectangle(optionsPanel.getX(),optionsPanel.getY(),optionsPanel.getWidth()+300,optionsPanel.getHeight()).getBoundsInParent())){
                removeOptionsPanel();
                addUI();
            }
        });

        root.setOnMouseDragged(e->{
            if(root.getChildren().contains(optionsPanel) && !new Rectangle(e.getX(),e.getY(),1,1).intersects(new Rectangle(optionsPanel.getX(),optionsPanel.getY(),optionsPanel.getWidth()+300,optionsPanel.getHeight()).getBoundsInParent())){
                removeOptionsPanel();
                addUI();
            }
        });

        hamburger = new ImageView(new Image("img/options icon.png"));
        hamburger.setFitWidth(80);
        hamburger.setFitHeight(50);
        hamburger.setX(1080);
        hamburger.setY(50);
        hamburgerOverlay.setOnMouseClicked(e ->{

            if(isAnimating) {
                stopAnimating();
            }

            if(!root.getChildren().contains(optionsPanel)){
                removeUI();
                generateOptionsPanel();
            } else {
                addUI();
                removeOptionsPanel();
            }

        });
        hamburgerOverlay.setOnMouseEntered(e ->{
            ((Node) e.getSource()).setCursor(Cursor.HAND);
        });

        binOverlay = new Rectangle(120,120);
        binOverlay.setOpacity(0);
        binOverlay.setX(1060);
        binOverlay.setY(440);
        bin = new ImageView(new Image("img/bin icon.jpg"));
        bin.setFitWidth(120);
        bin.setFitHeight(120);
        bin.setX(1060);
        bin.setY(440);
        binOverlay.setOnMouseClicked(e ->{
            if(!root.getChildren().contains(optionsPanel)) {
                if (isAnimating) {
                    stopAnimating();
                    tidyTheBoard();
                } else {
                    tidyTheBoard();
                }
                if (!root.getChildren().contains(txtArea)) {
                    addUI();
                }
            }
        });
        binOverlay.setOnMouseEntered(e ->{
            ((Node) e.getSource()).setCursor(Cursor.HAND);
        });


        txtArea = new TextArea("y = x");
        txtArea.setPrefWidth(120);
        txtArea.setPrefHeight(10);
        txtArea.setTranslateX(1060);
        txtArea.setTranslateY(155);

        ImageView pencil = new ImageView(new Image("img/pencil.png"));
        pencil.setFitWidth(20);
        pencil.setFitHeight(20);
        drawButton = new Button("Narysuj",pencil);
        drawButton.setContentDisplay(ContentDisplay.LEFT);
        drawButton.setId("narysuj");
        drawButton.setPrefWidth(120);
        drawButton.setPrefHeight(50);
        drawButton.setTranslateX(1060);
        drawButton.setTranslateY(190);
        drawButton.setGraphic(pencil);
        drawButton.setOnMouseEntered(e ->{
            ((Node) e.getSource()).setCursor(Cursor.HAND);
        });
        drawButton.setOnMouseClicked(e -> {
            if(!isAddingLines) {
                tidyTheBoard();
                reloadVariables();

                timer.start();
            }
        });

        txtArea2 = new TextArea(""+scale);
        txtArea2.setPrefWidth(120);
        txtArea2.setPrefHeight(10);
        txtArea2.setTranslateX(1060);
        txtArea2.setTranslateY(255);

        ImageView scicon = new ImageView(new Image("img/scaling icon.png"));
        scicon.setFitWidth(20);
        scicon.setFitHeight(20);
        scaleButton = new Button("Wyskaluj",scicon);
        scaleButton.setContentDisplay(ContentDisplay.LEFT);
        scaleButton.setId("wyskaluj");
        scaleButton.setPrefWidth(120);
        scaleButton.setPrefHeight(50);
        scaleButton.setTranslateX(1060);
        scaleButton.setTranslateY(290);
        scaleButton.setOnMouseEntered(e ->{
            ((Node) e.getSource()).setCursor(Cursor.HAND);
        });
        scaleButton.setOnMouseClicked(e -> {
            scale = Integer.parseInt(txtArea2.getText());
            upperText.setText("Skala: 1 - "+scale+" px | Prędkość: " + speed +" ms");
        });

        txtArea3 = new TextArea(""+speed);
        txtArea3.setPrefWidth(120);
        txtArea3.setPrefHeight(10);
        txtArea3.setTranslateX(1060);
        txtArea3.setTranslateY(355);

        ImageView spcon = new ImageView(new Image("img/speed icon.png"));
        spcon.setFitWidth(20);
        spcon.setFitHeight(20);
        speedButton = new Button("Prędkość",spcon);
        speedButton.setContentDisplay(ContentDisplay.LEFT);
        speedButton.setId("predkosc");
        speedButton.setPrefWidth(120);
        speedButton.setPrefHeight(50);
        speedButton.setTranslateX(1060);
        speedButton.setTranslateY(390);
        speedButton.setOnMouseEntered(e ->{
            ((Node) e.getSource()).setCursor(Cursor.HAND);
        });
        speedButton.setOnMouseClicked(e -> {
            speed = Integer.parseInt(txtArea3.getText());
            upperText.setText("Skala: 1 - "+scale+" px | Prędkość: " + speed +" ms");
        });



        upperText = new Text("Skala: 1 - "+scale+" px | Prędkość: " + speed + " ms");
        upperText.setFill(textColor);
        upperText.setFont(Font.font("Verdana",20));
        upperText.setX(420);
        upperText.setY(40);

        xLetter = new Text("x");
        xLetter.setFill(linesColor);
        xLetter.setFont(Font.font("Monospaced",15));
        xLetter.setX(1000);
        xLetter.setY(y0+ 15);

        yLetter = new Text("y");
        yLetter.setFill(linesColor);
        yLetter.setFont(Font.font("Monospaced",15));
        yLetter.setX(x0-15);
        yLetter.setY(yLine.getY()+10);

        deltaLine = new Rectangle(3,480,Color.CYAN);
        deltaLine.setX(60);
        deltaLine.setY(50);
        deltaLine.setOnMousePressed(e->{
            if(e.getX()>x0 && e.getX()<(960+x0)) {
                deltaLine.setX(e.getX());
                epsilonArea1.setWidth(limitLine.getX()+limitLine.getWidth()-deltaLine.getX());
                epsilonArea2.setWidth(limitLine.getX()+limitLine.getWidth()-deltaLine.getX());

                epsilonArea1.setX(deltaLine.getX());
                epsilonArea2.setX(deltaLine.getX());

                deltaLetter.setX(deltaLine.getX()-3);

                mRectUp.setX(deltaLine.getX());
                mRectUp.setWidth(mLine.getX()+mLine.getWidth()-deltaLine.getX());

                mRectDown.setX(deltaLine.getX());
                mRectDown.setWidth(mLine.getX()+mLine.getWidth()-deltaLine.getX());
            }
        });
        deltaLine.setOnMouseDragged(e->{
            if(e.getX()>x0 && e.getX()<(960+x0)){
                deltaLine.setX(e.getX());
                epsilonArea1.setWidth(limitLine.getX()+limitLine.getWidth()-deltaLine.getX());
                epsilonArea2.setWidth(limitLine.getX()+limitLine.getWidth()-deltaLine.getX());

                epsilonArea1.setX(deltaLine.getX());
                epsilonArea2.setX(deltaLine.getX());

                deltaLetter.setX(deltaLine.getX()-3);

                mRectUp.setX(deltaLine.getX());
                mRectUp.setWidth(mLine.getX()+mLine.getWidth()-deltaLine.getX());

                mRectDown.setX(deltaLine.getX());
                mRectDown.setWidth(mLine.getX()+mLine.getWidth()-deltaLine.getX());
            }
        });

        limitLine = new Rectangle(960,3,Color.GREEN);
        limitLine.setX(x0);
        limitLine.setY(y0);
        limitLine.setOnMousePressed(e->{



            if(e.getY()>50 && e.getY()<530) {
                double distance = limitLine.getY() - epsilonLine1.getY();
                limitLine.setY(e.getY());
                epsilonLine1.setY(limitLine.getY()-distance);
                epsilonLine2.setY(limitLine.getY()+distance);
                limitLetter.setY(limitLine.getY()+5);

                epsilonDistanceLine1.setY(epsilonLine1.getY());
                epsilonDistanceLine2.setY(limitLine.getY());

                epsilonLetter1.setY(epsilonLine1.getY()+(epsilonDistanceLine1.getHeight()/2));
                epsilonLetter2.setY(limitLine.getY()+(epsilonDistanceLine2.getHeight()/2));

                epsilonArea1.setY(epsilonLine1.getY());
                epsilonArea2.setY(limitLine.getY());
            }
        });
        limitLine.setOnMouseDragged(e->{

            if(e.getY()>50 && e.getY()<530) {
                double distance = limitLine.getY() - epsilonLine1.getY();
                limitLine.setY(e.getY());
                epsilonLine1.setY(limitLine.getY()-distance);
                epsilonLine2.setY(limitLine.getY()+distance);
                limitLetter.setY(limitLine.getY()+5);

                epsilonDistanceLine1.setY(epsilonLine1.getY());
                epsilonDistanceLine2.setY(limitLine.getY());

                epsilonLetter1.setY(epsilonLine1.getY()+(epsilonDistanceLine1.getHeight()/2));
                epsilonLetter2.setY(limitLine.getY()+(epsilonDistanceLine2.getHeight()/2));

                epsilonArea1.setY(epsilonLine1.getY());
                epsilonArea2.setY(limitLine.getY());
            }
        });

        epsilonLine1 = new Rectangle(960,3,Color.ORANGE);
        epsilonLine1.setX(x0);
        epsilonLine1.setY(limitLine.getY()-50);
        epsilonLine1.setOnMousePressed(e->{


            if(e.getY()< limitLine.getY() && e.getY()>50){
                epsilonLine1.setY(e.getY());
                double distance = limitLine.getY()-epsilonLine1.getY();
                epsilonLine2.setY(limitLine.getY()+distance);

                epsilonDistanceLine1.setY(epsilonLine1.getY());
                epsilonDistanceLine1.setHeight(limitLine.getY()-epsilonLine1.getY());
                epsilonDistanceLine2.setHeight(epsilonLine2.getY()-limitLine.getY());

                epsilonLetter1.setY(epsilonLine1.getY()+(epsilonDistanceLine1.getHeight()/2));
                epsilonLetter2.setY(limitLine.getY()+(epsilonDistanceLine2.getHeight()/2));

                epsilonArea1.setHeight(epsilonDistanceLine1.getHeight());
                epsilonArea1.setY(epsilonDistanceLine1.getY());

                epsilonArea2.setHeight(epsilonDistanceLine2.getHeight());
            }
        });
        epsilonLine1.setOnMouseDragged(e->{

            if(e.getY()< limitLine.getY() && e.getY()>50){
                epsilonLine1.setY(e.getY());
                double distance = limitLine.getY()-epsilonLine1.getY();
                epsilonLine2.setY(limitLine.getY()+distance);

                epsilonDistanceLine1.setY(epsilonLine1.getY());
                epsilonDistanceLine1.setHeight(limitLine.getY()-epsilonLine1.getY());
                epsilonDistanceLine2.setHeight(epsilonLine2.getY()-limitLine.getY());

                epsilonLetter1.setY(epsilonLine1.getY()+(epsilonDistanceLine1.getHeight()/2));
                epsilonLetter2.setY(limitLine.getY()+(epsilonDistanceLine2.getHeight()/2));

                epsilonArea1.setHeight(epsilonDistanceLine1.getHeight());
                epsilonArea1.setY(epsilonDistanceLine1.getY());

                epsilonArea2.setHeight(epsilonDistanceLine2.getHeight());
            }
        });

        epsilonLine2 = new Rectangle(960,3,Color.ORANGE);
        epsilonLine2.setX(x0);
        epsilonLine2.setY(limitLine.getY()+50);
        epsilonLine2.setOnMousePressed(e->{



            if(e.getY()> limitLine.getY() && e.getY()<530){
                epsilonLine2.setY(e.getY());
                double distance = epsilonLine2.getY()-limitLine.getY();
                epsilonLine1.setY(limitLine.getY()-distance);

                epsilonDistanceLine1.setY(epsilonLine1.getY());
                epsilonDistanceLine1.setHeight(limitLine.getY()-epsilonLine1.getY());
                epsilonDistanceLine2.setHeight(epsilonLine2.getY()-limitLine.getY());

                epsilonLetter1.setY(epsilonLine1.getY()+(epsilonDistanceLine1.getHeight()/2));
                epsilonLetter2.setY(limitLine.getY()+(epsilonDistanceLine2.getHeight()/2));

                epsilonArea1.setHeight(epsilonDistanceLine1.getHeight());
                epsilonArea1.setY(epsilonDistanceLine1.getY());

                epsilonArea2.setHeight(epsilonDistanceLine2.getHeight());
            }
        });
        epsilonLine2.setOnMouseDragged(e->{



            if(e.getY()> limitLine.getY() && e.getY()<530){
                epsilonLine2.setY(e.getY());
                double distance = epsilonLine2.getY()-limitLine.getY();
                epsilonLine1.setY(limitLine.getY()-distance);

                epsilonDistanceLine1.setY(epsilonLine1.getY());
                epsilonDistanceLine1.setHeight(limitLine.getY()-epsilonLine1.getY());
                epsilonDistanceLine2.setHeight(epsilonLine2.getY()-limitLine.getY());

                epsilonLetter1.setY(epsilonLine1.getY()+(epsilonDistanceLine1.getHeight()/2));
                epsilonLetter2.setY(limitLine.getY()+(epsilonDistanceLine2.getHeight()/2));

                epsilonArea1.setHeight(epsilonDistanceLine1.getHeight());
                epsilonArea1.setY(epsilonDistanceLine1.getY());

                epsilonArea2.setHeight(epsilonDistanceLine2.getHeight());
            }
        });

        epsilonDistanceLine1 = new Rectangle(1,limitLine.getY()-epsilonLine1.getY(),Color.ORANGE);
        epsilonDistanceLine1.setX(limitLine.getX()+limitLine.getWidth()-30);
        epsilonDistanceLine1.setY(epsilonLine1.getY());

        epsilonDistanceLine2 = new Rectangle(1,epsilonLine2.getY()-limitLine.getY(),Color.ORANGE);
        epsilonDistanceLine2.setX(limitLine.getX()+limitLine.getWidth()-30);
        epsilonDistanceLine2.setY(limitLine.getY());

        limitLetter = new Text("g");
        limitLetter.setX(limitLine.getX()+limitLine.getWidth()+5);
        limitLetter.setY(limitLine.getY()+5);
        limitLetter.setFill(Color.GREEN);
        limitLetter.setFont(Font.font("Monospaced",15));

        deltaLetter = new Text("δ");
        deltaLetter.setX(deltaLine.getX()-3);
        deltaLetter.setY(deltaLine.getY()+deltaLine.getHeight()+20);
        deltaLetter.setFill(Color.CYAN);
        deltaLetter.setFont(Font.font("Monospaced",15));

        epsilonLetter1 = new Text("ε");
        epsilonLetter1.setX(limitLine.getX()+limitLine.getWidth()+5);
        epsilonLetter1.setY(epsilonLine1.getY()+(epsilonDistanceLine1.getHeight()/2));
        epsilonLetter1.setFill(Color.ORANGE);
        epsilonLetter1.setFont(Font.font("Monospaced",15));

        epsilonLetter2 = new Text("ε");
        epsilonLetter2.setX(limitLine.getX()+limitLine.getWidth()+5);
        epsilonLetter2.setY(limitLine.getY()+(epsilonDistanceLine2.getHeight()/2));
        epsilonLetter2.setFill(Color.ORANGE);
        epsilonLetter2.setFont(Font.font("Monospaced",15));

        epsilonArea1 = new Rectangle(limitLine.getX()+limitLine.getWidth()-deltaLine.getX(),epsilonDistanceLine1.getHeight(),Color.ORANGE);
        epsilonArea1.setOpacity(0.2);
        epsilonArea1.setX(deltaLine.getX());
        epsilonArea1.setY(epsilonLine1.getY());

        epsilonArea2 = new Rectangle(limitLine.getX()+limitLine.getWidth()-deltaLine.getX(),epsilonDistanceLine2.getHeight(),Color.ORANGE);
        epsilonArea2.setOpacity(0.2);
        epsilonArea2.setX(deltaLine.getX());
        epsilonArea2.setY(limitLine.getY());

        mLine = new Rectangle(960,3,Color.DARKMAGENTA);
        mLine.setX(x0);
        mLine.setY(y0);
        mLine.setOnMousePressed(e->{

            if(e.getY()>50 && e.getY()<530) {
                mLine.setY(e.getY());
                mLetter.setX(mLine.getX()+mLine.getWidth()+5);
                mLetter.setY(mLine.getY()+5);

                mRectUp.setHeight(mLine.getY()-50);
                mRectDown.setHeight(530-mLine.getY());
                mRectDown.setY(mLine.getY());
            }
        });
        mLine.setOnMouseDragged(e->{

            if(e.getY()>50 && e.getY()<530) {
                mLine.setY(e.getY());
                mLetter.setX(mLine.getX()+mLine.getWidth()+5);
                mLetter.setY(mLine.getY()+5);

                mRectUp.setHeight(mLine.getY()-50);
                mRectDown.setHeight(530-mLine.getY());
                mRectDown.setY(mLine.getY());
            }
        });

        mRectUp = new Rectangle(mLine.getX()+mLine.getWidth()-deltaLine.getX(),mLine.getY()-50,Color.DARKMAGENTA);
        mRectUp.setOpacity(0.2);
        mRectUp.setX(deltaLine.getX());
        mRectUp.setY(50);

        mRectDown = new Rectangle(mLine.getX()+mLine.getWidth()-deltaLine.getX(),530-mLine.getY(),Color.DARKMAGENTA);
        mRectDown.setOpacity(0.2);
        mRectDown.setX(deltaLine.getX());
        mRectDown.setY(mLine.getY());

        mLetter = new Text("M");
        mLetter.setX(mLine.getX()+mLine.getWidth()+5);
        mLetter.setY(mLine.getY()+5);
        mLetter.setFill(Color.DARKMAGENTA);
        mLetter.setFont(Font.font("Monospaced",15));



        zbOpt = new CheckBox("Ciąg zbieżny");

        zbOpt.setTranslateX(optionsPanel.getX()+optionsPanel.getWidth()-180);
        zbOpt.setTranslateY(100);
        zbOpt.setTextFill(Color.WHITE);

        zbOpt.setOnMouseClicked(e->{
            if(rozbPOpt.isSelected()) {
                removeRozbPTools();
                rozbPOpt.setSelected(false);
                isPlusM = false;
            }

            if(rozbMOpt.isSelected()){
                removeRozbMTools();
                rozbMOpt.setSelected(false);
                isMinusM = false;
            }

            if(!root.getChildren().contains(epsilonLine1)) {
                refreshZbToolsCoordinates();
                addZbTools();
            } else{
                removeZbTools();
            }
        });

        rozbPOpt = new CheckBox("Ciąg rozbieżny +∞");

        rozbPOpt.setTranslateX(optionsPanel.getX()+optionsPanel.getWidth()-180);
        rozbPOpt.setTranslateY(130);
        rozbPOpt.setTextFill(Color.WHITE);

        rozbPOpt.setOnMouseClicked(e->{
            if(zbOpt.isSelected()) {
                removeZbTools();
                zbOpt.setSelected(false);
            }

            if(rozbMOpt.isSelected()){
                removeRozbMTools();
                rozbMOpt.setSelected(false);
                isMinusM = false;
            }

            if(!isPlusM){
                refreshRozbPToolsCoordinates();
                addRozbPTools();
                isPlusM = true;
            } else{
                removeRozbPTools();
                isPlusM = false;
            }
        });

        rozbMOpt = new CheckBox("Ciąg rozbieżny -∞");

        rozbMOpt.setTranslateX(optionsPanel.getX()+optionsPanel.getWidth()-180);
        rozbMOpt.setTranslateY(160);
        rozbMOpt.setTextFill(Color.WHITE);

        rozbMOpt.setOnMouseClicked(e->{
            if(zbOpt.isSelected()) {
                removeZbTools();
                zbOpt.setSelected(false);
            }
            if(rozbPOpt.isSelected()){
                removeRozbPTools();
                rozbPOpt.setSelected(false);
                isPlusM = false;
            }

            if(!isMinusM){
                refreshRozbMToolsCoordinates();
                addRozbMTools();
                isMinusM = true;
            } else{
                removeRozbMTools();
                isMinusM = false;
            }

        });

        darkDesignOpt = new CheckBox("Ciemny");

        darkDesignOpt.setSelected(true);
        darkDesignOpt.setTranslateX(optionsPanel.getX()+optionsPanel.getWidth()-360);
        darkDesignOpt.setTranslateY(100);
        darkDesignOpt.setTextFill(Color.WHITE);

        darkDesignOpt.setOnMouseClicked(e->{
            if (!paleDesignOpt.isSelected()){
                paleDesignOpt.setSelected(true);
                repaintDesingn("white");
            } else{
                paleDesignOpt.setSelected(false);
                repaintDesingn("black");
            }
        });

        paleDesignOpt = new CheckBox("Jasny");

        paleDesignOpt.setTranslateX(optionsPanel.getX()+optionsPanel.getWidth()-360);
        paleDesignOpt.setTranslateY(130);
        paleDesignOpt.setTextFill(Color.WHITE);

        paleDesignOpt.setOnMouseClicked(e->{
            if (!darkDesignOpt.isSelected()) {
                darkDesignOpt.setSelected(true);
                repaintDesingn("black");
            } else{
                darkDesignOpt.setSelected(false);
                repaintDesingn("white");
            }
        });

        backToMenuButton = new Button("X");
        backToMenuButton.setId("backToMenuButton");
        backToMenuButton.setTranslateX(10);
        backToMenuButton.setTranslateY(10);
        backToMenuButton.setPrefWidth(10);
        backToMenuButton.setPrefHeight(10);
        backToMenuButton.setOnMouseClicked(e->{
            if(isAnimating) stopAnimating();
            tidyTheBoard();
            menu.reloadMenu();
        });

        root.getChildren().add(hamburger);
        root.getChildren().add(hamburgerOverlay);
        root.getChildren().add(bin);
        root.getChildren().add(binOverlay);
        root.getChildren().add(upperText);
        root.getChildren().add(backToMenuButton);

        root.getChildren().add(xLine);
        root.getChildren().add(yLine);
        root.getChildren().add(xLetter);
        root.getChildren().add(yLetter);

        timer.start();

        return root;
    }

    private void initPoint(double x, double y){
        Rectangle point;

        if(blankPoint){
            point = new Rectangle(pointSize, 2 , pointColor);
            blankPoint = false;
        }
        else point = new Rectangle(pointSize, pointSize , pointColor);

        point.setX(x);
        point.setY(y);

        listOfPoints.add(point);
        root.getChildren().add(point);
    }

    private void initPointLines(double x, double y){
        Rectangle lineX = new Rectangle(x-50, 1 , linesColor);

        Rectangle lineY;
        if(y<=y0) {
            lineY = new Rectangle(1, y0 - y, linesColor);
        } else {
            lineY = new Rectangle(1, y-y0, linesColor);
        }


        Text tX = new Text(""+(int)xn);
        Text tY = new Text(""+(double)Math.round(fx * 100d) / 100d);

        tX.setFill(textColor);
        tY.setFill(textColor);

        tX.setFont(Font.font("Monospaced",10));
        tY.setFont(Font.font("Monospaced",10));

        lineX.setX(50);
        lineX.setY(y+1);

        lineY.setX(x+1);
        if(y<=y0) {
            lineY.setY(y);
        } else {
            lineY.setY(y0);
        }
        tX.setX(x);
        tX.setY(y0 + 20);

        tY.setX(20);
        tY.setY(y+5);

        listOfLines.add(lineX);
        listOfLines.add(lineY);
        listOfNums.add(tX);
        listOfNums.add(tY);

        lastLineIndex = listOfLines.size()-1;

        root.getChildren().add(lineX);
        root.getChildren().add(lineY);

        root.getChildren().add(tX);
        root.getChildren().add(tY);

    }

    private void reloadVariables(){
        xn = 1;
        fx = 0;

        lastLineIndex = 0;

        currentX = x0;
        currentY = y0;

        isAddingLines = true;
    }

    private String formAnEquation(String equation){
        String e = equation.replaceAll("\\s+","");
        e = e.substring(2);
        e = e.toLowerCase();

        return e;
    }

    private double f(double x, String e) {

        // Mechanizm przekształcania stringa w spójne i logiczne równanie
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");

        e = e.replace("sin(", "Math.sin(").
                replace("cos(", "Math.cos(").
                replace("tg(", "Math.tan(").
                replace("sqrt(", "Math.sqrt(").
                replace("pow(x,", "Math.pow(x,").
                replace("log(", "Math.log(").
                replace("abs(", "Math.abs(").
                replace("x", String.valueOf(x));


        if(e.contains("/")){
            String s = e.substring(e.indexOf('/')+1);
            try {
                if(Double.parseDouble(String.valueOf(engine.eval(s))) == 0.0){
                    blankPoint = true;
                    return 0;
                }
            } catch (ScriptException e1) {

            }
        }

        String infix = e;
        try {
            //Ostateczny wynik funkcji
            return Double.parseDouble(String.valueOf(engine.eval(infix)));
        } catch (ScriptException e1) {
            JOptionPane.showMessageDialog(null,"Program nie poradził sobie z tym równaniem.","Błąd",JOptionPane.WARNING_MESSAGE);
            System.exit(0);

        }
        return 0.0;
    }

    private void removeUI(){
        root.getChildren().remove(txtArea);
        root.getChildren().remove(drawButton);
        root.getChildren().remove(txtArea2);
        root.getChildren().remove(scaleButton);
        root.getChildren().remove(txtArea3);
        root.getChildren().remove(speedButton);
    }

    private void addUI(){
        root.getChildren().add(txtArea);
        root.getChildren().add(drawButton);
        root.getChildren().add(txtArea2);
        root.getChildren().add(scaleButton);
        root.getChildren().add(txtArea3);
        root.getChildren().add(speedButton);
    }

    private void drawPoint(){
        double x = x0 + xn * scale;
        double y = y0 - (f(xn, formAnEquation(txtArea.getText()))*scale);

        currentX = x;
        currentY = y;

        fx = f(xn, formAnEquation(txtArea.getText()));

        initPoint(x, y);
        initPointLines(x, y);

        xn += 1;
    }

    private void animateRemovingLines(){
        if(lastLineIndex>=1) {
            root.getChildren().remove(listOfLines.get(lastLineIndex));
            root.getChildren().remove(listOfLines.get(lastLineIndex-1));
            lastLineIndex--;
        } else {
            timer.stop();
            isAnimating = false;

            if(!root.getChildren().contains(txtArea)){
                addUI();
            }
        }
    }

    private void tidyTheBoard(){
        for(Node line:listOfLines){
            root.getChildren().remove(line);
        }
        for(Node point:listOfPoints){
            root.getChildren().remove(point);
        }
        for(Node num:listOfNums){
            root.getChildren().remove(num);
        }
        listOfPoints.clear();
        listOfLines.clear();
        listOfNums.clear();
    }

    private void stopAnimating(){
        isAnimating = false;
        isAddingLines = false;
        timer.stop();

    }

    private void generateOptionsPanel(){

        Text title = new Text("Opcje tablicy:");
        title.setFont(Font.font("Verdana",20));
        title.setX(optionsPanel.getX()+20);
        title.setY(30);
        title.setFill(Color.WHITE);

        Text toolsTxt = new Text("Narzędzia:");
        toolsTxt.setFont(Font.font("Verdana",17));
        toolsTxt.setX(optionsPanel.getX()+optionsPanel.getWidth()-180);
        toolsTxt.setY(80);
        toolsTxt.setFill(Color.WHITE);

        Text designsTxt = new Text("Motyw:");
        designsTxt.setFont(Font.font("Verdana",17));
        designsTxt.setX(toolsTxt.getX()-180);
        designsTxt.setY(80);
        designsTxt.setFill(Color.WHITE);

        Text commandsTxt = new Text("Komendy:");
        commandsTxt.setFont(Font.font("Verdana",17));
        commandsTxt.setX(optionsPanel.getX()+20);
        commandsTxt.setY(80);
        commandsTxt.setFill(Color.WHITE);

        Text commandsContent = new Text("\nsin n - sin(n) | cos n - cos(n) | tg n - tg(n) | log n - log(n)\n\n" +
                "n² - pow(n,2)   | √n - sqrt(n)    | |n| - abs(n)");
        commandsContent.setFont(Font.font("Verdana",13));
        commandsContent.setX(optionsPanel.getX()+20);
        commandsContent.setY(100);
        commandsContent.setFill(Color.WHITE);

        root.getChildren().add(optionsPanel);
        root.getChildren().add(title);
        root.getChildren().add(toolsTxt);
        root.getChildren().add(designsTxt);
        root.getChildren().add(commandsTxt);
        root.getChildren().add(commandsContent);
        root.getChildren().add(zbOpt);
        root.getChildren().add(rozbPOpt);
        root.getChildren().add(rozbMOpt);
        root.getChildren().add(darkDesignOpt);
        root.getChildren().add(paleDesignOpt);


        optionsPanelElements.add(optionsPanel);
        optionsPanelElements.add(title);
        optionsPanelElements.add(toolsTxt);
        optionsPanelElements.add(designsTxt);
        optionsPanelElements.add(commandsTxt);
        optionsPanelElements.add(commandsContent);
        optionsPanelElements.add(zbOpt);
        optionsPanelElements.add(rozbPOpt);
        optionsPanelElements.add(rozbMOpt);
        optionsPanelElements.add(darkDesignOpt);
        optionsPanelElements.add(paleDesignOpt);

    }

    private void removeOptionsPanel(){

        for(Node n:optionsPanelElements){
            root.getChildren().remove(n);
        }

        optionsPanelElements.clear();

    }

    private void addZbTools(){

        root.getChildren().add(epsilonLetter1);
        root.getChildren().add(epsilonLetter2);
        root.getChildren().add(limitLetter);
        root.getChildren().add(deltaLetter);

        root.getChildren().add(epsilonDistanceLine2);
        root.getChildren().add(epsilonDistanceLine1);
        root.getChildren().add(epsilonArea1);
        root.getChildren().add(epsilonArea2);

        root.getChildren().add(deltaLine);
        root.getChildren().add(epsilonLine1);
        root.getChildren().add(epsilonLine2);
        root.getChildren().add(limitLine);
    }

    private void removeZbTools(){

        refreshZbToolsCoordinates();

        root.getChildren().remove(epsilonLetter1);
        root.getChildren().remove(epsilonLetter2);
        root.getChildren().remove(limitLetter);
        root.getChildren().remove(deltaLetter);

        root.getChildren().remove(epsilonDistanceLine2);
        root.getChildren().remove(epsilonDistanceLine1);
        root.getChildren().remove(epsilonArea1);
        root.getChildren().remove(epsilonArea2);

        root.getChildren().remove(deltaLine);
        root.getChildren().remove(epsilonLine1);
        root.getChildren().remove(epsilonLine2);
        root.getChildren().remove(limitLine);
    }

    private void refreshZbToolsCoordinates(){
        deltaLine.setX(60);
        deltaLine.setY(50);

        limitLine.setX(x0);
        limitLine.setY(y0);

        epsilonLine1.setX(x0);
        epsilonLine1.setY(limitLine.getY()-50);

        epsilonLine2.setX(x0);
        epsilonLine2.setY(limitLine.getY()+50);

        epsilonDistanceLine1.setX(limitLine.getX()+limitLine.getWidth()-30);
        epsilonDistanceLine1.setY(epsilonLine1.getY());
        epsilonDistanceLine1.setHeight(limitLine.getY()-epsilonLine1.getY());

        epsilonDistanceLine2.setX(limitLine.getX()+limitLine.getWidth()-30);
        epsilonDistanceLine2.setY(limitLine.getY());
        epsilonDistanceLine2.setHeight(epsilonLine2.getY()-limitLine.getY());

        limitLetter.setX(limitLine.getX()+limitLine.getWidth()+5);
        limitLetter.setY(limitLine.getY()+5);

        deltaLetter.setX(deltaLine.getX()-3);
        deltaLetter.setY(deltaLine.getY()+deltaLine.getHeight()+20);

        epsilonLetter1.setX(limitLine.getX()+limitLine.getWidth()+5);
        epsilonLetter1.setY(epsilonLine1.getY()+(epsilonDistanceLine1.getHeight()/2));

        epsilonLetter2.setX(limitLine.getX()+limitLine.getWidth()+5);
        epsilonLetter2.setY(limitLine.getY()+(epsilonDistanceLine2.getHeight()/2));

        epsilonArea1.setX(deltaLine.getX());
        epsilonArea1.setY(epsilonLine1.getY());

        epsilonArea2.setX(deltaLine.getX());
        epsilonArea2.setY(limitLine.getY());

        epsilonArea1.setWidth(limitLine.getX()+limitLine.getWidth()-deltaLine.getX());
        epsilonArea1.setHeight(epsilonDistanceLine1.getHeight());

        epsilonArea2.setWidth(limitLine.getX()+limitLine.getWidth()-deltaLine.getX());
        epsilonArea2.setHeight(epsilonDistanceLine2.getHeight());
    }

    private void addRozbPTools(){

        root.getChildren().add(mLine);
        root.getChildren().add(mLetter);
        root.getChildren().add(deltaLine);
        root.getChildren().add(deltaLetter);
        root.getChildren().add(mRectUp);

        removeOptionsPanel();
        generateOptionsPanel();
    }
    private void addRozbMTools() {
        root.getChildren().add(mRectDown);
        root.getChildren().add(mLine);
        root.getChildren().add(mLetter);
        root.getChildren().add(deltaLine);
        root.getChildren().add(deltaLetter);

        removeOptionsPanel();
        generateOptionsPanel();
    }
    private void removeRozbPTools(){

        refreshRozbPToolsCoordinates();

        root.getChildren().remove(mLine);
        root.getChildren().remove(mLetter);
        root.getChildren().remove(deltaLine);
        root.getChildren().remove(deltaLetter);
        root.getChildren().remove(mRectUp);
    }
    private void removeRozbMTools() {

        refreshRozbMToolsCoordinates();

        root.getChildren().remove(mRectDown);
        root.getChildren().remove(mLine);
        root.getChildren().remove(mLetter);
        root.getChildren().remove(deltaLine);
        root.getChildren().remove(deltaLetter);
    }

    private void refreshRozbPToolsCoordinates(){
        mLine.setX(x0);
        mLine.setY(y0);
        deltaLine.setX(60);
        deltaLine.setY(50);
        mRectUp.setX(deltaLine.getX());
        mRectUp.setY(50);
        mRectUp.setWidth(mLine.getX()+mLine.getWidth()-deltaLine.getX());
        mRectUp.setHeight(mLine.getY()-50);
        mLetter.setX(mLine.getX()+mLine.getWidth()+5);
        mLetter.setY(mLine.getY()+5);
        deltaLetter.setX(deltaLine.getX()-3);
        deltaLetter.setY(deltaLine.getY()+deltaLine.getHeight()+20);
    }

    private void refreshRozbMToolsCoordinates(){
        mLine.setX(x0);
        mLine.setY(y0);
        deltaLine.setX(60);
        deltaLine.setY(50);
        mRectDown.setX(deltaLine.getX());
        mRectDown.setY(mLine.getY());
        mRectDown.setWidth(mLine.getX()+mLine.getWidth()-deltaLine.getX());
        mRectDown.setHeight(530-mLine.getY());
        mLetter.setX(mLine.getX()+mLine.getWidth()+5);
        mLetter.setY(mLine.getY()+5);
        deltaLetter.setX(deltaLine.getX()-3);
        deltaLetter.setY(deltaLine.getY()+deltaLine.getHeight()+20);
    }

    private void repaintDesingn(String type){

        if(isAnimating){
            stopAnimating();
        }
        tidyTheBoard();

        switch(type){
            case "white":

                linesColor = Color.BLACK;
                backgroundColor = Color.WHITE;
                textColor = Color.BLACK;
                pointColor = Color.RED;

                hamburger.setImage(new Image("img/option icon2.png"));
                bin.setImage(new Image("img/bin icon2.png"));

                upperText.setFill(textColor);
                xLine.setFill(linesColor);
                yLine.setFill(linesColor);
                xLetter.setFill(textColor);
                yLetter.setFill(textColor);

                background.setFill(backgroundColor);


                break;
            case "black":

                linesColor = Color.WHITE;
                backgroundColor = Color.BLACK;
                textColor = Color.WHITE;
                pointColor = Color.YELLOW;

                upperText.setFill(textColor);
                hamburger.setImage(new Image("img/options icon.png"));
                bin.setImage(new Image("img/bin icon.jpg"));
                xLine.setFill(linesColor);
                yLine.setFill(linesColor);
                xLetter.setFill(textColor);
                yLetter.setFill(textColor);

                background.setFill(backgroundColor);
                break;
            default:
        }
    }

}

