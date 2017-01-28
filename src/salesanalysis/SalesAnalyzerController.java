package salesanalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import common.Book;
import common.Common;
import common.Order;
import common.Sale;
import common.Store;

public class SalesAnalyzerController extends Common implements Initializable {
	public TextField amountTextField;

	public Label clockLabel;
	public ComboBox<Store> storeComboBox;
	public ComboBox<String> periodComboBox;
	public TableView<Sale> recentTableView;
	public TableView<Sale> popularTableView;
	public TableView<Sale> orderTableView;
	public TableView<Book> writerTableView;
	public ListView<String> writerListView;
	public TableView<Order> replenishmentTableView;
	public TabPane tabPane;

	ObservableList<Sale> recentSales = FXCollections.observableArrayList();
	ObservableList<Sale> popularSales = FXCollections.observableArrayList();
	ObservableList<Sale> orderSales = FXCollections.observableArrayList();
	ObservableList<Book> writerBooks = FXCollections.observableArrayList();
	ObservableList<String> writers = FXCollections.observableArrayList();
	ObservableList<Order> replenishmentOrders = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		storeComboBox.setItems(stores);
		storeComboBox.setCellFactory(storeCellFactory);
		storeComboBox.setButtonCell(storeCellFactory.call(null));
		storeComboBox.getSelectionModel().selectFirst();

		ObservableList<String> periodList = FXCollections.observableArrayList("‘S‘Ì");
		periodComboBox.setItems(periodList);
		periodComboBox.getSelectionModel().selectFirst();

		recentTableView.setItems(recentSales);
		popularTableView.setItems(popularSales);
		orderTableView.setItems(orderSales);
		writerTableView.setItems(writerBooks);
		writerListView.setItems(writers);
		replenishmentTableView.setItems(replenishmentOrders);

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

		recentTableView.getColumns().get(4)
				.setCellValueFactory(new PropertyValueFactory<>("saleDateProperty"));
		popularTableView.getColumns().get(4)
				.setCellValueFactory(new PropertyValueFactory<>("numProperty"));
		orderTableView.getColumns().get(4)
				.setCellValueFactory(new PropertyValueFactory<>("numProperty"));

		writerTableView.getColumns().get(0)
				.setCellValueFactory(new PropertyValueFactory<>("janCodeProperty"));
		writerTableView.getColumns().get(1)
				.setCellValueFactory(new PropertyValueFactory<>("bookTitleProperty"));
		writerTableView.getColumns().get(2)
				.setCellValueFactory(new PropertyValueFactory<>("publisherProperty"));

		replenishmentTableView.getColumns().get(0)
				.setCellValueFactory(new PropertyValueFactory<>("bookTitleProperty"));
		replenishmentTableView.getColumns().get(1)
				.setCellValueFactory(new PropertyValueFactory<>("amountProperty"));

