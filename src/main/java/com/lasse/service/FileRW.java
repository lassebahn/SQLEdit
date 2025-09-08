package com.lasse.service;

import java.io.*;
import java.util.*;

import org.jdom.output.Format;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lasse.model.Server;
import com.lasse.model.ServerListe;
import com.lasse.model.SqlAbfrage;
import com.lasse.model.SqlAbfragen;
import com.lasse.model.SqlCommandList;
import com.lasse.model.SqlCommandListAll;
import com.roha.srvcls.model.Schluessel;

/**
 * Schnittstelle zum Speichern der Daten
 * 
 * @author Lasse Schöttner
 * 
 */

@Service
public class FileRW {

	private String sqlDatei;
	private String serverDatei;
	private String sqlAbfragenDatei;
	private int MAXSQL = 100;	// Maximale Anzahl an SQL-Befehlen, die protokolliert werden
	private Properties props = new Properties();
	private static final Logger logger = LogManager.getLogger(FileRW.class);
	
	public FileRW() {
		setServerDatei();
		setSqlDatei();
		setSqlAbfragenDatei();
	}

	// Prüfen, ob Datei vorhanden ist:
	protected boolean checkDatei(String datei) {
		File fileobj = new File(datei);
		if (fileobj.exists() && fileobj.isFile())
			return true;
		else
			return false;
	}

	// int in String konvertieren mit fester Länge:
	public static String cnv(int i, int len) {
		String s = String.valueOf(i);
		if (s.startsWith("-")) {
			s = s.substring(1);
			while (s.length() < len - 1)
				s = "0" + s;
			s = "-" + s;
		} else
			while (s.length() < len)
				s = "0" + s;
		return s;
	}

	// boolean in String konvertieren::
	public static String cnv(boolean b) {
		String s = "";
		if (b)
			s = "1";
		else
			s = "0";
		return s;
	}

	// String in boolean konvertieren:
	public static boolean cnv(String s) {
		boolean b;
		if (s.equals("1"))
			b = true;
		else
			b = false;
		return b;
	}

	// String in String konvertieren:
	public static String cnvs(String s) {
		s = s.replace(':', '$');
		if (s.equals(""))
			return "§";
		else
			return s;
	}

	// String in String konvertieren:
	public static String cnvs2(String s) {
		s = s.replace('$', ':');
		if (s.equals("§"))
			return "";
		else
			return s;
	}
	
	
	/**
	 * Historie SQL-Befehle speichern
	 * @param liste
	 * @param servernr
	 * @throws Exception
	 */
	public void writeSqlJson(ArrayList<Schluessel> liste, int servernr) throws Exception {
		File f = new File(sqlDatei);
		SqlCommandListAll sqlCommandListAll = null;
		if (f.exists())
			sqlCommandListAll = readSqlJson();
		else
			sqlCommandListAll = new SqlCommandListAll();
		ArrayList<SqlCommandList> scls = sqlCommandListAll.getSqlCommandLists();
		ArrayList<SqlCommandList> scls2 = new ArrayList<SqlCommandList>();
		boolean gefunden = false;
		for (SqlCommandList scl  : scls) {
			if (scl.getServernr() == servernr) {
				scl.setSql(liste);
				gefunden = true;
			}
			scls2.add(scl);
		}
		if (!gefunden) {
			SqlCommandList scl = new SqlCommandList();
			scl.setServernr(servernr);
			scl.setSql(liste);
			scls2.add(scl);
		}
		sqlCommandListAll.setSqlCommandLists(scls2);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Schön formatiert
        mapper.writeValue(f, sqlCommandListAll);
    }
	
