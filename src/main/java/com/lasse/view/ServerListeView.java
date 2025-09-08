package com.lasse.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lasse.config.FxmlView;
import com.lasse.control.StageManager;
import com.lasse.model.Server;
import com.lasse.model.ServerListe;
import com.roha.srvcls.service.PasswordEncoder;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Liste Server anzeigen
 * @author Lasse Schöttner
 */
@Component
public class ServerListeView extends BasisListeController<Server> {
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
	@FXML
	protected TableView<Server> tableView;

	private ServerListe serverListe;
	private Server server;
	private PasswordEncoder pe;
	private boolean savePressed = false;

	@Lazy
	public ServerListeView(StageManager stageManager) {
		super("SERVER", FxmlView.SERVEREDITVIEW, stageManager);
	}

	@FXML
	public void initialize() {
		super.initialize();
		TableColumn<Server, String> name = createColumnString("name", "Name", 400);
		TableColumn<Server, String> typ = createColumnString("typtext", "Typ", 100);
		TableColumn<Server, String> user = createColumnString("user", "Benutzer", 100);

		tableView.getColumns().addAll(name, typ, user);
		serverListe = controller.getServerListe();
		ArrayList<Server> liste = new ArrayList<Server>();
		for (int i = 0; i < serverListe.size(); i++) {
			liste.add(serverListe.get(i));
		}
		tableView.setItems(FXCollections.<Server>observableArrayList(liste));
		Server selServer = null;
		for (int i = 0; i < serverListe.size(); i++) {
			Server s = serverListe.get(i);
			if (selNummer == 0 && s.getNummer() == controller.getServernummer(getStagenr())) {
				selServer = s;
				break;
			} else if (selNummer != 0 && s.getNummer() == selNummer) {
				selServer = s;
				break;
			}
		}
		tableView.getSelectionModel().select(selServer);
		tableView.scrollTo(selServer);
		checkButton();
		setSelNummer(0);
	}



	@FXML
	private void save() {
	}

	@FXML
	private void select() {
		savePressed = true;
		server = tableView.getSelectionModel().getSelectedItem();
		if (server == null)
			return;
		controller.setServerListe(getServerListe());
		controller.setServernummer(getServer().getNummer(), getStagenr());
		controller.setServer(getServer().getName(), getStagenr());
		controller.setServerTyp(getServer().getTyp(), getStagenr());
		controller.setUser(getServer().getUser(), getStagenr());
		controller.setPassword(getServer().getPassword(), getStagenr());
		try {
			controller.getFileRW().writeServerXMLNeu(controller.getServerListe(), controller.getServer(getStagenr()), getStagenr());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (controller.connect(true, getStagenr())) {
			controller.setStatus("Verbunden mit " + controller.getServer(getStagenr()), getStagenr());
		}
		stageManager.switchToNextScene(FxmlView.SQLEDIT, getStagenr());
	}

	public Server getServer() {
		return server;
	}


	public boolean isSavePressed() {
		return savePressed;
	}

	public ServerListe getServerListe() {
		return serverListe;
	}

	@FXML
	protected void cancel() {
		stageManager.switchToNextScene(FxmlView.SQLEDIT, getStagenr());
	}

	
}
