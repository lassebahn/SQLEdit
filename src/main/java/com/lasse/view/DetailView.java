package com.lasse.view;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lasse.viewmodel.FileTableModel;
import com.lasse.viewmodel.SqlWert;
import com.roha.srvcls.model.Satz;
import com.roha.srvcls.service.DefaultDBService;
import com.roha.srvcls.service.PasswordEncoder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Detail-Anzeige eines Datensatzes
 * @author Lasse Schöttner
 */
@Component
public class DetailView {

	@FXML
	private ScrollPane scrollpan01;
	@FXML
	private GridPane gridpane01;
	
	private Satz satz;
	private FileTableModel fileTable;
	private HashMap<String, String> feldLabel = new HashMap<String, String>();

	@Lazy
	public DetailView() {
	}

	public void init(Satz satz, FileTableModel fileTable) {
		this.satz = satz;
		this.fileTable = fileTable;
		ArrayList<String> key = new ArrayList<String>();
		ArrayList<Integer> keynr = new ArrayList<Integer>();
		ArrayList<Integer> keynrs = fileTable.getKeynrs();
		for (int i = 0; i < fileTable.getFeldnamen().size(); i++) {
			String feld = fileTable.getFeldnamen().get(i);
			String label = fileTable.getColumnLabel(i);
			feldLabel.put(feld, label);
			int k = keynrs.get(i);
			if (k > 0) {
				key.add(feld);
				keynr.add(k);
			}
		}
		int row = 0;
		if (key.size() > 0) {
			gridpane01.add(new Label("Schlüssel:"), 0, row);
			row++;
			for (int i = 0; i < key.size(); i++) {
				//gridpane01.add(new Label(keynr.get(i).toString()), 0, row);
				gridpane01.add(new Label(key.get(i) + " (" + keynr.get(i).toString() + ")"), 0, row);
				gridpane01.add(new Label(satz.getWertS(key.get(i))), 1, row);
				row++;
			}
			gridpane01.add(new Label(""), 0, row);
			row++;
		}
		ArrayList<String> felder = satz.getFeldnamen();
		for (String feld : felder) {
			Object o = satz.getWert(feld);
			String ueberschrift = feld;
			if (feldLabel.containsKey(feld)) {
				ueberschrift = feld + " " + feldLabel.get(feld);
			}
			gridpane01.add(new Label(ueberschrift), 0, row);
			if (o != null) {
				String wert = "";
				if (o instanceof BigDecimal) {
					SqlWert sw = new SqlWert(satz.getWertB(feld), java.sql.Types.DECIMAL);
					wert = sw.getValue();
				} else {
					wert = satz.getWertS(feld).trim();
				}
				gridpane01.add(new Label(wert), 1, row);
			} else {
				gridpane01.add(new Label("null"), 1, row);
			}
			row++;
		}
	}

	@FXML
	private void initialize() {
	}

	@FXML
	private void cancel() {
		closeStage();
	}

	private void closeStage() {
	    Stage stage = (Stage) gridpane01.getScene().getWindow();
	    stage.close();
	}
}
