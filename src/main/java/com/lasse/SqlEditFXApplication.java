package com.lasse;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.lasse.config.FxmlView;
import com.lasse.control.StageManager;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Hauptklasse der JavaFX-Anwendung
 * @author Lasse Schöttner
 *
 */
public class SqlEditFXApplication extends Application {
	private static Stage stage;
	private ConfigurableApplicationContext applicationContext;
	private StageManager stageManager;
	
	@Override
	public void init() {
		applicationContext = new SpringApplicationBuilder(Main.class).run();
	}

	@Override
	public void stop() {
		applicationContext.close();
		stage.close();
	}

	@Override
	public void start(Stage primaryStage) {
        stage = primaryStage;
        stageManager = applicationContext.getBean(StageManager.class, primaryStage);
        showSqlEditView();
    }
	
	private void showSqlEditView() {
	    stageManager.showStage(FxmlView.SQLEDIT, 0);
	}
}