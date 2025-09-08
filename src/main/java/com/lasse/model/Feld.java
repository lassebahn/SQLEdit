package com.lasse.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Modellklasse für ein Datenbankfeld.
 * @author Lasse Schöttner
 *
 */
public class Feld {
	private SimpleIntegerProperty key;
	private SimpleStringProperty name;
	private SimpleStringProperty bezeichnung;
	private SimpleStringProperty typ;
	private SimpleIntegerProperty laenge;
	private SimpleIntegerProperty nk;
	private SimpleStringProperty schema;
	private SimpleStringProperty tabelle;

	/**
	 * Alpha
	 * @param name
	 * @param bezeichnung
	 * @param typ
	 * @param laenge
	 * @param schema
	 * @param tabelle
	 */
    public Feld(String name, String bezeichnung, String typ, int laenge, String schema, String tabelle) {
        this.key = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty(name);
        this.bezeichnung = new SimpleStringProperty(bezeichnung);
        this.typ = new SimpleStringProperty(typ);
        this.laenge = new SimpleIntegerProperty(laenge);
        this.nk = new SimpleIntegerProperty(0);
        this.schema = new SimpleStringProperty(schema);
        this.tabelle = new SimpleStringProperty(tabelle);
    }
    
    /**
     * Numerisch
     * @param name
     * @param bezeichnung
     * @param typ
     * @param laenge
     * @param nk
     * @param schema
     * @param tabelle
     */
    public Feld(String name, String bezeichnung, String typ, int laenge, int nk, String schema, String tabelle) {
        this.key = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty(name);
        this.bezeichnung = new SimpleStringProperty(bezeichnung);
        this.typ = new SimpleStringProperty(typ);
        this.laenge = new SimpleIntegerProperty(laenge);
        this.nk = new SimpleIntegerProperty(nk);
        this.schema = new SimpleStringProperty(schema);
        this.tabelle = new SimpleStringProperty(tabelle);
    }

    /**
     * Alpha mit Key
     * @param key
     * @param name
     * @param bezeichnung
     * @param typ
     * @param laenge
     * @param schema
     * @param tabelle
     */
    public Feld(int key, String name, String bezeichnung, String typ, int laenge, String schema, String tabelle) {
        this.key = new SimpleIntegerProperty(key);
        this.name = new SimpleStringProperty(name);
        this.bezeichnung = new SimpleStringProperty(bezeichnung);
        this.typ = new SimpleStringProperty(typ);
        this.laenge = new SimpleIntegerProperty(laenge);
        this.nk = new SimpleIntegerProperty(0);
        this.schema = new SimpleStringProperty(schema);
        this.tabelle = new SimpleStringProperty(tabelle);
    }

    /**
     * Numerisch mit Key
     * @param key
     * @param name
     * @param bezeichnung
     * @param typ
     * @param laenge
     * @param nk
     * @param schema
     * @param tabelle
     */
    public Feld(int key, String name, String bezeichnung, String typ, int laenge, int nk, String schema, String tabelle) {
        this.key = new SimpleIntegerProperty(key);
        this.name = new SimpleStringProperty(name);
        this.bezeichnung = new SimpleStringProperty(bezeichnung);
        this.typ = new SimpleStringProperty(typ);
        this.laenge = new SimpleIntegerProperty(laenge);
        this.nk = new SimpleIntegerProperty(nk);
        this.schema = new SimpleStringProperty(schema);
        this.tabelle = new SimpleStringProperty(tabelle);
    }

	public int getKey() {
		return key.get();
	}
	public void setKey(int key) {
		this.key.set(key);
	}
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public String getBezeichnung() {
		return bezeichnung.get();
	}
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung.set(bezeichnung);
	}
	public String getTyp() {
		return typ.get();
	}
	public void setTyp(String typ) {
		this.typ.set(typ);
	}
	public int getLaenge() {
		return laenge.get();
	}
	public void setLaenge(int laenge) {
		this.laenge.set(laenge);
	}
	public int getNk() {
		return nk.get();
	}
	public void setNk(int nk) {
		this.nk.set(nk);
	}
	public String getSchema() {
		return schema.get();
	}
	public void setSchema(String schema) {
		this.schema.set(schema);
	}
	public String getTabelle() {
		return tabelle.get();
	}
	public void setTabelle(String tabelle) {
		this.tabelle.set(tabelle);
	}
	
	
}
