package com.lasse.model;

import java.util.ArrayList;

import org.springframework.context.ApplicationEvent;

import com.roha.srvcls.model.Schluessel;

/**
 * Event-Klasse, die ausgelöst wird, wenn SQL-Historie für die GUI bereitgestellt wird.
 * 
 */
public class EventSqlHistGUI extends ApplicationEvent {
    private final int servernr;
    private final ArrayList<Schluessel> sqlHistListe;

    public EventSqlHistGUI(Object source, int servernr, ArrayList<Schluessel> sqlHistListe) {
        super(source);
        this.sqlHistListe = sqlHistListe;
        this.servernr = servernr;
    }

    public ArrayList<Schluessel> getSqlHistListe() {
		return sqlHistListe;
	}
    
    
	public int getServernr() {
		return servernr;
	}

}