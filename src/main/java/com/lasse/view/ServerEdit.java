/*
Lasse Schöttner im Jahr 2022
Diese Software ist Eigentum der roha arzneimittel GmbH.
*/

package com.lasse.view;

import java.util.ArrayList;

import com.lasse.model.Server;
import com.roha.srvcls.service.DefaultDBService;
import com.roha.srvcls.service.PasswordEncoder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class ServerEdit extends BasisEdit<Server>{
	@FXML
	private TextField serverField;
	@FXML
	private ComboBox<String> typCombo;
	@FXML
	private TextField userField;
	@FXML
	private TextField passwordField;
	@FXML
	private Label typLabel;
	private PasswordEncoder pe;
	public final static String[] TYPEN = { "DB2/400", "DB2", "MS SQL Server" };

	public ServerEdit(boolean modal) {
		super(modal);
		pe = PasswordEncoder.getInstance();
	}

	protected void initDaten() {
		Server server = (Server)dekoderFx;
		if (mode == 1 || server == null) {
			server = new Server();
			server.setNummer(0);
			dekoderFx = server;
		}
		getIcons().add(new Image("sql.png"));
		serverField.setText(server.getName());
		userField.setText(server.getUser());
		passwordField.setText(pe.decode2(server.getPassword()));
		ArrayList<String> ts = new ArrayList<String>();
		ts.add(TYPEN[0]);
		ts.add(TYPEN[1]);
		ts.add(TYPEN[2]);
		ObservableList<String> observableList2 = FXCollections.observableList(ts);
		typCombo.setItems(observableList2);
		typCombo.getSelectionModel().select(server.getTyp());
	}
	
	@FXML
	protected void initialize() {
	}

	private void aktuTyp(int typ) {
		if (typ == DefaultDBService.DRIVER_DB2400) {
			typLabel.setText(TYPEN[0]);
		}
		if (typ == DefaultDBService.DRIVER_DB2) {
			typLabel.setText(TYPEN[1]);
		}
		if (typ == DefaultDBService.DRIVER_SQLSERVER) {
			typLabel.setText(TYPEN[2]);
		}
	}

	protected void syncDekoderFx() {
		super.syncDekoderFx();
		if (mode == 3) {
			Server s = new Server();
			dekoderFx = s;
		}
		Server s = (Server) dekoderFx;
		s.setName(serverField.getText());
		s.setUser(userField.getText());
		s.setPassword(pe.encode2(passwordField.getText()));
		s.setTyp(typCombo.getSelectionModel().getSelectedIndex());
	}
}
