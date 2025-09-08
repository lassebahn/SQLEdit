package com.lasse.model;

import org.springframework.context.ApplicationEvent;

import com.lasse.viewmodel.FileTableModel;

/**
 * Event-Klasse für das Lesen von Daten aus einer SQL-Datenbank.
 * @author Lasse Schöttner
 */
public class EventSqlRead extends ApplicationEvent {
    private final int servernr;
    private final String sql;
    private final int maxcol;
    private SqlAbfrage sqlAbfrage = null;
    private static final long serialVersionUID = 1L;

    public EventSqlRead(Object source, int servernr, String sql, int maxcol) {
        super(source);
        this.servernr = servernr;
        this.sql = sql;
        this.maxcol = maxcol;
   }

    public int getServernr() {
		return servernr;
	}
    
	public String getSql() {
		return sql;
	}

	public int getMaxcol() {
		return maxcol;
	}

	public SqlAbfrage getSqlAbfrage() {
		return sqlAbfrage;
	}

	public void setSqlAbfrage(SqlAbfrage sqlAbfrage) {
		this.sqlAbfrage = sqlAbfrage;
	}

}