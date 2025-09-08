package com.lasse.view;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

import org.apache.log4j.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * Formatierungshilfen für JavaFX TextField
 * @author Lasse Schöttner
 *
 */
public class Format {

	private UnaryOperator<Change> integerFilter;
	private UnaryOperator<Change> bigdecimalFilter;
	private StringConverter<Integer> converter;
	private StringConverter<BigDecimal> converterbc;
	private static Logger logger = Logger.getLogger(Format.class);

	public Format() {
		integerFilter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches("-?([1-9][0-9]*)?")) {
				return change;
			} else if ("-".equals(change.getText())) {
				if (change.getControlText().startsWith("-")) {
					change.setText("");
					change.setRange(0, 1);
					change.setCaretPosition(change.getCaretPosition() - 2);
					change.setAnchor(change.getAnchor() - 2);
					return change;
				} else {
					change.setRange(0, 0);
					return change;
				}
			}
			return null;
		};

		bigdecimalFilter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches("-?([1-9,][0-9,]*)?")) {
				return change;
			} else if ("-".equals(change.getText())) {
				if (change.getControlText().startsWith("-")) {
					change.setText("");
					change.setRange(0, 1);
					change.setCaretPosition(change.getCaretPosition() - 2);
					change.setAnchor(change.getAnchor() - 2);
					return change;
				} else {
					change.setRange(0, 0);
					return change;
				}
			}
			return null;
		};

		// modified version of standard converter that evaluates an empty string
		// as zero instead of null:
		converter = new IntegerStringConverter() {
			@Override
			public Integer fromString(String s) {
				if (s.isEmpty())
					return 0;
				return super.fromString(s);
			}
		};

		converterbc = new BigDecimalStringConverter() {
			@Override
			public BigDecimal fromString(String s) {
				if (s.isEmpty())
					return new BigDecimal(0);
				return super.fromString(s.replace(',', '.'));
			}
		};
	}

	public static void addTextLimiter(final TextField tf, final int maxLength) {
	    tf.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
	            if (tf.getText().length() > maxLength) {
	                String s = tf.getText().substring(0, maxLength);
	                tf.setText(s);
	            }
	        }
	    });
	}
	
	public TextFormatter<Integer> createTextFormatter() {
		TextFormatter<Integer> textFormatter = new TextFormatter<Integer>(converter, 0, integerFilter);
		return textFormatter;
	}

	public TextFormatter<BigDecimal> createTextFormatterBC() {
		TextFormatter<BigDecimal> textFormatter = new TextFormatter<BigDecimal>(converterbc, new BigDecimal(0), bigdecimalFilter);
		return textFormatter;
	}

	public static int str2Int(String wert) {
		int erg = 0;
		try {
			erg = new Integer(wert.trim());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}
		return erg;
	}

	public static BigDecimal str2BC(String wert) {
		BigDecimal erg =new BigDecimal(0);
		try {
			erg = new BigDecimal(wert.trim().replace(',', '.'));
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}
		return erg;
	}

}
