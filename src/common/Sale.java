package common;

import java.sql.Date;

import javafx.beans.property.SimpleStringProperty;

public class Sale {

	public Book book;
	public Date saleDate;
	public int age;
	public int num;

	private SimpleStringProperty janCodeProperty;
	private SimpleStringProperty priceProperty;
	private SimpleStringProperty discountProperty;
	private SimpleStringProperty bookTitleProperty;
	private SimpleStringProperty writerProperty;
	private SimpleStringProperty publisherProperty;
	private SimpleStringProperty saleDateProperty;
	private SimpleStringProperty numProperty;

	public Sale(Book book, Date saleDate, int age, int num) {
		this.book = book;
		this.saleDate = saleDate;
		this.age = age;
		this.num = num;

		janCodeProperty = new SimpleStringProperty(book.janCode);
		priceProperty = new SimpleStringProperty(String.valueOf(book.price));
		discountProperty = new SimpleStringProperty(String.valueOf(book.discount));
		bookTitleProperty = new SimpleStringProperty(book.bookTitle);
		writerProperty = new SimpleStringProperty(book.writer);
		publisherProperty = new SimpleStringProperty(book.publisher);
		saleDateProperty = new SimpleStringProperty(String.valueOf(saleDate));
		numProperty = new SimpleStringProperty(String.valueOf(num));
	}

	public String getJanCodeProperty() {
		return janCodeProperty.get();
	}

	public void setJanCodeProperty(String janCode) {
		janCodeProperty.set(janCode);
	}

	public String getPriceProperty() {
		return priceProperty.get();
	}

	public void setPriceProperty(String price) {
		priceProperty.set(price);
	}

	public String getDiscountProperty() {
		return discountProperty.get();
	}

	public void setDiscountProperty(String discount) {
		discountProperty.set(discount);
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

	public String getSaleDateProperty() {
		return saleDateProperty.get();
	}

	public void setSaleDaterProperty(String saleDate) {
		saleDateProperty.set(saleDate);
	}

	public String getNumProperty() {
		return numProperty.get();
	}

	public void setNumProperty(String num) {
		numProperty.set(num);
	}
}
