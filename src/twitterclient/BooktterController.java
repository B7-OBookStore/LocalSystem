package twitterclient;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import common.Book;
import common.Common;
import common.Store;

public class BooktterController extends Common implements Initializable {

	static final String CONSUMER_KEY = "Fpo4TGs1kFCStfmgTizvTBA3j";
	static final String CONSUMER_SECRET = "owXu5AAHffbbUBcsprqoGHX3t3ok6NSwZYM3jgKUzq4e9aNhwe";
	static final String ACCESS_TOKEN = "825582766939009026-xorW3aeDVWLXnU5LBb4JzBvkLe7qAqf";
	static final String ACCESS_TOKEN_SECRET = "CPmEdmkHlTdwG0KPqmcRXUCdNZsFnFw6xhsBfzALY5tJa";

	@FXML
	public VBox popularBox;
	@FXML
	public VBox arrivalBox;
	@FXML
	public VBox timelineBox;

	public ComboBox<Store> storeComboBox;
	public ComboBox<String> periodComboBox;
	public TextArea tweetArea;
	public ImageView tweetImgView;

	private Twitter twitter;
	private File tweetImgFile;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		storeComboBox.setItems(stores);
		storeComboBox.setCellFactory(storeCellFactory);
		storeComboBox.setButtonCell(storeCellFactory.call(null));
		storeComboBox.getSelectionModel().selectFirst();

		ObservableList<String> periodList = FXCollections.observableArrayList("ëSëÃ");
		periodComboBox.setItems(periodList);
		periodComboBox.getSelectionModel().selectFirst();

		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		twitter.setOAuthAccessToken(accessToken);

		reload();
		updateTimeline();
	}

	@FXML
	@Override
	public void reload() {
		popularBox.getChildren().clear();
		arrivalBox.getChildren().clear();

		try {
			String sqlStr = "SELECT SaleDetail.JANCode,SaleDetail.Price,SaleDetail.Discount,BookTitle,Writer,Publisher,GoogleID,COUNT(*) AS Count FROM SaleDetail "
					+ "INNER JOIN Sale ON SaleDetail.SaleNum = Sale.SaleNum "
					+ "INNER JOIN Item ON SaleDetail.JANCode = Item.JANCode "
					+ "INNER JOIN Book ON SaleDetail.JANCode = Book.JANCode "
					+ "WHERE SaleDetail.StoreNum="
					+ storeComboBox.getSelectionModel().getSelectedItem().storeNum
					+ " GROUP BY SaleDetail.JANCode ORDER BY Count DESC";
			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				Book book = new Book(rs.getString("JANCode"), rs.getInt("Price"),
						rs.getInt("Discount"), rs.getString("BookTitle"), rs.getString("Writer"),
						rs.getString("Publisher"), rs.getString("GoogleID"));

				VBox bookBox = getBookBox(book, "îÑÇÍÇƒÇ‹Ç∑ÅI");
				popularBox.getChildren().add(bookBox);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String sqlStr = "SELECT OrderedDetail.JANCode,Price,Discount,BookTitle,Writer,Publisher,GoogleID FROM Ordered "
					+ "INNER JOIN OrderedDetail ON Ordered.OrderNum=OrderedDetail.OrderNum AND Ordered.StoreNum=OrderedDetail.StoreNum "
					+ "INNER JOIN Item ON OrderedDetail.JANCode=Item.JANCode "
					+ "INNER JOIN Book ON OrderedDetail.JANCode=Book.JANCode "
					+ "WHERE Ordered.StoreNum="
					+ storeComboBox.getSelectionModel().getSelectedItem().storeNum
					+ " AND DeliveryStat=4 "
					+ "GROUP BY OrderedDetail.JANCode ORDER BY Ordered.OrderNum DESC";
			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				Book book = new Book(rs.getString("JANCode"), rs.getInt("Price"),
						rs.getInt("Discount"), rs.getString("BookTitle"), rs.getString("Writer"),
						rs.getString("Publisher"), rs.getString("GoogleID"));

				VBox bookBox = getBookBox(book, "ì¸â◊ÇµÇ‹ÇµÇΩÅI");
				arrivalBox.getChildren().add(bookBox);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	VBox getBookBox(Book book, String str) {
		VBox bookBox = new VBox(2);
		Label label = new Label(book.bookTitle);

		ImageView imgView = new ImageView();

		try {
			URL url = new URL("http://books.google.com/books/content?id=" + book.googleID
					+ "&printsec=frontcover&img=1&zoom=5&source=gbs_api");

			Platform.runLater(() -> {
				Image img = new Image(url.toString());
				imgView.setImage(img);
			});

			bookBox.setOnMouseClicked((MouseEvent) -> {
				tweetArea.setText("Åy"
						+ storeComboBox.getSelectionModel().getSelectedItem().storeName + "Åz\n"
						+ str + "\n" + book.bookTitle + "\n(" + book.writer + ")");
				Image img = new Image(url.toString());
				tweetImgView.setImage(img);

				try {
					BufferedImage bi = ImageIO.read(url);
					tweetImgFile = new File("thumbnail.jpg");
					ImageIO.write(bi, "jpg", tweetImgFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		label.setMaxWidth(128);
		bookBox.getStyleClass().add("bookBox");

		bookBox.getChildren().addAll(imgView, label);

		return bookBox;
	}

	void updateTimeline() {
		timelineBox.getChildren().clear();

		try {
			ResponseList<Status> statuses;
			statuses = twitter.getUserTimeline();

			for (Status status : statuses) {
				HBox tweetBox = new HBox(5);
				Image img = new Image(status.getUser().getProfileImageURL());
				ImageView imgView = new ImageView(img);
				Label label = new Label(status.getText());

				tweetBox.getChildren().addAll(imgView, label);
				timelineBox.getChildren().add(tweetBox);
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void tweet() {
		StatusUpdate su = new StatusUpdate(tweetArea.getText());
		if (tweetImgFile != null) {
			su.setMedia(tweetImgFile);
		}
		try {
			twitter.updateStatus(su);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		tweetArea.clear();
		removeImg();
		updateTimeline();
	}

	@FXML
	void removeImg() {
		tweetImgView.setImage(null);
		tweetImgFile = null;
	}
}
