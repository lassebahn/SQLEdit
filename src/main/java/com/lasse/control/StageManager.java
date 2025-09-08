package com.lasse.control;

import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.lasse.config.FxmlView;
import com.lasse.view.SqlEditView;
import com.lasse.viewmodel.SqlEditVM;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Verwaltung der Stages und Szenen
 * 
 * @author Lasse Schöttner
 *
 */
public class StageManager {

	private final Stage primaryStage;
	private final Stage[] stage;
	private Parent[] rootSqlEditView;
	private Object[] fxmlController;
	private final FxmlLoader fxmlLoader;
	private final String applicationTitle;
	@Autowired
	private ApplicationContext context;
	private static final Logger logger = LogManager.getLogger(StageManager.class);

	public StageManager(FxmlLoader fxmlLoader, Stage primaryStage, String applicationTitle) {
		this.primaryStage = primaryStage;
		this.fxmlLoader = fxmlLoader;
		this.applicationTitle = applicationTitle;
		primaryStage.getIcons().add(new Image("sql.png"));
		int max = Controller.MAX_SERVER - 1;
		stage = new Stage[max];
		rootSqlEditView = new Parent[Controller.MAX_SERVER];
		fxmlController = new Object[Controller.MAX_SERVER];
	}

	public void switchScene(final FxmlView view) {

		try {
			primaryStage.getIcons().add(new Image("sql.png"));
			primaryStage.setTitle(applicationTitle);

			FXMLLoader loader = fxmlLoader.load(view.getFxmlPath());
			Parent rootNode = loader.getRoot();

			Scene scene = new Scene(rootNode);
			// String stylesheet =
			// Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm();

			// scene.getStylesheets().add(stylesheet);

			primaryStage.setScene(scene);

			if (view == FxmlView.SQLEDIT) {
				SqlEditView s = context.getBean(SqlEditView.class);
				s.setStageManager(this, primaryStage);
				s.initData(0);
			}

			primaryStage.show();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T switchToNextScene(final FxmlView view, int stagenr) {
		FXMLLoader loader = null;
		T contr = null;
		try {
			Parent rootNode = null;
			if (view == FxmlView.SQLEDIT) {
				rootNode = rootSqlEditView[stagenr];
				contr = (T) fxmlController[stagenr];
			}
			if (rootNode == null) {
				loader = fxmlLoader.load(view.getFxmlPath());
				rootNode = loader.getRoot();
			}
			Optional<Stage> s = getStage(stagenr);
			Stage stage = primaryStage;
			if (s.isPresent())
				stage = s.get();
			stage.getScene().setRoot(rootNode);
			stage.show();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (loader != null)
			return loader.getController();
		else
			return contr;
	}

	public void showStage(final FxmlView view, int stagenr) {
		Optional<Stage> s = getStage(stagenr);
		if (s.isEmpty()) {
			s = Optional.of(new Stage());
			s.get().getIcons().add(new Image("sql.png"));
		}
		Stage stage = s.get();
		if (stage.isShowing()) {
			stage.requestFocus();
			return;
		}
		try {
			FXMLLoader loader = fxmlLoader.load(view.getFxmlPath());
			Parent rootNode = loader.getRoot();
			Scene scene = stage.getScene();
			if (scene == null) {
				scene = new Scene(rootNode);
				stage.setScene(scene);
			} else {
				scene.setRoot(rootNode);
			}
			Object o = loader.getController();
			if (o instanceof SqlEditView) {
				SqlEditView v = (SqlEditView) o;
				v.setStageManager(this, stage);
				// v.initData(stagenr);
				Platform.runLater(() -> {
					v.initData(stagenr);
				});
				int vnr = stagenr + 1;
				stage.setTitle("Verbindung " + vnr);
				rootSqlEditView[stagenr] = rootNode;
				fxmlController[stagenr] = v;
			}
			stage.show();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return;
	}

	public Stage createStage() {
		Stage stage = new Stage();
		stage.getIcons().add(new Image("sql.png"));
		return stage;
	}

	public FXMLLoader createFXMLLoader(final FxmlView view) {
		try {
			FXMLLoader loader = fxmlLoader.load(view.getFxmlPath());
			return loader;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void showStage(FXMLLoader loader, Stage stage, boolean wait) {
		if (stage == null)
			stage = createStage();
		Parent rootNode = loader.getRoot();
		Scene scene = stage.getScene();
		if (scene == null) {
			scene = new Scene(rootNode);
			stage.setScene(scene);
		} else {
			scene.setRoot(rootNode);
		}
		if (wait)
			stage.showAndWait();
		else
			stage.show();
		return;
	}

	public Scene getScene(int stagenr) {
		Optional<Stage> s = getStage(stagenr);
		if (s.isPresent())
			return s.get().getScene();
		return primaryStage.getScene();
	}

	public void closeRequest(SqlEditVM vm, int stagenr) {
		Optional<Stage> s = getStage(stagenr);
		if (s.isEmpty())
			s = Optional.of(primaryStage);
		EventHandler<WindowEvent> oldHandler = s.get().getOnCloseRequest();
		s.get().setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				vm.close();
				if (oldHandler != null) {
					oldHandler.handle(we);
				}
			}
		});
	}

	public void setCursor(Cursor c) {
		primaryStage.getScene().getRoot().setCursor(c);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public Optional<Stage> getStage(int stagenr) {
		Optional<Stage> s = Optional.empty();
		if (stagenr < 0 || stagenr >= Controller.MAX_SERVER)
			return s;
		if (stagenr == 0)
			s = Optional.of(primaryStage);
		else {
			if (stage[stagenr - 1] == null) {
				stage[stagenr - 1] = new Stage();
				stage[stagenr - 1].getIcons().add(new Image("sql.png"));
			}
			else if (!stage[stagenr - 1].isShowing()) {
				stage[stagenr - 1] = new Stage();
				stage[stagenr - 1].getIcons().add(new Image("sql.png"));
			}
			s = Optional.of(stage[stagenr - 1]);
		}
		return s;
	}

	public void close(int stagenr) {
		Optional<Stage> s = getStage(stagenr);
		if (s.isEmpty())
			s = Optional.of(primaryStage);
		s.get().close();
	}

}