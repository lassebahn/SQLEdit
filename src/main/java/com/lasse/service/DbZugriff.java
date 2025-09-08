package com.lasse.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lasse.control.Controller;
import com.lasse.model.Feld;
import com.lasse.model.Server;
import com.lasse.model.SqlAbfrage;
import com.lasse.model.SqlFeld;
import com.roha.srvcls.model.Satz;
import com.roha.srvcls.service.*;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * Datenbank-Zugriff
 * @author Lasse Schöttner
 * 
 */
public class DbZugriff extends DefaultDBService {

	private PreparedStatement pstmt;
	private ResultSet rs;
	private int driver;
	private ArrayList<String> felder = new ArrayList<String>();
	private ArrayList<String> labels = new ArrayList<String>();
	private ArrayList<Integer> types = new ArrayList<Integer>();
	private ArrayList<String> typeNames = new ArrayList<String>();
	private ArrayList<Integer> dispSizes = new ArrayList<Integer>();
	private ArrayList<Integer> precis = new ArrayList<Integer>();
	private ArrayList<Integer> scales = new ArrayList<Integer>();
	private ArrayList<String> tables = new ArrayList<String>();
	private ArrayList<String> schemas = new ArrayList<String>();
	private ArrayList<Integer> keynrs = new ArrayList<Integer>();
	private String sql;
	//private JdbcTemplate jdbcTemplate; 
	private static final Logger logger = LogManager.getLogger(DbZugriff.class);

	public DbZugriff(DB2 db2) {
		super(db2);
		driver = DRIVER_DB2;
	}

	public DbZugriff(DB2400 db2400) throws SQLException {
		super(db2400);
		driver = DRIVER_DB2400;
	}

	public DbZugriff(SQLServer sqlserver) throws SQLException {
		super(sqlserver);
		driver = DRIVER_SQLSERVER;
	}

	/**
	public DbZugriff(Server server) throws SQLException {
		jdbcTemplate = createJdbcTemplate(server);
		stmt = null;
		driver = server.getTyp();
	}

	private JdbcTemplate createJdbcTemplate(Server server) {
	    DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    if (server.getTyp() == DRIVER_SQLSERVER)
	    	dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    else if (server.getTyp() == DRIVER_DB2400)
	    	dataSource.setDriverClassName("com.ibm.as400.access.AS400JDBCDriver");
	    if (server.getTyp() == DRIVER_DB2)
	    	dataSource.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
	    dataSource.setUrl(server.getName());
	    dataSource.setUsername(server.getUser());
	    dataSource.setPassword(server.getPassword());
	    return new JdbcTemplate(dataSource);
	}
	*/

	public String completeSql(String sql) {
		if (sql.trim().equals(""))
			return sql;
		if (!sql.toUpperCase().startsWith("SELECT")) {
			if (sql.contains("FROM")) {
				sql = "SELECT * " + sql;
			} else {
				sql = "SELECT * FROM " + sql;
			}
		}
		return sql;
	}

