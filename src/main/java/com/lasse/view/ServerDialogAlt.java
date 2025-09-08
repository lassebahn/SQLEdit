package com.lasse.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import com.lasse.model.Server;
import com.lasse.model.ServerListe;
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
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ServerDialogAlt extends Stage {
	@FXML
	private TextField serverField;
	@FXML
	private ComboBox<String> typCombo;
	@FXML
	private TextField userField;
	@FXML
	private TextField passwordField;
	@FXML
	private ComboBox<String> serverCombo;
	@FXML
	private Label typLabel;

	private final static String[] TYPEN = { "DB2/400", "SQL Server", "DB2" };
	private ServerListe serverListe;
	private String server;
	private PasswordEncoder pe;
	private int serverTyp;
	private String user;
	private String password;
	private boolean savePressed = false;

	public ServerDialogAlt(Parent parent, ServerListe serverListe, String server) {
		this.serverListe = serverListe;
		pe = PasswordEncoder.getInstance();
		this.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerAlt.fxml"));
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
		ArrayList<String> servers = new ArrayList<String>();
		for (int i = 0; i < serverListe.size(); i++) {
			servers.add(serverListe.get(i).getName());
		}
		ObservableList<String> observableList = FXCollections.observableList(servers);
		serverCombo.setItems(observableList);
		serverCombo.setValue(server);
		serverField.setText("");
		ArrayList<String> ts = new ArrayList<String>();
		ts.add(TYPEN[0]);
		ts.add(TYPEN[1]);
		ts.add(TYPEN[2]);
		ObservableList<String> observableList2 = FXCollections.observableList(ts);
		typCombo.setItems(observableList2);
		aktuTyp();
	}

	@FXML
	private void add() {
		String neu = serverField.getText().trim();
		boolean vorhanden = false;
		for (int i = 0; i < serverCombo.getItems().size(); i++) {
			if (neu.equals(serverCombo.getItems().get(i))) {
				vorhanden = true;
			}
		}
		if (!vorhanden) {
			int typ = DefaultDBService.DRIVER_DB2400;
			String typSel = typCombo.getSelectionModel().getSelectedItem();
			if (typSel.equals(TYPEN[0])) {
				typ = DefaultDBService.DRIVER_DB2400;
			} else if (typSel.equals(TYPEN[1])) {
				typ = DefaultDBService.DRIVER_SQLSERVER;
			} else if (typSel.equals(TYPEN[2])) {
				typ = DefaultDBService.DRIVER_DB2;
			}
			Server server = new Server();
			server.setName(neu);
			server.setTyp(typ);
			server.setUser(userField.getText());
			server.setPassword(pe.encode2(passwordField.getText()));
			serverListe.add(server);
			serverCombo.getItems().add(neu);
			serverCombo.getSelectionModel().select(neu);
			aktuTyp();
		}
	}

	@FXML
	private void delete() {
		int i = serverCombo.getSelectionModel().getSelectedIndex();
		serverCombo.getItems().remove(i);
		serverListe.remove(i);
		serverCombo.getSelectionModel().selectFirst();
	}

	@FXML
	private void save() {
		int sel = serverCombo.getSelectionModel().getSelectedIndex();
		if (sel >= 0) {
			server = serverCombo.getSelectionModel().getSelectedItem();
			serverTyp = serverListe.get(sel).getTyp();
			user = serverListe.get(sel).getUser();
			password = serverListe.get(sel).getPassword();
		}
		savePressed = true;
		this.close();
	}

	@FXML
	private void copy() {
	}

	@FXML
	private void update() {
	}

	@FXML
	private void cancel() {
		this.close();
	}

	private void aktuTyp() {
		int typ = DefaultDBService.DRIVER_DB2400;
		String wert = serverCombo.getValue();
		for (int i = 0; i < serverListe.size(); i++) {
			if (wert.equals(serverListe.get(i).getName())) {
				typ = serverListe.get(i).getTyp();
			}
		}
		if (typ == DefaultDBService.DRIVER_DB2400) {
			typLabel.setText(TYPEN[0]);
		}
		if (typ == DefaultDBService.DRIVER_SQLSERVER) {
			typLabel.setText(TYPEN[1]);
		}
		if (typ == DefaultDBService.DRIVER_DB2) {
			typLabel.setText(TYPEN[2]);
		}
	}


	public String getServer() {
		return server;
	}

	public int getServerTyp() {
		return serverTyp;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}


	public boolean isSavePressed() {
		return savePressed;
	}

	public ServerListe getServerListe() {
		return serverListe;
	}

	
}
