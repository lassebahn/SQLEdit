package com.lasse.control;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.lasse.model.Daten;
import com.lasse.model.DatenListe;
import com.lasse.model.EventEinfuegenFeldSql2;
import com.lasse.model.EventEinfuegenFeldSqlResponse;
import com.lasse.model.EventReadSql;
import com.lasse.model.EventReadSqlResponse;
import com.lasse.model.EventSaveSql;
import com.lasse.model.EventSqlHistGUI;
import com.lasse.model.EventSqlHistRead;
import com.lasse.model.EventSqlRead;
import com.lasse.model.EventSqlReadMore;
import com.lasse.model.EventSqlResponse;
import com.lasse.model.Server;
import com.lasse.model.ServerListe;
import com.lasse.model.SqlAbfrage;
import com.lasse.model.SqlAbfragen;
import com.lasse.service.DbZugriff;
import com.lasse.service.FileRW;
import com.lasse.viewmodel.FileTableModel;
import com.roha.srvcls.control.DefaultController;
import com.roha.srvcls.model.Benutzer;
import com.roha.srvcls.model.Satz;
import com.roha.srvcls.model.Schluessel;
import com.roha.srvcls.service.AbstractDBService;
import com.roha.srvcls.service.DB2;
import com.roha.srvcls.service.DB2400;
import com.roha.srvcls.service.DefaultDBService;
import com.roha.srvcls.service.PasswordEncoder;
import com.roha.srvcls.service.SQLServer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Controller für den Datenbank-Zugriff
 * Lasse Schöttner
 */
@Component
public class Controller {

	public final static int MAX_SERVER = 4;
	private int[] servernummer = new int[MAX_SERVER];
	private String[] server = new String[MAX_SERVER];
	private String[] user = new String[MAX_SERVER];
	private String[] password = new String[MAX_SERVER];
	private int[] serverTyp = new int[MAX_SERVER];
	private DB2400[] db2400 = new DB2400[MAX_SERVER];
	private DB2[] db2 = new DB2[MAX_SERVER];
	private SQLServer[] sqlserver = new SQLServer[MAX_SERVER];
	private DbZugriff[] db = new DbZugriff[MAX_SERVER];
	private boolean[] connected = new boolean[MAX_SERVER];
	private FileTableModel[] fileTable = new FileTableModel[MAX_SERVER];
	private int[] max = new int[MAX_SERVER];
	private boolean[] more = new boolean[MAX_SERVER];
	private ResultSet[] rs = new ResultSet[MAX_SERVER];
	private ResultSetMetaData[] rsmd = new ResultSetMetaData[MAX_SERVER];
	private FileRW fileRW;
	private PasswordEncoder pe;
	private ServerListe serverListe;
	private StringProperty[] status = new SimpleStringProperty[MAX_SERVER];
	private final static int MAXSEITE = 100;
	private static final Logger logger = LogManager.getLogger(Controller.class);
	@Autowired
	private ApplicationEventPublisher publisher;

