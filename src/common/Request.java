package common;

public class Request {

	public Book book;
	public int requestNum;
	public int requestDetNum;
	public int userNum;
	public String name;
	public String phone;

	public Request(Book book, int requestNum, int requestDetNum, int userNum, String name,
			String phone) {
		this.book = book;
		this.requestNum = requestNum;
		this.requestDetNum = requestDetNum;
		this.userNum = userNum;
		this.name = name;
		this.phone = phone;
	}

}
