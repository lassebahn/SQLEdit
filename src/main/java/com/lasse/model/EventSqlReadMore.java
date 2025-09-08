package com.lasse.model;

import java.util.ArrayList;

import org.springframework.context.ApplicationEvent;

import com.lasse.viewmodel.FileTableModel;
import com.roha.srvcls.model.Satz;

/**
 * Event-Klasse für das Lesen von Daten aus einer SQL-Datenbank.
 * @author Lasse Schöttner
 */
public class EventSqlReadMore extends ApplicationEvent {
    private final int servernr;
    private final ArrayList<Satz> liste;
    private final String sql;
    private final int maxcol;
    private SqlAbfrage sqlAbfrage = null;
    private final FileTableModel fileTable;
    private static final long serialVersionUID = 1L;

    public EventSqlReadMore(Object source, int servernr, ArrayList<Satz> liste, String sql, int maxcol, FileTableModel fileTable) {
        super(source);
        this.servernr = servernr;
        this.liste = liste;
        this.sql = sql;
        this.maxcol = maxcol;
        this.fileTable = fileTable;
   }

    public int getServernr() {
		return servernr;
	}
    
    public ArrayList<Satz> getListe() {
		return liste;
	}

	public String getSql() {
		return sql;
	}

	public int getMaxcol() {
		return maxcol;
	}
	
	public FileTableModel getFileTable() {
		return fileTable;
	}
	
	public SqlAbfrage getSqlAbfrage() {
		return sqlAbfrage;
	}

	public void setSqlAbfrage(SqlAbfrage sqlAbfrage) {
		this.sqlAbfrage = sqlAbfrage;
	}

}