	/**
	 * Daten lesen Metadaten ermitteln
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public ResultSet read(String sql, boolean all, ResultSetMetaData rsmd, int max, int maxCol, SqlAbfrage sqlAbfrage) throws SQLException {
		closeStmt();
		String sql2 = "";
		boolean para = false;
		if (sqlAbfrage != null) {
			sql2 = sqlAbfrage.getSql();
			para = true;
		} else {
			sql2 = sql;
		}
		if (driver == DRIVER_SQLSERVER) {
			if (sql.toLowerCase().startsWith("select") && sql.length() > 6 && !sql.toLowerCase().contains(" top ")) {
				sql2 = "select TOP " + max + sql.substring(6);
			}
		}
		PreparedStatement pstmt = getConn(driver).prepareStatement(sql2);
		if (para) {
			ArrayList<SqlFeld> felder = sqlAbfrage.getFelder();
			for (SqlFeld f : felder) {
				pstmt.setString(felder.indexOf(f) + 1, f.getWert());
			}
		}
		rs = pstmt.executeQuery();

		if (driver == DRIVER_SQLSERVER) {

			createMetadataSqlServer(sql, all, rsmd, max, maxCol, sqlAbfrage);

		} else if (driver == DRIVER_DB2400) {

			HashMap<String, Satz> tabellen = new HashMap<String, Satz>();
			// ResultSetMetaData rsmd = getMetaData(sql, driver);
			if (rsmd == null) {
				rsmd = pstmt.getMetaData();
			}
			felder.clear();
			labels.clear();
			types.clear();
			typeNames.clear();
			dispSizes.clear();
			precis.clear();
			scales.clear();
			keynrs.clear();
			int maxCol2 = rsmd.getColumnCount();
			if (maxCol > 0 && maxCol < rsmd.getColumnCount())
				maxCol2 = maxCol;
			for (int i = 1; i <= maxCol2; i++) {
				String name = rsmd.getColumnName(i);
				name = name.toUpperCase();
				felder.add(name);
				labels.add(rsmd.getColumnLabel(i));
				types.add(rsmd.getColumnType(i));
				typeNames.add(rsmd.getColumnTypeName(i));
				dispSizes.add(rsmd.getColumnDisplaySize(i));
				precis.add(rsmd.getPrecision(i));
				scales.add(rsmd.getScale(i));
				tables.add(rsmd.getTableName(i));
				schemas.add(rsmd.getSchemaName(i));
				keynrs.add(0);
			}
			tables.clear();
			schemas.clear();
			labels.clear();
			for (int i = 0; i < felder.size(); i++) {
				schemas.add("");
				tables.add("");
				labels.add("");
			}
			tabellen = getSchemaTable(sql);
			Iterator<String> iter = tabellen.keySet().iterator();
			while (iter.hasNext()) {
				Satz s = tabellen.get(iter.next());
				String schema = s.getWertS("SCHEMA");
				String tabelle = s.getWertS("TABLE");
				HashMap<String, String> werte = getTextLHM(schema, tabelle, driver);
				for (int i = 0; i < felder.size(); i++) {
					String label = werte.get(felder.get(i));
					if (label != null) {
						schemas.set(i, schema);
						tables.set(i, tabelle);
						labels.set(i, label);
					}
				}
			}
			Statement stmt2 = db2400.getConnection().createStatement();
			for (int i = 0; i < felder.size(); i++) {
				sql2 = "SELECT * FROM QSYS2.SYSKEYCST WHERE TABLE_SCHEMA = '" + schemas.get(i) + "' AND TABLE_NAME = '"
						+ tables.get(i) + "' AND COLUMN_NAME = '" + felder.get(i) + "'";
				ResultSet rs2 = stmt2.executeQuery(sql2);
				if (rs2.next()) {
					keynrs.set(i, rs2.getInt("ORDINAL_POSITION"));
				}
				rs2.close();
			}
			stmt2.close();
		}

		this.sql = sql;
		return rs;
	}


	/**
	 * MetaDaten erstellen SQL Server
	 * 
	 * @param sql
	 * @param all: Alle Felder aus Tabellen hinzufügen
	 * @throws SQLException
	 */
	public void createMetadataSqlServer(String sql, boolean all, ResultSetMetaData rsmd, int max, int maxCol, SqlAbfrage sqlAbfrage)
			throws SQLException {
		tables.clear();
		schemas.clear();
		labels.clear();
		felder.clear();
		labels.clear();
		types.clear();
		typeNames.clear();
		dispSizes.clear();
		precis.clear();
		scales.clear();
		keynrs.clear();
		HashMap<String, Satz> tabellen = new HashMap<String, Satz>();
		String sql2 = "";
		if (sqlAbfrage != null) {
			sql2 = sqlAbfrage.getSql();
		} else {
			sql2 = sql;
		}
		tabellen = getSchemaTable(sql2);
		if (all) {
			Iterator<String> iter = tabellen.keySet().iterator();
			while (iter.hasNext()) {
				Satz s = tabellen.get(iter.next());
				String catalog = null;
				String schema = s.getWertS("SCHEMA");
				if (schema.contains(".")) {
					String[] str = schema.split(".");
					if (str.length == 2) {
						catalog = str[0];
						schema = str[1];
					}
				}
				if (catalog != null && catalog.equals(""))
					catalog = null;
				if (schema != null && schema.equals(""))
					schema = null;
				String tabelle = s.getWertS("TABLE");
				Satz st = getTableData(catalog, schema, tabelle);
				createFieldData(st.getWertS("CATALOG"), st.getWertS("SCHEMA"), st.getWertS("TABLE"));
			}
		} else {
			HashMap<String, String> labelGus = null;
			Iterator<String> iter = tabellen.keySet().iterator();
			while (iter.hasNext()) {
				Satz s = tabellen.get(iter.next());
				String tabelle = s.getWertS("TABLE");
				labelGus = getLabelGUS("", "", tabelle, labelGus);
			}
			PreparedStatement ps = null;
			if (rsmd == null) {
				String sql3 = sql2;
				if (sql2.toLowerCase().startsWith("select") && sql2.length() > 6
						&& !sql2.toLowerCase().contains(" top ")) {
					sql3 = "select TOP " + max + sql2.substring(6);
				}
				ps = getConn(driver).prepareStatement(sql3);
				rsmd = ps.getMetaData();
			}
			int maxCol2 = rsmd.getColumnCount();
			if (maxCol > 0 && maxCol < rsmd.getColumnCount())
				maxCol2 = maxCol;
			for (int i = 1; i <= maxCol2; i++) {
				String name = rsmd.getColumnName(i);
				name = name.toUpperCase();
				felder.add(name);
				// labels.add(rsmd.getColumnLabel(i));
				if (labelGus != null && labelGus.containsKey(name)) {
					labels.add(labelGus.get(name));
				} else {
					labels.add("");
				}
				types.add(rsmd.getColumnType(i));
				typeNames.add(rsmd.getColumnTypeName(i));
				dispSizes.add(rsmd.getColumnDisplaySize(i));
				precis.add(rsmd.getPrecision(i));
				scales.add(rsmd.getScale(i));
				tables.add(rsmd.getTableName(i));
				schemas.add(rsmd.getSchemaName(i));
				keynrs.add(0);
			}
			if (ps != null)
				ps.close();
		}
	}

