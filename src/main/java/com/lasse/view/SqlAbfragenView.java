package com.lasse.view;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lasse.config.FxmlView;
import com.lasse.control.Controller;
import com.lasse.control.StageManager;
import com.lasse.model.SqlAbfrage;
import com.lasse.model.SqlAbfragen;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Gespeicherte SQL-Abfragen anzeigen als Liste
 * @author Lasse Schöttner
 */
@Component
public class SqlAbfragenView extends BasisListeController<SqlAbfrage> {
	@FXML
	protected TableView<SqlAbfrage> tableView;
	@FXML
	protected Button execButton;

	private boolean savePressed = false;

	@Lazy
	public SqlAbfragenView(StageManager stageManager) {
		super("SQLABFRAGEN", FxmlView.SQLABFRAGEVIEW, stageManager);
	}

	public void init(Stage stage) {
		super.init(stage);
		stage.setTitle("Gespeicherte SQL-Abfragen");
	}

	@FXML
	public void initialize() {
		super.initialize();
		TableColumn<SqlAbfrage, String> name = createColumnString("name", "Name", 200);
		TableColumn<SqlAbfrage, String> sql = createColumnString("sql", "SQL", 500);
		tableView.getColumns().addAll(name, sql);
		SqlAbfragen sqlAbfragen = controller.getSqlAbfragen();
		tableView.setItems(FXCollections.<SqlAbfrage>observableArrayList(sqlAbfragen.getAbfragen()));
		checkButton();
		setSelNummer(0);
	}

	@Autowired
	public void setController(Controller c) {
		controller = c;
	}

	@FXML
	private void execute() {
		SqlAbfrage sqlAbfrage = tableView.getSelectionModel().getSelectedItem();
		if (sqlAbfrage != null) {
			Stage stage = stageManager.createStage();
			stage.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader loader = stageManager.createFXMLLoader(FxmlView.SQLABFRAGEEXEC);
			SqlAbfrageExec e = loader.getController();
			e.init(stage);
			e.setMode(5);
			e.setDekoderFx(sqlAbfrage);
			e.setStagenr(getStagenr());
			stageManager.showStage(loader, stage, true);
		}
		stage.close();
	}

	public boolean isSavePressed() {
		return savePressed;
	}

	@FXML
	protected void cancel() {
		stage.close();
	}

	@FXML
	protected void add() {
		super.add();
		laden();
		SqlAbfrage sel = tableView.getItems().getLast();
		tableView.getSelectionModel().select(sel);
	}

	@FXML
	protected void copy() {
		super.copy();
		laden();
		SqlAbfrage sel = tableView.getItems().getLast();
		tableView.getSelectionModel().select(sel);
	}

	@FXML
	protected void update() {
		int index = tableView.getSelectionModel().getSelectedIndex();
		super.update();
		laden();
		tableView.getSelectionModel().select(index);
	}

	@FXML
	protected void delete() {
		SqlAbfrage d0 = tableView.getSelectionModel().getSelectedItem();
		SqlAbfragen sqlAbfragen = controller.getSqlAbfragen();
		sqlAbfragen.getAbfragen().remove(d0.getNummer() - 1);
		controller.setSqlAbfragen(sqlAbfragen);
		checkButton();
		laden();
	}

	private void laden() {
		SqlAbfragen sqlAbfragen = controller.getSqlAbfragen();
		tableView.getItems().clear();
		tableView.setItems(FXCollections.<SqlAbfrage>observableArrayList(sqlAbfragen.getAbfragen()));
	}
	
	@Override
	protected void checkButton() {
		super.checkButton();
		if (tableView.getSelectionModel().getSelectedItem() != null) {
			if (execButton != null)
				execButton.setDisable(false);
		} else {
			if (execButton != null)
				execButton.setDisable(true);
		}
	}
}
