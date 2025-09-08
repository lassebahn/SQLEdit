package com.lasse.model;

import org.springframework.context.ApplicationEvent;

/**
 * @author Lasse Schöttner
 */
public class EventSqlFieldBatch extends ApplicationEvent {
    private final String sql;
    private final int servernr;

    public EventSqlFieldBatch(Object source, String sql, int servernr) {
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