	/**
	 * GUS-Feldbeschreibungen lesen
	 * 
	 * @param catalog
	 * @param schema
	 * @param tabelle
	 * @param labelGus
	 * @param keyMitTab: Tabelle in den Key aufnehmen
	 * @return
	 */
	private HashMap<String, String> getLabelGUS(String catalog, String schema, String tabelle,
			HashMap<String, String> labelGus) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				stmt = getConn(driver).createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (stmt != null) {
				boolean ok = true;
				try {
					String sql = "SELECT TOP 1 FIELD, DESCR FROM " + catalog + "." + schema + ".CATFIELD";
					sql += " WHERE PROJECT = 'os'";
					rs = stmt.executeQuery(sql);
					rs.next();
					rs.close();
				} catch (SQLException e) {
					ok = false;
				}
				if (ok) {
					if (labelGus == null) {
						labelGus = new HashMap<String, String>();
					}
					try {
						String sql = "SELECT FIELD, DESCR FROM " + catalog + "." + schema + ".CATFIELD";
						sql += " WHERE PROJECT = 'os'";
						sql += " AND TABLENAME = '" + tabelle + "'";
						rs = stmt.executeQuery(sql);
						while (rs.next()) {
							String key = rs.getString("FIELD").trim();
							if (!catalog.equals("") && !schema.equals("")) {
								key = catalog + "." + schema + "." + tabelle + "." + rs.getString("FIELD").trim();
							}
							labelGus.put(key, rs.getString("DESCR").trim());
						}
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
			}
		}
		return labelGus;
	}

