package com.lasse.model;

import org.springframework.context.ApplicationEvent;


public class EventSqlFieldGui extends ApplicationEvent {
    private final String sql;
    private final int servernr;

    public EventSqlFieldGui(Object source, String sql, int servernr) {
        super(source);
        this.sql = sql;
        this.servernr = servernr;
    }

    public String getSql() {
        return sql;
    }
    
    public int getServernr() {
		return servernr;
	}
}