		reload();
	}

	@FXML
	@Override
	public void reload() {
		recentSales.clear();
		popularSales.clear();
		orderSales.clear();
		writers.clear();
		replenishmentOrders.clear();

		try {
			String sqlStr = "SELECT Sale.SaleDate,Age,SaleDetail.JANCode,SaleDetail.Price,SaleDetail.Discount,BookTitle,Writer,Publisher,GoogleID FROM SaleDetail "
					+ "INNER JOIN Sale ON SaleDetail.SaleNum = Sale.SaleNum "
					+ "INNER JOIN Item ON SaleDetail.JANCode = Item.JANCode "
					+ "INNER JOIN Book ON SaleDetail.JANCode = Book.JANCode "
					+ "GROUP BY SaleDetail.JANCode ORDER BY SaleDate DESC";
			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				Book book = new Book(rs.getString("JANCode"), rs.getInt("Price"),
						rs.getInt("Discount"), rs.getString("BookTitle"), rs.getString("Writer"),
						rs.getString("Publisher"), rs.getString("GoogleID"));
				recentSales.add(new Sale(book, rs.getDate("SaleDate"), rs.getInt("Age"), 1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String sqlStr = "SELECT Sale.SaleDate,Age,SaleDetail.JANCode,SaleDetail.Price,SaleDetail.Discount,BookTitle,Writer,Publisher,GoogleID,COUNT(*) AS Count FROM SaleDetail "
					+ "INNER JOIN Sale ON SaleDetail.SaleNum = Sale.SaleNum "
					+ "INNER JOIN Item ON SaleDetail.JANCode = Item.JANCode "
					+ "INNER JOIN Book ON SaleDetail.JANCode = Book.JANCode "
					+ "GROUP BY SaleDetail.JANCode ORDER BY Count DESC";
			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				Book book = new Book(rs.getString("JANCode"), rs.getInt("Price"),
						rs.getInt("Discount"), rs.getString("BookTitle"), rs.getString("Writer"),
						rs.getString("Publisher"), rs.getString("GoogleID"));
				popularSales.add(new Sale(book, rs.getDate("SaleDate"), rs.getInt("Age"), rs
						.getInt("Count")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String sqlStr = "SELECT ReceiptLimit,RequestDetail.JANCode,Price,Discount,BookTitle,Writer,Publisher,GoogleID,COUNT(*) AS Count FROM RequestDetail "
					+ "INNER JOIN Request ON RequestDetail.RequestNum = Request.RequestNum "
					+ "INNER JOIN Item ON RequestDetail.JANCode = Item.JANCode "
					+ "INNER JOIN Book ON RequestDetail.JANCode = Book.JANCode "
					+ "GROUP BY RequestDetail.JANCode ORDER BY Count DESC";
			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				Book book = new Book(rs.getString("JANCode"), rs.getInt("Price"),
						rs.getInt("Discount"), rs.getString("BookTitle"), rs.getString("Writer"),
						rs.getString("Publisher"), rs.getString("GoogleID"));
				orderSales.add(new Sale(book, rs.getDate("ReceiptLimit"), 0, rs.getInt("Count")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String sqlStr = "SELECT Writer FROM SaleDetail "
					+ "INNER JOIN Item ON SaleDetail.JANCode = Item.JANCode "
					+ "INNER JOIN book ON SaleDetail.JANCode = Book.JANCode "
					+ "WHERE Writer IS NOT NULL " + "GROUP BY Writer ORDER BY count(*) DESC";

			ResultSet rs = stmt.executeQuery(sqlStr);

			while (rs.next()) {
				writers.add(rs.getString("Writer"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		loadCSV();
	}

	void setTableColumn(TableView<Sale> recentTableView2) {
		recentTableView2.getColumns().get(0)
				.setCellValueFactory(new PropertyValueFactory<>("janCodeProperty"));
		recentTableView2.getColumns().get(1)
				.setCellValueFactory(new PropertyValueFactory<>("bookTitleProperty"));
		recentTableView2.getColumns().get(2)
				.setCellValueFactory(new PropertyValueFactory<>("writerProperty"));
		recentTableView2.getColumns().get(3)
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

					for (int j = 0; j < identifiers.getLength(); j++) {
						String identifier = identifiers.item(j).getTextContent();
						if (identifier.matches("^ISBN:[0-9]{13}$")) {
							String janCode = identifier.substring(5);

							ResultSet rs = getRS("SELECT Price FROM Item WHERE JANCode=" + janCode);
							int price = 0;

							if (rs.next()) {
								price = rs.getInt("Price");
							} else {
								NodeList prices = entry.getElementsByTagName("gbs:price");
								for (int k = 0; k < prices.getLength(); k++) {
									Element monies = (Element) prices.item(k);
									Element money = (Element) monies.getElementsByTagName(
											"gd:money").item(0);

									if (monies.getAttribute("type").equals("SuggestedRetailPrice")
											&& money.getAttribute("currencyCode").equals("JPY")) {
										price = (int) Double.parseDouble(money
												.getAttribute("amount"));

									}
								}
							}

							if (price != 0) {
								String bookTitle = getElementString(entry, "dc:title", "@");
								String writer = getElementString(entry, "dc:creator", ",");
								String publisher = getElementString(entry, "dc:publisher", ",");

								writerBooks.add(new Book(janCode, price, 0, bookTitle, writer,
										publisher, googleID));
							}

							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String getElementString(Element element, String tagName, String delimiter) {
		String str = "";
		NodeList nodeList = element.getElementsByTagName(tagName);

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (i > 0)
				str += delimiter;
			str += nodeList.item(i).getLastChild().getNodeValue();
		}

		if (str.endsWith(delimiter)) {
			str = str.substring(0, str.lastIndexOf(delimiter));
		}

		return str;
	}

	@FXML
	void addReplenishment() {
		int index = tabPane.getSelectionModel().getSelectedIndex();
		Book book;

		switch (index) {
		case 0:
			book = recentTableView.getSelectionModel().getSelectedItem().book;
			replenishmentOrders.add(new Order(book, Integer.parseInt(amountTextField.getText())));
			break;
		case 1:
			book = popularTableView.getSelectionModel().getSelectedItem().book;
			replenishmentOrders.add(new Order(book, Integer.parseInt(amountTextField.getText())));
			break;
		case 2:
			book = orderTableView.getSelectionModel().getSelectedItem().book;
			replenishmentOrders.add(new Order(book, Integer.parseInt(amountTextField.getText())));
			break;
		case 3:
			book = writerTableView.getSelectionModel().getSelectedItem();
			replenishmentOrders.add(new Order(book, Integer.parseInt(amountTextField.getText())));
			break;
		}

		saveCSV();
	}

	void loadCSV() {
		File file = new File("order.csv");
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(file));

			String line = "";
			while ((line = br.readLine()) != null) {
				String[] order = line.split(",");

				Book book = new Book(order[0], Integer.valueOf(order[1]),
						Integer.valueOf(order[2]), order[3], order[4], order[5], order[6]);
				replenishmentOrders.add(new Order(book, Integer.valueOf(order[7])));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void saveCSV() {
		File file = new File("order.csv");
		BufferedWriter bw;

		try {
			bw = new BufferedWriter(new FileWriter(file));

			for (Order order : replenishmentOrders) {
				bw.write(order.book.janCode + ",");
				bw.write(order.book.price + ",");
				bw.write(order.book.discount + ",");
				bw.write(order.book.bookTitle + ",");
				bw.write(order.book.writer + ",");
				bw.write(order.book.publisher + ",");
				bw.write(order.book.googleID + ",");
				bw.write(order.amount + "");
				bw.newLine();
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
