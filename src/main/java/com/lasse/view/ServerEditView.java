/*
Lasse Schöttner im Jahr 2022
Diese Software ist Eigentum der roha arzneimittel GmbH.
*/

package com.lasse.view;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lasse.config.FxmlView;
import com.lasse.control.StageManager;
import com.lasse.model.Server;
import com.roha.srvcls.service.DefaultDBService;
import com.roha.srvcls.service.PasswordEncoder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Server bearbeiten
 * @author Lasse Schöttner
 */
@Component
public class ServerEditView extends BasisEditController<Server>{
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
	private ServerListeView serverListeView;
	public final static String[] TYPEN = { "DB2/400", "DB2", "SQL Server" };

	@Lazy
	public ServerEditView(StageManager stageManager) {
		super(stageManager);
		pe = PasswordEncoder.getInstance();
	}
	
	@Autowired
	public void setServerListeView(ServerListeView serverListeView) {
		this.serverListeView = serverListeView;
	}

	protected void initDaten() {
		Server server = (Server)dekoderFx;
		if (mode == 1 || server == null) {
			server = new Server();
			server.setNummer(0);
			dekoderFx = server;
		}
		serverField.setText(server.getName());
		userField.setText(server.getUser());
		passwordField.setText(pe.decode2(server.getPassword()));
		typCombo.getSelectionModel().select(server.getTyp());
	}
	
	@FXML
	public void initialize() {
		super.initialize();
		ArrayList<String> ts = new ArrayList<String>();
		ts.add(TYPEN[0]);
		ts.add(TYPEN[1]);
		ts.add(TYPEN[2]);
		ObservableList<String> observableList2 = FXCollections.observableList(ts);
		typCombo.setItems(observableList2);
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
	
	@FXML
	protected void save() {
		super.save();
		serverListeView.setSelNummer(dekoderFx.getNummer());
		stageManager.switchToNextScene(FxmlView.SERVERLISTEVIEW, getStagenr());
	}
	

	@FXML
	protected void cancel() {
		stageManager.switchToNextScene(FxmlView.SERVERLISTEVIEW, getStagenr());
	}

}
