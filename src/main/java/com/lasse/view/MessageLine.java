package com.lasse.view;

import java.util.GregorianCalendar;

import com.roha.srvcls.service.LasseTool;

import javafx.scene.control.ComboBox;

/**
 * Meldungszeile für Anzeige
 * @author Lasse Schöttner
 */
public class MessageLine extends ComboBox<String>{
	
	private static  int max = 20;

    public MessageLine() {
    	super();
    	this.setPromptText("Meldungen");
    	this.setMaxWidth(Double.MAX_VALUE);
	}
	
	public static String formatText(String text){
		GregorianCalendar gc = new GregorianCalendar();
		int stunde = gc.get(GregorianCalendar.HOUR_OF_DAY);
		int minute = gc.get(GregorianCalendar.MINUTE);
		int sekunde = gc.get(GregorianCalendar.SECOND);
		int millis = gc.get(GregorianCalendar.MILLISECOND);
		text = LasseTool.toStringFL(stunde,2) + ":" + LasseTool.toStringFL(minute,2) + ":" + LasseTool.toStringFL(sekunde,2) + ":" + LasseTool.toStringFL(millis,3) + " " + text;
		return text;
	}
	
	public void aktualisieren(){
		if (this.getItems().size() >= max){
			getItems().remove(0);
		}
		this.getSelectionModel().select(getItems().size()-1);
	}

	public static void aktualisieren(ComboBox<String> cb){
		if (cb.getItems().size() >= max){
			cb.getItems().remove(0);
		}
		cb.getSelectionModel().select(cb.getItems().size()-1);
	}

	
	public void addText(String text){
		text = formatText(text);
		this.getItems().add(text);
		aktualisieren();
	}

	public static void addText(ComboBox<String> cb, String text){
		text = formatText(text);
		cb.getItems().add(text);
		aktualisieren(cb);
	}
}
