package com.lasse;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import javafx.application.Application;

/**
 * Lasse Schöttner
 * Start der Anwendung mit Spring Boot und JavaFX
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
public class Main {

	public static void main(String[] args) {
		Application.launch(SqlEditFXApplication.class, args);
	}
}