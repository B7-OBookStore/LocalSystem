package accountingmanagement;

import java.awt.SplashScreen;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AccountingManager extends Application {

	private SplashScreen splashScreen;
	AccountingManagerController controller;

	@Override
	public void init() throws Exception {
		splashScreen = SplashScreen.getSplashScreen();
	};

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(
				"AccountingManager.fxml"));
		BorderPane root = (BorderPane) loader.load();
		controller = loader.getController();

		if (splashScreen != null)
			splashScreen.close();
		stage.setScene(new Scene(root));
		stage.setTitle("O‘“X ”„ã•ªÍƒVƒXƒeƒ€");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}