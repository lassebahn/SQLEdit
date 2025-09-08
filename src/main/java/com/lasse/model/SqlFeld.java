package com.lasse.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Parameter für gewpeicherte SQL-Abfragen
 */
public class SqlFeld {
	private StringProperty bezeichnung = new SimpleStringProperty("");
	private StringProperty wert = new SimpleStringProperty("");

	public String getBezeichnung() {
		return bezeichnung.get();
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung.set(bezeichnung);
	}
	
	public StringProperty bezeichnungProperty() {
		return bezeichnung;
	}

	public String getWert() {
		return wert.get();
	}

	public void setWert(String wert) {
		this.wert.set(wert);
	}
	
	public StringProperty wertProperty() {
		return wert;
	}

}
