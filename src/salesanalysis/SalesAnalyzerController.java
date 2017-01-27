package salesanalysis;

import java.net.URL;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import common.Book;
import common.Common;
import common.Store;

public class SalesAnalyzerController extends Common implements Initializable {
	public ComboBox<Store> storeComboBox;
	public ComboBox<String> periodComboBox;
	public TableView<Book> recentTableView;
	public TableView<Book> popularTableView;
	public TableView<Book> orderTableView;
	public TableView<Book> writerTableView;
	public ListView<String> writerListView;

	ObservableList<Book> recentBooks = FXCollections.observableArrayList();
	ObservableList<Book> popularBooks = FXCollections.observableArrayList();
	ObservableList<Book> orderBooks = FXCollections.observableArrayList();
	ObservableList<Book> writerBooks = FXCollections.observableArrayList();
	ObservableList<String> writers = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		storeComboBox.setItems(stores);
		storeComboBox.setCellFactory(storeCellFactory);
		storeComboBox.setButtonCell(storeCellFactory.call(null));
		storeComboBox.getSelectionModel().selectFirst();

		ObservableList<String> periodList = FXCollections.observableArrayList("ëSëÃ");
		periodComboBox.setItems(periodList);
		periodComboBox.getSelectionModel().selectFirst();

		recentTableView.setItems(recentBooks);
		popularTableView.setItems(popularBooks);
		orderTableView.setItems(orderBooks);
		writerTableView.setItems(writerBooks);
		writerListView.setItems(writers);

		writerListView
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(ObservableValue<? extends String> ov, String old_val, String new_val) -> {
							searchBooks(new_val);
						});

		setTableColumn(recentTableView);
		setTableColumn(popularTableView);
		setTableColumn(orderTableView);

		writerTableView.getColumns().get(0)
				.setCellValueFactory(new PropertyValueFactory<>("janCodeProperty"));
		writerTableView.getColumns().get(1)
				.setCellValueFactory(new PropertyValueFactory<>("titleProperty"));
		writerTableView.getColumns().get(2)
				.setCellValueFactory(new PropertyValueFactory<>("publisherProperty"));

		reload();
	}

	@FXML
	void reload() {
		try {
			String sqlStr = "SELECT saledetail.JANCode,saledetail.Price,ProductName,Writer,Publisher,ISBN10,MagazineCode,GoogleID FROM saledetail "
					+ "INNER JOIN product ON saledetail.JANCode = product.JANCode "
					+ "INNER JOIN book ON saledetail.JANCode = book.JANCode "
					+ "GROUP BY saledetail.JANCode ORDER BY SaleDate DESC";
			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				recentBooks.add(new Book(rs.getLong("JANCode"), rs.getInt("Price"), rs
						.getString("ProductName"), rs.getString("Writer"), rs
						.getString("Publisher"), rs.getString("ISBN10"), rs
						.getString("MagazineCode"), rs.getString("GoogleID")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String sqlStr = "SELECT saledetail.JANCode,saledetail.Price,ProductName,Writer,Publisher,ISBN10,MagazineCode,GoogleID FROM saledetail "
					+ "INNER JOIN product ON saledetail.JANCode = product.JANCode "
					+ "INNER JOIN book ON saledetail.JANCode = book.JANCode "
					+ "GROUP BY saledetail.JANCode ORDER BY count(*) DESC";
			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				popularBooks.add(new Book(rs.getLong("JANCode"), rs.getInt("Price"), rs
						.getString("ProductName"), rs.getString("Writer"), rs
						.getString("Publisher"), rs.getString("ISBN10"), rs
						.getString("MagazineCode"), rs.getString("GoogleID")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String sqlStr = "SELECT orderbookdetail.JANCode,Price,ProductName,Writer,Publisher,ISBN10,MagazineCode,GoogleID FROM orderbookdetail "
					+ "INNER JOIN product ON orderbookdetail.JANCode = product.JANCode "
					+ "INNER JOIN book ON orderbookdetail.JANCode = book.JANCode "
					+ "GROUP BY orderbookdetail.JANCode ORDER BY count(*) DESC";
			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				orderBooks.add(new Book(rs.getLong("JANCode"), rs.getInt("Price"), rs
						.getString("ProductName"), rs.getString("Writer"), rs
						.getString("Publisher"), rs.getString("ISBN10"), rs
						.getString("MagazineCode"), rs.getString("GoogleID")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String sqlStr = "SELECT Writer FROM saledetail "
					+ "INNER JOIN product ON saledetail.JANCode = product.JANCode "
					+ "INNER JOIN book ON saledetail.JANCode = book.JANCode "
					+ "WHERE Writer IS NOT NULL " + "GROUP BY Writer ORDER BY count(*) DESC";

			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				writers.add(rs.getString("Writer"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void setTableColumn(TableView<Book> tableView) {
		tableView.getColumns().get(0)
				.setCellValueFactory(new PropertyValueFactory<>("janCodeProperty"));
		tableView.getColumns().get(1)
				.setCellValueFactory(new PropertyValueFactory<>("titleProperty"));
		tableView.getColumns().get(2)
				.setCellValueFactory(new PropertyValueFactory<>("writerProperty"));
		tableView.getColumns().get(3)
				.setCellValueFactory(new PropertyValueFactory<>("publisherProperty"));
	}

	void searchBooks(String q) {
		try {
			writerBooks.clear();
			String q_encoded = URLEncoder.encode(q, "UTF-8");

			URL url = new URL("https://books.google.com/books/feeds/volumes?q=" + q_encoded);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(url.openStream());

			NodeList entries = doc.getElementsByTagName("entry");

			for (int i = 0; i < entries.getLength(); i++) {
				Element entry = (Element) entries.item(i);

				NodeList identifiers = entry.getElementsByTagName("dc:identifier");

				try {
					String googleID = identifiers.item(0).getLastChild().getNodeValue();
					long janCode = 1L;
					String isbn10 = "";

					int price = 0;
					/*NodeList prices = entry.getElementsByTagName("gbs:price");
					for (int j = 0; j < prices.getLength(); j++) {
						Element monies = (Element) prices.item(j);
						Element money = (Element) monies.getElementsByTagName("money").item(0);

						if (monies.getAttribute("type").equals("SuggestedRetailPrice")
								&& money.getAttribute("currencyCode").equals("JPY")) {
							price = Integer.parseInt(money.getNodeValue());
						}
					}*/

					String productName = getElementString(entry, "dc:title");
					String writer = getElementString(entry, "dc:creator");
					String publisher = getElementString(entry, "dc:publisher");

					writerBooks.add(new Book(janCode, price, productName, writer, publisher,
							isbn10, null, googleID));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String getElementString(Element element, String tagName) {
		String str = "";
		NodeList nodeList = element.getElementsByTagName(tagName);

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (i > 0)
				str += "Å@";
			str += nodeList.item(i).getLastChild().getNodeValue();
		}

		return str.trim();
	}
}
