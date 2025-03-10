package com.alexzafra.conecta4.modelos;

import java.awt.Color;

/**
 * Clase que representa a un jugador del Conecta 4.
 * Guarda información como el nombre, color y puntuación.
 */
public class Jugador {
    private int id;             // Identificador del jugador (1 o 2)
    private String nombre;      // Nombre del jugador
    private Color color;        // Color de las fichas del jugador
    private int puntuacion;     // Puntuación acumulada (partidas ganadas)

    /**
     * Constructor del jugador.
     * @param id Identificador del jugador (1 o 2)
     * @param nombre Nombre del jugador
     * @param color Color de las fichas del jugador
     */
    public Jugador(int id, String nombre, Color color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.puntuacion = 0;
    }

    /**
     * Obtiene el ID del jugador.
     * @return ID del jugador (1 o 2)
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el nombre del jugador.
     * @return Nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del jugador.
     * @param nombre Nuevo nombre para el jugador
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el color de las fichas del jugador.
     * @return Color de las fichas
     */
    public Color getColor() {
        return color;
    }

    /**
     * Establece el color de las fichas del jugador.
     * @param color Nuevo color para las fichas
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Obtiene la puntuación actual del jugador.
     * @return Puntuación (número de partidas ganadas)
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    /**
     * Incrementa la puntuación del jugador en 1 punto (tras ganar una partida).
     */
    public void incrementarPuntuacion() {
        puntuacion++;
    }

    /**
     * Reinicia la puntuación del jugador a 0.
     */
    public void reiniciarPuntuacion() {
        puntuacion = 0;
    }

    /**
     * Devuelve una representación en texto del jugador.
     * @return String con el nombre y puntuación del jugador
     */
    @Override
    public String toString() {
        return nombre + " (" + puntuacion + " pts)";
    }
}