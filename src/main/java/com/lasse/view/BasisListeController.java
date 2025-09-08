package com.lasse.view;

import java.util.function.Function;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.lasse.config.FxmlView;
import com.lasse.control.Controller;
import com.lasse.control.StageManager;
import com.lasse.model.Daten;
import com.lasse.model.DatenListe;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

/**
 * Basisklasse JavaFX Liste
 * @param <TFX>
 */
public class BasisListeController<TFX extends Daten> {

	@FXML
	protected Button addButton;
	@FXML
	protected Button copyButton;
	@FXML
	protected Button updateButton;
	@FXML
	protected Button deleteButton;
	@FXML
	protected Button assignButton;
	@FXML
	protected Button cancelButton;
	@FXML
	protected TableView<TFX> tableView;
	protected String items;
	protected FxmlView fxmlViewEdit;
	protected Controller controller;
	protected int selNummer;
	protected boolean zuordnen = false;
	protected int selNummer2;
	protected boolean zuordnen2 = false;
	protected final StageManager stageManager;
	@Autowired
	private ApplicationContext context;
	private int stagenr;
	protected Stage stage;
	private static Logger logger = Logger.getLogger(BasisListeController.class);

	/**
	 * Konstruktor für Node im bestehenden Fenster
	 * 
	 * @param items
	 * @param edit
	 */
	public BasisListeController(String items, FxmlView fxmlViewEdit, StageManager stageManager) {
		this.items = items;
		this.fxmlViewEdit = fxmlViewEdit;
		this.stageManager = stageManager;
	}

	public void init(Stage stage) {
		this.stage = stage;
	}

	@Autowired
	public void setController(Controller c) {
		controller = c;
	}

	public int getStagenr() {
		return stagenr;
	}

	public void setStagenr(int stagenr) {
		this.stagenr = stagenr;
	}

	@FXML
	public void initialize() {
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
	 * 
	 * @param name
	 * @param bezeichnung
	 * @param options
	 * @param prefWidth
	 * @return
	 */
	protected TableColumn<TFX, String> createColumnComboBox(String name, String bezeichnung, String[] options,
			double prefWidth) {
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
		if (stage == null) {
			BasisEditController<TFX> e = stageManager.switchToNextScene(fxmlViewEdit, stagenr);
			e.setDekoderFx(null);
			e.setMode(1);
			e.setStagenr(stagenr);
			e.setItems(items);
		} else {
			Stage stage = stageManager.createStage();
			stage.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader loader = stageManager.createFXMLLoader(fxmlViewEdit);
			BasisEditController<TFX> e = loader.getController();
			e.setMode(1);
			e.init(stage);
			e.setDekoderFx(null);
			e.setStagenr(stagenr);
			e.setItems(items);
			stageManager.showStage(loader, stage, true);
		}
	}

	@FXML
	protected void copy() {
		TFX d0 = tableView.getSelectionModel().getSelectedItem();
		if (d0 != null) {
			if (stage == null) {
				FXMLLoader loader = stageManager.switchToNextScene(fxmlViewEdit, stagenr);
				BasisEditController<TFX> e = loader.getController();
				e.setMode(3);
				d0.setNummer(0);
				e.setDekoderFx(d0);
				e.setStagenr(stagenr);
				e.setItems(items);
			} else {
				Stage stage = stageManager.createStage();
				stage.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader loader = stageManager.createFXMLLoader(fxmlViewEdit);
				BasisEditController<TFX> e = loader.getController();
				e.init(stage);
				e.setMode(3);
				d0.setNummer(0);
				e.setDekoderFx(d0);
				e.setStagenr(stagenr);
				e.setItems(items);
				stageManager.showStage(loader, stage, true);
			}
		}
	}

	@FXML
	protected void update() {
		TFX d0 = tableView.getSelectionModel().getSelectedItem();
		if (d0 != null) {
			if (stage == null) {
				BasisEditController<TFX> e = stageManager.switchToNextScene(fxmlViewEdit, stagenr);
				e.setMode(2);
				e.setDekoderFx(d0);
				e.setStagenr(stagenr);
				e.setItems(items);
			} else {
				Stage stage = stageManager.createStage();
				stage.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader loader = stageManager.createFXMLLoader(fxmlViewEdit);
				BasisEditController<TFX> e = loader.getController();
				e.init(stage);
				e.setMode(2);
				e.setDekoderFx(d0);
				e.setStagenr(stagenr);
				e.setItems(items);
				stageManager.showStage(loader, stage, true);
			}
		}
	}

	@FXML
	protected void delete() {
		TFX d0 = tableView.getSelectionModel().getSelectedItem();
		if (d0 != null) {
			DatenListe<TFX> datenListe = controller.getDatenListe(items);
			datenListe.remove(d0);
			tableView.getItems().remove(d0);
			controller.speichern(items, getStagenr());
		}
		checkButton();
	}

	@FXML
	protected void cancel() {
		if (stage != null) {
			stage.close();
		}
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

	public int getSelNummer() {
		return selNummer;
	}

	public void setSelNummer(int selNummer) {
		this.selNummer = selNummer;
	}
}
