package ordermanagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import common.Book;
import common.Common;
import common.Order;
import common.Request;
import common.Store;

public class OrderManagerController extends Common implements Initializable {

	public ScrollPane orderPane;
	public ScrollPane replenishmentPane;
	public TableView<Order> replenishmentTableView;
	public ComboBox<Store> storeComboBox;
	public VBox orderBox;
	public TextField janCodeField;
	public TextField amountField;
	public ObservableList<Order> replenishmentOrders = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		storeComboBox.setItems(stores);
		storeComboBox.setCellFactory(storeCellFactory);
		storeComboBox.setButtonCell(storeCellFactory.call(null));
		storeComboBox.getSelectionModel().selectFirst();

		orderPane.managedProperty().bind(orderPane.visibleProperty());
		replenishmentPane.managedProperty().bind(replenishmentPane.visibleProperty());
		replenishmentTableView.setItems(replenishmentOrders);

		replenishmentTableView.getColumns().get(0)
				.setCellValueFactory(new PropertyValueFactory<>("janCodeProperty"));
		replenishmentTableView.getColumns().get(1)
				.setCellValueFactory(new PropertyValueFactory<>("bookTitleProperty"));
		replenishmentTableView.getColumns().get(2)
				.setCellValueFactory(new PropertyValueFactory<>("writerProperty"));
		replenishmentTableView.getColumns().get(3)
				.setCellValueFactory(new PropertyValueFactory<>("publisherProperty"));
		replenishmentTableView.getColumns().get(4)
				.setCellValueFactory(new PropertyValueFactory<>("amountProperty"));
		viewOrder();

