/*
Lasse Schöttner im Jahr 2022
*/

package com.lasse.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Daten {
	private StringProperty name = new SimpleStringProperty("");
	private IntegerProperty nummer = new SimpleIntegerProperty(0);

	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public IntegerProperty nummerProperty() {
		return nummer;
	}

	public Integer getNummer() {
		return nummer.get();
	}

	public void setNummer(int nummer) {
		this.nummer.set(nummer);
	}


}
