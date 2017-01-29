package common;

import java.sql.Date;

import javafx.beans.property.SimpleStringProperty;

public class Proceed {

	public Date date;
	public int amount;

	private SimpleStringProperty dateProperty;
	private SimpleStringProperty amountProperty;

	public Proceed(Date date, int amount) {
		this.date = date;
		this.amount = amount;

		dateProperty = new SimpleStringProperty(String.valueOf(date));
		amountProperty = new SimpleStringProperty(String.valueOf(amount));
	}

	public String getDateProperty() {
		return dateProperty.get();
	}

	public void setDateProperty(String date) {
		dateProperty.set(date);
	}

	public String getAmountProperty() {
		return amountProperty.get();
	}

	public void setAmountProperty(String amount) {
		amountProperty.set(amount);
	}
}
