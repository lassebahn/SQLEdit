package com.lasse.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * Dialog für Excel-Ausgabe
 * @author Lasse Schöttner
 *
 */
public class ExcelDialog extends Stage {
	@FXML
	private TextField dateiField;
	@FXML
	private TextField ueberschriftField;
	@FXML
	private RadioButton labelRB;
	@FXML
	private RadioButton feldRB;
	@FXML
	private RadioButton beidesRB;
	
	private String datei;
	private String ueberschrift;
	private char colHeader = 'B';

	private boolean savePressed = false;

	public ExcelDialog(String datei, String ueberschrift) {
		this.setTitle("Ausgabe in Excel-Datei");
		getIcons().add(new Image("sql.png"));
		this.datei = datei;
		this.ueberschrift = ueberschrift;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Excel.fxml"));
		fxmlLoader.setController(this);

		// Nice to have this in a load() method instead of constructor, but this seems
		// to be the convention.
		try {
			setScene(new Scene((Parent) fxmlLoader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void initialize() {
		dateiField.setText(datei);
		ueberschriftField.setText(ueberschrift);
		final ToggleGroup group = new ToggleGroup();
		labelRB.setToggleGroup(group);
		feldRB.setToggleGroup(group);
		beidesRB.setToggleGroup(group);
		beidesRB.setSelected(true);
	}

	@FXML
	private void save() {
		datei = dateiField.getText();
		ueberschrift = ueberschriftField.getText();
		if (beidesRB.isSelected()) colHeader = 'B';
		else if (feldRB.isSelected()) colHeader = 'F';
		else if (labelRB.isSelected()) colHeader = 'L';
		savePressed = true;
		this.close();
	}

	@FXML
	private void cancel() {
		this.close();
	}

	public boolean isSavePressed() {
		return savePressed;
	}

	public String getDatei() {
		return datei;
	}

	public String getUeberschrift() {
		return ueberschrift;
	}

	public char getColHeader() {
		return colHeader;
	}

	
}
