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

public class Main extends Application {

    private Pane root;

    private Rectangle binOverlay;
    private Rectangle hamburgerOverlay;
    private Rectangle optionsPanel;
    private Rectangle deltaLine;
    private Rectangle limitLine;
    private Rectangle epsilonLine1;
    private Rectangle epsilonLine2;
    private Rectangle epsilonDistanceLine1;
    private Rectangle epsilonDistanceLine2;
    private Rectangle epsilonArea1;
    private Rectangle epsilonArea2;

    private Text upperText;
    private Text deltaLetter;
    private Text epsilonLetter1;
    private Text epsilonLetter2;
    private Text limitLetter;

    private TextArea txtArea;
    private TextArea txtArea2;
    private TextArea txtArea3;

    private ImageView bin;
    private ImageView hamburger;

    private Button drawButton;
    private Button scaleButton;
    private Button speedButton;

    private CheckBox zbOpt;
    private CheckBox rozbPOpt;
    private CheckBox rozbMOpt;

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

    private boolean blankPoint = false;
    private boolean isAddingLines = true;
    private boolean isAnimating = false;

    private AnimationTimer timer;

    private List<Node> listOfLines = new ArrayList<>();
    private List<Node> listOfPoints = new ArrayList<>();
    private List<Text> listOfNums = new ArrayList<>();
    private List<Node> optionsPanelElements = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createRoot()));
        stage.setTitle("Wykresy ciągów");
        stage.setWidth(1200);
        stage.setHeight(600);
        stage.setResizable(false);

        root.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm()
        );

        stage.show();

    }

    private Pane createRoot(){
        root = new Pane();

        //osie
        Rectangle xLine = new Rectangle( x0,y0,960,2);
        Rectangle yLine = new Rectangle(x0,50,2,480);

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
        root.getChildren().add(new Rectangle(1200,600, Color.BLACK));


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
        upperText.setFill(Color.WHITE);
        upperText.setFont(Font.font("Verdana",20));
        upperText.setX(420);
        upperText.setY(40);

        Text xLetter = new Text("x");
        xLetter.setFill(Color.WHITE);
        xLetter.setFont(Font.font("Monospaced",15));
        xLetter.setX(1000);
        xLetter.setY(y0+ 15);

        Text yLetter = new Text("y");
        yLetter.setFill(Color.WHITE);
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

        zbOpt = new CheckBox("Ciąg zbieżny");

        zbOpt.setTranslateX(optionsPanel.getX()+optionsPanel.getWidth()-180);
        zbOpt.setTranslateY(100);
        zbOpt.setTextFill(Color.WHITE);

        zbOpt.setOnMouseClicked(e->{
            if(rozbPOpt.isSelected()) {
                //removeRozbTools();
                rozbPOpt.setSelected(false);
            }

            if(rozbMOpt.isSelected()){
                rozbMOpt.setSelected(false);
            }

            if(!root.getChildren().contains(deltaLine)) addZbTools();
            else removeZbTools();
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
                rozbMOpt.setSelected(false);
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
                rozbPOpt.setSelected(false);
            }

        });

        root.getChildren().add(hamburger);
        root.getChildren().add(hamburgerOverlay);
        root.getChildren().add(bin);
        root.getChildren().add(binOverlay);
        root.getChildren().add(upperText);

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
            point = new Rectangle(pointSize, 2 , Color.WHITE);
            blankPoint = false;
        }
        else point = new Rectangle(pointSize, pointSize , Color.YELLOW);

        point.setX(x);
        point.setY(y);

        listOfPoints.add(point);
        root.getChildren().add(point);
    }

    private void initPointLines(double x, double y){
        Rectangle lineX = new Rectangle(x-50, 1 , Color.WHITE);

        Rectangle lineY;
        if(y<=y0) {
            lineY = new Rectangle(1, y0 - y, Color.WHITE);
        } else {
            lineY = new Rectangle(1, y-y0, Color.WHITE);
        }


        Text tX = new Text(""+(int)xn);
        Text tY = new Text(""+(double)Math.round(fx * 100d) / 100d);

        tX.setFill(Color.WHITE);
        tY.setFill(Color.WHITE);

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
                JOptionPane.showMessageDialog(null,"Program nie poradził sobie z tym równaniem.","Błąd",JOptionPane.WARNING_MESSAGE);
                System.exit(0);
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

        root.getChildren().add(optionsPanel);
        root.getChildren().add(title);
        root.getChildren().add(toolsTxt);
        root.getChildren().add(zbOpt);
        root.getChildren().add(rozbPOpt);
        root.getChildren().add(rozbMOpt);

        optionsPanelElements.add(optionsPanel);
        optionsPanelElements.add(title);
        optionsPanelElements.add(toolsTxt);
        optionsPanelElements.add(zbOpt);
        optionsPanelElements.add(rozbPOpt);
        optionsPanelElements.add(rozbMOpt);
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

}
