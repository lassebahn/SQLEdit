package com.lasse.view;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.roha.srvcls.model.Schluessel;
import com.lasse.config.FxmlView;
import com.lasse.control.Controller;
import com.lasse.control.FxEventBridge;
import com.lasse.control.StageManager;
import com.lasse.model.EventEinfuegenFeldSql1;
import com.lasse.model.EventEinfuegenFeldSql2;
import com.lasse.model.EventEinfuegenFeldSqlResponse;
import com.lasse.model.EventReadSql;
import com.lasse.model.EventReadSqlResponse;
import com.lasse.model.EventSaveSql;
import com.lasse.model.EventSqlRead;
import com.lasse.model.EventSqlReadMore;
import com.lasse.model.EventSqlResponse;
import com.lasse.model.EventString;
import com.lasse.model.Feld;
import com.lasse.service.DbZugriff;
import com.lasse.service.FileRW;
import com.lasse.viewmodel.FileTableModel;
import com.lasse.viewmodel.SqlEditVM;
import com.roha.srvcls.model.Satz;
import com.roha.srvcls.service.ExcelService2;
import com.roha.srvcls.service.LasseTool;

import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Hauptfenster für Anzeige der Daten aus SQL-Abfrage
 * @author Lasse Schöttner
 */
@Component
@Scope("prototype") // wichtig für manuelle Mehrfach-Instanzen
public class SqlEditView {
	@FXML
	protected ScrollPane scrollpane1;
	@FXML
	protected TextArea sqlField;
	@FXML
	protected TextField status;
	@FXML
	protected TextField maxcols;
	@FXML
	protected ComboBox<Schluessel> sqlHist;
	@FXML
	protected TabPane tabPane;
	@FXML
	protected TableView<Satz> tableView;
	// @FXML private MessageLine msgLine;
	@FXML
	protected ComboBox<String> msgLine2;
	@FXML
	protected Button readMoreButton;
	@FXML
	protected Button detailButton;
	@FXML
	protected MenuItem menuItemDisconnect;
	@FXML
	protected Parent root;

	// protected SqlEditVM vm;
	protected StageManager stageManager;
	protected Controller controller;
	private FileTableModel fileTable;
	private FileRW fileRW;
	private int servernr = 0;
	private int cursor;
	public final static int MAXCOL = 50;
	// SQL-Befehl pro Tab speichern:
	private HashMap<String, String> tabSql = new HashMap<String, String>();
	private static final AtomicInteger counter = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger(SqlEditView.class);
	@Autowired
	private ApplicationEventPublisher publisher;

	// private PrototypeBeanLister prototypeBeanLister;

	// @Autowired
	// public void setPrototypeBeanLister(PrototypeBeanLister prototypeBeanLister) {
	// this.prototypeBeanLister = prototypeBeanLister;
	// }

	private FxEventBridge eventBridge;

	@Lazy
	public SqlEditView(Controller controller, FileRW fileRW, FxEventBridge eventBridge) {
		this.controller = controller;
		this.fileRW = fileRW;
		this.eventBridge = eventBridge;
		int count = counter.incrementAndGet();
		// System.out.println("SqlEditView erstellt. Instanzen: " + count);
		eventBridge.registerListener(EventString.class, this::messageEvent);
		eventBridge.registerListener(EventSqlResponse.class, this::sqlResponse);
		eventBridge.registerListener(EventReadSqlResponse.class, this::readSqlResponse);
		eventBridge.registerListener(EventEinfuegenFeldSql1.class, this::eventEinfuegenFeld1);
		eventBridge.registerListener(EventEinfuegenFeldSqlResponse.class, this::eventEinfuegenFeld2);
	}

	private void messageEvent(EventString e) {
		MessageLine.addText(msgLine2, e.getMessage());
	}

	public void setStageManager(StageManager stageManager, Stage stage) {
		this.stageManager = stageManager;
		EventHandler<WindowEvent> oldHandler = stage.getOnCloseRequest();
		stage.setOnCloseRequest(event -> {
			cleanup();
			if (oldHandler != null) {
				oldHandler.handle(event);
			}
		});
	}

