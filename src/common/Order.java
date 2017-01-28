package common;

import javafx.beans.property.SimpleStringProperty;

public class Order {

	public Book book;
	public int amount;

	private SimpleStringProperty janCodeProperty;
	private SimpleStringProperty bookTitleProperty;
	private SimpleStringProperty writerProperty;
	private SimpleStringProperty publisherProperty;
	private SimpleStringProperty amountProperty;

	public Order(Book book, int amount) {
		this.book = book;
		this.amount = amount;

		janCodeProperty = new SimpleStringProperty(book.janCode);
		bookTitleProperty = new SimpleStringProperty(book.bookTitle);
		writerProperty = new SimpleStringProperty(book.writer);
		publisherProperty = new SimpleStringProperty(book.publisher);
		amountProperty = new SimpleStringProperty(String.valueOf(amount));
	}

	public String getJanCodeProperty() {
		return janCodeProperty.get();
	}

	public void setJanCodeProperty(String janCode) {
		janCodeProperty.set(janCode);
	}

	public String getBookTitleProperty() {
		return bookTitleProperty.get();
	}

	public void setBookTitleProperty(String bookTitle) {
		bookTitleProperty.set(bookTitle);
	}

	public String getWriterProperty() {
		return writerProperty.get();
	}

	public void setWriterProperty(String writer) {
		writerProperty.set(writer);
	}

	public String getPublisherProperty() {
		return publisherProperty.get();
	}

	public void setPublisherProperty(String publisher) {
		publisherProperty.set(publisher);
	}

	public String getAmountProperty() {
		return amountProperty.get();
	}

	public void setAmoutProperty(String amount) {
		amountProperty.set(amount);
	}
}
