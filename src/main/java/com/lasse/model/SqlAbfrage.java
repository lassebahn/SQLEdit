package com.lasse.model;

import java.util.ArrayList;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * SQL-Abfrage Container-Klasse.
 * @author Lasse Schöttner
 */

public class SqlAbfrage extends Daten {
	private StringProperty sql = new SimpleStringProperty("");
	private ArrayList<SqlFeld> felder = new ArrayList<SqlFeld>();

	
	public String getSql() {
		return sql.get();
	}
	public void setSql(String sql) {
		this.sql.set(sql);
	}
	public ArrayList<SqlFeld> getFelder() {
		return felder;
	}
	public void setFelder(ArrayList<SqlFeld> felder) {
		this.felder = felder;
	}
	public StringProperty sqlProperty() {
		return sql;
	}

}
