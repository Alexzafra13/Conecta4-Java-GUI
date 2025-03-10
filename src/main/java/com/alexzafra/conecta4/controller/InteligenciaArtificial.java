package com.alexzafra.conecta4.controller;

import com.alexzafra.conecta4.modelos.Tablero;

import java.util.Random;

/**
 * Clase que implementa la inteligencia artificial para el juego Conecta 4.
 * Contiene diferentes niveles de dificultad para el juego contra la máquina.
 */
public class InteligenciaArtificial {
    // Constantes para los niveles de dificultad
    public static final int NIVEL_FACIL = 1;
    public static final int NIVEL_MEDIO = 2;
    public static final int NIVEL_DIFICIL = 3;
    public static final int NIVEL_DEMENCIAL = 4;

    // Generador de números aleatorios para movimientos aleatorios
    private Random random;

    // Tablero del juego
    private Tablero tablero;

    // Nivel de dificultad actual
    private int nivelDificultad;

    // Variable para controlar si ya se ha insertado ficha en turno actual
    private boolean fichaInsertada;

    /**
     * Constructor de la IA
     * @param tablero Tablero del juego
     * @param nivel Nivel de dificultad
     */
    public InteligenciaArtificial(Tablero tablero, int nivel) {
        this.tablero = tablero;
        this.nivelDificultad = nivel;
        this.random = new Random();
    }

    /**
     * Cambia el nivel de dificultad de la IA
     * @param nivel Nuevo nivel de dificultad
     */
    public void setNivelDificultad(int nivel) {
        this.nivelDificultad = nivel;
    }

    /**
     * Obtiene el nivel de dificultad actual
     * @return Nivel de dificultad actual
     */
    public int getNivelDificultad() {
        return nivelDificultad;
    }

