package common;

import javafx.beans.property.SimpleStringProperty;

public class Other extends Item {

	String name;
	String manufacturer;
	String genre;

	private SimpleStringProperty bookTitleProperty; // BookTitle�Ƃ��������͂����������ACashRegister�ŕ\������ۂ̌݊����ێ��̂���

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
