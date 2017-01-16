package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class Common {

	private static final String URL = "jdbc:mysql://ja-cdbr-azure-east-a.cloudapp.net/b7_obookstore";
	private static final String ID = "b62d87cb5623a5";
	private static final String PASSWORD = "6d93d6d8";

	protected Connection con;
	protected Statement stmt;

	protected ObservableList<Store> stores = FXCollections.observableArrayList();

	protected Callback<ListView<Store>, ListCell<Store>> storeCellFactory = (ListView<Store> param) -> new ListCell<Store>() {
		@Override
		protected void updateItem(Store item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null && !empty) {
				setText(item.storeName);
			}
		}
	};

	public Common() {
		sqlConnect();

		try {
			setStores();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sqlConnect() {
		try {
			con = DriverManager.getConnection(URL, ID, PASSWORD);
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sqlClose() {
		try {
			con.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setStores() throws SQLException {
		String sqlStr = "SELECT * FROM store";
		ResultSet rs = stmt.executeQuery(sqlStr);

		while (rs.next()) {
			stores.add(new Store(rs.getInt("StoreNumber"), rs.getString("StoreName")));
		}
	}

	public String getEmployeeName(int EmployeeNumber) throws SQLException {
		String sqlStr = "SELECT EmployeeName FROM employee WHERE EmployeeNumber = "
				+ EmployeeNumber;
		ResultSet rs = stmt.executeQuery(sqlStr);
		rs.next();

		return rs.getString("EmployeeName");
	}

	public int getNextNumber(String column, String table) throws SQLException {
		String sqlStr = "SELECT MAX(" + column + ") FROM " + table;
		ResultSet rs = stmt.executeQuery(sqlStr);
		rs.next();

		return rs.getInt(column) + 1;
	}

	public String getTextWithNull(TextField textField) {
		if (textField.getText().isEmpty()) {
			return null;
		} else {
			return textField.getText();
		}
	}
}
