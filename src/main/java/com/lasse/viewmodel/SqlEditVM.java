package com.lasse.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lasse.config.FxmlView;
import com.lasse.control.Controller;
import com.lasse.control.StageManager;
import com.lasse.model.Feld;
import com.roha.srvcls.model.Schluessel;
import com.lasse.service.DbZugriff;
import com.lasse.view.DetailView;
import com.lasse.view.ExcelDialog;
import com.lasse.view.FelderView;
import com.lasse.view.MessageLine;
import com.lasse.view.ServerDialog;
import com.lasse.view.SqlEditView;
import com.roha.srvcls.model.Satz;
import com.roha.srvcls.service.AbstractDBService;
import com.roha.srvcls.service.ExcelService2;
import com.roha.srvcls.service.PasswordEncoder;

/**
 * Datenbank-Zugriff (alt)
 * @author Lasse Schöttner
 */
public class SqlEditVM {
	private StringProperty sqlField = new SimpleStringProperty("");
	private String sqlHistValue = "";
	private Controller controller;
	//private String server;
	//private String user;
	//private String password;
	//private int serverTyp;
	//private DB2400 db2400 = null;
	//private DB2 db2 = null;
	//private SQLServer sqlserver = null;
	//private DbZugriff db = null;
	//private boolean connected = false;
	//private FileRW fileRW;
	private String prevLibrName, prevFileName, prevLibrNameP, prevFileNameP;
	private Properties prevNames = new Properties();
	private int max;
	private boolean more = false;
	private PasswordEncoder pe;
	public final static String PROP_FILE = "sqledit.properties";
	private final static int MAXSEITE = 100;
	private FileTableModel fileTable;
	private ResultSetMetaData rsmd;
	private PreparedStatement psrsmd;
	private ResultSet rs;
	private ObservableList<Schluessel> sqlHist = null;
	private ArrayList<Schluessel> sqlHistListe = null;
	private ComboBox<String> msgLine;
	private int cursor;
	private SqlEditView sqlEditView;
	private int servernr = 0;
	private StageManager stageManager;
	private static final Logger logger = LogManager.getLogger(Controller.class);

	public SqlEditVM(SqlEditView sqlEditView, Controller controller, int servernr, StageManager stageManager) {
		this.sqlEditView = sqlEditView;
		this.controller = controller;
		this.servernr = servernr;
		this.stageManager = stageManager;
		ArrayList<String> liste = new ArrayList<String>();
		pe = PasswordEncoder.getInstance();
		max = MAXSEITE;
		fileTable = new FileTableModel();
	}


	public void init() {
		controller.setStatus("Aktueller Server: " + controller.getServer(servernr) + " - Keine Datenbank-Verbindung vorhanden.", servernr);
	}

	/**
	 * SQL-Historie lesen
	 * 
	 * @return
	 */
	public ObservableList<Schluessel> readSqlHist() {
		try {
			sqlHistListe = controller.getFileRW().readSqlJson(servernr);
			sqlHist = FXCollections.observableList(sqlHistListe);
			return sqlHist;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			sqlHistListe = new ArrayList<Schluessel>();
			sqlHist = FXCollections.observableList(sqlHistListe);
			return sqlHist;
		}
	}

	public int execute(TableView<Satz> table, int maxCol) {
		controller.connect(false, servernr);
		if (!controller.isConnected(servernr))
			return -1;
		// listModel.setTypUeberschrift(ueMenu.getCurrUEKZ());
		String s = sqlField.getValue();
		s = controller.getDb(servernr).completeSql(s);
		sqlField.setValue(s);
		int anz = lesen(s, table, MAXSEITE, maxCol);
		if (anz >= 0) {
			String bez = s.replaceAll("\r", " ");
			bez = bez.replaceAll("\n", " ");
			Schluessel schl = new Schluessel(s, bez);
			sqlHist.add(0, schl);
		}
		return anz;
	}

	public int readMore(TableView<Satz> table, int maxCol) {
		controller.connect(false, servernr);
		if (!controller.isConnected(servernr))
			return -1;
		if (controller.getDb(servernr).getDriver() == AbstractDBService.DRIVER_SQLSERVER) {
			String s = sqlField.getValue();
			max = max + MAXSEITE;
			int anz = lesen(s, table, max, maxCol);
			return anz;
		} else {
			return mehrLesen(table);
		}
	}

