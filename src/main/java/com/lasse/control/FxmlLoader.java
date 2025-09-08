package com.lasse.control;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Hilfsklasse zum Laden von FXML-Dateien mit Spring-Integration
 * @author Lasse Schöttner
 *
 */
@Component
public class FxmlLoader {

    private final ApplicationContext context;
    
    public FxmlLoader(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Controller wird über Spring-Context gesetzt
     * @param fxmlPath
     * @return
     * @throws IOException
     */
    public FXMLLoader load(String fxmlPath) throws IOException {
    	//System.out.println(Arrays.toString(context.getBeanDefinitionNames()));
    	FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        loader.setLocation(getClass().getResource(fxmlPath));
        Parent p = loader.load();
        return loader;
    }
    
    /**
     * Controller wird manuell gesetzt
     * @param fxmlPath
     * @param controllerFxml
     * @return
     * @throws IOException
     */
    public Parent load(String fxmlPath, Object controllerFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL fxmlUrl = getClass().getResource(fxmlPath);
        loader.setLocation(getClass().getResource(fxmlPath));
        loader.setController(controllerFxml);
        return loader.load();
    }

}