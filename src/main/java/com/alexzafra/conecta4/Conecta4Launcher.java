package com.alexzafra.conecta4;

/**
 * Clase lanzadora para ejecutar la aplicación JavaFX desde un JAR
 * Necesaria porque javafx.application.Application no puede ser la clase principal en un JAR
 */
public class Conecta4Launcher {

    /**
     * Método main que lanza la aplicación JavaFX
     * @param
     */
    public static void main(String[] args) {
        Conecta4Main.main(args);
    }
}