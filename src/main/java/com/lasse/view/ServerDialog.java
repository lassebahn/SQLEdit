package com.lasse.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import com.lasse.control.Controller;
import com.lasse.model.Daten;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

/**
 * Server Liste anzeigen (alt)
 */
public class ServerDialog extends BasisListe<Server, ServerEdit> {
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

	public ServerDialog(Controller controller) {
		super("SERVER", new ServerEdit(true), controller);
		getIcons().add(new Image("sql.png"));
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerDialog.fxml"));
		fxmlLoader.setController(this);

		// Nice to have this in a load() method instead of constructor, but this seems
		// to be the convention.
		try {
			setScene(new Scene((Parent) fxmlLoader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	public ServerDialog(Parent parent, ServerListe serverListe, String server) {
		this.serverListe = serverListe;
		pe = PasswordEncoder.getInstance();
		this.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Server.fxml"));
		fxmlLoader.setController(this);

		// Nice to have this in a load() method instead of constructor, but this seems
		// to be the convention.
		try {
			setScene(new Scene((Parent) fxmlLoader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

	@FXML
	protected void initialize() {
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
			if (s.getName().equals(controller.getServer(0))) {
				selServer = s;
				break;
			}
		}
		tableView.getSelectionModel().select(selServer);
		checkButton();
	}



	@FXML
	private void save() {
	}

	@FXML
	private void select() {
		savePressed = true;
		server = tableView.getSelectionModel().getSelectedItem();
		this.close();
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

	
}