	/**
	 * Historie SQL-Befehle lesen
	 * @param servernr
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Schluessel> readSqlJson(int servernr) throws Exception {
		File f = new File(sqlDatei);
		if (!f.exists())
			writeSqlJson(new ArrayList<Schluessel>(), servernr);
        ObjectMapper mapper = new ObjectMapper();
        SqlCommandListAll sqlCommandListAll = mapper.readValue(f, SqlCommandListAll.class);
        return sqlCommandListAll.getSqlCommandList(servernr).getSql();
    }

	public SqlCommandListAll readSqlJson() throws Exception {
		File f = new File(sqlDatei);
		if (!f.exists())
			writeSqlJson(new ArrayList<Schluessel>(), 0);
        ObjectMapper mapper = new ObjectMapper();
        SqlCommandListAll sqlCommandListAll = mapper.readValue(f, SqlCommandListAll.class);
        return sqlCommandListAll;
    }

	/**
	 * Sql-Befehle speichern
	 * 
	 * @param liste
	 */
	public void writeSqlXML(ArrayList<Schluessel> liste, int servernr) throws IOException {
		// Create the root element
		Element root = new Element("sqls");
		// create the document
		Document myDocument = new Document(root);
		for (int i = 0; i < liste.size(); i++) {
			Schluessel s = liste.get(i);
			Element sql = new Element("sql");
			addElement(sql, "wert", s.getWert());
			root.addContent(sql);
		}
		schreiben(myDocument, sqlDatei);
	}

	/**
	 * Sql-Befehle lesen
	 */
	public ArrayList<Schluessel> readSqlXML(int servernr) {
		int zae = 0;
		ArrayList<Schluessel> liste = new ArrayList<Schluessel>();
		if (!checkDatei(sqlDatei))
			return liste;
		try {
			Element root;
			root = lesen(sqlDatei);
			List<?> los = root.getChildren();
			Iterator<?> iterator = los.iterator();
			while (iterator.hasNext()) {
				Element sql = (Element) iterator.next();
				if (sql.getName().equals("sql")) {
					List<?> werte = sql.getChildren();
					Iterator<?> iteratorw = werte.iterator();
					while (iteratorw.hasNext()) {
						Element wert = (Element) iteratorw.next();
						if (wert.getName().equals("wert")) {
							String bez = wert.getText().replaceAll("\r", " ");
							bez = bez.replaceAll("\n", " ");
							Schluessel s = new Schluessel(wert.getText(), bez);
							liste.add(s);
							zae++;
						}
					}
				}
				if (zae >= MAXSQL){
					break;
				}
			}
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
			liste.clear();
		}
		return liste;
	}

	/**
	 * Server speichern
	 * 
	 * @param liste
	 */
	public void writeServerXML(Vector<String> servers, Vector<Integer> typen, Vector<String> users, Vector<String> passwords, String server)
			throws IOException {
		// Create the root element
		Element root = new Element("servers");
		// create the document
		Document myDocument = new Document(root);
		for (int i = 0; i < servers.size(); i++) {
			String s = servers.get(i);
			Element ele = new Element("server");
			addElement(ele, "name", s);
			addElement(ele, "typ", typen.get(i));
			addElement(ele, "user", users.get(i));
			addElement(ele, "password", passwords.get(i));
			if (s.equals(server)) {
				addElement(ele, "aktu", "Y");
			} else {
				addElement(ele, "aktu", "N");
			}
			root.addContent(ele);
		}
		schreiben(myDocument, serverDatei);
	}

	/**
	 * Server speichern
	 * 
	 * @param liste
	 */
	public void writeServerXMLNeu(ServerListe serverListe, String server, int servernr)
			throws IOException {
		// Create the root element
		Element root = new Element("servers");
		// create the document
		Document myDocument = new Document(root);
		for (int i = 0; i < serverListe.size(); i++) {
			Server s = serverListe.get(i);
			Element ele = new Element("server");
			addElement(ele, "nummer", s.getNummer());
			addElement(ele, "name", s.getName());
			addElement(ele, "typ", s.getTyp());
			addElement(ele, "user", s.getUser());
			addElement(ele, "password", s.getPassword());
			if (s.getName().equals(server)) {
				addElement(ele, "aktu" + servernr, "Y");
			} else {
				addElement(ele, "aktu" + servernr, "N");
			}
			if (servernr != 0)
				addElement(ele, "aktu0", s.getAktu0());
			if (servernr != 1)
				addElement(ele, "aktu1", s.getAktu1());
			if (servernr != 2)
				addElement(ele, "aktu2", s.getAktu2());
			if (servernr != 3)
				addElement(ele, "aktu3", s.getAktu3());
			root.addContent(ele);
		}
		schreiben(myDocument, serverDatei);
	}

