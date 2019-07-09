package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        MenuBar menuBar = createMenu();

        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);

        menuPane.getChildren().add(menuBar);

        controller = loader.getController();
        controller.createPlayGround();


        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 4");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu(){
        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");

        MenuItem gameHelp = new MenuItem("Game Help");
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem aboutDev = new MenuItem("About Developer");
        gameHelp.setOnAction(event -> aboutGame());
        aboutDev.setOnAction(event -> aboutDev());

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> controller.exitGame());

        fileMenu.getItems().addAll(newGame,separatorMenuItem1,resetGame,separatorMenuItem2,exitGame);
        helpMenu.getItems().addAll(gameHelp,separatorMenuItem,aboutDev);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;
    }

    private void aboutDev() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Know the Developer");
        alert.setContentText("I am Prateep Dey. I am an engineering student currently pursuing engineering from Hooghly Engineering and Technology College. I love coding specially coding games. I hope you like my Project. Cheers!!");
        alert.show();
    }

    private void aboutGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("How To Play Connect 4");
        alert.setHeaderText("How to Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
        alert.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
