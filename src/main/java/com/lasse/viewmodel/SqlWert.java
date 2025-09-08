package com.lasse.viewmodel;

import java.math.BigDecimal;

import com.roha.srvcls.service.LasseTool;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Container SQL-Wert für Anzeige
 * @author Lasse Schöttner
 */
public class SqlWert implements ObservableValue<String> {
	
	private String werts;
	private BigDecimal wertb;
	private int type;
	
	public SqlWert(String wert, int type){
		werts = wert;
		this.type = type;
	}

	public SqlWert(BigDecimal wert, int type){
		wertb = wert;
		this.type = type;
	}
	
	@Override
	public void addListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(ChangeListener<? super String> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getValue() {
		if (type == java.sql.Types.DECIMAL){
			if (wertb != null){
				return LasseTool.BDtoString(wertb, "null");
			} else if (werts != null){
				return werts;
			} else {
				return "null";
			}
		} else {
			if (werts != null){
				return werts;
			} else if (wertb != null){
				return LasseTool.BDtoString(wertb, "null");
			} else {
				return "null";
			}
		}
	}

	@Override
	public void removeListener(ChangeListener<? super String> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
