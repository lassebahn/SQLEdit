package com.lasse.model;

import java.util.ArrayList;

import org.springframework.context.ApplicationEvent;

import com.lasse.viewmodel.FileTableModel;
import com.roha.srvcls.model.Satz;

/**
 * Event-Klasse für die Antwort auf eine SQL-Abfrage.
 * @author Lasse Schöttner
 */
public class EventSqlResponse extends ApplicationEvent {
    private final int servernr;
    private final ArrayList<Satz> liste;
    private final String sql;
    private final boolean more;
    private final FileTableModel fileTable;
    private final boolean erfolgreich;
    private final String message;
    private final String status;
    private final boolean connected;
    private final boolean neueTabelle;
    private static final long serialVersionUID = 1L;

    public EventSqlResponse(Object source, int servernr, ArrayList<Satz> liste, String sql, FileTableModel fileTable, boolean more, boolean erfolgreich, String message, String status, boolean connected, boolean neueTabelle) {
    	super(source);
        this.servernr = servernr;
        this.liste = liste;
        this.sql = sql;
        this.fileTable = fileTable;
        this.more = more;
        this.erfolgreich = erfolgreich;
        this.message = message;
        this.status = status;
        this.connected = connected;
        this.neueTabelle = neueTabelle;
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

	public FileTableModel getFileTable() {
		return fileTable;
	}
	
	public boolean isMore() {
		return more;
	}
	
	public boolean isErfolgreich() {
		return erfolgreich;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getStatus() {
		return status;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public boolean isNeueTabelle() {
		return neueTabelle;
	}
}