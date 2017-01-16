package common;

public class OrderBook extends Book {

	public int orderNumber;
	public int orderDetailNumber;

	public int customerNumber;
	public String name;
	public String phone;
	public String mail;
	public int contact;

	public OrderBook(long janCode, int price, String productName, String writer, String publisher,
			String isbn10, String magazineCode, String googleID, int orderNumber,
			int orderDetailNumber, int customerNumber, String name, String phone, String mail,
			int contact) {
		super(janCode, price, productName, writer, publisher, isbn10, magazineCode, googleID);

		this.orderNumber = orderNumber;
		this.orderDetailNumber = orderDetailNumber;
		this.customerNumber = customerNumber;
		this.name = name;
		this.phone = phone;
		this.mail = mail;
		this.contact = contact;
	}

}