		reload();
	}

	@FXML
	@Override
	public void reload() {
		reloadOrder();
		loadCSV();
	}

	void reloadOrder() {
		// 客注画面をクリア
		orderBox.getChildren().clear();
		replenishmentOrders.clear();

		try {
			// データベースに問い合わせ
			String sqlStr = "SELECT RequestDetail.RequestNum,RequestDetNum,UserNum,Name,Phone,ZipCode,Pref,City,Address,Apartment,"
					+ "RequestDetail.JANCode,Price,Discount,BookTitle,Writer,Publisher,GoogleID,StockAmount "
					+ "FROM RequestDetail "
					+ "INNER JOIN Request ON RequestDetail.RequestNum = Request.RequestNum "
					+ "INNER JOIN Item ON RequestDetail.JANCode = Item.JANCode "
					+ "INNER JOIN Book ON RequestDetail.JANCode = Book.JANCode "
					+ "LEFT JOIN Stock ON RequestDetail.JANCode = Stock.JANCode AND Request.StoreNum = Stock.StoreNum "
					+ "WHERE (DeliveryStat = 0 OR DeliveryStat IS NULL) "
					+ "AND Request.StoreNum = "
					+ storeComboBox.getSelectionModel().getSelectedItem().storeNum;
			ResultSet rs = getRS(sqlStr);

			if (!rs.next()) {
				orderBox.getChildren().add(new Label("現在入っている客注はありません"));
			} else {
				do {
					// オブジェクトを作る
					Book book = new Book(rs.getString("JANCode"), rs.getInt("Price"),
							rs.getInt("Discount"), rs.getString("BookTitle"),
							rs.getString("Writer"), rs.getString("Publisher"),
							rs.getString("GoogleID"));
					Request request = new Request(book, rs.getInt("RequestNum"),
							rs.getInt("RequestDetNum"), rs.getInt("UserNum"), rs.getString("Name"),
							rs.getString("Phone"));
					book.stock = rs.getInt("StockAmount"); // 在庫数をセット

					// レイアウト用のコンテナを作る
					HBox hBox = new HBox(5);
					GridPane gridPane = new GridPane(); // 左側；書籍や注文者の情報を並べる部分
					VBox toolBox = new VBox(); // 右側；各種操作を行うボタンが入る部分
					HBox buttonBox = new HBox(); // 編集、削除ボタンが入る部分
					VBox actionBox = new VBox(5); // 調達に関するボタンが入る部分
					VBox stockBox = new VBox(2); // 調達に関するボタンの中で在庫のボタンが入る部分
					VBox awardBox = new VBox(2); // 調達に関するボタンの中で発注のボタンが入る部分

					// コントロールを作る
					Hyperlink edit = new Hyperlink("\uD83D\uDD27");
					Hyperlink remove = new Hyperlink("\u2715");
					Label stockLabel = new Label();
					Button reserveButton = new Button("在庫から取置");
					Button printCheckButton = new Button("刊行確認");
					Button orderButton = new Button("自動発注");

					{// 書籍や注文者の情報を設定
						// 見出し部分を設定
						gridPane.add(new Label("OrderNumber"), 0, 0);
						gridPane.add(new Label("OrderDetailNumber"), 0, 1);
						gridPane.add(new Separator(), 0, 2, 2, 1);
						gridPane.add(new Label("顧客番号"), 0, 3);
						gridPane.add(new Label("名前"), 0, 4);
						gridPane.add(new Label("電話番号"), 0, 5);
						gridPane.add(new Separator(), 0, 6, 2, 1);
						gridPane.add(new Label("JANコード"), 0, 7);
						gridPane.add(new Label("価格"), 0, 8);
						gridPane.add(new Label("書名"), 0, 9);
						gridPane.add(new Label("著者"), 0, 10);
						gridPane.add(new Label("出版社"), 0, 11);

						// 内容部分を設定
						gridPane.add(new Label(String.valueOf(request.requestNum)), 1, 0);
						gridPane.add(new Label(String.valueOf(request.requestDetNum)), 1, 1);
						gridPane.add(new Label(String.valueOf(request.userNum)), 1, 3);
						gridPane.add(new Label(request.name), 1, 4);
						gridPane.add(new Label(request.phone), 1, 5);
						gridPane.add(new Label(String.valueOf(book.janCode)), 1, 7);
						if (book.price == 0) {
							Label priceLabel = new Label("￥(登録してください)");
							priceLabel.setTextFill(Color.RED);
							gridPane.add(priceLabel, 1, 8);
						} else {
							gridPane.add(new Label("￥" + book.price), 1, 8);
						}
						gridPane.add(new Label(book.bookTitle), 1, 9);
						gridPane.add(new Label(book.writer), 1, 10);
						gridPane.add(new Label(book.publisher), 1, 11);
					}

					// コンテナのスタイルを設定
					VBox.setVgrow(buttonBox, Priority.ALWAYS);
					HBox.setHgrow(gridPane, Priority.ALWAYS);
					gridPane.setHgap(20);
					gridPane.getColumnConstraints().add(new ColumnConstraints(110));
					buttonBox.setAlignment(Pos.TOP_RIGHT);
					toolBox.setMinWidth(80);
					hBox.setMaxWidth(Double.MAX_VALUE);
					hBox.setPadding(new Insets(10));
					hBox.setStyle("-fx-background-color: #FFFFFF");
					hBox.getChildren().addAll(gridPane, toolBox);

					// コントロールのスタイルを設定
					stockLabel.setMaxWidth(Double.MAX_VALUE);
					stockLabel.setTextFill(Color.WHITE);
					stockLabel.getStyleClass().remove("label");
					stockLabel.getStyleClass().add("button");
					reserveButton.setMaxWidth(Double.MAX_VALUE);
					printCheckButton.setMaxWidth(Double.MAX_VALUE);
					orderButton.setMaxWidth(Double.MAX_VALUE);

					// 在庫ボタンを設定
					if (book.stock == 0) {
						// 在庫がないとき
						stockLabel.setText("在庫無");
						stockLabel.setStyle("-fx-background-color:red");
					} else {
						// 在庫があるとき
						stockLabel.setText("在庫有 (" + rs.getInt("StockAmount") + ")");
						stockLabel.setStyle("-fx-background-color:green");
					}

					// ボタンを押したときの処理を設定
					printCheckButton.setOnAction((ActionEvent) -> {
						if (Book.isPrinting(book.janCode)) {
							// 刊行中と確認できたとき
							printCheckButton.setText("刊行中");
							printCheckButton.setTextFill(Color.WHITE);
							printCheckButton.setStyle("-fx-background-color:green");
						} else {
							// 刊行中と確認できなかったとき
							printCheckButton.setText("絶版注意");
							printCheckButton.setTextFill(Color.WHITE);
							printCheckButton.setStyle("-fx-background-color:#B8860B");
						}
					});
					orderButton.setOnAction((ActionEvent) -> request(request));

					// コントロールをレイアウトに追加
					buttonBox.getChildren().addAll(edit, remove);
					stockBox.getChildren().addAll(new Label("在庫から調達"), stockLabel, reserveButton);
					awardBox.getChildren().addAll(new Label("発注して調達"), printCheckButton,
							orderButton);
					actionBox.getChildren().addAll(stockBox, awardBox);
					toolBox.getChildren().addAll(buttonBox, actionBox);
					orderBox.getChildren().add(hBox);
				} while (rs.next());
				Button addButton = new Button("\u2795追加");
				orderBox.getChildren().add(addButton);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 客注野郎に客注
	void request(Request request) {
		File file = new File("request.csv");
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));

			bw.write(request.name + ",");
			bw.write(request.requestNum + ",");
			bw.write(request.requestDetNum + ",");
			bw.write(request.book.publisher + ",");
			bw.write(request.book.janCode + ",");
			bw.write(request.book.bookTitle + ",");
			bw.write(request.book.writer + ",");
			bw.write("1" + ",");

			bw.newLine();
			bw.close();

			String cmd = "cmd.exe /c start request.bat";
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void order() {
		int orderNum = 0;
		try {
			ResultSet rs = getRS("SELECT COUNT(*) FROM Ordered WHERE StoreNum="
					+ storeComboBox.getSelectionModel().getSelectedItem().storeNum);
			if (rs.next()) {
				orderNum = rs.getInt(1);

				stmt.executeUpdate("INSERT INTO Ordered VALUES(" + orderNum + ","
						+ storeComboBox.getSelectionModel().getSelectedItem().storeNum
						+ ",'客注野郎.com',0)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < replenishmentOrders.size(); i++) {
			Order order = replenishmentOrders.get(i);

			try {
				ResultSet rs = getRS("SELECT * FROM Item,Book WHERE Item.JANCode=Book.JANCode AND Item.JANCode="
						+ order.book.janCode);
				if (!rs.next()) {
					stmt.executeUpdate("INSERT INTO Item VALUES(" + order.book.janCode + ","
							+ order.book.price + ",NULL)");
					stmt.executeUpdate("INSERT INTO Book VALUES(" + order.book.janCode + ","
							+ order.book.bookTitle + "," + order.book.writer + ","
							+ order.book.publisher + "," + order.book.googleID + ")");
				}

				stmt.executeUpdate("INSERT INTO OrderedDetail VALUES(" + orderNum + ","
						+ storeComboBox.getSelectionModel().getSelectedItem().storeNum + "," + i
						+ "," + order.book.janCode + "," + order.amount + ")");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		try {
			String cmd = "cmd.exe /c start order.bat";
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}

		replenishmentOrders.clear();
		saveCSV();
	}

	@FXML
	void addReplenishment() {
		try {
			ResultSet rs = getRS("SELECT Book.JANCode,Price,Discount,BookTitle,Writer,Publisher,GoogleID FROM Item "
					+ "INNER JOIN Book ON Item.JANCode = Book.JANCode WHERE Book.JANCode="
					+ janCodeField.getText());
			if (rs.next()) {
				Book book = new Book(rs.getString("JANCode"), rs.getInt("Price"),
						rs.getInt("Discount"), rs.getString("BookTitle"), rs.getString("Writer"),
						rs.getString("publisher"), rs.getString("GoogleID"));
				addReplenishmentList(book, Integer.valueOf(amountField.getText()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		saveCSV();
	}

	void addReplenishmentList(Book book, int amount) {
		for (Order order : replenishmentOrders) {
			if (order.book.janCode.equals(book.janCode)) {
				order.amount += amount;
				return;
			}
		}
		replenishmentOrders.add(new Order(book, amount));
	}

	void loadCSV() {
		File file = new File("order.csv");
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(file));

			String line = "";
			while ((line = br.readLine()) != null) {
				String[] order = line.split("\t");

				Book book = new Book(order[0], Integer.valueOf(order[1]),
						Integer.valueOf(order[2]), order[3], order[4], order[5], order[6]);
				replenishmentOrders.add(new Order(book, Integer.valueOf(order[7])));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void saveCSV() {
		File file = new File("order.csv");
		BufferedWriter bw;

		try {
			bw = new BufferedWriter(new FileWriter(file));

			for (Order order : replenishmentOrders) {
				bw.write(order.book.janCode + "\t");
				bw.write(order.book.price + "\t");
				bw.write(order.book.discount + "\t");
				bw.write(order.book.bookTitle + "\t");
				bw.write(order.book.writer + "\t");
				bw.write(order.book.publisher + "\t");
				bw.write(order.book.googleID + "\t");
				bw.write(order.amount + "");
				bw.newLine();
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void viewOrder() {
		orderPane.setVisible(true);
		replenishmentPane.setVisible(false);
	}

	@FXML
	void viewReplenishment() {
		orderPane.setVisible(false);
		replenishmentPane.setVisible(true);
	}

}
