package com.alexzafra.conecta4.controller;

import com.alexzafra.conecta4.modelos.Tablero;
import com.alexzafra.conecta4.modelos.Jugador;

import java.awt.Color;

/**
 * Controlador del juego Conecta 4.
 * Gestiona la lógica del juego y comunica el modelo con la vista.
 */
public class ControladorJuego {
    private Tablero tablero;               // Tablero del juego
    private Jugador jugador1;              // Jugador 1 (normalmente rojo)
    private Jugador jugador2;              // Jugador 2 (normalmente amarillo)
    private Jugador jugadorActual;         // Jugador que tiene el turno actual
    private boolean juegoTerminado;        // Indica si el juego ha terminado
    private boolean empate;                // Indica si el juego terminó en empate

    // Modo de juego
    private boolean modoUnJugador;
    private InteligenciaArtificial ia;

    // Variables para seguimiento del último movimiento
    private int ultimaFilaMovimiento = -1;
    private int ultimaColumnaMovimiento = -1;

    /**
     * Constructor del controlador del juego.
     * Inicializa el tablero, los jugadores y el estado del juego.
     */
    public ControladorJuego() {
        // Inicializar el tablero
        tablero = new Tablero();

        // Crear jugadores por defecto
        jugador1 = new Jugador(1, "Jugador 1", Color.RED);
        jugador2 = new Jugador(2, "Jugador 2", Color.YELLOW);

        // Establecer jugador inicial
        jugadorActual = jugador1;

        // Estado inicial del juego
        juegoTerminado = false;
        empate = false;

        // Por defecto, modo dos jugadores
        modoUnJugador = false;
    }

    /**
     * Configura el juego para modo un jugador contra la máquina
     * @param nivelDificultad Nivel de dificultad de la IA (1-4)
     */
    public void configurarModoUnJugador(int nivelDificultad) {
        modoUnJugador = true;
        ia = new InteligenciaArtificial(tablero, nivelDificultad);
        jugador2.setNombre("Máquina");
    }

    /**
     * Configura el juego para modo dos jugadores
     */
    public void configurarModoDosJugadores() {
        modoUnJugador = false;
        jugador2.setNombre("Jugador 2");
    }

    /**
     * Reinicia el juego para una nueva partida.
     * El tablero se vacía y se restablece el jugador inicial.
     */
    public void reiniciarJuego() {
        tablero.reiniciar();
        jugadorActual = jugador1;
        juegoTerminado = false;
        empate = false;
        ultimaFilaMovimiento = -1;
        ultimaColumnaMovimiento = -1;
    }

    /**
     * Realiza un movimiento en la columna seleccionada.
     * @param columna Columna donde se quiere colocar la ficha (0-6)
     * @return true si el movimiento es válido, false en caso contrario
     */
    public boolean realizarMovimiento(int columna) {
        // Verificar si el juego ha terminado o la columna es inválida
        if (juegoTerminado || columna < 0 || columna >= Tablero.COLUMNAS) {
            return false;
        }

        // Verificar si la columna está llena
        if (tablero.columnaLlena(columna)) {
            return false;
        }

        // Encontrar la fila disponible
        int fila = tablero.obtenerFilaDisponible(columna);

        // Colocar ficha del jugador actual
        tablero.colocarFicha(fila, columna, jugadorActual.getId());

        // Guardar la posición del último movimiento
        ultimaFilaMovimiento = fila;
        ultimaColumnaMovimiento = columna;

        // Comprobar si hay ganador
        if (tablero.hayGanador(fila, columna)) {
            juegoTerminado = true;
            jugadorActual.incrementarPuntuacion();
            return true;
        }

        // Comprobar si hay empate
        if (tablero.tableroLleno()) {
            juegoTerminado = true;
            empate = true;
            return true;
        }

        // Cambiar turno al otro jugador
        jugadorActual = (jugadorActual == jugador1) ? jugador2 : jugador1;

        // Si es modo un jugador y le toca a la máquina, hacer movimiento automático
        if (modoUnJugador && jugadorActual == jugador2) {
            // Nota: Ahora el movimiento de la máquina se maneja de forma diferente a través de la interfaz gráfica
            // para permitir la animación de caída, así que no llamamos a realizarMovimientoMaquina() aquí
        }

        return true;
    }

