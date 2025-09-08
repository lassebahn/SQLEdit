package com.lasse.model;

import java.util.ArrayList;

import org.springframework.context.ApplicationEvent;

import com.roha.srvcls.model.Satz;

/**
 * 
 */
public class EventLesen extends ApplicationEvent {
    private final int servernr;

    public EventLesen(Object source, int servernr, ArrayList<Satz> liste) {
        super(source);
        this.servernr = servernr;
    }

    public int getServernr() {
		return servernr;
	}
}