package com.lasse.view;

import org.apache.log4j.Logger;

import com.lasse.model.Daten;

import javafx.fxml.FXML;

public class BasisEdit<TFX extends Daten> extends BasisEditB {

	protected TFX dekoderFx;
	protected static Logger logger = Logger.getLogger(BasisEdit.class);

	public BasisEdit(boolean modal) {
		super(modal);
	}

	public BasisEdit(boolean modal, String fxml) {
		super(modal, fxml);
	}

	/**
	public BasisEdit(TFX dekoderFx, boolean modal, String nameFxml) {
		this.nameFxml = nameFxml;
		init(dekoderFx, modal);
	}
	*/
	
	public void setDekoderFx(TFX dekoderFx) {
		this.dekoderFx = dekoderFx;
		save = false;
		initDaten();
	}
	

	@FXML
	protected void save() {
		syncDekoderFx();
		save = true;
		this.close();
	}

	protected void syncDekoderFx() {
	}


	public TFX getDekoderFx() {
		return dekoderFx;
	}


}
