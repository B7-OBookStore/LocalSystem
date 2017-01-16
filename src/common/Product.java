package common;

import javafx.beans.property.SimpleStringProperty;

public class Product {

	public long janCode;
	public int price;
	public String productName;

	private SimpleStringProperty janCodeProperty;
	private SimpleStringProperty productNameProperty;
	private SimpleStringProperty priceProperty;
	private SimpleStringProperty discountProperty;

	public Product(long janCode, int price, String productName) {
		this.janCode = janCode;
		this.price = price;
		this.productName = productName;

		janCodeProperty = new SimpleStringProperty(String.valueOf(janCode));
		productNameProperty = new SimpleStringProperty(productName);
		priceProperty = new SimpleStringProperty(String.valueOf(price));
		discountProperty = new SimpleStringProperty("");
	}

	public String getJanCodeProperty() {
		return janCodeProperty.get();
	}

	public void setJanCodeProperty(String janCode) {
		janCodeProperty.set(janCode);
	}

	public String getTitleProperty() {
		return productNameProperty.get();
	}

	public void setTitleProperty(String title) {
		productNameProperty.set(title);
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
