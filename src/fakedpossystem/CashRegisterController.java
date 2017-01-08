package fakedpossystem;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class CashRegisterController implements Initializable {
	@FXML
	ComboBox<String> storeComboBox;
	@FXML
	Label employeeLabel;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		storeComboBox.getItems().addAll("–{“X", "‰w‘O“X");
		storeComboBox.getSelectionModel().selectFirst();
	}

}