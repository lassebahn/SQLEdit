package com.lasse.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Historie SQL-Befehle für alle DB's
 * @author Lasse Schöttner
 */
public class SqlCommandListAll {
	private ArrayList<SqlCommandList> sqlCommandLists = new ArrayList<SqlCommandList>();
	private HashMap<Integer, SqlCommandList> hm = new HashMap<Integer, SqlCommandList>();

	public ArrayList<SqlCommandList> getSqlCommandLists() {
		return sqlCommandLists;
	}

	public void setSqlCommandLists(ArrayList<SqlCommandList> sqlCommandLists) {
		this.sqlCommandLists = sqlCommandLists;
		hm.clear();
		for (SqlCommandList liste : sqlCommandLists) {
			hm.put(liste.getServernr(), liste);
		}
	}

	public SqlCommandList getSqlCommandList(int servernr) {
		if (hm.containsKey(servernr)) {
			return hm.get(servernr);
		} else {
			return new SqlCommandList();
		}
	}
}
