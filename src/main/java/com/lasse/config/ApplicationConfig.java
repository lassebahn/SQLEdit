package com.lasse.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.lasse.control.FxmlLoader;
import com.lasse.control.StageManager;

import javafx.stage.Stage;

/**
 * Konfiguration Spring
 */
@Configuration
public class ApplicationConfig {

    private final FxmlLoader fxmlLoader;
    private final String applicationTitle;

    public ApplicationConfig(FxmlLoader fxmlLoader,
                             @Value("${application.title}") String applicationTitle) {
        this.fxmlLoader = fxmlLoader;
        this.applicationTitle = applicationTitle;
    }
    
    /**
     * StageManager erzeugen
     * @param stage
     * @return
     * @throws IOException
     */
    @Bean
    @Lazy
    StageManager stageManager(Stage stage) throws IOException {
        return new StageManager(fxmlLoader, stage, applicationTitle);
    }
}