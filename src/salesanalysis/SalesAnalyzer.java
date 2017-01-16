package salesanalysis;

import java.awt.SplashScreen;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ordermanagement.OrderManagerController;

public class SalesAnalyzer extends Application {

	private SplashScreen splashScreen;
	OrderManagerController controller;

	@Override
	public void init() throws Exception {
		splashScreen = SplashScreen.getSplashScreen();
	};

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(
				"OrderManager.fxml"));
		BorderPane root = (BorderPane) loader.load();
		controller = loader.getController();

		if (splashScreen != null)
			splashScreen.close();
		stage.setScene(new Scene(root));
		stage.setTitle("O���X ���㕪�̓V�X�e��");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}