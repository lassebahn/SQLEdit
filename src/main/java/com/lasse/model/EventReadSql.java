package com.lasse.model;

import java.io.File;

import org.springframework.context.ApplicationEvent;


/**
 * Event-Klasse für das Lesen von SQL-Abfragen.
 * @author Lasse Schöttner
 * 
 */
public class EventReadSql extends ApplicationEvent {
    private final int servernr;
    private final File file;
    private static final long serialVersionUID = 1L;

    public EventReadSql(Object source, int servernr, File file) {
        super(source);
        this.servernr = servernr;
        this.file = file;
   }

    public int getServernr() {
		return servernr;
	}
    
	public File getFile() {
		return file;
	}
	
}