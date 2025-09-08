package com.lasse.view;


import java.io.IOException;
import java.util.function.Function;

import org.apache.log4j.Logger;

import com.lasse.control.Controller;
import com.lasse.model.Daten;
import com.lasse.model.DatenListe;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class BasisListe<TFX extends Daten, EDIT extends BasisEdit<TFX>> extends Stage {

	@FXML
	protected Button addButton;
	@FXML
	protected Button copyButton;
	@FXML
	protected Button updateButton;
	@FXML
	protected Button deleteButton;
	@FXML
	protected Button cancelButton;
	@FXML
	protected Button assignButton;
	@FXML
	protected TableView<TFX> tableView;
	protected String items;
	protected EDIT edit;
	private String nameFxml;
	private FXMLLoader fxmlLoader;
	private Node node;
	private boolean neuesFenster = false;
	protected Controller controller;
	protected int selNummer;
	protected boolean zuordnen = false;
	protected int selNummer2;
	protected boolean zuordnen2 = false;
	private static Logger logger = Logger.getLogger(BasisListe.class);

	/**
	 * Konstruktor für Node im bestehenden Fenster
	 * 
	 * @param items
	 * @param edit
	 */
	public BasisListe(String items, EDIT edit, Controller c) {
		this.items = items;
		this.edit = edit;
		controller = c;
		neuesFenster = false;
		setFxml(this.getClass().getSimpleName() + ".fxml");
	}

	/**
	 * Konstruktor für Node im bestehenden Fenster
	 * ohne Edit-Funktion
	 * 
	 * @param items
	 * @param edit
	 */
	public BasisListe(String items) {
		this.items = items;
		this.edit = null;
		neuesFenster = false;
		setFxml(this.getClass().getSimpleName() + ".fxml");
	}

	/**
	 * Konstruktor für neues Fenster
	 * 
	 * @param items
	 * @param edit
	 * @param modal
	 */
	public BasisListe(String items, EDIT edit, boolean modal) {
		this.items = items;
		this.edit = edit;
		neuesFenster = true;
		setFxml(this.getClass().getSimpleName() + ".fxml");
		if (modal)
			this.initModality(Modality.APPLICATION_MODAL);
		else
			this.initModality(Modality.NONE);
	}

	/**
	 * Konstruktor für neues Fenster mit Zuordnung
	 * 
	 * @param items
	 * @param edit
	 * @param modal
	 */
	public BasisListe(String items, EDIT edit, boolean modal, int selNummer, int conTyp) {
		this.items = items;
		this.edit = edit;
		this.selNummer = selNummer;
		zuordnen = true;
		neuesFenster = true;
		setFxml(this.getClass().getSimpleName() + ".fxml");
		if (modal)
			this.initModality(Modality.APPLICATION_MODAL);
		else
			this.initModality(Modality.NONE);
	}

	
	/**
	 * Konstruktor für neues Fenster mit Zuordnung
	 * 
	 * @param items
	 * @param edit
	 * @param modal
	 */
	public BasisListe(String items, EDIT edit, boolean modal, int selNummer, boolean zuordnen) {
		this.items = items;
		this.edit = edit;
		this.selNummer = selNummer;
		this.zuordnen = zuordnen;
		neuesFenster = true;
		setFxml(this.getClass().getSimpleName() + ".fxml");
		if (modal)
			this.initModality(Modality.APPLICATION_MODAL);
		else
			this.initModality(Modality.NONE);
	}

	/**
	 * Konstruktor für neues Fenster mit Zuordnung, 2
	 * 
	 * @param items
	 * @param edit
	 * @param modal
	 */
	public BasisListe(String items, EDIT edit, boolean modal, int selNummer, int selNummer2, boolean zuordnen2) {
		this.items = items;
		this.edit = edit;
		this.selNummer = selNummer;
		this.selNummer2 = selNummer2;
		zuordnen = true;
		zuordnen2 = true;
		neuesFenster = true;
		setFxml(this.getClass().getSimpleName() + ".fxml");
		if (modal)
			this.initModality(Modality.APPLICATION_MODAL);
		else
			this.initModality(Modality.NONE);
	}

	/**
	 * Konstruktor für neues Fenster
	 * ohne Edit
	 * 
	 * @param items
	 * @param edit
	 * @param modal
	 */
	public BasisListe(String items, boolean modal) {
		this.items = items;
		this.edit = null;
		neuesFenster = true;
		setFxml(this.getClass().getSimpleName() + ".fxml");
		if (modal)
			this.initModality(Modality.APPLICATION_MODAL);
		else
			this.initModality(Modality.NONE);
	}

	private void setFxml(String fxml) {
		try {
			nameFxml = fxml;
			fxmlLoader = new FXMLLoader(getClass().getResource(nameFxml));
			fxmlLoader.setController(this);
			if (neuesFenster) {
				try {
					setScene(new Scene((Parent) fxmlLoader.load()));
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			} else {
				node = fxmlLoader.load();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Node getNode() {
		return node;
	}

	@FXML
	protected void initialize() {
		tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				checkButton();
				if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
					update();
				}
			}
		});
		if (!zuordnen) {
			if (assignButton != null)
				assignButton.setVisible(false);
		}
		if (!neuesFenster) {
			if (cancelButton != null) cancelButton.setVisible(false);
		}
		checkButton();
	}

	protected TableColumn<TFX, String> createColumnString(String name, String bezeichnung, double prefWidth) {
		TableColumn<TFX, String> nameCol = new TableColumn<>(bezeichnung);
		nameCol.setCellValueFactory(new PropertyValueFactory<>(name));
		nameCol.setPrefWidth(prefWidth);
		nameCol.setMinWidth(10);
		nameCol.setMaxWidth(5000);
		nameCol.setResizable(true);
		return nameCol;
	}

	protected TableColumn<TFX, Integer> createColumnInteger(String name, String bezeichnung, double prefWidth) {
		TableColumn<TFX, Integer> nameCol = new TableColumn<>(bezeichnung);
		nameCol.setCellValueFactory(new PropertyValueFactory<>(name));
		nameCol.setPrefWidth(prefWidth);
		nameCol.setMinWidth(10);
		nameCol.setMaxWidth(5000);
		nameCol.setResizable(true);

		nameCol.setCellFactory(colx -> {
			TableCell<TFX, Integer> cell = new TableCell<TFX, Integer>() {
				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					// Cleanup the cell before populating it
					this.setText(null);
					this.setGraphic(null);
					if (!empty) {
						this.setText(item.toString());
					}
				}

			};
            cell.setStyle("-fx-alignment: CENTER-RIGHT;");
			return cell;
		});

		return nameCol;
	}

	protected TableColumn<TFX, Boolean> createColumnBoolean(String name, String bezeichnung, String textTrue,
			String textFalse, double prefWidth) {
		TableColumn<TFX, Boolean> col = new TableColumn<>(bezeichnung);
		col.setPrefWidth(prefWidth);
		col.setMinWidth(10);
		col.setMaxWidth(5000);
		col.setResizable(true);
		col.setCellValueFactory(new PropertyValueFactory<>(name));
		col.setCellFactory(colx -> {
			TableCell<TFX, Boolean> cell = new TableCell<TFX, Boolean>() {
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					// Cleanup the cell before populating it
					this.setText(null);
					this.setGraphic(null);
					if (!empty) {
						if (item.booleanValue()) {
							this.setText(textTrue);
						} else {
							this.setText(textFalse);
						}
					}
				}
			};
			return cell;
		});
		return col;
	}

	/**
	 * Spalte mit Button
	 * 
	 * @param name
	 * @param bezeichnung
	 * @param text
	 * @param function
	 * @param prefWidth
	 * @return
	 */
	protected TableColumn<TFX, String> createColumnButton(String name, String bezeichnung, String text,
			Function<TFX, TFX> function, double prefWidth) {
		TableColumn<TFX, String> col = new TableColumn<>(bezeichnung);
		col.setPrefWidth(prefWidth);
		col.setMinWidth(10);
		col.setMaxWidth(5000);
		col.setResizable(true);
		col.setCellValueFactory(new PropertyValueFactory<>(name));
		col.setCellFactory(new Callback<TableColumn<TFX, String>, TableCell<TFX, String>>() {

			@Override
			public TableCell<TFX, String> call(TableColumn<TFX, String> p) {
				ButtonCell<TFX> buttonCell = new ButtonCell<TFX>(text, function);
				return buttonCell;
			}

		});

		return col;
	}


	/**
	 * Spalte ComboBox
	 * @param name
	 * @param bezeichnung
	 * @param options
	 * @param prefWidth
	 * @return
	 */
	protected TableColumn<TFX, String> createColumnComboBox(String name, String bezeichnung, String[] options, double prefWidth) {
		TableColumn<TFX, String> col = new TableColumn<>(bezeichnung);
		col.setCellValueFactory(new PropertyValueFactory<>(name));
	    col.setPrefWidth(prefWidth);
		col.setMinWidth(10);
		col.setMaxWidth(5000);
		col.setResizable(true);
		col.setEditable(true);
		col.setCellFactory(ComboBoxTableCell.<TFX, String>forTableColumn(FXCollections.observableArrayList(options)));
		return col;
	}
	
	
	@FXML
	protected void add() {
		if (edit == null) return;
		edit.setDekoderFx(null);
		edit.setMode(1);
		edit.showAndWait();
		if (edit.isSave()) {
			DatenListe<TFX> datenListe = controller.getDatenListe(items);
			TFX dfx = edit.getDekoderFx();
			if (dfx.getNummer() == 0) {
				int nr = datenListe.getNewDatenNr();
				dfx.setNummer(nr);
			}
			datenListe.add(dfx);
			tableView.getItems().add(dfx);
			controller.speichern(items, 0);
		}
		checkButton();
	}

	@FXML
	protected void copy() {
		if (edit == null) return;
		TFX d0 = tableView.getSelectionModel().getSelectedItem();
		if (d0 != null) {
			edit.setDekoderFx(d0);
			edit.setMode(3);
			edit.showAndWait();
			if (edit.isSave()) {
				DatenListe<TFX> datenListe = controller.getDatenListe(items);
				TFX dfx = edit.getDekoderFx();
				if (dfx.getNummer() == 0) {
					int nr = datenListe.getNewDatenNr();
					dfx.setNummer(nr);
				}
				datenListe.add(dfx);
				tableView.getItems().add(dfx);
				controller.speichern(items, 0);
			}
		}
		checkButton();
	}

	@FXML
	protected void update() {
		if (edit == null) return;
		TFX d0 = tableView.getSelectionModel().getSelectedItem();
		if (d0 != null) {
			edit.setDekoderFx(d0);
			edit.setMode(2);
			edit.showAndWait();
			if (edit.isSave()) {
				DatenListe<TFX> datenListe = controller.getDatenListe(items);
				TFX dfx = edit.getDekoderFx();
				if (dfx.getNummer() == 0) {
					int nr = datenListe.getNewDatenNr();
					dfx.setNummer(nr);
					datenListe.init();
				}
				controller.speichern(items, 0);
			}
		}
		checkButton();
	}

	@FXML
	protected void delete() {
		TFX d0 = tableView.getSelectionModel().getSelectedItem();
		if (d0 != null) {
			DatenListe<TFX> datenListe = controller.getDatenListe(items);
			datenListe.remove(d0);
			tableView.getItems().remove(d0);
			controller.speichern(items, 0);
		}
		checkButton();
	}

	@FXML
	protected void cancel() {
		if (neuesFenster)
			this.close();
	}


	protected void checkButton() {
		if (tableView.getSelectionModel().getSelectedItem() != null) {
			if (updateButton != null)
				updateButton.setDisable(false);
			if (copyButton != null)
				copyButton.setDisable(false);
			if (deleteButton != null)
				deleteButton.setDisable(false);
		} else {
			if (updateButton != null)
				updateButton.setDisable(true);
			if (copyButton != null)
				copyButton.setDisable(true);
			if (deleteButton != null)
				deleteButton.setDisable(true);
		}
	}
}
