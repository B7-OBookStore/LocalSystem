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
import common.Product;
import common.Store;

public class CashRegisterController extends Common implements Initializable {

	public ComboBox<Store> storeComboBox;
	public Label employeeNameLabel;
	public Label clockLabel;
	public Label sumLabel;
	public Label changeLabel;
	public TextField employeeNumberField;
	public TextField orderNumberField;
	public TextField orderDetailNumberField;
	public TextField janCodeField;
	public TextField cashField;
	public TextField ageField;
	public Button loginButton;
	public Button logoutButton;
	public Button confirmButton;
	public TableView<Product> purchaseTable;

	private ObservableList<Product> products = FXCollections.observableArrayList();

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
				.setCellValueFactory(new PropertyValueFactory<>("titleProperty"));
		purchaseTable.getColumns().get(2)
				.setCellValueFactory(new PropertyValueFactory<>("priceProperty"));
		purchaseTable.getColumns().get(3)
				.setCellValueFactory(new PropertyValueFactory<>("discountProperty"));

		purchaseTable.setItems(products);
	}

	@FXML
	void login() {
		try {
			String sqlStr = "SELECT * FROM employee WHERE EmployeeNumber = "
					+ employeeNumberField.getText();
			ResultSet rs = stmt.executeQuery(sqlStr);

			if (rs.next()) {
				employeeNameLabel.setText(rs.getString("EmployeeName"));
				storeComboBox.getSelectionModel().select(rs.getInt("StoreNumber"));

				employeeNumberField.setDisable(true);
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
		employeeNameLabel.setText("ログインしてください");

		employeeNumberField.setDisable(false);
		storeComboBox.setDisable(false);

		loginButton.setVisible(true);
		logoutButton.setVisible(false);
	}

	@FXML
	void addPurchase() {
		try {
			String sqlStr = "UPDATE stock SET Num = Num - 1 WHERE StoreNumber = "
					+ storeComboBox.getSelectionModel().getSelectedItem().storeNumber
					+ " AND JANCode = " + janCodeField.getText();
			stmt.executeUpdate(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		addProduct(Long.parseLong(janCodeField.getText()));
		janCodeField.clear();
	}

	@FXML
	void addOrder() {
		String orderNumber = orderNumberField.getText();
		String orderDetailNumber = orderDetailNumberField.getText();

		if (orderNumber.isEmpty() || orderDetailNumber.isEmpty()) {
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
		gridPane.add(new Label("メールアドレス"), 0, 2);
		gridPane.add(button, 0, 3);

		try {
			String sqlStr = "SELECT * FROM orderbook WHERE OrderNumber = " + orderNumber;
			ResultSet rs = stmt.executeQuery(sqlStr);

			if (rs.next()) {
				gridPane.add(new Label(rs.getString("Name")), 1, 0);
				gridPane.add(new Label(rs.getString("Phone")), 1, 1);
				gridPane.add(new Label(rs.getString("Mail")), 1, 2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		button.setOnAction((ActionEvent) -> {
			try {
				String sqlStr = "SELECT * FROM orderbookdetail WHERE OrderNumber = " + orderNumber
						+ " AND OrderDetailNumber = " + orderDetailNumber;
				ResultSet rs = stmt.executeQuery(sqlStr);

				if (rs.next()) {
					addProduct(rs.getLong("JANCode"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String sqlStr = "UPDATE orderbookdetail SET DeliveryStatus = 2 WHERE OrderNumber = "
						+ orderNumber + " AND OrderDetailNumber = " + orderDetailNumber;
				stmt.executeUpdate(sqlStr);
			} catch (Exception e) {
				e.printStackTrace();
			}

			orderNumberField.clear();
			orderDetailNumberField.clear();

			stage.close();
		});

		Window window = orderNumberField.getScene().getWindow();
		Scene scene = new Scene(gridPane);
		stage.setScene(scene);
		stage.initOwner(window);
		stage.show();
	}

	void addProduct(long janCode) {
		try {
			String sqlStr = "SELECT * FROM product,book WHERE product.janCode = book.janCode and product.janCode = "
					+ janCode;
			ResultSet rs = stmt.executeQuery(sqlStr);
			rs.next();

			products.add(new Book(rs.getLong("JANCode"), rs.getInt("Price"), rs
					.getString("ProductName"), rs.getString("Writer"), rs.getString("Publisher"),
					rs.getString("ISBN10"), rs.getString("MagazineCode"), rs.getString("googleID")));
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

		for (Product product : products) {
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
		String employeeNumber = getTextWithNull(employeeNumberField);
		int saleNumber = 0;

		try {
			String sqlStr = "SELECT COUNT(*) FROM sale WHERE SaleDate = CURRENT_DATE";
			ResultSet rs = stmt.executeQuery(sqlStr);
			if (rs.next()) {
				saleNumber = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			String sqlStr = "INSERT INTO sale VALUES(" + saleNumber + ",CURRENT_DATE,"
					+ employeeNumber + "," + age + ")";
			stmt.executeUpdate(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < products.size(); i++) {
			Product product = products.get(i);
			try {
				String sqlStr = "INSERT INTO saledetail VALUES(" + saleNumber + ",CURRENT_DATE,"
						+ i + "," + product.janCode + "," + product.price + ",NULL)";
				stmt.executeUpdate(sqlStr);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		changeLabel.setText(String.valueOf(change));
		cashField.clear();
		ageField.clear();
		products.clear();
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