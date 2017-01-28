package fakedpossystem;

import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import common.Book;
import common.Common;
import common.Item;
import common.Other;
import common.Store;

public class CashRegisterController extends Common implements Initializable {

	public ComboBox<Store> storeComboBox;
	public Label staffNameLabel;
	public Label clockLabel;
	public Label sumLabel;
	public Label changeLabel;
	public TextField staffNumField;
	public TextField requestNumField;
	public TextField requestDetNumField;
	public TextField janCodeField;
	public TextField cashField;
	public TextField ageField;
	public Button loginButton;
	public Button logoutButton;
	public Button confirmButton;
	public TableView<Item> purchaseTable;

	private ObservableList<Item> items = FXCollections.observableArrayList();

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		storeComboBox.setItems(stores);
		storeComboBox.setCellFactory(storeCellFactory);
		storeComboBox.setButtonCell(storeCellFactory.call(null));
		storeComboBox.getSelectionModel().selectFirst();

		loginButton.managedProperty().bind(loginButton.visibleProperty());
		logoutButton.managedProperty().bind(logoutButton.visibleProperty());

		purchaseTable.getColumns().get(0)
				.setCellValueFactory(new PropertyValueFactory<>("janCodeProperty"));
		purchaseTable.getColumns().get(1)
				.setCellValueFactory(new PropertyValueFactory<>("bookTitleProperty"));
		purchaseTable.getColumns().get(2)
				.setCellValueFactory(new PropertyValueFactory<>("priceProperty"));
		purchaseTable.getColumns().get(3)
				.setCellValueFactory(new PropertyValueFactory<>("discountProperty"));

		purchaseTable.setItems(items);

