package fakedpossystem;

import java.awt.SplashScreen;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CashRegister extends Application {

	private SplashScreen splashScreen;
	CashRegisterController controller;

	@Override
	public void init() throws Exception {
		splashScreen = SplashScreen.getSplashScreen();
	};

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(
				"CashRegister.fxml"));
		BorderPane root = (BorderPane) loader.load();
		controller = loader.getController();

		splashScreen.close();
		stage.setScene(new Scene(root));
		stage.setTitle("O���X �G�ZPOS���W�V�X�e��");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
