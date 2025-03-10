package com.alexzafra.conecta4.util;

import javafx.geometry.Dimension2D;

/**
 * Clase singleton para mantener la configuración global de la ventana
 */
public class ConfiguracionVentana {
    // Instancia única
    private static ConfiguracionVentana instancia;

    // Configuraciones por defecto
    private Dimension2D resolucionActual;
    private boolean pantallaCompleta;

    // Constructor privado
    private ConfiguracionVentana() {
        // Resolución por defecto
        resolucionActual = ResolucionesJuego.RES_1024x768;
        pantallaCompleta = false;
    }

    // Método para obtener la instancia única
    public static synchronized ConfiguracionVentana getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionVentana();
        }
        return instancia;
    }

    /**
     * Establece la resolución actual
     * @param resolucion Nueva resolución
     */
    public void setResolucion(Dimension2D resolucion) {
        this.resolucionActual = resolucion;
    }

    /**
     * Obtiene la resolución actual
     * @return Resolución actual
     */
    public Dimension2D getResolucion() {
        return resolucionActual;
    }

    /**
     * Establece el modo de pantalla completa
     * @param pantallaCompleta Nuevo estado de pantalla completa
     */
    public void setPantallaCompleta(boolean pantallaCompleta) {
        this.pantallaCompleta = pantallaCompleta;
    }

    /**
     * Verifica si está en modo pantalla completa
     * @return true si está en pantalla completa, false en caso contrario
     */
    public boolean isPantallaCompleta() {
        return pantallaCompleta;
    }
}