	/**
	 * Schema und Tabelle aus Sql-String ermitteln
	 * 
	 * @param sql
	 * @return
	 */
	private HashMap<String, Satz> getSchemaTableAlt(String sql) {
		HashMap<String, Satz> tabellen = new HashMap<String, Satz>();
		sql = sql.toUpperCase();
		int i = LasseTool.indexOfString(sql, "FROM", 0);
		int j = LasseTool.indexOfString(sql, "WHERE", 0);
		if (j == -1) {
			j = LasseTool.indexOfString(sql, "GROUP BY", 0);
			if (j == -1) {
				j = LasseTool.indexOfString(sql, "HAVING", 0);
				if (j == -1) {
					j = LasseTool.indexOfString(sql, "ORDER BY", 0);
				}
			}
		}
		if (j < 0)
			j = sql.length();
		if (i >= 0) {
			String such = sql.substring(i + 5, j);
			StringTokenizer st = new StringTokenizer(such, " ");
			while (st.hasMoreTokens()) {
				String x = st.nextToken();
				StringTokenizer st2 = new StringTokenizer(x, ".");
				int anz = st2.countTokens();
				String db = "";
				String schema = "";
				String tabelle = "";
				String key = "";
				if (anz == 1) {
					if (st2.hasMoreTokens())
						tabelle = st2.nextToken();
					tabelle = tabelle.replaceAll(",", "");
					key = tabelle;
				} else if (anz == 2) {
					if (st2.hasMoreTokens())
						schema = st2.nextToken();
					if (st2.hasMoreTokens())
						tabelle = st2.nextToken();
					schema = schema.replaceAll(",", "");
					tabelle = tabelle.replaceAll(",", "");
					key = schema + "." + tabelle;
				} else if (anz == 3) {
					if (st2.hasMoreTokens())
						db = st2.nextToken();
					if (st2.hasMoreTokens())
						schema = st2.nextToken();
					if (st2.hasMoreTokens())
						tabelle = st2.nextToken();
					db = db.replaceAll(",", "");
					schema = schema.replaceAll(",", "");
					tabelle = tabelle.replaceAll(",", "");
					key = db + "." + schema + "." + tabelle;
				}
				Satz s = new Satz();
				s.putFeld("SCHEMA", schema);
				if (!db.equals("")) {
					s.putFeld("SCHEMA", db + "." + schema);
				}
				s.putFeld("TABLE", tabelle);
				tabellen.put(key, s);
			}
		}
		return tabellen;
	}

