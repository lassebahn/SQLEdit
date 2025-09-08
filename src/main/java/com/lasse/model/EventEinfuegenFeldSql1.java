package com.lasse.model;

import org.springframework.context.ApplicationEvent;

/**
 * Event-Klasse für das Einfügen eines Feldes in eine SQL-Abfrage.
 * @author Lasse Schöttner
 */
public class EventEinfuegenFeldSql1 extends ApplicationEvent {
    private final int servernr;
    private final String feld;
    private static final long serialVersionUID = 1L;

    public EventEinfuegenFeldSql1(Object source, int servernr, String feld) {
        super(source);
        this.servernr = servernr;
        this.feld = feld;
   }

    public int getServernr() {
		return servernr;
	}
    
	public String getFeld() {
		return feld;
	}
}