package ordermanagement;

import java.awt.SplashScreen;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import common.Book;

public class OrderManager extends Application {

	private SplashScreen splashScreen;

	public static String boxStyle = "-fx-background-color: #FFFFFF;" + "-fxborder-radius: 10px;"
			+ "-fx-padding: 10;";

	@Override
	public void init() throws Exception {
		splashScreen = SplashScreen.getSplashScreen();
	};

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane root = getRoot();
		stage.setScene(new Scene(root));

		splashScreen.close();
		stage.show();
		stage.requestFocus();
	}

	BorderPane getRoot() {
		BorderPane root = new BorderPane();

		Label title = new Label("O書店客注受付システム");
		VBox leftBlock = new VBox();
		ScrollPane centerBlock = getCenterBlock();

		root.setPrefSize(700, 500);
		root.setStyle("-fx-background-color: #8B4513;");
		title.setStyle("-fx-padding: 10;-fx-font-size: 20;-fx-text-fill: #FFFFFF;");
		leftBlock.setStyle(boxStyle);

		leftBlock.getChildren().add(new Label("画面は仮のものです"));

		root.setTop(title);
		root.setLeft(leftBlock);
		root.setCenter(centerBlock);

		return root;
	}

	ScrollPane getCenterBlock() {
		ScrollPane centerBlock = new ScrollPane();
		VBox container = new VBox(10);

		container.getChildren().add(new Button("\u2795 追加"));
		container.setAlignment(Pos.CENTER_RIGHT);

		try {
			addBook(container, new Book("1xMfNwAACAAJ"));
			addBook(container, new Book("_E4DvgAACAAJ"));
			addBook(container, new Book("Z9gjDAAAQBAJ"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		centerBlock.setStyle("-fx-fit-to-width: true;");
		container.setStyle("-fx-padding: 10;");

		centerBlock.setContent(container);

		return centerBlock;
	}

	void addBook(VBox container, Book book) {
		BorderPane pane = new BorderPane();
		GridPane orderTable = new GridPane();
		VBox buttonBlock = getButtonBlock(book);

		orderTable.add(new Label("書名"), 0, 0);
		orderTable.add(new Label(book.title), 1, 0);

		orderTable.add(new Label("出版社"), 0, 1);
		orderTable.add(new Label(book.publisher), 1, 1);

		orderTable.add(new Label("著者"), 0, 2);
		orderTable.add(new Label(book.author), 1, 2);

		orderTable.add(new Label("JAN (ISBN-13)"), 0, 3);
		orderTable.add(new Label(book.janCode), 1, 3);

		orderTable.add(new Label("ISBN-10/雑誌コード"), 0, 4);
		orderTable.add(new Label(book.ownCode), 1, 4);

		orderTable.add(new Label("在庫"), 0, 5);
		if (book.isStocked) {
			orderTable.add(new Label("あり"), 1, 5);
		} else {
			orderTable.add(new Label("なし"), 1, 5);
		}

		pane.setStyle(boxStyle);
		orderTable.setHgap(10);
		orderTable.setVgap(5);

		pane.setLeft(orderTable);
		pane.setRight(buttonBlock);

		container.getChildren().add(pane);
	}

	VBox getButtonBlock(Book book) {
		VBox buttonBlock = new VBox();
		HBox editButtonBox = new HBox();
		HBox actionButtonBox = new HBox(5);

		VBox.setVgrow(actionButtonBox, Priority.ALWAYS);

		editButtonBox.setAlignment(Pos.TOP_RIGHT);
		actionButtonBox.setAlignment(Pos.BOTTOM_RIGHT);

		Button editButton = new Button("\uD83D\uDD27");
		Button removeButton = new Button("\u2715");
		Button pubButton = new Button();
		Button actionButton = new Button("発注");

		editButton.setStyle("-fx-background-color: #FFFFFF;");
		removeButton.setStyle("-fx-background-color: #FFFFFF;");

		if (book.isInPrint) {
			pubButton.setText("刊行中");
			pubButton.setStyle("-fx-background-color: #ADFF2F");
		} else {
			pubButton.setText("刊行確認");
			actionButton.setDisable(true);
		}

		actionButton.setOnAction((ActionEvent) -> {
			order(book);
		});

		editButtonBox.getChildren().addAll(editButton, removeButton);
		actionButtonBox.getChildren().addAll(pubButton, actionButton);

		buttonBlock.getChildren().addAll(editButtonBox, actionButtonBox);

		return buttonBlock;
	}

	void order(Book book) {
		File file = new File("order.csv");
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));

			bw.write("長谷川萌美" + ",");
			bw.write("はせがわもえみ" + ",");
			bw.write("42" + ",");
			bw.write(book.publisher + ",");
			bw.write(book.janCode + ",");
			bw.write(book.title + ",");
			bw.write(book.author + ",");
			bw.write("1" + ",");

			bw.newLine();
			bw.close();

			String cmd = "cmd.exe /c start order.bat";
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}