	/**
	 * Schema und Tabelle aus Sql-String ermitteln mit JSQLParser
	 * 
	 * @param sql
	 * @return
	 */
	private HashMap<String, Satz> getSchemaTable(String sql) {
		sql = sql.toUpperCase();
		HashMap<String, Satz> tabellen = new HashMap<String, Satz>();
		try {
			Set<String> tables = TablesNamesFinder.findTables(sql);
			Iterator<String> iter = tables.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				logger.info(key);
				String db = "";
				String schema = "";
				String tabelle = "";
				StringTokenizer st = new StringTokenizer(key, ".");
				int anz = st.countTokens();
				if (anz == 1) {
					tabelle = st.nextToken();
				} else if (anz == 2) {
					schema = st.nextToken();
					tabelle = st.nextToken();
				} else if (anz == 3) {
					db = st.nextToken();
					schema = st.nextToken();
					tabelle = st.nextToken();
				}
				Satz s = new Satz();
				s.putFeld("SCHEMA", schema);
				if (!db.equals("")) {
					s.putFeld("SCHEMA", db + "." + schema);
				}
				s.putFeld("TABLE", tabelle);
				tabellen.put(key, s);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return tabellen;
	}

	public static List<String> extractTableNames(String sql) {
		List<String> tables = new ArrayList<>();

		// Normalize for case-insensitive matching
		String cleanedSql = sql.replaceAll("\\s+", " ");

		// Extract FROM ... (up to WHERE, ORDER BY, etc.)
		Pattern fromPattern = Pattern.compile("FROM (.+?)(WHERE|ORDER BY|GROUP BY|HAVING|$)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = fromPattern.matcher(cleanedSql);

		if (matcher.find()) {
			String fromPart = matcher.group(1).trim();

			// Split by comma for multiple tables
			for (String entry : fromPart.split(",")) {
				entry = entry.trim();
				// Remove alias and WITH hints
				entry = entry.replaceAll("WITH \\(.*?\\)", "").trim();
				String[] parts = entry.split("\\s+");
				if (parts.length > 0) {
					String table = parts[0].trim();
					if (!tables.contains(table)) {
						tables.add(table);
					}
				}
			}
		}

		return tables;
	}

	/**
	 * Catalog (Datenbank) und Schema zu Tabelle ermitteln
	 * 
	 * @param catalog
	 * @param schema
	 * @param tabelle
	 * @return
	 */
	private Satz getTableData(String catalog, String schema, String tabelle) {
		Satz s = new Satz();
		DatabaseMetaData dbmd;
		try {
			dbmd = getConn(driver).getMetaData();
			ResultSet rs = dbmd.getTables(catalog, schema, tabelle, null);
			// ResultSetMetaData rsmd = rs.getMetaData();

			// Display the result set data.
			if (rs.next()) {
				s.putFeld("CATALOG", rs.getString("TABLE_CAT"));
				s.putFeld("SCHEMA", rs.getString("TABLE_SCHEM"));
				s.putFeld("TABLE", rs.getString("table_name"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * Feld-Daten ermitteln (SQL Server)
	 * 
	 * @param catalog
	 * @param schema
	 * @param tabelle
	 * @return
	 */
	private void createFieldData(String catalog, String schema, String tabelle) {
		DatabaseMetaData dbmd;
		try {
			HashMap<String, String> labelGus = getLabelGUS(catalog, schema, tabelle, null);
			dbmd = getConn(driver).getMetaData();
			ResultSet rs = dbmd.getColumns(catalog, schema, tabelle, null);
			while (rs.next()) {
				String name = rs.getString("COLUMN_NAME");
				name = name.toUpperCase();
				schemas.add(catalog + "." + schema);
				tables.add(tabelle);
				felder.add(name);
				String key = catalog + "." + schema + "." + tabelle + "." + name;
				if (labelGus != null && labelGus.containsKey(key)) {
					labels.add(labelGus.get(key));
				} else {
					labels.add("");
				}
				types.add(rs.getInt("DATA_TYPE"));
				typeNames.add(rs.getString("TYPE_NAME"));
				dispSizes.add(rs.getInt("COLUMN_SIZE"));
				precis.add(rs.getInt("COLUMN_SIZE"));
				scales.add(rs.getInt("DECIMAL_DIGITS"));
				keynrs.add(0);
			}
			rs.close();
			HashMap<String, Integer> hilf = new HashMap<String, Integer>();
			ResultSet rs2 = sqls.getConnection().getMetaData().getPrimaryKeys(catalog, schema, tabelle);
			while (rs2.next()) {
				String key2 = catalog + "." + schema + "." + tabelle + "." + rs2.getString("COLUMN_NAME").trim();
				hilf.put(key2, rs2.getInt("KEY_SEQ"));
			}
			rs2.close();
			for (int i = 0; i < felder.size(); i++) {
				String key = schemas.get(i).trim() + "." + tables.get(i) + "." + felder.get(i).trim();
				if (hilf.containsKey(key)) {
					keynrs.set(i, hilf.get(key));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		closeStmt();
		super.close();
	}

	public void closeStmt() {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getFelder() {
		return felder;
	}

	public ArrayList<String> getLabels() {
		return labels;
	}

	public ArrayList<Integer> getTypes() {
		return types;
	}

	public ArrayList<String> getTypeNames() {
		return typeNames;
	}

	public ArrayList<Integer> getDispSizes() {
		return dispSizes;
	}

	public ArrayList<Integer> getPrecis() {
		return precis;
	}

	public ArrayList<Integer> getScales() {
		return scales;
	}

	public ArrayList<String> getTables() {
		return tables;
	}

	public ArrayList<String> getSchemas() {
		return schemas;
	}

	public ArrayList<Integer> getKeynrs() {
		return keynrs;
	}

	public ArrayList<Satz> createFieldList(String sql) throws SQLException {
		HashMap<String, Satz> hm = getSchemaTable(sql);
		String sql2 = "SELECT * FROM ";
		if (driver == DRIVER_SQLSERVER) {
			sql2 = "SELECT TOP 1 * FROM ";
		}
		Iterator<String> iter = hm.keySet().iterator();
		boolean erster = true;
		while (iter.hasNext()) {
			Satz s = hm.get(iter.next());
			String schema = s.getWertS("SCHEMA");
			String table = s.getWertS("TABLE");
			if (schema.equals("")) {
				if (erster)
					sql2 = sql2 + table;
				else
					sql2 = sql2 + ", " + table;
			} else {
				if (erster)
					sql2 = sql2 + schema + "." + table;
				else
					sql2 = sql2 + ", " + schema + "." + table;
			}
			erster = false;
		}
		if (this.sql == null || !this.sql.equals(sql2)) {
			read(sql2, true, null, 1, 0, null);
			closeStmt();
		}
		ArrayList<Satz> liste = new ArrayList<Satz>();
		for (int i = 0; i < felder.size(); i++) {
			Satz s = new Satz();
			s.putFeld("NAME", felder.get(i));
			if (i < labels.size())
				s.putFeld("LABEL", labels.get(i));
			else
				s.putFeld("LABEL", "");
			if (i < typeNames.size())
				s.putFeld("TYPE", typeNames.get(i));
			else
				s.putFeld("TYPE", "");
			if (i < precis.size())
				s.putFeld("PRECIS", precis.get(i).toString());
			else
				s.putFeld("PRECIS", "");
			if (i < scales.size())
				s.putFeld("SCALE", scales.get(i).toString());
			else
				s.putFeld("SCALE", "");
			if (i < schemas.size())
				s.putFeld("SCHEMA", schemas.get(i));
			else
				s.putFeld("SCHEMA", "");
			if (i < tables.size())
				s.putFeld("TABLE", tables.get(i));
			else
				s.putFeld("TABLES", "");
			if (i < keynrs.size() && keynrs.get(i) > 0)
				s.putFeld("KEYNR", keynrs.get(i).toString());
			else
				s.putFeld("KEYNR", "");
			liste.add(s);
		}
		return liste;
	}

	public ArrayList<Feld> createFieldList2(String sql) throws SQLException {
		HashMap<String, Satz> hm = getSchemaTable(sql);
		String sql2 = "SELECT * FROM ";
		if (driver == DRIVER_SQLSERVER) {
			sql2 = "SELECT TOP 1 * FROM ";
		}
		Iterator<String> iter = hm.keySet().iterator();
		boolean erster = true;
		while (iter.hasNext()) {
			Satz s = hm.get(iter.next());
			String schema = s.getWertS("SCHEMA");
			String table = s.getWertS("TABLE");
			if (schema.equals("")) {
				if (erster)
					sql2 = sql2 + table;
				else
					sql2 = sql2 + ", " + table;
			} else {
				if (erster)
					sql2 = sql2 + schema + "." + table;
				else
					sql2 = sql2 + ", " + schema + "." + table;
			}
			erster = false;
		}
		if (this.sql == null || !this.sql.equals(sql2)) {
			read(sql2, true, null, 1, 0, null);
			closeStmt();
		}
		ArrayList<Feld> liste = new ArrayList<Feld>();
		for (int i = 0; i < felder.size(); i++) {
			String name = felder.get(i);
			String label = null;
			String type = null;
			int preci = 0;
			int scale = 0;
			String schema = null;
			String table = null;
			int keynr = 0;
			if (i < labels.size())
				label = labels.get(i);
			else
				label = "";
			if (i < typeNames.size())
				type = typeNames.get(i);
			else
				type = "";
			if (i < precis.size())
				preci = precis.get(i);
			if (i < scales.size())
				scale = scales.get(i);
			if (i < schemas.size())
				schema = schemas.get(i);
			else
				schema = "";
			if (i < tables.size())
				table = tables.get(i);
			else
				table = "";
			if (i < keynrs.size() && keynrs.get(i) > 0)
				keynr = keynrs.get(i);
			Feld feld = new Feld(keynr, name, label, type, preci, scale, schema, table);
			liste.add(feld);
		}
		return liste;
	}

	public ArrayList<String> createFieldName() {
		ArrayList<String> liste = new ArrayList<String>();
		liste.add("KEYNR");
		liste.add("NAME");
		liste.add("LABEL");
		liste.add("TYPE");
		liste.add("PRECIS");
		liste.add("SCALE");
		liste.add("SCHEMA");
		liste.add("TABLE");
		return liste;
	}

	public ArrayList<String> createFieldLabel() {
		ArrayList<String> liste = new ArrayList<String>();
		liste.add("Key");
		liste.add("Feld-Name");
		liste.add("Bezeichnung");
		liste.add("Typ");
		liste.add("Länge");
		liste.add("NK");
		liste.add("Schema");
		liste.add("Tabelle");
		return liste;
	}

	public ArrayList<Integer> createFieldDispSize() {
		ArrayList<Integer> liste = new ArrayList<Integer>();
		liste.add(20);
		liste.add(100);
		liste.add(300);
		liste.add(100);
		liste.add(50);
		liste.add(50);
		liste.add(100);
		liste.add(100);
		return liste;
	}

	@Override
	public ResultSetMetaData getMetaData(String sql) throws SQLException {
		return super.getMetaData(sql, driver);
	}

	public int getDriver() {
		return driver;
	}

}
