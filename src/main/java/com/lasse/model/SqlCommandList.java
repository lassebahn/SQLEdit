package com.lasse.model;

import java.util.ArrayList;

import com.roha.srvcls.model.Schluessel;

/**
 * Liste SQL-Befehle für Historie
 * @author Lasse Schöttner
 * 
 */
public class SqlCommandList {
	private int servernr;
	private ArrayList<Schluessel> sql;
	
	public int getServernr() {
		return servernr;
	}
	public void setServernr(int servernr) {
		this.servernr = servernr;
	}
	public ArrayList<Schluessel> getSql() {
		return sql;
	}
	public void setSql(ArrayList<Schluessel> sql) {
		this.sql = sql;
	}
	
}
