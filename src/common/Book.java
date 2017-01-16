package common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Book extends Product {

	public String writer;
	public String publisher;
	public String isbn10;
	public String magazineCode;
	public String googleID;

	public boolean printing;
	public int stock;

	public Book(long janCode, int price, String productName, String writer, String publisher,
			String isbn10, String magazineCode, String googleID) {
		super(janCode, price, productName);

		this.writer = writer;
		this.publisher = publisher;
		this.isbn10 = isbn10;
		this.magazineCode = magazineCode;
		this.googleID = googleID;
	}

	public static Boolean isPrinting(long janCode) {
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
}
