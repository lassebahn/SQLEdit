package com.lasse.view;

import java.util.function.Function;

import com.lasse.model.Daten;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

/**
 * Button-Zelle für JavaFX TableView
 * @author Lasse Schöttner
 * @param <TFX>
 */
public class ButtonCell<TFX extends Daten> extends TableCell<TFX, String> {
	private final Button button;

	public ButtonCell(String label, Function<TFX, TFX> function) {
		// this.getStyleClass().add("action-button-table-cell");

		button = new Button(label);
		button.setOnAction((ActionEvent e) -> {
			function.apply(getCurrentItem());
		});
		button.setMaxWidth(Double.MAX_VALUE);
	}

	// Display button if the row is not empty
	@Override
	protected void updateItem(String t, boolean empty) {
		super.updateItem(t, empty);
		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(button);
		}
	}
	
    public TFX getCurrentItem() {
        return (TFX) getTableView().getItems().get(getIndex());
    }
}
