package com.alexzafra.conecta4.modelos;

/**
 * Clase que representa el tablero del juego Conecta 4.
 * Mantiene el estado del tablero y proporciona métodos para manipularlo.
 */
public class Tablero {
    // Constantes que definen el tamaño del tablero
    public static final int FILAS = 6;
    public static final int COLUMNAS = 7;

    // Constantes para representar el estado de las celdas
    public static final int VACIO = 0;
    public static final int JUGADOR_1 = 1;
    public static final int JUGADOR_2 = 2;

    // Matriz que representa el tablero
    private int[][] matriz;

    // Almacena las posiciones de las fichas ganadoras (para efectos visuales)
    private int[][] posicionesGanadoras;

    /**
     * Constructor del tablero. Inicializa un tablero vacío.
     */
    public Tablero() {
        matriz = new int[FILAS][COLUMNAS];
        reiniciar();
    }

    /**
     * Reinicia el tablero para una nueva partida. Todas las celdas se vacían.
     */
    public void reiniciar() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                matriz[fila][columna] = VACIO;
            }
        }
        posicionesGanadoras = null;
    }

    /**
     * Verifica si una columna está llena (no se pueden colocar más fichas).
     * @param columna Índice de la columna a verificar (0-6)
     * @return true si la columna está llena, false en caso contrario
     */
    public boolean columnaLlena(int columna) {
        // La columna está llena si la primera fila (la de arriba) no está vacía
        return matriz[0][columna] != VACIO;
    }

    /**
     * Encuentra la primera fila disponible en una columna (de abajo hacia arriba)
     * donde se puede colocar una ficha.
     * @param columna Índice de la columna (0-6)
     * @return Índice de la fila disponible, o -1 si la columna está llena
     */
    public int obtenerFilaDisponible(int columna) {
        for (int fila = FILAS - 1; fila >= 0; fila--) {
            if (matriz[fila][columna] == VACIO) {
                return fila;
            }
        }
        return -1; // Columna llena
    }

    /**
     * Coloca una ficha en el tablero en la posición especificada.
     * @param fila Fila donde colocar la ficha (0-5)
     * @param columna Columna donde colocar la ficha (0-6)
     * @param jugador Código del jugador (1 o 2)
     */
    public void colocarFicha(int fila, int columna, int jugador) {
        matriz[fila][columna] = jugador;
    }

    /**
     * Obtiene el valor de una celda del tablero.
     * @param fila Fila de la celda (0-5)
     * @param columna Columna de la celda (0-6)
     * @return Valor de la celda (VACIO, JUGADOR_1 o JUGADOR_2)
     */
    public int obtenerCasilla(int fila, int columna) {
        return matriz[fila][columna];
    }

    /**
     * Verifica si el tablero está completamente lleno.
     * @return true si el tablero está lleno (empate), false en caso contrario
     */
    public boolean tableroLleno() {
        for (int col = 0; col < COLUMNAS; col++) {
            if (!columnaLlena(col)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica si hay un ganador después de colocar una ficha.
     * @param fila Fila de la última ficha colocada
     * @param columna Columna de la última ficha colocada
     * @return true si hay un ganador, false en caso contrario
     */
    public boolean hayGanador(int fila, int columna) {
        int jugador = matriz[fila][columna];

        // Verificar horizontales
        if (verificarHorizontal(fila, jugador)) return true;

        // Verificar verticales
        if (verificarVertical(columna, jugador)) return true;

        // Verificar diagonales
        if (verificarDiagonalAscendente(fila, columna, jugador)) return true;
        if (verificarDiagonalDescendente(fila, columna, jugador)) return true;

        return false;
    }

    /**
     * Verifica líneas horizontales para buscar 4 en línea.
     * @param fila Fila donde buscar
     * @param jugador Jugador a comprobar
     * @return true si hay 4 en línea, false en caso contrario
     */
    private boolean verificarHorizontal(int fila, int jugador) {
        int contador = 0;
        int columnaInicial = 0;

        for (int col = 0; col < COLUMNAS; col++) {
            if (matriz[fila][col] == jugador) {
                if (contador == 0) columnaInicial = col;
                contador++;
                if (contador >= 4) {
                    // Guardar posiciones ganadoras
                    posicionesGanadoras = new int[4][2];
                    for (int i = 0; i < 4; i++) {
                        posicionesGanadoras[i][0] = fila;
                        posicionesGanadoras[i][1] = columnaInicial + i;
                    }
                    return true;
                }
            } else {
                contador = 0;
                columnaInicial = col + 1;
            }
        }

        return false;
    }

    /**
     * Verifica líneas verticales para buscar 4 en línea.
     * @param columna Columna donde buscar
     * @param jugador Jugador a comprobar
     * @return true si hay 4 en línea, false en caso contrario
     */
    private boolean verificarVertical(int columna, int jugador) {
        int contador = 0;
        int filaInicial = 0;

        for (int fila = 0; fila < FILAS; fila++) {
            if (matriz[fila][columna] == jugador) {
                if (contador == 0) filaInicial = fila;
                contador++;
                if (contador >= 4) {
                    // Guardar posiciones ganadoras
                    posicionesGanadoras = new int[4][2];
                    for (int i = 0; i < 4; i++) {
                        posicionesGanadoras[i][0] = filaInicial + i;
                        posicionesGanadoras[i][1] = columna;
                    }
                    return true;
                }
            } else {
                contador = 0;
                filaInicial = fila + 1;
            }
        }

        return false;
    }

    /**
     * Verifica diagonales ascendentes (↗) para buscar 4 en línea.
     * @param fila Fila de la última ficha colocada
     * @param columna Columna de la última ficha colocada
     * @param jugador Jugador a comprobar
     * @return true si hay 4 en línea, false en caso contrario
     */
    private boolean verificarDiagonalAscendente(int fila, int columna, int jugador) {
        // Encontrar la "esquina" inferior izquierda de la diagonal
        int filaInicial = fila;
        int columnaInicial = columna;

        while (filaInicial < FILAS - 1 && columnaInicial > 0) {
            filaInicial++;
            columnaInicial--;
        }

        // Recorrer la diagonal desde la esquina
        int contador = 0;
        int f = filaInicial;
        int c = columnaInicial;
        int filaGanadora = 0;
        int columnaGanadora = 0;

        while (f >= 0 && c < COLUMNAS) {
            if (matriz[f][c] == jugador) {
                if (contador == 0) {
                    filaGanadora = f;
                    columnaGanadora = c;
                }
                contador++;
                if (contador >= 4) {
                    // Guardar posiciones ganadoras
                    posicionesGanadoras = new int[4][2];
                    for (int i = 0; i < 4; i++) {
                        posicionesGanadoras[i][0] = filaGanadora - i;
                        posicionesGanadoras[i][1] = columnaGanadora + i;
                    }
                    return true;
                }
            } else {
                contador = 0;
                filaGanadora = f - 1;
                columnaGanadora = c + 1;
            }
            f--;
            c++;
        }

        return false;
    }

    /**
     * Verifica diagonales descendentes (↘) para buscar 4 en línea.
     * @param fila Fila de la última ficha colocada
     * @param columna Columna de la última ficha colocada
     * @param jugador Jugador a comprobar
     * @return true si hay 4 en línea, false en caso contrario
     */
    private boolean verificarDiagonalDescendente(int fila, int columna, int jugador) {
        // Encontrar la "esquina" superior izquierda de la diagonal
        int filaInicial = fila;
        int columnaInicial = columna;

        while (filaInicial > 0 && columnaInicial > 0) {
            filaInicial--;
            columnaInicial--;
        }

        // Recorrer la diagonal desde la esquina
        int contador = 0;
        int f = filaInicial;
        int c = columnaInicial;
        int filaGanadora = 0;
        int columnaGanadora = 0;

        while (f < FILAS && c < COLUMNAS) {
            if (matriz[f][c] == jugador) {
                if (contador == 0) {
                    filaGanadora = f;
                    columnaGanadora = c;
                }
                contador++;
                if (contador >= 4) {
                    // Guardar posiciones ganadoras
                    posicionesGanadoras = new int[4][2];
                    for (int i = 0; i < 4; i++) {
                        posicionesGanadoras[i][0] = filaGanadora + i;
                        posicionesGanadoras[i][1] = columnaGanadora + i;
                    }
                    return true;
                }
            } else {
                contador = 0;
                filaGanadora = f + 1;
                columnaGanadora = c + 1;
            }
            f++;
            c++;
        }

        return false;
    }

    /**
     * Obtiene las posiciones de las fichas ganadoras.
     * @return Array de posiciones [fila, columna] de las fichas ganadoras
     */
    public int[][] obtenerPosicionesGanadoras() {
        return posicionesGanadoras;
    }
}