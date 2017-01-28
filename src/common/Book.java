package common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javafx.beans.property.SimpleStringProperty;

public class Book extends Item {

	public String bookTitle;
	public String writer;
	public String publisher;
	public String googleID;

	public boolean printing;
	public int stock;

	private SimpleStringProperty bookTitleProperty;
	private SimpleStringProperty writerProperty;
	private SimpleStringProperty publisherProperty;

	public Book(String janCode, int price, int discount, String bookTitle, String writer,
			String publisher, String googleID) {
		super(janCode, price, discount);

		this.bookTitle = bookTitle;
		this.writer = writer;
		this.publisher = publisher;
		this.googleID = googleID;

		bookTitleProperty = new SimpleStringProperty(bookTitle);
		writerProperty = new SimpleStringProperty(writer);
		publisherProperty = new SimpleStringProperty(publisher);
	}

	public static Boolean isPrinting(String janCode) {
		try {
			URL url = new URL(
					"http://www.books.or.jp/ResultList.aspx?scode=&searchtype=1&showcount=1&startindex=0&isbn="
							+ janCode);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream input = connection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			StringBuffer buf = new StringBuffer();

			while (reader.readLine() != null) {
				buf.append(reader.readLine());
				buf.append("\n");
			}

			String html = buf.toString();

			int index = html.indexOf("<div id=\"htcFoundCount\" class=\"num\">");
			String count = html.substring(index + 36, index + 37);

			if (count.equals("0")) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
}
