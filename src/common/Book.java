package common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Book {

	public String id; // Google BooksのID
	public String title; // 書籍タイトル
	public String publisher; // 出版社
	public String author; // 作者
	public String publishedDate; // 出版日

	public String janCode; // JANコード(ISBN-13)
	public String ownCode; // ISBN-10、または雑誌コード

	public boolean isStocked;
	public boolean isInPrint;

	public static final Pattern PTN_ISBN10 = Pattern.compile("^\\d{9}(\\d|x)$");
	public static final Pattern PTN_ISBN13 = Pattern.compile("^\\d{13}$");

	public Book(String id) throws Exception {
		this.id = id;
		setInfoById(id);
		this.isInPrint = isInPrint();
	}

	public Book(String title, String publisher, String author, String code) throws Exception {
		this.title = title;
		this.publisher = publisher;
		this.author = author;
		setCode(code);
		this.isInPrint = isInPrint();
	}

	void setInfoById(String id) throws Exception {
		URL url = new URL("https://www.google.com/books/feeds/volumes/" + id);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(url.openStream());

		NodeList codeList = doc.getElementsByTagName("dc:identifier");
		for (int i = 0; i < codeList.getLength(); i++) {
			String code = codeList.item(i).getLastChild().getNodeValue().substring(5);

			if (PTN_ISBN10.matcher(code).find()) {
				this.ownCode = code;
			}
			if (PTN_ISBN13.matcher(code).find()) {
				this.janCode = code;
			}
		}

		title = getElementString(doc, "dc:title");
		publisher = getElementString(doc, "dc:publisher");
		author = getElementString(doc, "dc:creator");
		publishedDate = getElementString(doc, "dc:date");
	}

	String getElementString(Document doc, String tagName) {
		String str = "";
		NodeList nodeList = doc.getElementsByTagName(tagName);

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (i > 0)
				str += "　";
			str += nodeList.item(i).getLastChild().getNodeValue();
		}

		return str;
	}

	Boolean isInPrint() throws Exception {
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
	}

	void setCode(String code) {
		if (PTN_ISBN13.matcher(code).find()) {
			this.janCode = code;
			this.ownCode = getOwnCode(code);
		} else {
			this.janCode = getJanCode(code);
			this.ownCode = code;
		}
	}

	String getOwnCode(String janCode) {
		String number = janCode.substring(3, 12);
		String ownCheckDigit = getOwnCheckDigit(number);
		String ownCode = number + ownCheckDigit;
		return ownCode;
	}

	String getJanCode(String ownCode) {
		String number = "";

		if (PTN_ISBN10.matcher(ownCode).find()) {
			// ISBN10
			number = "978" + ownCode.substring(0, 9);
		} else {
			// 雑誌コード
			if (ownCode.charAt(0) == 'T') {
				// Tで始まる雑誌コードの場合
				ownCode = ownCode.substring(3, 10);
			}
			number = "4910" + ownCode + publishedDate.charAt(3);
		}

		String janCheckDigit = getJanCheckDigit(number);
		String janCode = number + janCheckDigit;
		return janCode;
	}

	String getOwnCheckDigit(String number) {
		int sum = 0;
		for (int i = 0; i < 9; i++) {
			int base = Integer.parseInt(number.substring(i, i + 1));
			int multi = 10 - i;
			sum += base * multi;
		}
		int check = 11 - (sum % 11);

		if (check == 10) {
			return "X";
		} else if (check == 11) {
			return "0";
		} else {
			return Integer.toString(check);
		}
	}

	String getJanCheckDigit(String number) {
		// 偶数桁の合計
		int evenSum = 0;
		for (int evenIndex = 0; evenIndex < number.length(); evenIndex += 2) {
			evenSum += Integer.parseInt(number.substring(evenIndex, evenIndex + 1));
		}

		// 奇数桁の合計
		int oddSum = 0;
		for (int oddIndex = 1; oddIndex < number.length(); oddIndex += 2) {
			oddSum += Integer.parseInt(number.substring(oddIndex, oddIndex + 1));
		}

		int checkSum = evenSum + (oddSum * 3);
		int checkDigit = 10 - (checkSum % 10); // 10から1の位を引く
		checkDigit %= 10; // 10は0にする

		return Integer.toString(checkDigit);
	}
}
