package com.lasse.model;

import java.util.ArrayList;

/**
 * SQL-Abfragen Container-Klasse.
 * @author Lasse Schöttner
 */

public class SqlAbfragen {
	private ArrayList<SqlAbfrage> abfragen = new ArrayList<SqlAbfrage>();

	public ArrayList<SqlAbfrage> getAbfragen() {
		return abfragen;
	}

	public void setAbfragen(ArrayList<SqlAbfrage> abfragen) {
		this.abfragen = abfragen;
	}
}
