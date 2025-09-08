/*
Lasse Schöttner im Jahr 2022
Diese Software ist Eigentum der roha arzneimittel GmbH.
*/

package com.lasse.model;

import com.lasse.view.ServerDialog;
import com.lasse.view.ServerEdit;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Datenbank-Server
 * 
 * @author Lasse Schöttner
 *
 */
public class Server extends Daten {
	private IntegerProperty typ = new SimpleIntegerProperty(0);
	private StringProperty typtext = new SimpleStringProperty("");
	private StringProperty user = new SimpleStringProperty("");
	private StringProperty password = new SimpleStringProperty("");
	private StringProperty aktu0 = new SimpleStringProperty("N");
	private StringProperty aktu1 = new SimpleStringProperty("N");
	private StringProperty aktu2 = new SimpleStringProperty("N");
	private StringProperty aktu3 = new SimpleStringProperty("N");

	public IntegerProperty typProperty() {
		return typ;
	}

	public Integer getTyp() {
		return typ.get();
	}

	public void setTyp(int typ) {
		this.typ.set(typ);
		typtext.set(ServerEdit.TYPEN[typ]);
	}

	public String getTyptext() {
		return typtext.get();
	}

	public void setTypText(String typtext) {
		this.typtext.set(typtext);
	}

	public StringProperty userProperty() {
		return user;
	}

	public String getUser() {
		return user.get();
	}

	public void setUser(String user) {
		this.user.set(user);
	}

	public StringProperty passwordProperty() {
		return password;
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

	public StringProperty aktu0Property() {
		return aktu0;
	}

	public String getAktu0() {
		return aktu0.get();
	}

	public void setAktu0(String aktu0) {
		this.aktu0.set(aktu0);
	}
	public StringProperty aktu1Property() {
		return aktu1;
	}

	public String getAktu1() {
		return aktu1.get();
	}

	public void setAktu1(String aktu1) {
		this.aktu1.set(aktu1);
	}

	public StringProperty aktu2Property() {
		return aktu2;
	}

	public String getAktu2() {
		return aktu2.get();
	}

	public void setAktu2(String aktu2) {
		this.aktu2.set(aktu2);
	}

	public StringProperty aktu3Property() {
		return aktu3;
	}

	public String getAktu3() {
		return aktu3.get();
	}

	public void setAktu3(String aktu3) {
		this.aktu3.set(aktu3);
	}

}
