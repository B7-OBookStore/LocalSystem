package fakedpossystem;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import com.mysql.jdbc.Statement;
import common.Common;
import common.Store;

public class CashRegisterController extends Common implements Initializable {

	Connection con;
	Statement stmt;

	public ComboBox<Store> storeComboBox;

	public Label employeeNameLabel;
	public Label clockLabel;
	public TextField employeeNumberField;
	public Button loginButton;
	public Button logoutButton;

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
}