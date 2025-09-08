package com.lasse.view;

import org.apache.log4j.Logger;

import com.lasse.control.StageManager;
import com.lasse.model.Daten;
import com.lasse.model.DatenListe;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Anzeige JavaFX, Werte editieren
 * @author Lasse Schöttner
 * @param <TFX>
 */
public class BasisEditController<TFX extends Daten> extends BasisEditBController {

	protected TFX dekoderFx;
	protected String items;
	protected Stage stage;
	protected static Logger logger = Logger.getLogger(BasisEditController.class);

	public BasisEditController(StageManager stageManager) {
		super(stageManager);
	}
	
	public void init(Stage stage) {
		this.stage = stage;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public void setDekoderFx(TFX dekoderFx) {
		this.dekoderFx = dekoderFx;
		save = false;
		initDaten();
	}
	

	@FXML
	protected void save() {
		syncDekoderFx();
		DatenListe<TFX> datenListe = controller.getDatenListe(items);
		TFX dfx = getDekoderFx();
		if (dfx.getNummer() == 0) {
			int nr = datenListe.getNewDatenNr();
			dfx.setNummer(nr);
		}
		if (mode ==  1 || mode == 3)
			datenListe.add(dfx);
		datenListe.init();
		controller.speichern(items, getStagenr());
		save = true;
		if (stage != null)
			stage.close();
	}

	protected void syncDekoderFx() {
	}


	public TFX getDekoderFx() {
		return dekoderFx;
	}

}
