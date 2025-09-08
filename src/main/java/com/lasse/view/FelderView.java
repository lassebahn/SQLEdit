package com.lasse.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lasse.model.EventEinfuegenFeldSql1;
import com.lasse.model.Feld;
import com.lasse.viewmodel.SqlEditVM;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Liste mit Datenbank-Feldern anzeigen
 * @author Lasse Schöttner
 */
@Component
public class FelderView {

	@FXML
	private Button cancelButton;
	@FXML
	private Button insertButton;
	@FXML
	private Button findButton;
	@FXML
	private TableView<Feld> tableView;
	@FXML
	private MessageLine msgLine;
	@FXML
	private TextField findField;
	private int servernr;
	private ArrayList<Feld> felder;
	private TableColumn keyCol = new TableColumn("Key");
	private TableColumn nameCol = new TableColumn("Feld-Name");
	private TableColumn bezCol = new TableColumn("Bezeichnung");
	private TableColumn typCol = new TableColumn("Typ");
	private TableColumn laengeCol = new TableColumn("Länge");
	private TableColumn nkCol = new TableColumn("NK");
	private TableColumn schemaCol = new TableColumn("Schema");
	private TableColumn tabelleCol = new TableColumn("Tabelle");
	@Autowired
	private ApplicationEventPublisher publisher;

	@Lazy
	public FelderView() {
	}

	public void init(ArrayList<Feld> felder, int servernr) {
		this.felder = felder;
		this.servernr = servernr;
		ObservableList<Feld> data = FXCollections.observableArrayList(felder);
		tableView.setItems(data);
	}

	@FXML
	private void initialize() {
		tableView.getColumns().addAll(keyCol, nameCol, bezCol, typCol, laengeCol, nkCol, schemaCol, tabelleCol);
		keyCol.setCellValueFactory(new PropertyValueFactory<Feld, Integer>("key"));
		nameCol.setCellValueFactory(new PropertyValueFactory<Feld, String>("name"));
		bezCol.setCellValueFactory(new PropertyValueFactory<Feld, String>("bezeichnung"));
		typCol.setCellValueFactory(new PropertyValueFactory<Feld, String>("typ"));
		laengeCol.setCellValueFactory(new PropertyValueFactory<Feld, Integer>("laenge"));
		nkCol.setCellValueFactory(new PropertyValueFactory<Feld, Integer>("nk"));
		schemaCol.setCellValueFactory(new PropertyValueFactory<Feld, String>("schema"));
		tabelleCol.setCellValueFactory(new PropertyValueFactory<Feld, String>("tabelle"));

		findButton.setDefaultButton(true);
		tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 2) {
						insert();
					}
				}
			}
		});
	}

	@FXML
	private void insert() {
		String feld = tableView.getSelectionModel().getSelectedItem().getName();
		EventEinfuegenFeldSql1 vm = new EventEinfuegenFeldSql1(this, servernr, feld);
		publisher.publishEvent(vm);
	}

	@FXML
	private void cancel() {
		closeStage();
	}

	private void closeStage() {
	    Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
	}

	@FXML
	private void find() {
		String s = findField.getText().toLowerCase();
		if (!s.trim().equals("")) {
			int curr = tableView.getSelectionModel().getSelectedIndex();
			int i = SqlEditVM.findText(felder, curr, findField.getText());
			if (i == -1)
				msgLine.addText("Nichts gefunden für '" + s + "'");
			else if (i == curr)
				msgLine.addText("Einziges Ergebnis");
			else {
				tableView.getSelectionModel().select(i);
				TableViewSkin<?> ts = (TableViewSkin<?>) tableView.getSkin();
				VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(1);
				// int first = vf.getFirstVisibleCell().getIndex();
				// int last = vf.getLastVisibleCell().getIndex();
				// if((i - ((last - first) / 2)) >= 0) {
				// vf.scrollTo(i - ((last - first) / 2));
				// }
				vf.scrollTo(i);
			}
		}
	}

}
