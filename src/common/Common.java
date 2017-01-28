package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.Duration;

public class Common {

	private static final String URL = "jdbc:mysql://127.0.0.1/websysb7";
	private static final String ID = "root";
	private static final String PASSWORD = "b7";

	protected Connection con;
	protected Statement stmt;

	protected Timeline autoReload;

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
		try {
			sqlConnect();
			setStores();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		autoReload = new Timeline(new KeyFrame(new Duration(10000), e -> {
			reload();
		}));
		autoReload.setCycleCount(Timeline.INDEFINITE);
	}

	public void enableAutoReload() {
		if (autoReload.getStatus() == Status.STOPPED) {
			autoReload.play();
		} else {
			autoReload.stop();
		}
	}

	public void sqlConnect() throws SQLException {
		try {
			con = DriverManager.getConnection(URL, ID, PASSWORD);
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sqlClose() throws SQLException {
		con.close();
		stmt.close();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO 自動生成されたメソッド・スタブ
		return super.clone();
	}

	public void reload() {
	}

	public ResultSet getRS(String sql) throws SQLException {
		if (con.isClosed() || stmt.isClosed()) {
			sqlConnect();
		}
		ResultSet rs = stmt.executeQuery(sql);

		return rs;
	};

	public void setStores() throws SQLException {
		String sqlStr = "SELECT * FROM Store";
		ResultSet rs = getRS(sqlStr);

		while (rs.next()) {
			stores.add(new Store(rs.getInt("StoreNum"), rs.getString("StoreName")));
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