    /**
     * Realiza un movimiento de la IA según el nivel de dificultad
     * @param jugadorId ID del jugador (para verificar jugadas)
     * @param maquinaId ID de la máquina
     * @return Columna donde se colocó la ficha
     */
    public int realizarMovimiento(int jugadorId, int maquinaId) {
        // Reiniciar la variable de control
        fichaInsertada = false;

        // Realizar movimiento según el nivel de dificultad
        switch (nivelDificultad) {
            case NIVEL_FACIL:
                return movimientoFacil();

            case NIVEL_MEDIO:
                if (!fichaInsertada) {
                    comprobarFilas(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    comprobarColumnas(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    comprobarDiagonales(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    return movimientoFacil();
                }
                break;

            case NIVEL_DIFICIL:
                if (!fichaInsertada) {
                    preveerJugadaFila(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    comprobarFilas(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    comprobarColumnas(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    comprobarDiagonales(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    return movimientoFacil();
                }
                break;

            case NIVEL_DEMENCIAL:
                if (!fichaInsertada) {
                    priorizarVictoriaMaquina(maquinaId);
                }
                if (!fichaInsertada) {
                    preveerJugadaColumna(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    preveerJugadaFila(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    comprobarFilas(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    comprobarColumnas(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    comprobarDiagonales(jugadorId, maquinaId);
                }
                if (!fichaInsertada) {
                    return movimientoFacil();
                }
                break;
        }

        // Si ya se insertó la ficha en alguna de las funciones anteriores, retornamos -1
        // para indicar que no es necesario hacer un movimiento aleatorio
        return -1;
    }

    /**
     * Realiza un movimiento aleatorio en cualquier columna no llena
     * @return Columna donde se colocó la ficha
     */
    private int movimientoFacil() {
        int columna;
        do {
            columna = random.nextInt(Tablero.COLUMNAS);
        } while (tablero.columnaLlena(columna));

        return columna;
    }

    /**
     * Prioriza ganar el juego si es posible
     * @param maquinaId ID de la máquina
     */
    private void priorizarVictoriaMaquina(int maquinaId) {
        // Recorremos las celdas para ver las posibles jugadas
        for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
            // Verificar si la columna no está llena
            if (!tablero.columnaLlena(columna)) {
                // Encontrar la fila disponible
                int fila = tablero.obtenerFilaDisponible(columna);

                // Colocar temporalmente la ficha para después verificar si ganaría
                tablero.colocarFicha(fila, columna, maquinaId);

                // Verificar si con este movimiento ganaría
                if (tablero.hayGanador(fila, columna)) {
                    fichaInsertada = true;
                    return;
                }

                // Restaurar el tablero
                tablero.colocarFicha(fila, columna, Tablero.VACIO);
            }
        }
    }

    /**
     * Anticipa y bloquea jugadas del oponente en filas
     * @param jugadorId ID del jugador
     * @param maquinaId ID de la máquina
     */
    private void preveerJugadaFila(int jugadorId, int maquinaId) {
        // Recorrer las filas de abajo hacia arriba
        for (int fila = Tablero.FILAS - 1; fila >= 0; fila--) {
            for (int col = 0; col < Tablero.COLUMNAS - 1; col++) {
                // Verificar si hay 2 fichas consecutivas del jugador en la fila
                if (col + 1 < Tablero.COLUMNAS &&
                        tablero.obtenerCasilla(fila, col) == jugadorId &&
                        tablero.obtenerCasilla(fila, col + 1) == jugadorId) {

                    // Bloquear hacia la derecha si la celda está vacía y no está "en el aire"
                    if (col + 2 < Tablero.COLUMNAS &&
                            tablero.obtenerCasilla(fila, col + 2) == Tablero.VACIO &&
                            (fila == Tablero.FILAS - 1 || tablero.obtenerCasilla(fila + 1, col + 2) != Tablero.VACIO)) {

                        tablero.colocarFicha(fila, col + 2, maquinaId);
                        fichaInsertada = true;
                        return;
                    }

                    // Bloquear hacia la izquierda si la celda está vacía y no está "en el aire"
                    if (col - 1 >= 0 &&
                            tablero.obtenerCasilla(fila, col - 1) == Tablero.VACIO &&
                            (fila == Tablero.FILAS - 1 || tablero.obtenerCasilla(fila + 1, col - 1) != Tablero.VACIO)) {

                        tablero.colocarFicha(fila, col - 1, maquinaId);
                        fichaInsertada = true;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Anticipa y bloquea jugadas del oponente en columnas
     * @param jugadorId ID del jugador
     * @param maquinaId ID de la máquina
     */
    private void preveerJugadaColumna(int jugadorId, int maquinaId) {
        // Recorrer las columnas
        for (int col = 0; col < Tablero.COLUMNAS; col++) {
            // Revisar de abajo hacia arriba
            for (int fila = Tablero.FILAS - 1; fila >= 1; fila--) {

                // Verificar si hay 2 fichas consecutivas del jugador en la columna
                if (fila - 1 >= 0 &&
                        tablero.obtenerCasilla(fila, col) == jugadorId &&
                        tablero.obtenerCasilla(fila - 1, col) == jugadorId) {

                    // Bloquear colocando la ficha en la siguiente celda hacia abajo, si está vacía
                    if (fila - 2 >= 0 && tablero.obtenerCasilla(fila - 2, col) == Tablero.VACIO) {
                        tablero.colocarFicha(fila - 2, col, maquinaId);
                        fichaInsertada = true;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Busca y bloquea 3 fichas en fila del oponente
     * @param jugadorId ID del jugador
     * @param maquinaId ID de la máquina
     */
    private void comprobarFilas(int jugadorId, int maquinaId) {
        // Recorrer las filas de abajo hacia arriba
        for (int fila = Tablero.FILAS - 1; fila >= 0; fila--) {
            for (int col = 0; col <= Tablero.COLUMNAS - 3; col++) {

                // Verificar si hay 3 fichas consecutivas del jugador en la fila
                if (tablero.obtenerCasilla(fila, col) == jugadorId &&
                        tablero.obtenerCasilla(fila, col + 1) == jugadorId &&
                        tablero.obtenerCasilla(fila, col + 2) == jugadorId) {

                    // Bloquear hacia la derecha si la celda está vacía y no está "en el aire"
                    if (col + 3 < Tablero.COLUMNAS &&
                            tablero.obtenerCasilla(fila, col + 3) == Tablero.VACIO &&
                            (fila == Tablero.FILAS - 1 || tablero.obtenerCasilla(fila + 1, col + 3) != Tablero.VACIO)) {

                        tablero.colocarFicha(fila, col + 3, maquinaId);
                        fichaInsertada = true;
                        return;
                    }

                    // Bloquear hacia la izquierda si la celda está vacía y no está "en el aire"
                    if (col - 1 >= 0 &&
                            tablero.obtenerCasilla(fila, col - 1) == Tablero.VACIO &&
                            (fila == Tablero.FILAS - 1 || tablero.obtenerCasilla(fila + 1, col - 1) != Tablero.VACIO)) {

                        tablero.colocarFicha(fila, col - 1, maquinaId);
                        fichaInsertada = true;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Busca y bloquea 3 fichas en columna del oponente
     * @param jugadorId ID del jugador
     * @param maquinaId ID de la máquina
     */
    private void comprobarColumnas(int jugadorId, int maquinaId) {
        // Recorrer las columnas
        for (int col = 0; col < Tablero.COLUMNAS; col++) {
            for (int fila = Tablero.FILAS - 1; fila >= 2; fila--) {

                // Verificar si hay 3 fichas consecutivas del jugador en la columna
                if (tablero.obtenerCasilla(fila, col) == jugadorId &&
                        tablero.obtenerCasilla(fila - 1, col) == jugadorId &&
                        tablero.obtenerCasilla(fila - 2, col) == jugadorId) {

                    // Bloquear hacia arriba si la celda está vacía
                    if (fila - 3 >= 0 && tablero.obtenerCasilla(fila - 3, col) == Tablero.VACIO) {
                        tablero.colocarFicha(fila - 3, col, maquinaId);
                        fichaInsertada = true;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Busca y bloquea 3 fichas en diagonal del oponente
     * @param jugadorId ID del jugador
     * @param maquinaId ID de la máquina
     */
    private void comprobarDiagonales(int jugadorId, int maquinaId) {
        // Diagonales de abajo-izquierda a arriba-derecha
        for (int fila = Tablero.FILAS - 1; fila >= 2; fila--) {
            for (int col = 0; col <= Tablero.COLUMNAS - 3; col++) {

                // Verificar diagonal ascendente
                if (tablero.obtenerCasilla(fila, col) == jugadorId &&
                        tablero.obtenerCasilla(fila - 1, col + 1) == jugadorId &&
                        tablero.obtenerCasilla(fila - 2, col + 2) == jugadorId) {

                    // Bloquear hacia arriba-derecha
                    if (fila - 3 >= 0 && col + 3 < Tablero.COLUMNAS &&
                            tablero.obtenerCasilla(fila - 3, col + 3) == Tablero.VACIO) {

                        tablero.colocarFicha(fila - 3, col + 3, maquinaId);
                        fichaInsertada = true;
                        return;
                    }

                    // Bloquear hacia abajo-izquierda
                    if (fila + 1 < Tablero.FILAS && col - 1 >= 0 &&
                            tablero.obtenerCasilla(fila + 1, col - 1) == Tablero.VACIO &&
                            (fila + 1 == Tablero.FILAS - 1 || tablero.obtenerCasilla(fila + 2, col - 1) != Tablero.VACIO)) {

                        tablero.colocarFicha(fila + 1, col - 1, maquinaId);
                        fichaInsertada = true;
                        return;
                    }
                }
            }
        }

        // Diagonales de arriba-izquierda a abajo-derecha
        for (int fila = 0; fila <= Tablero.FILAS - 3; fila++) {
            for (int col = 0; col <= Tablero.COLUMNAS - 3; col++) {

                // Verificar diagonal descendente
                if (tablero.obtenerCasilla(fila, col) == jugadorId &&
                        tablero.obtenerCasilla(fila + 1, col + 1) == jugadorId &&
                        tablero.obtenerCasilla(fila + 2, col + 2) == jugadorId) {

                    // Bloquear hacia abajo-derecha
                    if (fila + 3 < Tablero.FILAS && col + 3 < Tablero.COLUMNAS &&
                            tablero.obtenerCasilla(fila + 3, col + 3) == Tablero.VACIO &&
                            (fila + 3 == Tablero.FILAS - 1 || tablero.obtenerCasilla(fila + 4, col + 3) != Tablero.VACIO)) {

                        tablero.colocarFicha(fila + 3, col + 3, maquinaId);
                        fichaInsertada = true;
                        return;
                    }

                    // Bloquear hacia arriba-izquierda
                    if (fila - 1 >= 0 && col - 1 >= 0 &&
                            tablero.obtenerCasilla(fila - 1, col - 1) == Tablero.VACIO &&
                            (fila == Tablero.FILAS - 1 || tablero.obtenerCasilla(fila, col - 1) != Tablero.VACIO)) {

                        tablero.colocarFicha(fila - 1, col - 1, maquinaId);
                        fichaInsertada = true;
                        return;
                    }
                }
            }
        }
    }
}