	/*
	 * Server lesen
	 */
	public Object[] readServerXML() throws JDOMException, IOException {
		Object[] rueck = new Object[8];
		Vector<String> liste = new Vector<String>();
		Vector<Integer> typen = new Vector<Integer>();
		Vector<String> users = new Vector<String>();
		Vector<String> passwords = new Vector<String>();
		rueck[0] = liste;
		rueck[1] = typen;
		rueck[2] = "";
		rueck[3] = 0;
		rueck[4] = users;
		rueck[5] = passwords;
		rueck[6] = "";
		rueck[7] = "";
		if (!checkDatei(serverDatei))
			return rueck;
		String hilf = "";
		int hilftyp = 0;
		String hilfuser = "";
		String hilfpassword = "";
		Element root = lesen(serverDatei);
		List<?> los = root.getChildren();
		Iterator<?> iterator = los.iterator();
		while (iterator.hasNext()) {
			Element server = (Element) iterator.next();
			if (server.getName().equals("server")) {
				List<?> werte = server.getChildren();
				Iterator<?> iteratorw = werte.iterator();
				while (iteratorw.hasNext()) {
					Element wert = (Element) iteratorw.next();
					if (wert.getName().equals("name")) {
						liste.add(wert.getText());
						hilf = wert.getText();
					}
					else if (wert.getName().equals("aktu")) {
						if (wert.getText().equals("Y")){
							rueck[2] = hilf;
							rueck[3] = hilftyp;
							rueck[6] = hilfuser;
							rueck[7] = hilfpassword;
						}
					}
					else if (wert.getName().equals("typ")) {
						typen.add(new Integer(wert.getText()));
						hilftyp = new Integer(wert.getText());
					}
					else if (wert.getName().equals("user")) {
						users.add(wert.getText());
						hilfuser = wert.getText();
					}
					else if (wert.getName().equals("password")) {
						passwords.add(wert.getText());
						hilfpassword = wert.getText();
					}
				}
			}
		}
		return rueck;
	}

	/*
	 * Server lesen
	 */
	public ServerListe readServerXMLNeu() throws JDOMException, IOException {
		ServerListe serverListe = new ServerListe();
		if (!checkDatei(serverDatei))
			return serverListe;
		Element root = lesen(serverDatei);
		List<?> los = root.getChildren();
		Iterator<?> iterator = los.iterator();
		while (iterator.hasNext()) {
			Element server = (Element) iterator.next();
			if (server.getName().equals("server")) {
				Server s = new Server();
				serverListe.add(s);
				List<?> werte = server.getChildren();
				Iterator<?> iteratorw = werte.iterator();
				while (iteratorw.hasNext()) {
					Element wert = (Element) iteratorw.next();
					if (wert.getName().equals("name")) {
						s.setName(wert.getText());
					}
					else if (wert.getName().equals("nummer")) {
						s.setNummer(Integer.parseInt(wert.getText()));
					}
					else if (wert.getName().equals("aktu0")) {
						s.setAktu0(wert.getText());
					}
					else if (wert.getName().equals("aktu1")) {
						s.setAktu1(wert.getText());
					}
					else if (wert.getName().equals("aktu2")) {
						s.setAktu2(wert.getText());
					}
					else if (wert.getName().equals("aktu3")) {
						s.setAktu3(wert.getText());
					}
					else if (wert.getName().equals("typ")) {
						s.setTyp(Integer.parseInt(wert.getText()));
					}
					else if (wert.getName().equals("user")) {
						s.setUser(wert.getText());
					}
					else if (wert.getName().equals("password")) {
						s.setPassword(wert.getText());
					}
				}
			}
		}
		return serverListe;
	}

	/**
	 */
	protected void setSqlDatei() {
		sqlDatei = System.getProperty("user.dir") + File.separator + "sql.json";
	}

