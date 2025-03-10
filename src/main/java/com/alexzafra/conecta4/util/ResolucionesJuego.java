package com.alexzafra.conecta4.util;

import javafx.geometry.Dimension2D;

/**
 * Clase utilitaria que proporciona las resoluciones estándar para el juego
 */
public class ResolucionesJuego {

    // Resoluciones comunes para videojuegos
    public static final Dimension2D RES_800x600 = new Dimension2D(800, 600);      // SVGA
    public static final Dimension2D RES_1024x768 = new Dimension2D(1024, 768);    // XGA
    public static final Dimension2D RES_1280x720 = new Dimension2D(1280, 720);    // HD (720p)
    public static final Dimension2D RES_1366x768 = new Dimension2D(1366, 768);    // HD Común en laptops
    public static final Dimension2D RES_1600x900 = new Dimension2D(1600, 900);    // HD+
    public static final Dimension2D RES_1920x1080 = new Dimension2D(1920, 1080);  // Full HD (1080p)
    public static final Dimension2D RES_2560x1440 = new Dimension2D(2560, 1440);  // 2K / QHD / WQHD
    public static final Dimension2D RES_3840x2160 = new Dimension2D(3840, 2160);  // 4K / UHD

    // Array con todas las resoluciones para usar en combos
    public static final Dimension2D[] RESOLUCIONES_DISPONIBLES = {
            RES_800x600,
            RES_1024x768,
            RES_1280x720,
            RES_1366x768,
            RES_1600x900,
            RES_1920x1080,
            RES_2560x1440,
            RES_3840x2160
    };

    // Array con los nombres para mostrar en las interfaces
    public static final String[] NOMBRES_RESOLUCIONES = {
            "800 × 600 (SVGA)",
            "1024 × 768 (XGA)",
            "1280 × 720 (HD)",
            "1366 × 768 (HD Laptop)",
            "1600 × 900 (HD+)",
            "1920 × 1080 (Full HD)",
            "2560 × 1440 (2K / QHD)",
            "3840 × 2160 (4K / UHD)"
    };

    /**
     * Obtiene la dimensión correspondiente a un nombre de resolución
     * @param nombre Nombre de la resolución (ej: "800 × 600")
     * @return Dimensión correspondiente o 1024x768 por defecto
     */
    public static Dimension2D obtenerResolucionPorNombre(String nombre) {
        for (int i = 0; i < NOMBRES_RESOLUCIONES.length; i++) {
            if (NOMBRES_RESOLUCIONES[i].equals(nombre)) {
                return RESOLUCIONES_DISPONIBLES[i];
            }
        }

        // Si no encuentra una coincidencia exacta, intenta extraer las dimensiones del nombre
        try {
            String dimensiones = nombre.split("\\(")[0].trim();
            String[] partes = dimensiones.split("×");
            if (partes.length == 2) {
                int ancho = Integer.parseInt(partes[0].trim());
                int alto = Integer.parseInt(partes[1].trim());
                return new Dimension2D(ancho, alto);
            }
        } catch (Exception e) {
            // Si hay error en el parsing, usar valor por defecto
        }

        return RES_1024x768; // Valor por defecto
    }

    /**
     * Obtiene el nombre para mostrar de una resolución
     * @param resolucion Dimensión a buscar
     * @return Nombre para mostrar o valor por defecto
     */
    public static String obtenerNombreResolucion(Dimension2D resolucion) {
        for (int i = 0; i < RESOLUCIONES_DISPONIBLES.length; i++) {
            Dimension2D res = RESOLUCIONES_DISPONIBLES[i];
            if (Math.abs(res.getWidth() - resolucion.getWidth()) < 1 &&
                    Math.abs(res.getHeight() - resolucion.getHeight()) < 1) {
                return NOMBRES_RESOLUCIONES[i];
            }
        }
        return String.format("%.0f × %.0f (Personalizada)",
                resolucion.getWidth(), resolucion.getHeight());
    }
}