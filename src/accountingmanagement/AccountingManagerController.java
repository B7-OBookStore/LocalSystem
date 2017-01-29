package accountingmanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

import common.Common;
import common.Proceed;
import common.Store;

public class AccountingManagerController extends Common implements Initializable {

	public ComboBox<Store> storeComboBox;
	public ComboBox<String> periodComboBox;
	public RadioButton allButton;
	public RadioButton storeButton;
	public TableView<Proceed> proceedTableView;
	public AreaChart<String, Number> proceedChart;
	ObservableList<Proceed> proceeds = FXCollections.observableArrayList();

	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		storeComboBox.setItems(stores);
		storeComboBox.setCellFactory(storeCellFactory);
		storeComboBox.setButtonCell(storeCellFactory.call(null));
		storeComboBox.getSelectionModel().selectFirst();

		ObservableList<String> periodList = FXCollections.observableArrayList("‘S‘Ì");
		periodComboBox.setItems(periodList);
		periodComboBox.getSelectionModel().selectFirst();

		ToggleGroup toggleGroup = new ToggleGroup();
		allButton.setToggleGroup(toggleGroup);
		storeButton.setToggleGroup(toggleGroup);
		storeButton.selectedProperty().set(true);

		proceedTableView.setItems(proceeds);

		proceedTableView.getColumns().get(0)
				.setCellValueFactory(new PropertyValueFactory<>("dateProperty"));
		proceedTableView.getColumns().get(1)
				.setCellValueFactory(new PropertyValueFactory<>("amountProperty"));

		reload();
	}

	@Override
	public void reload() {
		proceeds.clear();
		proceedChart.getData().clear();

		if (allButton.isSelected()) {
			reloadAll();
		} else {
			reloadOne();
		}
	}

	void reloadAll() {
		for (Store store : stores) {
			try {
				XYChart.Series<String, Number> series = new XYChart.Series<>();
				series.setName(store.storeName);

				String sqlStr = "SELECT SaleDetail.SaleDate,SUM(Price) AS Amount From SaleDetail "
						+ "INNER JOIN Sale ON SaleDetail.SaleNum = Sale.SaleNum "
						+ "WHERE SaleDetail.StoreNum=" + store.storeNum
						+ " GROUP BY SaleDetail.SaleDate ORDER BY SaleDate";
				ResultSet rs = getRS(sqlStr);

				while (rs.next()) {
					series.getData()
							.add(new Data<String, Number>(rs.getString("SaleDate"), rs
									.getInt("Amount")));
				}

				proceedChart.getData().add(series);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		try {
			String sqlStr = "SELECT SaleDetail.SaleDate,SUM(Price) AS Amount From SaleDetail "
					+ "INNER JOIN Sale ON SaleDetail.SaleNum = Sale.SaleNum "
					+ "GROUP BY SaleDetail.SaleDate ORDER BY SaleDate";
			ResultSet rs = getRS(sqlStr);

			while (rs.next()) {
				Proceed proceed = new Proceed(rs.getDate("SaleDate"), rs.getInt("Amount"));
				proceeds.add(proceed);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void reloadOne() {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName(storeComboBox.getSelectionModel().getSelectedItem().storeName);

		try {
			String sqlStr = "SELECT SaleDetail.SaleDate,SUM(Price) AS Amount From SaleDetail "
					+ "INNER JOIN Sale ON SaleDetail.SaleNum = Sale.SaleNum "
					+ "WHERE SaleDetail.StoreNum="
					+ storeComboBox.getSelectionModel().getSelectedItem().storeNum
					+ " GROUP BY SaleDetail.SaleDate ORDER BY SaleDate";
			ResultSet rs = getRS(sqlStr);

			while (rs.next()) {
				Proceed proceed = new Proceed(rs.getDate("SaleDate"), rs.getInt("Amount"));
				proceeds.add(proceed);

				series.getData().add(
						new Data<String, Number>(rs.getString("SaleDate"), rs.getInt("Amount")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		proceedChart.getData().add(series);
	}
}
