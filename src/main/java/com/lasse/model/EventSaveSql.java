package com.lasse.model;

import java.io.File;

import org.springframework.context.ApplicationEvent;


/**
 * Event-Klasse für das Speichern von SQL-Abfragen.
 * @author Lasse Schöttner
 */
public class EventSaveSql extends ApplicationEvent {
    private final int servernr;
    private final String sql;
    private final File file;
    private static final long serialVersionUID = 1L;

    public EventSaveSql(Object source, int servernr, String sql, File file) {
        super(source);
        this.servernr = servernr;
        this.sql = sql;
        this.file = file;
   }

    public int getServernr() {
		return servernr;
	}
    
	public String getSql() {
		return sql;
	}

	public File getFile() {
		return file;
	}
	
}