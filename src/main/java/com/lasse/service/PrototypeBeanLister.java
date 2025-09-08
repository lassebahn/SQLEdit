package com.lasse.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Spring Beans mit "prototype" anzeigen
 */
@Component
public class PrototypeBeanLister {

    private final ApplicationContext context;

    public PrototypeBeanLister(ApplicationContext context) {
        this.context = context;
    }

    public void listPrototypeBeans() {
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        String[] allBeanNames = factory.getBeanDefinitionNames();

        Arrays.stream(allBeanNames)
                .map(name -> new Object() {
                    String nameStr = name;
                    BeanDefinition def = factory.getBeanDefinition(name);
                })
                .filter(entry -> "prototype".equals(entry.def.getScope()))
                .forEach(entry -> {
                    System.out.printf("Prototype Bean: %-30s -> %s%n",
                            entry.nameStr,
                            entry.def.getBeanClassName() != null
                                    ? entry.def.getBeanClassName()
                                    : "(Klasse zur Laufzeit bestimmt)");
                });
    }
}
