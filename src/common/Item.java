package common;

import javafx.beans.property.SimpleStringProperty;

public class Item {

	public String janCode;
	public int price;
	public int discount;

	private SimpleStringProperty janCodeProperty;
	private SimpleStringProperty priceProperty;
	private SimpleStringProperty discountProperty;

	public Item(String janCode, int price, int discount) {
		this.janCode = janCode;
		this.price = price;

		janCodeProperty = new SimpleStringProperty(String.valueOf(janCode));
		priceProperty = new SimpleStringProperty(String.valueOf(price));
		discountProperty = new SimpleStringProperty(String.valueOf(discount));
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

}