	/**
	 * Liste mit Datenbank-Feldern anzeigen
	 */
	public void fields() {
		try {
			if (!controller.isConnected(servernr)) {
				controller.connect(false, servernr);
			}
			if (!controller.isConnected(servernr)) {
				MessageLine.addText(msgLine, "Keine Verbindung zur Datenbank!");
			}
			String sql = sqlField.getValue();
			ArrayList<Feld> felder;
			felder = controller.getDb(servernr).createFieldList2(sql);
			
			Stage stage = stageManager.createStage();
			stage.initModality(Modality.NONE);
			stage.initStyle(StageStyle.UTILITY);
			stage.setAlwaysOnTop(true);
			FXMLLoader loader = stageManager.createFXMLLoader(FxmlView.FELDERVIEW);
			FelderView felderView = loader.getController();
			felderView.init(felder, servernr);
			stageManager.showStage(loader, stage, false);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}


	public StringProperty getSqlField() {
		return sqlField;
	}

	public void setSqlField(String text) {
		sqlField.setValue(text);
	}

	/**
	 * Daten lesen mit SQL
	 * 
	 * @param s
	 * @param table
	 * @return
	 */
	public int lesen(String s, TableView<Satz> table, int max, int maxCol) {
		int anz = 0;
		try {
			// listModel.setTypUeberschrift(ueMenu.getCurrUEKZ());
			if (controller.getDb(servernr) != null) {
				DbZugriff db = controller.getDb(servernr);
				if (rs != null)
					rs.close();
				ArrayList<Satz> liste = new ArrayList<Satz>();
				if (!s.equals("")) {
					// rsmd = db.getMetaData(s);
					if (psrsmd != null && !psrsmd.isClosed())
						psrsmd.close();
					if (rs != null && !rs.isClosed())
						rs.close();
					psrsmd = db.getConn(db.getDriver()).prepareStatement(s);
					rsmd = psrsmd.getMetaData();
					more = false;
					rs = db.read(s, false, rsmd, max, maxCol, null);
					int i = 0;
					while (rs.next()) {
						Satz sa = db.convertRsE(rsmd, rs, null, maxCol);
						liste.add(sa);
						i++;
						if (i >= max) {
							more = true;
							break;
						}
					}
					db.closeStmt();
					fileTable.setFeldnamen(db.getFelder());
					fileTable.setDispSizes(db.getDispSizes());
					fileTable.setLabel(db.getLabels());
					fileTable.setTypes(db.getTypes());
					fileTable.setSchemas(db.getSchemas());
					fileTable.setTables(db.getTables());
					fileTable.setKeynrs(db.getKeynrs());
					// fileTable.setText(s);
					table = fileTable.initTableView(table);
					ObservableList<Satz> data = FXCollections.observableList(liste);
					table.setItems(data);
					anz = i;
					controller.setStatus("Verbunden mit " + controller.getServer(servernr), servernr);
				}
			} else {
				controller.setStatus("Aktueller Server: " + controller.getServer(servernr) + " - Keine Datenbank-Verbindung vorhanden.", servernr);
				anz = -1;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			MessageLine.addText(msgLine, "Error: " + e.getMessage());
			return -1;
		}
		return anz;
	}

	private int mehrLesen(TableView<Satz> table) {
		int anz = 0;
		try {
			if (more && rs != null) {
				//rsmd = psrsmd.getMetaData();
				int iv = max;
				max = max + MAXSEITE;
				ArrayList<Satz> liste = new ArrayList<Satz>();
				more = false;
				int i = 0;
				while (rs.next()) {
					Satz sa = controller.getDb(servernr).convertRsE(rsmd, rs);
					liste.add(sa);
					i++;
					if (i >= max) {
						more = true;
						break;
					}
				}
				for (int i2 = 0; i2 < liste.size(); i2++) {
					table.getItems().add(liste.get(i2));
				}
				anz = i;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			MessageLine.addText(msgLine, MessageLine.formatText("Error: " + e.getMessage()));
			anz = -1;
			// if (msgLine != null)
			// msgLine.setText("Error: " + e.getMessage());
		}
		return anz;
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
				String s = sqlField.getValue();
				if (s.trim().equals(""))
					return 0;
				s = db.completeSql(s);
				sqlField.setValue(s);
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

	/**
	 * SQL-Abfrage speichern
	 * @param f
	 * @return
	 */
	public boolean save(File f) {
		boolean ok = true;
		try {
			String s = sqlField.getValue();
			if (s.trim().equals(""))
				return false;
			controller.connect(false, servernr);
			DbZugriff db = controller.getDb(servernr);
			s = db.completeSql(s);
			BufferedWriter writer = new BufferedWriter(new FileWriter(f)); // Erzeugen eines effizienten Writers für
																				// Textdateien
			writer.write(s);
			writer.close();
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
		return ok;
	}

	/**
	 * SQL-Abfrage öffnen
	 * @param f
	 * @return
	 */
	public String open(File f) {
		String s = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
            	s += zeile;
            } 
            in.close();
			if (!s.trim().equals("")) sqlField.setValue(s);
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
		return s;
	}

	
	private void insertField() {
	}


	public void serverDialog() {
		//ServerDialog sd = new ServerDialog(null, controller.getServerListe(), controller.getServer());
		ServerDialog sd = new ServerDialog(controller);
		sd.showAndWait();
		if (sd.isSavePressed()) {
			controller.setServerListe(sd.getServerListe());
			controller.setServer(sd.getServer().getName(), servernr);
			controller.setServerTyp(sd.getServer().getTyp(), servernr);
			controller.setUser(sd.getServer().getUser(), servernr);
			controller.setPassword(sd.getServer().getPassword(), servernr);
			try {
				controller.getFileRW().writeServerXMLNeu(controller.getServerListe(), controller.getServer(servernr), servernr);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (controller.connect(true, servernr)) {
				controller.setStatus("Verbunden mit " + controller.getServer(servernr), servernr);
			}
		}
	}

	/**
	 * Detail-Anzeige
	 * 
	 * @param satz
	 */
	public void detail(Satz satz) {
		Stage stage = stageManager.createStage();
		stage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = stageManager.createFXMLLoader(FxmlView.DETAILVIEW);
		DetailView detailView = loader.getController();
		detailView.init(satz, fileTable);
		stageManager.showStage(loader, stage, false);
	}

	public void setMsgLine(ComboBox<String> msgLine) {
		this.msgLine = msgLine;
	}

	public void close() {
		try {
			controller.getFileRW().writeSqlJson(sqlHistListe, servernr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (psrsmd != null) {
			try {
				psrsmd.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		controller.disconnect(servernr);
	}

	public static int findText(ArrayList<Feld> data, int currRow, String searchText) {
		searchText = searchText.toLowerCase();
		int searchRow = currRow + 1;
		int lastRow = data.size() - 1;
		if (searchRow > lastRow)
			searchRow = 0;
		boolean match = false;
		boolean searchDone = false;
		while (!match && !searchDone) {
			Feld f = data.get(searchRow);
			if (f.getName().toLowerCase().contains(searchText)) {
				match = true;
			}
			if (f.getBezeichnung().toLowerCase().contains(searchText)) {
				match = true;
			}
			if (!match) {
				if (searchRow == currRow) // back to where we started? All done
					searchDone = true;
				else if (++searchRow > lastRow) // increment row, possibly
												// wrapping
				{
					searchRow = 0;
					if (currRow == -1) // no current row, so we started at zero,
										// and end there.
						searchDone = true;
				}
			}
		}
		return match ? searchRow : -1;
	}

	public void einfuegenFeldSql(String feld) {
		String s = sqlField.getValue().toUpperCase();
		if (feld == null)
			return;
		int iorder = s.indexOf("ORDER BY");
		int igroup = s.indexOf("GROUP BY");
		int iwhere = s.indexOf("WHERE");
		int iselect = s.indexOf("SELECT");
		if (iorder < cursor && iorder >= 0) {
			einfuegen(s, iorder, "ORDER BY", feld, ",");
		} else if (igroup < cursor && igroup >= 0) {
			einfuegen(s, igroup, "GROUP BY", feld, ",");
		} else if (iwhere < cursor && iwhere >= 0) {
			einfuegen(s, iwhere, "WHERE", feld, " AND ");
		} else if (iselect < cursor && iselect >= 0) {
			einfuegen(s, iselect, "SELECT", feld, ",");
		}
	}

	private void einfuegen(String s, int index, String such, String feld, String vb) {
		String z = s.substring(index, cursor).trim();
		String z1 = s.substring(cursor - 1, cursor);
		String ccc = vb;
		if (z.equals(such)) {
			if (z1.equals(" "))
				ccc = "";
			else
				ccc = " ";
		}
		sqlField.setValue(s.substring(0, cursor) + ccc + feld + s.substring(cursor, s.length()));
		int pos = cursor + ccc.length() + feld.length();
		cursor = pos;
		sqlEditView.positionCaret(cursor);
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	/**
	 * Sortieren nach Spalte mit DESC/ASC
	 * 
	 * @param table
	 * @param col
	 */
	public void sortieren(TableView<Satz> table, int col, int maxCol) {
		String feld = fileTable.getFeldName(col);
		if (!feld.equals("")) {
			String sqlAlt = "";
			if (sqlHistListe.size() > 0)
				sqlAlt = sqlHistListe.get(0).getWert();
			String order = "ORDER BY " + feld;
			if (sqlAlt.contains(order + " ASC")) {
				order = order + " DESC";
			} else {
				order = order + " ASC";
			}
			int ende = sqlAlt.indexOf("ORDER BY");
			if (ende >= 0) {
				sqlAlt = sqlAlt.substring(0, ende);
			}
			String s = sqlAlt.trim() + " " + order;
			sqlField.setValue(s);
			execute(table, maxCol);
		}
	}

	public String getLabel(int col) {
		String label = null;
		if (col >= 0 && col < fileTable.getLabel().size()) {
			label = fileTable.getLabel().get(col);
		}
		return label;
	}
	
	public boolean isConnected() {
		return controller.isConnected(servernr);
	}
	
	public void disconnect() {
		controller.disconnect(servernr);
	}

}
