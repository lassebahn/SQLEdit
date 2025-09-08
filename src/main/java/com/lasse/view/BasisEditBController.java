package com.lasse.view;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lasse.control.Controller;
import com.lasse.control.StageManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.StringConverter;

/**
 * Neue Anzeige für Eingabe von Werten
 * @author lasse
 *
 */
public class BasisEditBController {

	@FXML
	protected Button saveButton;
	@FXML
	protected Button cancelButton;
	@FXML
	protected ComboBox<String> msgLine;
	

	protected int mode;
	protected boolean save;
	protected UnaryOperator<Change> integerFilter;
	protected UnaryOperator<Change> bigdecimalFilter;
	protected StringConverter<Integer> converter;
	protected StringConverter<BigDecimal> converterbc;
	private Format format;
	protected StageManager stageManager;
	protected Controller controller;
	private int stagenr;
	protected static Logger logger = Logger.getLogger(BasisEditBController.class);

	public BasisEditBController(StageManager stageManager) {
		this.stageManager = stageManager;
		init();
	}
	

	@Autowired
	public void setController(Controller c) {
		controller = c;
	}

	

	public int getStagenr() {
		return stagenr;
	}


	public void setStagenr(int stagenr) {
		this.stagenr = stagenr;
	}


	public void init() {
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
	public void initialize() {
	}

	@FXML
	protected void save() {
		save = true;
	}

	@FXML
	protected void cancel() {
		save = false;
	}

	public static int str2Int(String wert) {
		return Format.str2Int(wert);
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public boolean isSave() {
		return save;
	}
	
	
}
