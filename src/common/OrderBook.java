package common;

public class OrderBook extends Book {

	public int orderNumber;
	public int orderDetailNumber;

	public int customerNumber;
	public String name;
	public String phone;
	public String mail;
	public int contact;

	public OrderBook(String janCode, int price, int discount, String bookTitle, String writer,
			String publisher, String googleID, int orderNumber, int orderDetailNumber,
			int customerNumber, String name, String phone, String mail, int contact) {
		super(janCode, price, discount, bookTitle, writer, publisher, googleID);

		this.orderNumber = orderNumber;
		this.orderDetailNumber = orderDetailNumber;
		this.customerNumber = customerNumber;
		this.name = name;
		this.phone = phone;
		this.mail = mail;
		this.contact = contact;
	}

}
