/*
Lasse Schöttner im Jahr 2022
*/

package com.lasse.view;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lasse.control.StageManager;
import com.lasse.model.SqlAbfrage;
import com.lasse.model.SqlAbfragen;
import com.lasse.model.SqlFeld;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

/**
 * Gespeicherte SQL-Abfrage bearbeiten
 * @author Lasse Schöttner
 */
@Component
public class SqlAbfrageView extends BasisEditController<SqlAbfrage> {
	@FXML
	private TextField name;
	@FXML
	private TextArea sql;
	@FXML
	private TableView<SqlFeld> tableView;
	@FXML
	protected ComboBox<String> msgLine2;

	@Lazy
	public SqlAbfrageView(StageManager stageManager) {
		super(stageManager);
	}

	public void init(Stage stage) {
		super.init(stage);
		stage.setTitle("SQL-Abfrage bearbeiten");
	}

	protected void initDaten() {
		SqlAbfrage sqlAbfrage = dekoderFx;
		if (mode == 1 || sqlAbfrage == null) {
			sqlAbfrage = new SqlAbfrage();
			sqlAbfrage.setNummer(0);
			dekoderFx = sqlAbfrage;
		}
		name.setText(sqlAbfrage.getName());
		sql.setText(sqlAbfrage.getSql());
		tableView.setEditable(true);
		TableColumn<SqlFeld, String> nameCol = new TableColumn<>("Bezeichnung Parameter");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));
		nameCol.setPrefWidth(500);
		nameCol.setMinWidth(10);
		nameCol.setMaxWidth(5000);
		nameCol.setResizable(true);
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		tableView.getColumns().addAll(nameCol);
		ObservableList<SqlFeld> observableList = FXCollections.observableList(dekoderFx.getFelder());
		tableView.setItems(observableList);
		nameCol.setOnEditCommit(event -> {
			SqlFeld f = event.getRowValue();
			f.setBezeichnung(event.getNewValue());
		});
	}

	@FXML
	public void initialize() {
		super.initialize();
	}

	protected void syncDekoderFx() {
		super.syncDekoderFx();
		if (mode == 3) {
			SqlAbfrage s = new SqlAbfrage();
			dekoderFx = s;
		}
		SqlAbfrage s = dekoderFx;
		s.setName(name.getText());
		s.setSql(sql.getText());
	}

	@FXML
	protected void save() {
		if (check()) {
			syncDekoderFx();
			SqlAbfragen sqlAbfragen = controller.getSqlAbfragen();
			SqlAbfrage dfx = getDekoderFx();
			if (mode == 1 || mode == 3)
				sqlAbfragen.getAbfragen().add(dfx);
			else {
				sqlAbfragen.getAbfragen().set(dfx.getNummer() - 1, dfx);
			}
			controller.setSqlAbfragen(sqlAbfragen);
			save = true;
			if (stage != null)
				stage.close();
		}
	}

	@FXML
	protected void cancel() {
		stage.close();
	}

	@FXML
	protected void delete() {
		SqlFeld f = tableView.getSelectionModel().getSelectedItem();
		if (f != null) {
			tableView.getItems().remove(f);
		}
	}

	@FXML
	protected void addParameter() {
		SqlFeld f = new SqlFeld();
		tableView.getItems().add(f);
		tableView.getSelectionModel().select(f);
	}

	@FXML
	protected void removeParameter() {
		if (tableView.getItems().size() > 0)
			tableView.getItems().removeLast();
	}

	/**
	 * Prüfen Eingabe
	 */
	private boolean check() {
		boolean ok = true;
		String s = sql.getText();
		long anzSqlPara = s.chars().filter(ch -> ch == '?').count();
		int anzPara = tableView.getItems().size();
		if (anzPara != anzSqlPara) {
			msgLine2.setValue(
					"Anzahl Parameter stimmt nicht überein! (in SQL: " + anzSqlPara + ", in Liste: " + anzPara + ")");
			ok = false;
		}
		return ok;
	}

}
