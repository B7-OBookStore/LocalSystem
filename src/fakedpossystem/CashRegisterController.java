package fakedpossystem;

import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import common.Book;
import common.Common;
import common.Store;

public class CashRegisterController extends Common implements Initializable {

	public ComboBox<Store> storeComboBox;
	public Label employeeNameLabel;
	public Label clockLabel;
	public TextField employeeNumberField;
	public Button loginButton;
	public Button logoutButton;
	public TableView<Book> purchaseTable;

	private ObservableList<Book> purchaseBooks = FXCollections.observableArrayList();

	Callback<ListView<Store>, ListCell<Store>> storeCellFactory = (ListView<Store> param) -> new ListCell<Store>() {
		@Override
		protected void updateItem(Store item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null && !empty) {
				setText(item.storeName);
			}
		}
	};

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

		purchaseTable.setItems(purchaseBooks);
	}

	@FXML
	void login() {
		try {
			employeeNameLabel.setText(getEmployeeName(Integer.parseInt(employeeNumberField
					.getText())));
			employeeNumberField.setDisable(true);

			loginButton.setVisible(false);
			logoutButton.setVisible(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void logout() {
		employeeNameLabel.setText("ÉçÉOÉCÉìÇµÇƒÇ≠ÇæÇ≥Ç¢");
		employeeNumberField.setDisable(false);

		loginButton.setVisible(true);
		logoutButton.setVisible(false);
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