package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String DISC_COLOUR_ONE = "#24303E";
	private static final String DISC_COLOUR_TWO = "#4CAA88";
	private static String PLAYER_ONE = "Player One";
	private static String PLAYER_TWO = "Player Two";

	private boolean isPlayerOneTurn = true;
	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];
	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public Label playerTurnLabel;

	@FXML
	public Button setPlayerName;
	@FXML
	public TextField playerOneName,playerTwoName;


	private boolean isAllowedToInsert=true;




	public void createPlayGround() {

		setPlayerName.setOnAction(event -> {
			PLAYER_ONE = playerOneName.getText();
			PLAYER_TWO = playerTwoName.getText();
			playerNameLabel.setText(PLAYER_ONE);
		});


		Shape shape = gameStructuralGrid();
		rootGridPane.add(shape, 0, 1);

		List<Rectangle> rectangleList = clickableColumns();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}

	}

	private Shape gameStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

		for (int row = 0; row < ROWS; row++) {
			for (int columns = 0; columns < COLUMNS; columns++) {

				Circle circle = new Circle();
				circle.setRadius(40);
				circle.setCenterX(40);
				circle.setCenterY(40);
				circle.setSmooth(true);

				circle.setTranslateX(columns * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> clickableColumns() {

		List<Rectangle> rectangleList = new ArrayList<>();
		for (int i = 0; i < COLUMNS; i++) {

			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(i * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = i;
			rectangle.setOnMouseClicked(event -> {
				if(isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc, int column) {
		int row = ROWS - 1;
		while (row >= 0) {
			if (getDiscIfPresent(row,column) == null)
				break;

			row--;
		}
		if (row < 0) {
			return;
		}
		insertedDiscArray[row][column] = disc;
		insertedDiscsPane.getChildren().addAll(disc);
		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		int currentRow = row;
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true;
			if (gameEnded(currentRow, column)) {
				gameOver();
				return;
			}

			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
		});
		translateTransition.play();
	}

	private void gameOver() {
		String winner = isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO;
		System.out.println("Winner is : "+winner);

		Alert winnerAlert = new Alert(Alert.AlertType.INFORMATION);
		winnerAlert.setTitle("Connect 4");
		winnerAlert.setHeaderText(winner + " is the winner.");
		winnerAlert.setContentText("Want to play Again?");

		Platform.runLater(()->{
			ButtonType yesButton = new ButtonType("Yes");
			ButtonType noButton = new ButtonType("No, Exit");
			winnerAlert.getButtonTypes().setAll(yesButton,noButton);
			Main main = new Main();
			Optional<ButtonType> btnclicked = winnerAlert.showAndWait();
			if(btnclicked.isPresent() && btnclicked.get() == yesButton){
				resetGame();
			}
			else{
				exitGame();
			}
		});

	}

	public void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	public void resetGame() {
		insertedDiscsPane.getChildren().clear();
		for (int row = 0; row < insertedDiscArray.length; row++){
			for(int column = 0; column < insertedDiscArray[row].length; column++ ){
				insertedDiscArray[row][column]= null;
			}
		}
		isPlayerOneTurn = true;
		playerNameLabel.setText(PLAYER_ONE);
		createPlayGround();
	}

	private static class Disc extends Circle {
		private final boolean isPlayerOneMove;

		Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove ? Color.valueOf(DISC_COLOUR_ONE) : Color.valueOf(DISC_COLOUR_TWO));
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);
		}
	}

	private boolean gameEnded(int row, int column) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
				.mapToObj(r -> new Point2D(r, column))
				.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
				.mapToObj(c -> new Point2D(row, c))
				.collect(Collectors.toList());
		Point2D startpoint1 = new Point2D(row-3,column+3);
		List<Point2D> diagonal1points = IntStream.rangeClosed(0,6).mapToObj(i -> startpoint1.add(i,-i)).collect(Collectors.toList());
		Point2D startpoint2 = new Point2D(row-3,column-3);
		List<Point2D> diagonal2points = IntStream.rangeClosed(0,6).mapToObj(i -> startpoint2.add(i,i)).collect(Collectors.toList());
		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)||checkCombinations(diagonal1points)||checkCombinations(diagonal2points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;

		for (Point2D point: points) {

			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {  // if the last inserted Disc belongs to the current player

				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}

		return false;
	}

	private Disc getDiscIfPresent(int row, int column){
		if(row>=ROWS || row < 0 ||  column >= COLUMNS || column < 0)
			return null;

		return insertedDiscArray[row][column];
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {


	}
}

