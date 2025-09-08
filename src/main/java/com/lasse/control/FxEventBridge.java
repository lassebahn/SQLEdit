package com.lasse.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.application.Platform;

/**
 * Brücke zwischen Spring Events und JavaFX Thread
 * @author Lasse Schöttner
 *
 */
@Component
public class FxEventBridge {

    private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    public <T> void registerListener(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public <T> void unregisterListener(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<?>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }

    @EventListener
    public void onAnyEvent(ApplicationEvent event) {
        // Alles in den JavaFX-Thread verschieben
        Platform.runLater(() -> {
            List<Consumer<?>> list = listeners.get(event.getClass());
            if (list != null) {
                for (Consumer<?> rawListener : new ArrayList<>(list)) {
                    @SuppressWarnings("unchecked")
                    Consumer<ApplicationEvent> typed = (Consumer<ApplicationEvent>) rawListener;
                    typed.accept(event);
                }
            }
        });
    }
}