	public Controller(FileRW fileRW) {
		this.fileRW = fileRW;
		try {
			for (int i = 0; i < MAX_SERVER; i++) {
				servernummer[i] = 0;
				server[i] = null;
				user[i] = null;
				password[i] = null;
				serverTyp[i] = 0;
				db2400[i] = null;
				db2[i] = null;
				sqlserver[i] = null;
				db[i] = null;
				connected[i] = false;
				status[i] = new SimpleStringProperty("keine Verbindung");
				fileTable[i] = new FileTableModel();
				max[i] = MAXSEITE;
				more[i] = true;
			}
			pe = PasswordEncoder.getInstance();
			serverListe = fileRW.readServerXMLNeu();
			for (int i = 0; i < serverListe.size(); i++) {
				if (serverListe.get(i).getAktu0().equals("Y")) {
					setServernummer(serverListe.get(i).getNummer(), 0);
					setServer(serverListe.get(i).getName(), 0);
					setServerTyp(serverListe.get(i).getTyp(), 0);
					setUser(serverListe.get(i).getUser(), 0);
					setPassword(serverListe.get(i).getPassword(), 0);
				} else if (serverListe.get(i).getAktu1().equals("Y")) {
					setServernummer(serverListe.get(i).getNummer(), 1);
					setServer(serverListe.get(i).getName(), 1);
					setServerTyp(serverListe.get(i).getTyp(), 1);
					setUser(serverListe.get(i).getUser(), 1);
					setPassword(serverListe.get(i).getPassword(), 1);
				} else if (serverListe.get(i).getAktu2().equals("Y")) {
					setServernummer(serverListe.get(i).getNummer(), 2);
					setServer(serverListe.get(i).getName(), 2);
					setServerTyp(serverListe.get(i).getTyp(), 2);
					setUser(serverListe.get(i).getUser(), 2);
					setPassword(serverListe.get(i).getPassword(), 2);
				} else if (serverListe.get(i).getAktu3().equals("Y")) {
					setServernummer(serverListe.get(i).getNummer(), 3);
					setServer(serverListe.get(i).getName(), 3);
					setServerTyp(serverListe.get(i).getTyp(), 3);
					setUser(serverListe.get(i).getUser(), 3);
					setPassword(serverListe.get(i).getPassword(), 3);
				}
			}
			
			//CompletableFuture<Boolean> erfolg = cb.run();
		} catch (JDOMException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @return
	 */
	public <T extends Daten> DatenListe getDatenListe(String typ) {
		if (typ.equals("SERVER"))
			return (DatenListe<T>) serverListe;
		else
			return null;
	}
	

	/**
	 * Daten speichern
	 * @param art
	 * @param servernr
	 */
	public void speichern(String art, int servernr) {
		if (art.equals("SERVER")) {
			try {
				for (int i = 0; i < getServerListe().size(); i++) {
					Server s = getServerListe().get(i);
					if (s.getNummer() == 0) {
						s.setNummer(getServerListe().getNewDatenNr());
					}
				}
				getFileRW().writeServerXMLNeu(getServerListe(), getServer(servernr), servernr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getServernummer(int i) {
		return servernummer[i];
	}

	public void setServernummer(int servernummer, int i) {
		this.servernummer[i] = servernummer;
	}

	public String getServer(int i) {
		return server[i];
	}

	public void setServer(String server, int i) {
		this.server[i] = server;
	}

	public String getUser(int i) {
		return user[i];
	}

	public void setUser(String user, int i) {
		this.user[i] = user;
	}

	public String getPassword(int i) {
		return password[i];
	}

	public void setPassword(String password, int i) {
		this.password[i] = password;
	}

	public int getServerTyp(int i) {
		return serverTyp[i];
	}

	public void setServerTyp(int serverTyp, int i) {
		this.serverTyp[i] = serverTyp;
	}

	public DB2400 getDb2400(int i) {
		return db2400[i];
	}

	public void setDb2400(DB2400 db2400, int i) {
		this.db2400[i] = db2400;
	}

	public DB2 getDb2(int i) {
		return db2[i];
	}

	public void setDb2(DB2 db2, int i) {
		this.db2[i] = db2;
	}

	public SQLServer getSqlserver(int i) {
		return sqlserver[i];
	}

	public void setSqlserver(SQLServer sqlserver, int i) {
		this.sqlserver[i] = sqlserver;
	}

	public DbZugriff getDb(int i) {
		return db[i];
	}

	public void setDb(DbZugriff db, int i) {
		this.db[i] = db;
	}

	public boolean isConnected(int i) {
		return connected[i];
	}

	public void setConnected(boolean connected, int i) {
		this.connected[i] = connected;
	}

	public FileRW getFileRW() {
		return fileRW;
	}

	public void setFileRW(FileRW fileRW) {
		this.fileRW = fileRW;
	}

	public ServerListe getServerListe() {
		return serverListe;
	}

	public void setServerListe(ServerListe serverListe) {
		this.serverListe = serverListe;
	}

	/**
	 * Private helper method to do the connection to Database
	 */
	public boolean connect(boolean neu, int i) {
		boolean ok = true;
		if (!connected[i] || neu || db[i] == null) {
			if (neu) {
				disconnect(i);
			}
			try {
				logger.info("Verbindung wird hergestellt...");
				Benutzer b = DefaultController.getStdUser();
				String kw = pe.decode2(password[i]);
				if (serverTyp[i] == DefaultDBService.DRIVER_DB2400) {
					db2400[i] = new DB2400(server[i], user[i], kw);
					// db2400 = new DB2400(server);
					db[i] = new DbZugriff(db2400[i]);
				} else if (serverTyp[i] == DefaultDBService.DRIVER_SQLSERVER) {
					// sqlserver = new SQLServer(server, "ssadmin", "mama");
					String systemname = "localhost";
					String port = "1433";
					String dbname = "";
					String[] w1 = server[i].split(";");
					if (w1.length > 0) {
						String[] w2 = w1[0].split(":");
						if (w2.length > 0) {
							if (w2[0].startsWith("//")) {
								systemname = w2[0].substring(2, w2[0].length());
							}
						}
						if (w2.length > 1) {
							port = w2[1];
						}
						for (int j = 1; j < w1.length; j++) {
							if (w1[j].toLowerCase().startsWith("databasename")) {
								String[] w3 = server[i].split("=");
								if (w3.length > 1) {
									dbname = w3[1];
								}
							}
						}
					}
					// sqlserver = new SQLServer(server, user, kw);
					sqlserver[i] = new SQLServer(systemname, port, dbname, user[i], kw);
					db[i] = new DbZugriff(sqlserver[i]);
				} else if (serverTyp[i] == DefaultDBService.DRIVER_DB2) {
					// db2 = new DB2(server, "db2admin", "mama");
					// Achtung: Windows-Firewall kann das blockieren
					db2[i] = new DB2(server[i], user[i], kw);
					db[i] = new DbZugriff(db2[i]);
				}
				System.out.println("Verbindung hergestellt!");
			} catch (SQLException e) {
				ok = false;
				logger.error(e.getMessage(), e);
			} catch (InstantiationException e) {
				ok = false;
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				ok = false;
				logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				ok = false;
				logger.error(e.getMessage(), e);
			}
		}
		if (ok) {
			connected[i] = true;
		}
		// if (connected){
		// closeButton.setEnabled(true);
		// } else{
		// closeButton.setEnabled(false);
		// }
		// setStatusText();
		return ok;
	}

	/**
	 * Verbindung trennen
	 */
	public void disconnect(int i) {
		if (rs[i] != null) {
			try {
				rs[i].close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
			rs[i] = null;
		}
		if (db[i] != null)
			db[i].close();
		if (db2400[i] != null)
			db2400[i].disconnect();
		if (db2[i] != null)
			db2[i].disconnect();
		if (sqlserver[i] != null)
			sqlserver[i].disconnect();
		db[i] = null;
		connected[i] = false;
		logger.info("Verbindung " + i + " getrennt");
	}

	public StringProperty getStatus(int i) {
		return status[i];
	}

	public void setStatus(String text, int i) {
		this.status[i].set(text);
	}

	/**
	 * SQL-Historie lesen
	 * 
	 * @return
	 */
	@EventListener
	public void readSqlHist(EventSqlHistRead event) {
		ArrayList<Schluessel> sqlHistListe = null;
		try {
			sqlHistListe = getFileRW().readSqlJson(event.getServernr());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			sqlHistListe = new ArrayList<Schluessel>();
		}
		EventSqlHistGUI sqlHistEvent = new EventSqlHistGUI(this, event.getServernr(), sqlHistListe);
		publisher.publishEvent(sqlHistEvent);
	}

	/**
	 * Daten lesen mit SQL
	 * 
	 * @param table
	 * @param maxCol
	 * @return
	 */
	@EventListener
	@Async
	public void read(EventSqlRead event) {
		connect(false, event.getServernr());
		if (!isConnected(event.getServernr())) {
			int servernr = event.getServernr();
			EventSqlResponse er = new EventSqlResponse(this, servernr, null, event.getSql(), null, false, false, "Keine Datenbank-Verbindung vorhanden.", getStatus(servernr).get(), false, true);
			publisher.publishEvent(er);
		}
		int maxrow = MAXSEITE;
		if (event.getMaxrow() > 0) {
			maxrow = event.getMaxrow();
		}
		max[event.getServernr()] = maxrow;
		String s = event.getSql();
		s = getDb(event.getServernr()).completeSql(s);
		// sql zurückmelden...
		ArrayList<Satz> liste = new ArrayList<Satz>();
		int anz = lesen(s, liste, maxrow, event.getMaxcol(), event.getServernr(), true, event.getSqlAbfrage());
	}

	@EventListener
	@Async
	public void readMore(EventSqlReadMore event) {
		connect(false, event.getServernr());
		if (!isConnected(event.getServernr())) {
			int servernr = event.getServernr();
			EventSqlResponse er = new EventSqlResponse(this, servernr, null, event.getSql(), null, false, false, "Keine Datenbank-Verbindung vorhanden.", getStatus(servernr).get(), false, false);
			publisher.publishEvent(er);
		}
		if (getDb(event.getServernr()).getDriver() == AbstractDBService.DRIVER_SQLSERVER) {
			String s = event.getSql();
			max[event.getServernr()] = max[event.getServernr()] + MAXSEITE;
			int anz = lesen(s, event.getListe(), MAXSEITE, event.getMaxcol(), event.getServernr(), false, event.getSqlAbfrage());
		} else {
			mehrLesen(event.getListe(), event.getServernr());
		}
	}

	
	/**
	 * Daten lesen mit SQL
	 * 
	 * @param s
	 * @param table
	 * @return
	 */
	public int lesen(String sql, ArrayList<Satz> liste, int max, int maxCol, int servernr, boolean neueTabelle, SqlAbfrage sqlAbfrage) {
		int anz = 0;
		String sql2 = "";
		if (sqlAbfrage != null) {
			sql2 = sqlAbfrage.getSql();
		} else {
			sql2 = sql;
		}
		try {
			if (getDb(servernr) != null) {
				DbZugriff db = getDb(servernr);
				if (!sql.equals("") || sqlAbfrage != null) {
					if (rs[servernr] != null) {
						rs[servernr].close();
						rs[servernr] = null;
					}
					PreparedStatement psrsmd = db.getConn(db.getDriver()).prepareStatement(sql2);
					rsmd[servernr] = psrsmd.getMetaData();
					more[servernr] = false;
					rs[servernr] = db.read(sql, false, rsmd[servernr], max, maxCol, sqlAbfrage);
					int i = 0;
					while (rs[servernr].next()) {
						Satz sa = db.convertRsE(rsmd[servernr], rs[servernr], null, maxCol);
						liste.add(sa);
						i++;
						if (i >= max) {
							more[servernr] = true;
							break;
						}
					}
					psrsmd.close();
					db.closeStmt();
					fileTable[servernr].setFeldnamen(db.getFelder());
					fileTable[servernr].setDispSizes(db.getDispSizes());
					fileTable[servernr].setLabel(db.getLabels());
					fileTable[servernr].setTypes(db.getTypes());
					fileTable[servernr].setSchemas(db.getSchemas());
					fileTable[servernr].setTables(db.getTables());
					fileTable[servernr].setKeynrs(db.getKeynrs());
					anz = i;
					setStatus("Verbunden mit " + getServer(servernr), servernr);
					EventSqlResponse event = new EventSqlResponse(this, servernr, liste, sql2, fileTable[servernr], more[servernr], true, "Es wurden " + anz + " Datensätze gelesen.", getStatus(servernr).get(), true, neueTabelle);
					publisher.publishEvent(event);
				}
			} else {
				setStatus("Aktueller Server: " + getServer(servernr) + " - Keine Datenbank-Verbindung vorhanden.", servernr);
				EventSqlResponse event = new EventSqlResponse(this, servernr, liste, sql2, fileTable[servernr], more[servernr], false, "Keine Datenbank-Verbindung vorhanden.", getStatus(servernr).get(), false, neueTabelle);
				publisher.publishEvent(event);
				anz = -1;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			//MessageLine.addText(msgLine, "Error: " + e.getMessage());
			EventSqlResponse event = new EventSqlResponse(this, servernr, liste, sql2, fileTable[servernr], more[servernr], false, "Error: " + e.getMessage(), getStatus(servernr).get(), true, neueTabelle);
			publisher.publishEvent(event);
			return -1;
		}
		return anz;
	}
	
	
	/**
	 * Mehr Daten lesen
	 * @param liste
	 * @param servernr
	 * @return
	 */
	private int mehrLesen(ArrayList<Satz> liste, int servernr) {
		int anz = 0;
		try {
			if (more[servernr] && rs != null) {
				//rsmd = psrsmd.getMetaData();
				//int iv = max;
				max[servernr] = max[servernr] + MAXSEITE;
				more[servernr] = false;
				int i = 0;
				while (rs[servernr].next()) {
					Satz sa = getDb(servernr).convertRsE(rsmd[servernr], rs[servernr]);
					liste.add(sa);
					i++;
					if (i >= max[servernr]) {
						more[servernr] = true;
						break;
					}
				}
				anz = i;
				EventSqlResponse event = new EventSqlResponse(this, servernr, liste, "", fileTable[servernr], more[servernr], true, "Es wurden " + anz + " Datensätze gelesen.", getStatus(servernr).get(), true, false);
				publisher.publishEvent(event);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			//MessageLine.addText(msgLine, MessageLine.formatText("Error: " + e.getMessage()));
			EventSqlResponse event = new EventSqlResponse(this, servernr, liste, "", fileTable[servernr], more[servernr], false, "Error: " + e.getMessage(), getStatus(servernr).get(), true, false);
			publisher.publishEvent(event);
			anz = -1;
		}
		return anz;
	}

	/**
	 * SQL-Abfrage speichern
	 * @param f
	 * @return
	 */
	@EventListener
	public boolean save(EventSaveSql event) {
		boolean ok = true;
		try {
			String s = event.getSql();
			if (s.trim().equals(""))
				return false;
			connect(false, event.getServernr());
			DbZugriff db = getDb(event.getServernr());
			s = db.completeSql(s);
			BufferedWriter writer = new BufferedWriter(new FileWriter(event.getFile())); // Erzeugen eines effizienten Writers für
																				// Textdateien
			writer.write(s);
			writer.close();
			fileRW.saveLastUsedPath(event.getFile().getParent());
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
	@EventListener
	public void open(EventReadSql event) {
		String s = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(event.getFile())); // Erzeugen eines effizienten Readers für Textdateien
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
            	s += zeile;
            } 
            in.close();
			fileRW.saveLastUsedPath(event.getFile().getParent());
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
		if (s != null)
			publisher.publishEvent(new EventReadSqlResponse(this, event.getServernr(), s));
	}

	/**
	 * Feld in SQL einfügen
	 * @param feld
	 */
	@EventListener
	public void einfuegenFeldSql(EventEinfuegenFeldSql2 event) {
		String s = event.getSql().toUpperCase();
		if (event.getFeld() == null)
			return;
		int iorder = s.indexOf("ORDER BY");
		int igroup = s.indexOf("GROUP BY");
		int iwhere = s.indexOf("WHERE");
		int iselect = s.indexOf("SELECT");
		if (iorder < event.getCursor() && iorder >= 0) {
			einfuegen(s, iorder, "ORDER BY", event.getFeld(), ",", event.getCursor(), event.getServernr());
		} else if (igroup < event.getCursor() && igroup >= 0) {
			einfuegen(s, igroup, "GROUP BY", event.getFeld(), ",", event.getCursor(), event.getServernr());
		} else if (iwhere < event.getCursor() && iwhere >= 0) {
			einfuegen(s, iwhere, "WHERE", event.getFeld(), " AND ", event.getCursor(), event.getServernr());
		} else if (iselect < event.getCursor() && iselect >= 0) {
			einfuegen(s, iselect, "SELECT", event.getFeld(), ",", event.getCursor(), event.getServernr());
		}
	}

	private void einfuegen(String s, int index, String such, String feld, String vb, int cursor, int servernr) {
		String z = s.substring(index, cursor).trim();
		String z1 = s.substring(cursor - 1, cursor);
		String ccc = vb;
		if (z.equals(such)) {
			if (z1.equals(" "))
				ccc = "";
			else
				ccc = " ";
		}
		String sql = s.substring(0, cursor) + ccc + feld + s.substring(cursor, s.length());
		int pos = cursor + ccc.length() + feld.length();
		cursor = pos;
		EventEinfuegenFeldSqlResponse er = new EventEinfuegenFeldSqlResponse(this, servernr, sql, cursor);
		publisher.publishEvent(er);
	}

	/**
	 * SQL sortieren
	 * @param sql
	 * @param feld
	 * @return
	 */
	public static String createOrderSql(String sql, String feld) {
		String order = "ORDER BY " + feld;
		if (sql.contains(order + " ASC")) {
			order = order + " DESC";
		} else {
			order = order + " ASC";
		}
		int ende = sql.indexOf("ORDER BY");
		if (ende >= 0) {
			sql = sql.substring(0, ende);
		}
		sql = sql.trim() + " " + order;
		return sql;
	}
	
	public SqlAbfragen getSqlAbfragen() {
		return fileRW.loadSqlAbfragen();
	}
	
	public void setSqlAbfragen(SqlAbfragen sqlAbfragen) {
		fileRW.saveSqlAbfragen(sqlAbfragen);
	}

}
