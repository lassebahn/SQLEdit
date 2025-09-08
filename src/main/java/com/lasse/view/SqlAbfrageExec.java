/*
Lasse Schöttner im Jahr 2022
Diese Software ist Eigentum der roha arzneimittel GmbH.
*/

package com.lasse.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lasse.control.Controller;
import com.lasse.control.StageManager;
import com.lasse.model.EventSqlRead;
import com.lasse.model.SqlAbfrage;
import com.lasse.model.SqlAbfragen;
import com.lasse.model.SqlFeld;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

/**
 * Gespeicherte SQL-Abfrage ausführen
 * @author Lasse Schöttner
 */
@Component
public class SqlAbfrageExec extends BasisEditController<SqlAbfrage> {
	@FXML
	private TextField name;
	@FXML
	private TextArea sql;
	@FXML
	private TableView<SqlFeld> tableView;
	@FXML
	protected ComboBox<String> msgLine2;
	@Autowired
	private ApplicationEventPublisher publisher;

	@Lazy
	public SqlAbfrageExec(StageManager stageManager) {
		super(stageManager);
	}

	public void init(Stage stage) {
		super.init(stage);
		stage.setTitle("SQL-Abfrage ausführen");
	}
	
	@Autowired
	public void setController(Controller c) {
		controller = c;
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
		nameCol.setPrefWidth(250);
		nameCol.setMinWidth(10);
		nameCol.setMaxWidth(5000);
		nameCol.setResizable(true);
		TableColumn<SqlFeld, String> wertCol = new TableColumn<>("Wert Parameter");
		wertCol.setCellValueFactory(new PropertyValueFactory<>("wert"));
		wertCol.setPrefWidth(250);
		wertCol.setMinWidth(10);
		wertCol.setMaxWidth(5000);
		wertCol.setResizable(true);
		wertCol.setCellFactory(TextFieldTableCell.forTableColumn());
		tableView.getColumns().addAll(nameCol, wertCol);
		ObservableList<SqlFeld> observableList = FXCollections.observableList(dekoderFx.getFelder());
		tableView.setItems(observableList);
		wertCol.setOnEditCommit(event -> {
			SqlFeld f = event.getRowValue();
			f.setWert(event.getNewValue());
		});
	}

	@FXML
	public void initialize() {
		super.initialize();
	}

	@FXML
	protected void exec() {
		int maxCol = SqlEditView.MAXCOL;
		stageManager.setCursor(Cursor.WAIT);
		EventSqlRead event = new EventSqlRead(this, getStagenr(), "", maxCol);
		event.setSqlAbfrage(dekoderFx);
		publisher.publishEvent(event);
		SqlAbfragen sqlAbfragen = controller.getSqlAbfragen();
		sqlAbfragen.getAbfragen().set(dekoderFx.getNummer() - 1, dekoderFx);
		controller.setSqlAbfragen(sqlAbfragen);
		stage.close();
	}

	@FXML
	protected void cancel() {
		stage.close();
	}


}