		autoReload.play();
	}

	@Override
	public void reload() {
		try {
			ResultSet rs = getRS("SELECT CURRENT_DATE");
			if (rs.next()) {
				clockLabel.setText(rs.getString("CURRENT_DATE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void login() {
		try {
			ResultSet rs = getRS("Select * FROM Staff WHERE StaffNum=" + staffNumField.getText());

			if (rs.next()) {
				staffNameLabel.setText(rs.getString("StaffName"));
				storeComboBox.getSelectionModel().select(rs.getInt("StoreNum"));

				staffNumField.setDisable(true);
				storeComboBox.setDisable(true);

				loginButton.setVisible(false);
				logoutButton.setVisible(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void logout() {
		staffNameLabel.setText("ログインしてください");

		staffNumField.setDisable(false);
		storeComboBox.setDisable(false);

		loginButton.setVisible(true);
		logoutButton.setVisible(false);
	}

	@FXML
	void addPurchase() {
		try {
			String sqlStr = "UPDATE Stock SET StockAmount = StockAmount - 1 WHERE StoreNum = "
					+ storeComboBox.getSelectionModel().getSelectedItem().storeNum
					+ " AND JANCode = " + janCodeField.getText();
			stmt.executeUpdate(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		addItem(janCodeField.getText());
		janCodeField.clear();
	}

	@FXML
	void addRequest() {
		String requestNum = requestNumField.getText();
		String requestDetNum = requestDetNumField.getText();

		if (requestNum.isEmpty() || requestDetNum.isEmpty()) {
			return;
		}

		Stage stage = new Stage();

		GridPane gridPane = new GridPane();
		Button button = new Button("OK");

		gridPane.setPadding(new Insets(10));
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		gridPane.add(new Label("名前"), 0, 0);
		gridPane.add(new Label("電話番号"), 0, 1);
		gridPane.add(new Label("住所"), 0, 2);
		gridPane.add(button, 0, 7);

		try {
			ResultSet rs = getRS("SELECT * FROM Request WHERE RequestNum=" + requestNum);

			if (rs.next()) {
				gridPane.add(new Label(rs.getString("Name")), 1, 0);
				gridPane.add(new Label(rs.getString("Phone")), 1, 1);
				gridPane.add(new Label("〒" + rs.getString("ZipCode")), 1, 2);
				gridPane.add(new Label(rs.getString("Pref")), 1, 3);
				gridPane.add(new Label(rs.getString("City")), 1, 4);
				gridPane.add(new Label(rs.getString("Address")), 1, 5);
				gridPane.add(new Label(rs.getString("Apartment")), 1, 6);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		button.setOnAction((ActionEvent) -> {
			try {
				String sqlStr = "SELECT * FROM RequestDetail WHERE RequestNum = " + requestNum
						+ " AND RequestDetNum = " + requestDetNum;
				ResultSet rs = getRS(sqlStr);

				if (rs.next()) {
					addItem(rs.getString("JANCode"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String sqlStr = "UPDATE RequestDetail SET DeliveryStat = 4 WHERE RequestNum = "
						+ requestNum + " AND RequestDetNum = " + requestDetNum;
				stmt.executeUpdate(sqlStr);
			} catch (Exception e) {
				e.printStackTrace();
			}

			requestNumField.clear();
			requestDetNumField.clear();

			stage.close();
		});

		Window window = requestNumField.getScene().getWindow();
		Scene scene = new Scene(gridPane);
		stage.setScene(scene);
		stage.initOwner(window);
		stage.show();
	}

	void addItem(String janCode) {
		try {
			ResultSet bookRS = getRS("SELECT * FROM Item,Book WHERE Item.JANCode = Book.JANCode and Item.JANCode = "
					+ janCode);

			if (bookRS.next()) {
				items.add(new Book(bookRS.getString("JANCode"), bookRS.getInt("Price"), bookRS
						.getInt("Discount"), bookRS.getString("BookTitle"), bookRS
						.getString("Writer"), bookRS.getString("Publisher"), bookRS
						.getString("googleID")));
			} else {
				ResultSet otherRS = getRS("SELECT * FROM Item,Other WHERE Item.JANCode = Other.JANCode and Item.JANCode = "
						+ janCode);
				if (otherRS.next()) {
					items.add(new Other(otherRS.getString("JANCode"), otherRS.getInt("Price"),
							otherRS.getInt("Discount"), otherRS.getString("Name"), otherRS
									.getString("Manufacturer"), otherRS.getString("Genre")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sumLabel.setText("");
		changeLabel.setText("");
		confirmButton.setDisable(true);
		cashField.setDisable(true);
	}

	@FXML
	void sum() {
		int sum = 0;

		for (Item product : items) {
			sum += product.price;
		}

		sumLabel.setText(String.valueOf(sum));
		changeLabel.setText("");
		cashField.setDisable(false);
		confirmButton.setDisable(false);
	}

	@FXML
	void confirm() {
		int change = Integer.parseInt(cashField.getText()) - Integer.parseInt(sumLabel.getText());

		String age = getTextWithNull(ageField);
		String staffNum = getTextWithNull(staffNumField);
		int storeNum = storeComboBox.getSelectionModel().getSelectedItem().storeNum;
		int saleNum = 0;

		try {
			ResultSet rs = getRS("SHOW TABLE STATUS WHERE Name = 'Sale'");
			if (rs.next()) {
				saleNum = rs.getInt("Auto_increment");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			String sqlStr = "INSERT INTO Sale VALUES(" + saleNum + ",CURRENT_DATE," + storeNum
					+ "," + staffNum + "," + age + ")";
			stmt.executeUpdate(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			try {
				String sqlStr = "INSERT INTO SaleDetail VALUES(" + saleNum + ",CURRENT_DATE,"
						+ storeNum + "," + i + "," + item.janCode + "," + item.price + ","
						+ item.discount + ")";
				stmt.executeUpdate(sqlStr);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		try {
			String sqlStr = "UPDATE Request,(SELECT DISTINCT RequestNum FROM Request "
					+ "WHERE RequestNum NOT IN (SELECT RequestNum FROM RequestDetail WHERE DeliveryStat<>4))Sub "
					+ "SET Request.ReceiptStat = true WHERE Request.RequestNum=Sub.RequestNum";
			stmt.executeUpdate(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		changeLabel.setText(String.valueOf(change));
		cashField.clear();
		ageField.clear();
		items.clear();
		confirmButton.setDisable(true);
		cashField.setDisable(true);
	}

	@FXML
	void press1() {
		press(java.awt.event.KeyEvent.VK_1);
	}

	@FXML
	void press2() {
		press(java.awt.event.KeyEvent.VK_2);
	}

	@FXML
	void press3() {
		press(java.awt.event.KeyEvent.VK_3);
	}

	@FXML
	void press4() {
		press(java.awt.event.KeyEvent.VK_4);
	}

	@FXML
	void press5() {
		press(java.awt.event.KeyEvent.VK_5);
	}

	@FXML
	void press6() {
		press(java.awt.event.KeyEvent.VK_6);
	}

	@FXML
	void press7() {
		press(java.awt.event.KeyEvent.VK_7);
	}

	@FXML
	void press8() {
		press(java.awt.event.KeyEvent.VK_8);
	}

	@FXML
	void press9() {
		press(java.awt.event.KeyEvent.VK_9);
	}

	@FXML
	void press0() {
		press(java.awt.event.KeyEvent.VK_0);
	}

	@FXML
	void press(int key) {
		try {
			Robot robot = new Robot();
			robot.keyPress(key);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}