	@PreDestroy
	public void cleanup() {
		try {
			controller.getFileRW().writeSqlJson(new ArrayList<Schluessel>(sqlHist.getItems()), servernr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		controller.disconnect(servernr);
		int count = counter.decrementAndGet();
		eventBridge.unregisterListener(EventString.class, this::messageEvent);
		System.out.println("SqlEditView zerstört. Instanzen übrig: " + count);
	}

	public void initData(int servernr) {
		this.servernr = servernr;
		// vm = new SqlEditVM(this, controller, servernr, stageManager);
		// stageManager.closeRequest(vm, servernr);
		// status.textProperty().bind(controller.getStatus(servernr));
		// vm.setMsgLine(msgLine2);
		// vm.init();
		status.setText(
				"Aktueller Server: " + controller.getServer(servernr) + " - Keine Datenbank-Verbindung vorhanden.");
		// sqlField.textProperty().bindBidirectional(vm.getSqlField());
		sqlHist.valueProperty().addListener(new ChangeListener<Schluessel>() {
			@Override
			public void changed(ObservableValue<? extends Schluessel> observable, Schluessel oldValue,
					Schluessel newValue) {
				if (newValue != null && !newValue.equals(""))
					sqlField.setText(newValue.getWert());
			}
		});
		try {
			ArrayList<Schluessel> sqlHistListe = fileRW.readSqlJson(servernr);
			if (sqlHistListe == null) {
				sqlHistListe = new ArrayList<Schluessel>();
			}
			sqlHist.setItems(FXCollections.observableList(sqlHistListe));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		sqlField.caretPositionProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				cursor = sqlField.getCaretPosition();
			}
		});

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		// borderpane1.prefHeightProperty().bind(stage.heightProperty());
		// borderpane1.prefWidthProperty().bind(stage.widthProperty());
		// borderpane1.setPrefHeight(primaryScreenBounds.getHeight()-50);
		// borderpane1.setPrefWidth(primaryScreenBounds.getWidth()-30);
		Scene scene = stageManager.getScene(servernr);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode().equals(KeyCode.F5)) {
					execute();
				}
			}
		});
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				int anz = mouseEvent.getClickCount();
				TableView<Satz> tableViewSel = getSelTableView();
				if (anz == 2) {
					if (tableViewSel.getSelectionModel().getSelectedCells() != null
							&& tableViewSel.getSelectionModel().getSelectedCells().size() > 0) {
						TablePosition tp = tableViewSel.getSelectionModel().getSelectedCells().get(0);
						int col = tp.getColumn();
						sortieren(col);
					}
				}
				if (anz == 1) {
					if (tableViewSel.getSelectionModel().getSelectedCells() != null
							&& tableViewSel.getSelectionModel().getSelectedCells().size() > 0) {
						TablePosition tp = tableViewSel.getSelectionModel().getSelectedCells().get(0);
						int col = tp.getColumn();
						String label = getLabel(col);
						if (label != null) {
							// msgLine.addText(label);
							MessageLine.addText(msgLine2, label);
						}
					}
				}
			}
		});

		// prototypeBeanLister.listPrototypeBeans();
	}

	public String getLabel(int col) {
		String label = null;
		if (fileTable == null)
			return label;
		if (col >= 0 && col < fileTable.getLabel().size()) {
			label = fileTable.getLabel().get(col);
		}
		return label;
	}

	@FXML
	public void initialize() {
		maxcols.setText("" + MAXCOL);
		sqlHist.setVisibleRowCount(25);
		menuItemDisconnect.setDisable(true);
		readMoreButton.setDisable(true);
		detailButton.setDisable(true);
		// tableView.setPrefHeight(800);
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				detailButton.setDisable(false);
			} else {
				detailButton.setDisable(true);
			}
		});
		tableView.setPlaceholder(new Label("Keine Sätze gefunden."));
		tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
			if (newTab != null) {
				tabSql.put(oldTab.getText(), sqlField.getText());
				if (tabSql.containsKey(newTab.getText())) {
					sqlField.setText(tabSql.get(newTab.getText()));
				} else {
					sqlField.setText("");
				}
				TableView tableViewSel = getSelTableView();
				Object o = tableViewSel.getSelectionModel().getSelectedItem();
				if (o != null) {
					detailButton.setDisable(false);
				} else {
					detailButton.setDisable(true);
				}
			}
		});
	}

	/**
	 * Event Einfügen Feld an den Controller weiterleiten
	 * 
	 * @param e
	 */
	private void eventEinfuegenFeld1(EventEinfuegenFeldSql1 e) {
		if (e.getServernr() != this.servernr)
			return;
		EventEinfuegenFeldSql2 e2 = new EventEinfuegenFeldSql2(this, servernr, sqlField.getText(), e.getFeld(), cursor);
		publisher.publishEvent(e2);
	}

	/**
	 * Antwort für EventEinfuegenFeldSqlResponse
	 * 
	 * @param e
	 */
	private void eventEinfuegenFeld2(EventEinfuegenFeldSqlResponse e) {
		if (e.getServernr() != this.servernr)
			return;
		sqlField.setText(e.getSql());
		positionCaret(cursor);
	}

	/**
	 * SQL-Abfrage ausführen
	 */
	@FXML
	private void execute() {
		MessageLine.addText(msgLine2, "Abfrage wird ausgeführt...");
		int maxCol = 0;
		try {
			maxCol = Integer.parseInt(maxcols.getText());
		} catch (NumberFormatException e) {
		}
		stageManager.setCursor(Cursor.WAIT);
		EventSqlRead event = new EventSqlRead(this, servernr, sqlField.getText(), maxCol, 0);
		publisher.publishEvent(event);
	}

	/**
	 * SQL-Antwort empfangen
	 * 
	 * @param e
	 */
	private void sqlResponse(EventSqlResponse e) {
		if (e.getServernr() != this.servernr)
			return;
		stageManager.setCursor(Cursor.DEFAULT);
		if (e.getMessage() != null && !e.getMessage().equals("")) {
			MessageLine.addText(msgLine2, e.getMessage());
		}
		if (e.isConnected()) {
			menuItemDisconnect.setDisable(false);
		}
		setStatus(e.getStatus());
		if (e.isErfolgreich()) {
			if (e.isMore()) {
				readMoreButton.setDisable(false);
			} else {
				readMoreButton.setDisable(true);
			}
			sqlHist.setValue(null);
			TableView<Satz> tableViewSel = getSelTableView();
			if (e.isNeueTabelle()) {
				tableViewSel.getItems().clear();
				tableViewSel = e.getFileTable().initTableView(tableViewSel);
				fileTable = e.getFileTable();
			}
			ObservableList<Satz> data = FXCollections.observableList(e.getListe());
			tableViewSel.setItems(data);
			if (e.getListe() != null) {
				MessageLine.addText(msgLine2, e.getListe().size() + " Sätze gelesen.");
			}
			String s = e.getSql();
			// SQL in Historie eintragen
			String bez = s.replaceAll("\r", " ");
			bez = bez.replaceAll("\n", " ");
			Schluessel schl = new Schluessel(s, bez);
			sqlHist.getItems().add(0, schl);
		} else {
			readMoreButton.setDisable(true);
			TableView<Satz> tableViewSel = getSelTableView();
			tableViewSel.getItems().clear();
			tableViewSel.getColumns().clear();
		}
	}

	@FXML
	private void readMore() {
		MessageLine.addText(msgLine2, "Abfrage wird ausgeführt...");
		int maxCol = 0;
		try {
			maxCol = Integer.parseInt(maxcols.getText());
		} catch (NumberFormatException e) {
		}
		stageManager.setCursor(Cursor.WAIT);
		TableView<Satz> tableViewSel = getSelTableView();
		ArrayList<Satz> liste = new ArrayList<>(tableViewSel.getItems());
		EventSqlReadMore event = new EventSqlReadMore(this, servernr, liste, sqlField.getText(), maxCol, fileTable);
		publisher.publishEvent(event);
	}

	@FXML
	private void fields() {
		try {
			if (!controller.isConnected(servernr)) {
				controller.connect(false, servernr);
			}
			if (!controller.isConnected(servernr)) {
				MessageLine.addText(msgLine2, "Keine Verbindung zur Datenbank!");
			}
			String sql = sqlField.getText();
			ArrayList<Feld> felder;
			felder = controller.getDb(servernr).createFieldList2(sql);

			Stage stage = stageManager.createStage();
			stage.initModality(Modality.NONE);
			stage.initStyle(StageStyle.UTILITY);
			stage.setAlwaysOnTop(true);
			FXMLLoader loader = stageManager.createFXMLLoader(FxmlView.FELDERVIEW);
			FelderView felderView = loader.getController();
			felderView.init(felder, servernr);
			stage.getIcons().add(new Image("sql.png"));
			stage.setTitle("Feld-Liste");
			stageManager.showStage(loader, stage, false);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Detail-Anzeige Datensatz
	 */
	@FXML
	private void detail() {
		TableView<Satz> tableViewSel = getSelTableView();
		Satz satz = tableViewSel.getSelectionModel().getSelectedItem();
		if (satz != null) {
			Stage stage = stageManager.createStage();
			stage.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader loader = stageManager.createFXMLLoader(FxmlView.DETAILVIEW);
			DetailView detailView = loader.getController();
			detailView.init(satz, fileTable);
			stageManager.showStage(loader, stage, false);
		}
	}

	/**
	 * Gespeicherte Abfragen
	 */
	@FXML
	private void abfragen() {
		Stage stage = stageManager.createStage();
		stage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = stageManager.createFXMLLoader(FxmlView.SQLABFRAGENVIEW);
		SqlAbfragenView v = loader.getController();
		v.init(stage);
		v.setStagenr(servernr);
		stage.hide();
		stageManager.showStage(loader, stage, true);
	}

	@FXML
	private void disconnect() {
		controller.disconnect(servernr);
		menuItemDisconnect.setDisable(true);
		readMoreButton.setDisable(true);
	}

	@FXML
	private void sqlFieldChange() {
	}

	@FXML
	private void sqlFieldFocusChange() {
	}

	/**
	 * Sortieren nach Spalte mit DESC/ASC
	 * 
	 * @param table
	 * @param col
	 */
	public void sortieren(int col) {
		if (fileTable == null)
			return;
		String feld = fileTable.getFeldName(col);
		if (!feld.equals("")) {
			String sqlAlt = "";
			ObservableList<Schluessel> liste = sqlHist.getItems();
			if (liste.size() > 0)
				sqlAlt = liste.get(0).getWert();
			String s = Controller.createOrderSql(sqlAlt, feld);
			sqlField.setText(s);
			execute();
		}
	}

	/**
	 * Anzeigen Server-Konfiguration
	 */
	@FXML
	private void server() {
		ServerListeView c = stageManager.switchToNextScene(FxmlView.SERVERLISTEVIEW, servernr);
		c.setStagenr(servernr);
		// vm.serverDialog();
	}

	@FXML
	private void excel() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(fileRW.readLastUsedPath()));
		File selectedDirectory = directoryChooser.showDialog(stageManager.getStage(servernr).get());
		if (selectedDirectory != null) {
			String dir = selectedDirectory.getAbsolutePath();
			LocalDateTime now = LocalDateTime.now();
			String file = dir + "\\" + "sqledit" + "_" + LasseTool.toStringFL(now.getYear(), 4)
					+ LasseTool.toStringFL(now.getMonthValue(), 2) + LasseTool.toStringFL(now.getDayOfMonth(), 2) + "_"
					+ LasseTool.toStringFL(now.getHour(), 2) + LasseTool.toStringFL(now.getMinute(), 2)
					+ LasseTool.toStringFL(now.getSecond(), 2) + ".xls";
			int anz = excel(file);
			MessageLine.addText(msgLine2, anz + " Sätze wurden in Datei " + file + " ausgegeben.");
			fileRW.saveLastUsedPath(dir);
		}
	}

	/**
	 * Excel-Datei erstellen
	 * 
	 * @param file
	 * @return
	 */
	public int excel(String file) {
		int anz = 0;
		try {
			controller.connect(false, servernr);
			DbZugriff db = controller.getDb(servernr);
			if (db != null) {
				String s = sqlField.getText();
				if (s.trim().equals(""))
					return 0;
				s = db.completeSql(s);
				sqlField.setText(s);
				ExcelDialog ed = new ExcelDialog(file, s);
				ed.showAndWait();
				if (ed.isSavePressed()) {
					ResultSet rsx = db.read(s, false, null, 65500, 0, null);
					ArrayList<String> fx = db.getFelder();
					ArrayList<String> lx = db.getLabels();
					ArrayList<String> colHeader = new ArrayList<String>();
					for (int i = 0; i < fx.size(); i++) {
						if (ed.getColHeader() == 'B')
							colHeader.add(fx.get(i) + " " + lx.get(i));
						else if (ed.getColHeader() == 'F')
							colHeader.add(fx.get(i));
						else if (ed.getColHeader() == 'L')
							colHeader.add(lx.get(i));
					}
					ExcelService2 excel = new ExcelService2(ed.getDatei());
					anz = excel.toExcel(rsx, ed.getUeberschrift(), colHeader);
					db.closeStmt();
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return anz;
	}

	@FXML
	private void save() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("SQL-Abfrage speichern");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialDirectory(new File(fileRW.readLastUsedPath()));
		Optional<Stage> s = stageManager.getStage(servernr);
		if (s.isPresent()) {
			File file = fileChooser.showSaveDialog(s.get());
			if (file != null) {
				EventSaveSql event = new EventSaveSql(this, servernr, sqlField.getText(), file);
				publisher.publishEvent(event);
			}
		}
	}

	@FXML
	private void open() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("SQL-Abfrage öffnen");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialDirectory(new File(fileRW.readLastUsedPath()));
		File file = fileChooser.showOpenDialog(stageManager.getStage(servernr).get());
		if (file != null) {
			EventReadSql event = new EventReadSql(this, servernr, file);
			publisher.publishEvent(event);
		}
	}

	/**
	 * Antwort für EventReadSql
	 * 
	 * @param e
	 */
	private void readSqlResponse(EventReadSqlResponse e) {
		if (e.getServernr() != this.servernr)
			return;
		if (e.getSql() != null && !e.getSql().equals("")) {
			sqlField.setText(e.getSql());
		}
	}

	public void setStatus(String text) {
		this.status.setText(text);
	}

	public void positionCaret(int pos) {
		sqlField.positionCaret(pos);
	}

	@FXML
	private void close() {
		stageManager.close(servernr);
	}

	@FXML
	private void fenster2() {
		stageManager.showStage(FxmlView.SQLEDIT, 1);
	}

	@FXML
	private void fenster3() {
		stageManager.showStage(FxmlView.SQLEDIT, 2);
	}

	@FXML
	private void fenster4() {
		stageManager.showStage(FxmlView.SQLEDIT, 3);
	}

	@FXML
	private void addTab() {
		int nr = tabPane.getTabs().size() + 1;
		// Neuer Tab
		Tab tab = new Tab("Tabelle " + nr);

		// VBox als Container
		VBox vbox = new VBox();
		vbox.setSpacing(10);

		// TableView erstellen
		TableView<Satz> tableView = new TableView<>();
		tableView.setPlaceholder(new Label("Keine Sätze gefunden."));
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				detailButton.setDisable(false);
			} else {
				detailButton.setDisable(true);
			}
		});
		VBox.setVgrow(tableView, Priority.ALWAYS); // <-- wichtig!
		tableView.setMaxHeight(Double.MAX_VALUE); // damit sie "wachst"
		tableView.setMaxWidth(Double.MAX_VALUE); // optional, für volle Breite
		// TableView zur VBox hinzufügen
		vbox.getChildren().add(tableView);

		// VBox als Inhalt des Tabs setzen
		tab.setContent(vbox);

		// Tab zur TabPane hinzufügen
		tabPane.getTabs().add(tab);
	}

	private TableView<Satz> getSelTableView() {
		TableView<Satz> t = null;
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		Node content = tab.getContent(); // Das ist z. B. eine VBox
		if (content instanceof VBox) {
			VBox vbox = (VBox) content;
			for (Node node : vbox.getChildren()) {
				if (node instanceof TableView) {
					t = (TableView<Satz>) node;
				}
			}
		}
		return t;
	}
}