	protected void setServerDatei() {
		serverDatei = System.getProperty("user.dir") + File.separator
				+ "server.xml";
	}

	protected void setSqlAbfragenDatei() {
		sqlAbfragenDatei = System.getProperty("user.dir") + File.separator
				+ "sqlabfragen.json";
	}

	private void addElement(Element ober, String name, int wert) {
		Element unter = new Element(name);
		unter.addContent(Integer.toString(wert));
		ober.addContent(unter);
	}

	private void addElement(Element ober, String name, long wert) {
		Element unter = new Element(name);
		unter.addContent(Long.toString(wert));
		ober.addContent(unter);
	}

	private void addElement(Element ober, String name, boolean wert) {
		Element unter = new Element(name);
		unter.addContent(Boolean.toString(wert));
		ober.addContent(unter);
	}

	private void addElement(Element ober, String name, String wert) {
		Element unter = new Element(name);
		unter.addContent(wert);
		ober.addContent(unter);
	}

	private void addElement(Element ober, String name, Integer wert) {
		Element unter = new Element(name);
		if (wert != null)
			unter.addContent(wert.toString());
		else
			unter.addContent("null");
		ober.addContent(unter);
	}

	private void addElement(Element ober, String name, char wert) {
		Element unter = new Element(name);
		unter.addContent(Character.toString(wert));
		ober.addContent(unter);
	}

	private void schreiben(Document myDocument, String datei)
			throws IOException {
		FileWriter writer;
		writer = new FileWriter(datei);
		// Damit das XML-Dokument schoen formattiert wird holen wir uns ein
		// Format
		Format format = Format.getPrettyFormat();
		// und setzen das encoding, da in unseren Buechern auch Umlaute
		// vorkommen koennten.
		// Mit format kann man z.B. auch die Einrueckung beeinflussen
		format.setEncoding("iso-8859-1");
		XMLOutputter outputter = new XMLOutputter(format);
		// outputter.output(myDocument, System.out);
		outputter.output(myDocument, writer);
		writer.close();
	}

	private Element lesen(String datei) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		File file = new File(datei);
		Document doc;
		doc = builder.build(file);
		Element root = doc.getRootElement();
		return root;
	}

	/**
	 * Letzten Pfad für Datei-Auswahl speichern
	 * @param pfad
	 */
	public void saveLastUsedPath(String pfad) {
		props.setProperty("lastUsedPath", pfad);
		// speichern
		try (FileOutputStream out = new FileOutputStream("myconfig.properties")) {
		    props.store(out, "Benutzerpfade");
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Letzten Pfad für Datei-Auswahl lesen
	 * @return
	 */
	public String readLastUsedPath() {
		String pfad = System.getProperty("user.dir");
		// laden
		try (FileInputStream in = new FileInputStream("myconfig.properties")) {
		    props.load(in);
		    pfad = props.getProperty("lastUsedPath", pfad);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return pfad;
	}

	/**
	 * Schreiben SQL-Abfragen
	 * @param sqlAbfragen
	 */
	@CachePut(value = "lasse.sqlabfragen")
	public SqlAbfragen saveSqlAbfragen(SqlAbfragen sqlAbfragen) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			int i = 0;
			ArrayList<SqlAbfrage> l = sqlAbfragen.getAbfragen();
			for (SqlAbfrage s: l) {
				i++;
				s.setNummer(i);
			}
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createGenerator(new File(sqlAbfragenDatei), com.fasterxml.jackson.core.JsonEncoding.UTF8);
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(generator, sqlAbfragen);
			generator.close();
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return sqlAbfragen;
	}
	
	/**
	 * SQL-Abfragen lesen
	 * @return
	 */
	@Cacheable( "lasse.sqlabfragen" )
	public SqlAbfragen loadSqlAbfragen() {
		logger.info("loadSqlAbfragen");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        	File f = new File(sqlAbfragenDatei);
            return objectMapper.readValue(f, SqlAbfragen.class);
        } catch (IOException e) {
			logger.error(e.getMessage(), e);
            return new SqlAbfragen();
        }
    }
}
