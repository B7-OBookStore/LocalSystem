package common;

import javafx.beans.property.SimpleStringProperty;

public class Other extends Item {

	String name;
	String manufacturer;
	String genre;

	private SimpleStringProperty bookTitleProperty; // BookTitleという命名はおかしいが、CashRegisterで表示する際の互換性維持のため

	public Other(String janCode, int price, int discount, String name, String manufacturer,
			String genre) {
		super(janCode, price, discount);

		this.name = name;
		this.manufacturer = manufacturer;
		this.genre = genre;

		bookTitleProperty = new SimpleStringProperty(name);
	}

	public String getBookTitleProperty() {
		return bookTitleProperty.get();
	}

	public void setBookTitleProperty(String bookTitle) {
		bookTitleProperty.set(bookTitle);
	}

}