    /**
     * Realiza el movimiento de la máquina en modo un jugador
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    public boolean realizarMovimientoMaquina() {
        // Si no es turno de la máquina, no hacer nada
        if (jugadorActual != jugador2 || juegoTerminado) {
            return false;
        }

        // Determinar la columna donde la máquina colocará la ficha
        int columna = ia.realizarMovimiento(jugador1.getId(), jugador2.getId());

        // Si la columna es -1, significa que la IA ya colocó la ficha en alguna estrategia
        if (columna == -1) {
            // La IA ya colocó la ficha, verificar el estado del juego

            // Buscar la posición donde la IA colocó la ficha
            encontrarUltimoMovimientoIA();

            // Comprobar si con ese movimiento la máquina ganó
            if (ultimaFilaMovimiento >= 0 && ultimaColumnaMovimiento >= 0) {
                if (tablero.hayGanador(ultimaFilaMovimiento, ultimaColumnaMovimiento)) {
                    juegoTerminado = true;
                    jugador2.incrementarPuntuacion();
                    return true;
                }
            }

            // Comprobar si hay empate
            if (tablero.tableroLleno()) {
                juegoTerminado = true;
                empate = true;
                return true;
            }

            // Cambiar turno al jugador humano
            jugadorActual = jugador1;
            return true;
        } else {
            // Encontrar la fila disponible
            int fila = tablero.obtenerFilaDisponible(columna);

            // Guardar la posición del movimiento
            ultimaFilaMovimiento = fila;
            ultimaColumnaMovimiento = columna;

            // Colocar ficha de la máquina
            tablero.colocarFicha(fila, columna, jugador2.getId());

            // Comprobar si hay ganador
            if (tablero.hayGanador(fila, columna)) {
                juegoTerminado = true;
                jugador2.incrementarPuntuacion();
                return true;
            }

            // Comprobar si hay empate
            if (tablero.tableroLleno()) {
                juegoTerminado = true;
                empate = true;
                return true;
            }

            // Cambiar turno al jugador humano
            jugadorActual = jugador1;
            return true;
        }
    }

    /**
     * Busca en el tablero la última ficha colocada por la IA
     */
    private void encontrarUltimoMovimientoIA() {
        // Recorrer el tablero buscando la última ficha de la IA (jugador 2)
        for (int fila = 0; fila < Tablero.FILAS; fila++) {
            for (int col = 0; col < Tablero.COLUMNAS; col++) {
                if (tablero.obtenerCasilla(fila, col) == jugador2.getId()) {
                    // Verificar si esta posición no tenía ficha antes
                    ultimaFilaMovimiento = fila;
                    ultimaColumnaMovimiento = col;
                    // No retornamos de inmediato, pues queremos encontrar
                    // la ficha que esté más arriba (última colocada)
                }
            }
        }
    }

    /**
     * Devuelve si el juego está en modo un jugador o dos jugadores
     * @return true si es modo un jugador, false si es modo dos jugadores
     */
    public boolean esModoUnJugador() {
        return modoUnJugador;
    }

    /**
     * Obtiene el tablero del juego.
     * @return El tablero del juego
     */
    public Tablero getTablero() {
        return tablero;
    }

    /**
     * Obtiene el jugador 1.
     * @return El jugador 1
     */
    public Jugador getJugador1() {
        return jugador1;
    }

    /**
     * Obtiene el jugador 2.
     * @return El jugador 2
     */
    public Jugador getJugador2() {
        return jugador2;
    }

    /**
     * Obtiene el jugador actual (el que tiene el turno).
     * @return El jugador actual
     */
    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    /**
     * Verifica si el juego ha terminado.
     * @return true si el juego ha terminado, false en caso contrario
     */
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    /**
     * Verifica si el juego ha terminado en empate.
     * @return true si hay empate, false en caso contrario
     */
    public boolean isEmpate() {
        return empate;
    }

    /**
     * Obtiene la fila de la última ficha colocada
     * @return Fila de la última ficha o -1 si no hay
     */
    public int getUltimaFilaMovimiento() {
        return ultimaFilaMovimiento;
    }

    /**
     * Obtiene la columna de la última ficha colocada
     * @return Columna de la última ficha o -1 si no hay
     */
    public int getUltimaColumnaMovimiento() {
        return ultimaColumnaMovimiento;
    }

    /**
     * Obtiene la IA del juego
     * @return Inteligencia artificial
     */
    public InteligenciaArtificial getIA() {
        return ia;
    }

    /**
     * Obtiene el mensaje de estado actual del juego.
     * @return Mensaje descriptivo del estado actual
     */
    public String getMensajeEstado() {
        if (juegoTerminado) {
            if (empate) {
                return "¡Empate!";
            } else {
                return "¡" + jugadorActual.getNombre() + " ha ganado!";
            }
        } else {
            return "Turno de " + jugadorActual.getNombre();
        }
    }

    /**
     * Establece explícitamente el estado de juego terminado
     * @param juegoTerminado Nuevo estado
     */
    public void setJuegoTerminado(boolean juegoTerminado) {
        this.juegoTerminado = juegoTerminado;
    }

    /**
     * Establece explícitamente el estado de empate
     * @param empate Nuevo estado
     */
    public void setEmpate(boolean empate) {
        this.empate = empate;
    }

    /**
     * Cambia el turno al otro jugador
     */
    public void cambiarTurno() {
        jugadorActual = (jugadorActual == jugador1) ? jugador2 : jugador1;
    }
}