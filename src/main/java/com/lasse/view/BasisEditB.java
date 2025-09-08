package com.lasse.view;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.UnaryOperator;

import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Neues Fenster für Eingabe von Werten
 * @author lasse
 *
 */
public class BasisEditB extends Stage {

	@FXML
	protected Button saveButton;
	@FXML
	protected Button cancelButton;
	@FXML
	protected ComboBox<String> msgLine;
	

	protected boolean save = false;
	protected String nameFxml;
	protected int mode;
	protected UnaryOperator<Change> integerFilter;
	protected UnaryOperator<Change> bigdecimalFilter;
	protected StringConverter<Integer> converter;
	protected StringConverter<BigDecimal> converterbc;
	private Format format;
	protected static Logger logger = Logger.getLogger(BasisEditB.class);

	public BasisEditB(boolean modal) {
		init(modal);
		setFxml(this.getClass().getSimpleName() + ".fxml");
	}

	public BasisEditB(boolean modal, String fxml) {
		init(modal);
		setFxml(fxml);
	}

	
	public void setMode (int mode) {
		this.mode = mode;
	}
	
	private void setFxml(String fxml) {
		nameFxml = fxml;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(nameFxml));
		fxmlLoader.setController(this);

		// Nice to have this in a load() method instead of constructor, but this seems
		// to be the convention.
		try {
			setScene(new Scene((Parent) fxmlLoader.load()));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void init(boolean modal) {
		// if (dekoderFx != null) dekoderFxi = dekoderFx.kopieren();

		if (modal)
			this.initModality(Modality.APPLICATION_MODAL);
		else
			this.initModality(Modality.NONE);
		
		format = new Format();
	}

	protected void initDaten() {
	}
	
	public static void addTextLimiter(final TextField tf, final int maxLength) {
		Format.addTextLimiter(tf, maxLength);
	}
	
	public TextFormatter<Integer> createTextFormatter() {
		return format.createTextFormatter();
	}

	public TextFormatter<BigDecimal> createTextFormatterBC() {
		return format.createTextFormatterBC();
	}

	@FXML
	protected void initialize() {
	}

	@FXML
	protected void save() {
		save = true;
		this.close();
	}

	@FXML
	protected void cancel() {
		save = false;
		this.close();
	}

	public boolean isSave() {
		return save;
	}

	public static int str2Int(String wert) {
		return Format.str2Int(wert);
	}
}
