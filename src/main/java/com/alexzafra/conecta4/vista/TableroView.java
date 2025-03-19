package com.alexzafra.conecta4.vista;

import com.alexzafra.conecta4.controller.ControladorJuego;
import com.alexzafra.conecta4.modelos.Tablero;
import com.alexzafra.conecta4.util.SistemaAudio;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Panel que dibuja el tablero del juego Conecta 4 en JavaFX.
 * Gestiona la interacción del usuario con el tablero y muestra las animaciones.
 */
public class TableroView extends Pane {
    // Constantes para el tamaño y apariencia del tablero
    private double tamanoCelda = 80; // Configurable para permitir escalado
    private double tamanoFicha = 65; // Configurable para permitir escalado

    // Controlador del juego
    private ControladorJuego controlador;

    // Referencia a la ventana principal para callbacks
    private VentanaPrincipal ventanaPrincipal;

    // Canvas para dibujar
    private Canvas canvas;

    // Variables para la animación de caída de fichas
    private Timeline animacionCaida;
    private int filaAnimacion;
    private int columnaAnimacion;
    private double yAnimacion;
    private boolean animando;
    private boolean esMovimientoIA;

    // Variables para efecto de velocidad de caída
    private double velocidadActual;
    private double aceleracion = 0.8; // Ajusta este valor para cambiar la aceleración

    // Variables para almacenar la última jugada de la IA
    private int ultimaFilaIA = -1;
    private int ultimaColumnaIA = -1;
    private boolean mostrarUltimaJugadaIA = false;
    private Timeline animacionUltimaJugadaIA;
    private double pulsoBrillo = 0.0;

    // Variables para efecto de parpadeo de fichas ganadoras
    private Timeline animacionParpadeo;
    private boolean mostrarFichasGanadoras;

    // Variables para seguimiento del ratón
    private int columnaActual;

    /**
     * Constructor del panel del tablero.
     * @param controlador Controlador del juego
     * @param ventanaPrincipal Referencia a la ventana principal
     */
    public TableroView(ControladorJuego controlador, VentanaPrincipal ventanaPrincipal) {
        this.controlador = controlador;
        this.ventanaPrincipal = ventanaPrincipal;

        // Inicializar variables de animación
        animando = false;
        mostrarFichasGanadoras = true;
        esMovimientoIA = false;

        // Crear canvas
        canvas = new Canvas(Tablero.COLUMNAS * tamanoCelda, Tablero.FILAS * tamanoCelda);
        getChildren().add(canvas);

        // Establecer estilo del panel
        setStyle("-fx-background-color: #14328c;"); // Azul oscuro
        getStyleClass().add("tablero");

        // Configurar eventos del ratón
        configurarEventosRaton();

        // Dibujar tablero inicial
        dibujarTablero();

        // Configurar animación para resaltar última jugada de la IA
        configurarAnimacionUltimaJugadaIA();
    }

    /**
     * Configura la animación para resaltar última jugada de la IA
     */
    private void configurarAnimacionUltimaJugadaIA() {
        // Timeline para ocultar el resaltado después de un tiempo
        Timeline ocultarResaltado = new Timeline(
                new KeyFrame(Duration.seconds(2.5), event -> {
                    mostrarUltimaJugadaIA = false;
                    dibujarTablero();
                })
        );
        ocultarResaltado.setCycleCount(1);

        // Timeline para animar el efecto pulsante
        Timeline animacionPulso = new Timeline(
                new KeyFrame(Duration.millis(50), event -> {
                    // Incrementar el pulso para crear un efecto pulsante
                    pulsoBrillo += 0.15;
                    if (pulsoBrillo > Math.PI * 2) {
                        pulsoBrillo = 0;
                    }
                    // Redibujar para actualizar el efecto visual si está visible
                    if (mostrarUltimaJugadaIA) {
                        dibujarTablero();
                    }
                })
        );
        animacionPulso.setCycleCount(50); // 50 ciclos de 50ms = 2.5 segundos

        // Guardar referencias a ambas animaciones
        animacionUltimaJugadaIA = new Timeline();
        animacionUltimaJugadaIA.getKeyFrames().add(
                new KeyFrame(Duration.millis(1), event -> {
                    // Iniciar ambas animaciones
                    ocultarResaltado.play();
                    animacionPulso.play();
                })
        );
        animacionUltimaJugadaIA.setCycleCount(1);
    }

    /**
     * Establece la posición de la última jugada de la IA para resaltarla
     * @param fila Fila de la jugada
     * @param columna Columna de la jugada
     */
    public void marcarUltimaJugadaIA(int fila, int columna) {
        ultimaFilaIA = fila;
        ultimaColumnaIA = columna;
        mostrarUltimaJugadaIA = true;

        // Detener animación previa si existe
        if (animacionUltimaJugadaIA != null) {
            animacionUltimaJugadaIA.stop();
        }

        // Iniciar nueva animación
        animacionUltimaJugadaIA.play();

        // Reiniciar la fase del pulso
        pulsoBrillo = 0.0;

        // Actualizar tablero para mostrar el resaltado
        dibujarTablero();
    }

    /**
     * Ajusta el tamaño de las celdas y fichas según la resolución
     * @param anchoVentana Ancho disponible
     * @param altoVentana Alto disponible
     */
    public void ajustarTamanos(double anchoVentana, double altoVentana) {
        // Revisar que los valores sean válidos
        if (anchoVentana <= 0 || altoVentana <= 0) {
            return;
        }

        try {
            // Márgenes ajustables
            double margenHorizontal = Math.min(40, anchoVentana * 0.05);
            double margenVertical = Math.min(40, altoVentana * 0.05);

            // Ancho y alto disponibles después de considerar márgenes
            double anchoDisponible = anchoVentana - margenHorizontal * 2;
            double altoDisponible = altoVentana - margenVertical * 2;

            // Calcular tamaño de celda basado en el espacio disponible
            double tamanoOptimoCeldaAncho = anchoDisponible / Tablero.COLUMNAS;
            double tamanoOptimoCeldaAlto = altoDisponible / Tablero.FILAS;

            // Usar el menor para mantener las celdas cuadradas, con un límite máximo
            tamanoCelda = Math.min(Math.min(tamanoOptimoCeldaAncho, tamanoOptimoCeldaAlto), 200);

            // Ajustar tamaño de ficha proporcionalmente (80% del tamaño de celda)
            tamanoFicha = tamanoCelda * 0.8;

            // Actualizar tamaño del canvas
            double anchoCanvas = Tablero.COLUMNAS * tamanoCelda;
            double altoCanvas = Tablero.FILAS * tamanoCelda;

            canvas.setWidth(anchoCanvas);
            canvas.setHeight(altoCanvas);

            // Centrar el canvas horizontalmente
            double xPos = (anchoVentana - anchoCanvas) / 2;
            canvas.setLayoutX(xPos > 0 ? xPos : 0);
            canvas.setLayoutY(0);

            // Redibujar el tablero con los nuevos tamaños
            dibujarTablero();

            System.out.println("Tablero redimensionado: " +
                    anchoCanvas + "x" + altoCanvas +
                    " (tamaño celda: " + tamanoCelda +
                    ", tamaño ficha: " + tamanoFicha + ")");
        } catch (Exception e) {
            System.err.println("Error al ajustar tamaños del tablero: " + e.getMessage());
        }
    }

    /**
     * Configura los event listeners para el ratón.
     */
    private void configurarEventosRaton() {
        // MouseClicked - cuando se hace clic en el tablero
        canvas.setOnMouseClicked(this::manejarClicRaton);

        // MouseMoved - cuando se mueve el ratón sobre el tablero
        canvas.setOnMouseMoved(e -> {
            columnaActual = (int)(e.getX() / tamanoCelda);
            dibujarTablero(); // Redibujar para mostrar el efecto hover
        });

        // MouseExited - cuando el ratón sale del tablero
        canvas.setOnMouseExited(e -> {
            columnaActual = -1;
            dibujarTablero();
        });
    }

    /**
     * Maneja el evento de clic del ratón en el tablero
     */
    private void manejarClicRaton(MouseEvent e) {
        // Ignorar clics mientras se está animando o el juego ha terminado
        if (animando || controlador.isJuegoTerminado()) {
            return;
        }

        // Si es modo un jugador y es turno de la máquina, ignorar clics
        if (controlador.esModoUnJugador() &&
                controlador.getJugadorActual() == controlador.getJugador2()) {
            return;
        }

        // Calcular la columna donde se hizo clic
        int columna = (int)(e.getX() / tamanoCelda);

        // Verificar que la columna es válida
        if (columna < 0 || columna >= Tablero.COLUMNAS) {
            return;
        }

        // Verificar si la columna está llena
        if (controlador.getTablero().columnaLlena(columna)) {
            return;
        }

        // Encontrar la fila disponible
        int filaDisponible = controlador.getTablero().obtenerFilaDisponible(columna);
        if (filaDisponible != -1) {
            // Iniciar animación de caída de ficha
            esMovimientoIA = false;
            iniciarAnimacionCaida(columna, filaDisponible);

            // Reproducir sonido de ficha colocada
            try {
                SistemaAudio.getInstancia().reproducirEfecto("ficha_colocada");
            } catch (Exception ex) {
                System.err.println("Error al reproducir sonido: " + ex.getMessage());
            }
        }
    }

    /**
     * Inicia la animación de caída de una ficha en la columna y fila especificadas
     * @param columna Columna donde caerá la ficha
     * @param fila Fila donde terminará la ficha
     */
    private void iniciarAnimacionCaida(int columna, int fila) {
        // Guardar posición final
        columnaAnimacion = columna;
        filaAnimacion = fila;

        // Posición inicial (arriba del tablero)
        yAnimacion = 0;

        // Para una caída más realista, añadimos una velocidad inicial y aceleración
        velocidadActual = 3.0; // Velocidad inicial

        // Marcar que está animando
        animando = true;

        // Si existe una animación anterior, detenerla
        if (animacionCaida != null) {
            animacionCaida.stop();
        }

        // Calcular posición final en píxeles
        double destinoY = filaAnimacion * tamanoCelda + tamanoCelda / 2;

        // Crear y arrancar la animación
        animacionCaida = new Timeline(
                new KeyFrame(Duration.millis(16), event -> {
                    // Actualizar velocidad con aceleración progresiva (simula gravedad)
                    velocidadActual += aceleracion;

                    // Actualizar posición Y
                    yAnimacion += velocidadActual;

                    // Comprobar si la animación ha terminado
                    if (yAnimacion >= destinoY) {
                        yAnimacion = destinoY;

                        // Detener la animación
                        animacionCaida.stop();
                        animando = false;

                        // Ahora que la animación ha terminado, realizar el movimiento en el modelo
                        realizarMovimientoReal();
                    }

                    // Repintar el tablero
                    dibujarTablero();
                })
        );

        animacionCaida.setCycleCount(Timeline.INDEFINITE);
        animacionCaida.play();
    }

    /**
     * Realiza el movimiento real en el modelo después de que termine la animación visual
     */
    private void realizarMovimientoReal() {
        // Realizar el movimiento en el modelo según quien lo realizó
        boolean movimientoExitoso;
        if (esMovimientoIA) {
            // Para la IA, colocamos directamente la ficha
            int fila = controlador.getTablero().obtenerFilaDisponible(columnaAnimacion);
            controlador.getTablero().colocarFicha(fila, columnaAnimacion, controlador.getJugador2().getId());

            // Actualizar variables para controlar el estado del juego
            ultimaFilaIA = fila;
            ultimaColumnaIA = columnaAnimacion;
            mostrarUltimaJugadaIA = true;

            // Iniciar temporizador para desactivar el resaltado
            if (animacionUltimaJugadaIA != null) {
                animacionUltimaJugadaIA.stop();
            }
            animacionUltimaJugadaIA.play();

            // Verificar si la IA ganó
            if (controlador.getTablero().hayGanador(fila, columnaAnimacion)) {
                controlador.setJuegoTerminado(true);
                controlador.getJugador2().incrementarPuntuacion();
                iniciarEfectoParpadeo();
                try {
                    SistemaAudio.getInstancia().reproducirEfecto("victoria");
                } catch (Exception e) {
                    System.err.println("Error al reproducir sonido de victoria: " + e.getMessage());
                }
            } else if (controlador.getTablero().tableroLleno()) {
                // Verificar empate
                controlador.setJuegoTerminado(true);
                controlador.setEmpate(true);
                try {
                    SistemaAudio.getInstancia().reproducirEfecto("empate");
                } catch (Exception e) {
                    System.err.println("Error al reproducir sonido de empate: " + e.getMessage());
                }
            } else {
                // Cambiar turno al jugador humano
                controlador.cambiarTurno();
            }

            movimientoExitoso = true;
        } else {
            // Para el jugador humano, usamos el método normal
            movimientoExitoso = controlador.realizarMovimiento(columnaAnimacion);

            // Si hay ganador, iniciar efecto de parpadeo
            if (movimientoExitoso && controlador.isJuegoTerminado()) {
                if (!controlador.isEmpate()) {
                    iniciarEfectoParpadeo();
                    try {
                        SistemaAudio.getInstancia().reproducirEfecto("victoria");
                    } catch (Exception e) {
                        System.err.println("Error al reproducir sonido de victoria: " + e.getMessage());
                    }
                } else {
                    try {
                        SistemaAudio.getInstancia().reproducirEfecto("empate");
                    } catch (Exception e) {
                        System.err.println("Error al reproducir sonido de empate: " + e.getMessage());
                    }
                }
            }
        }

        // Actualizar el estado del juego en la ventana principal
        if (movimientoExitoso) {
            ventanaPrincipal.actualizarEstadoJuego();
        }
    }

    /**
     * Inicia la animación de caída para la ficha de la IA
     * @param columna Columna donde se colocará la ficha
     */
    public void iniciarAnimacionCaidaIA(int columna) {
        // Verificar si la columna es válida y no está llena
        if (columna < 0 || columna >= Tablero.COLUMNAS || controlador.getTablero().columnaLlena(columna)) {
            return;
        }

        // Encontrar la fila disponible
        int filaDisponible = controlador.getTablero().obtenerFilaDisponible(columna);
        if (filaDisponible != -1) {
            // Marcar que este es un movimiento de la IA
            esMovimientoIA = true;

            // Iniciar animación de caída
            iniciarAnimacionCaida(columna, filaDisponible);

            // Reproducir sonido de ficha colocada
            try {
                SistemaAudio.getInstancia().reproducirEfecto("ficha_colocada");
            } catch (Exception ex) {
                System.err.println("Error al reproducir sonido: " + ex.getMessage());
            }
        }
    }

    /**
     * Inicia el efecto de parpadeo para las fichas ganadoras.
     */
    private void iniciarEfectoParpadeo() {
        mostrarFichasGanadoras = true;

        // Si existe una animación anterior, detenerla
        if (animacionParpadeo != null) {
            animacionParpadeo.stop();
        }

        // Crear y arrancar la animación de parpadeo
        animacionParpadeo = new Timeline(
                new KeyFrame(Duration.millis(400), event -> {
                    mostrarFichasGanadoras = !mostrarFichasGanadoras;
                    dibujarTablero();
                })
        );

        animacionParpadeo.setCycleCount(Timeline.INDEFINITE);
        animacionParpadeo.play();
    }

    /**
     * Detiene todos los efectos visuales (animaciones y parpadeos).
     */
    public void detenerEfectos() {
        if (animacionCaida != null) {
            animacionCaida.stop();
        }

        if (animacionParpadeo != null) {
            animacionParpadeo.stop();
        }

        if (animacionUltimaJugadaIA != null) {
            animacionUltimaJugadaIA.stop();
        }

        animando = false;
        mostrarUltimaJugadaIA = false;
    }

    /**
     * Dibuja el tablero y las fichas.
     */
    public void dibujarTablero() {
        try {
            // Obtener el contexto gráfico
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Limpiar el canvas
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Dibujar fondo del tablero
            gc.setFill(Color.web("#14328c")); // Azul oscuro
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Dibujar marco del tablero
            gc.setStroke(Color.web("#0a288c"));
            gc.setLineWidth(4);
            gc.strokeRoundRect(2, 2, canvas.getWidth() - 4, canvas.getHeight() - 4, 15, 15);

            // Obtener referencias del modelo
            Tablero tablero = controlador.getTablero();

            // Agregar efecto de sombra para las fichas
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
            dropShadow.setColor(Color.color(0, 0, 0, 0.3));

            // Agregar efecto de iluminación para dar volumen a las fichas
            Lighting lighting = new Lighting();
            Light.Distant light = new Light.Distant();
            light.setAzimuth(-135.0);
            light.setElevation(30.0);
            lighting.setLight(light);
            lighting.setSurfaceScale(1.5);

            // Dibujar celdas y fichas ya colocadas
            for (int fila = 0; fila < Tablero.FILAS; fila++) {
                for (int col = 0; col < Tablero.COLUMNAS; col++) {
                    double x = col * tamanoCelda + tamanoCelda / 2;
                    double y = fila * tamanoCelda + tamanoCelda / 2;

                    // Dibujar hueco (fondo)
                    gc.setFill(Color.web("#0a2882"));
                    gc.fillRoundRect(x - tamanoFicha/2 - 5, y - tamanoFicha/2 - 5,
                            tamanoFicha + 10, tamanoFicha + 10, 10, 10);

                    // Dibujar hueco (circulo blanco vacío)
                    gc.setFill(Color.WHITE);
                    gc.fillOval(x - tamanoFicha/2, y - tamanoFicha/2, tamanoFicha, tamanoFicha);

                    // Obtener valor de la celda
                    int valorCelda = tablero.obtenerCasilla(fila, col);

                    // Verificar si es una ficha ganadora para efecto de parpadeo
                    boolean esPosicionGanadora = false;
                    if (controlador.isJuegoTerminado() && !controlador.isEmpate()) {
                        int[][] posicionesGanadoras = tablero.obtenerPosicionesGanadoras();
                        if (posicionesGanadoras != null) {
                            for (int[] pos : posicionesGanadoras) {
                                if (pos[0] == fila && pos[1] == col) {
                                    esPosicionGanadora = true;
                                    break;
                                }
                            }
                        }
                    }

                    // Si es una posición ganadora y está en modo "ocultar", no dibujar la ficha
                    if (esPosicionGanadora && !mostrarFichasGanadoras) {
                        continue;
                    }

                    // Aplicar efecto de sombra
                    gc.setEffect(dropShadow);

                    // Verificar si es la última jugada de la IA para resaltarla
                    boolean esUltimaJugadaIA = mostrarUltimaJugadaIA && fila == ultimaFilaIA && col == ultimaColumnaIA;

                    // Dibujar ficha si existe en esta celda
                    if (valorCelda == Tablero.JUGADOR_1) {
                        gc.setFill(convertirAwtColorAJavaFX(controlador.getJugador1().getColor()));
                        gc.fillOval(x - tamanoFicha/2, y - tamanoFicha/2, tamanoFicha, tamanoFicha);

                        // Agregar brillo a la ficha
                        gc.setFill(Color.color(1, 1, 1, 0.3));
                        gc.fillOval(x - tamanoFicha/4, y - tamanoFicha/3, tamanoFicha/4, tamanoFicha/4);
                    } else if (valorCelda == Tablero.JUGADOR_2) {
                        Color colorFicha = convertirAwtColorAJavaFX(controlador.getJugador2().getColor());
                        gc.setFill(colorFicha);
                        gc.fillOval(x - tamanoFicha/2, y - tamanoFicha/2, tamanoFicha, tamanoFicha);

                        // Agregar brillo a la ficha
                        gc.setFill(Color.color(1, 1, 1, 0.3));
                        gc.fillOval(x - tamanoFicha/4, y - tamanoFicha/3, tamanoFicha/4, tamanoFicha/4);

                        // Si es la última jugada de la IA, añadir un resaltado adicional
                        if (esUltimaJugadaIA) {
                            // Calcular factor de pulso (entre 0.4 y 1.0)
                            double factorPulso = 0.4 + 0.6 * Math.abs(Math.sin(pulsoBrillo));

                            // Dibujar un círculo exterior de destello
                            gc.setGlobalAlpha(0.7 * factorPulso);
                            gc.setFill(Color.WHITE);
                            double tamanoExterior = tamanoFicha * (1.2 + 0.2 * factorPulso);
                            gc.fillOval(x - tamanoExterior/2, y - tamanoExterior/2, tamanoExterior, tamanoExterior);

                            // Dibujar un borde de color brillante
                            gc.setGlobalAlpha(1.0);
                            gc.setStroke(Color.YELLOW);
                            gc.setLineWidth(3 + factorPulso * 2);
                            gc.strokeOval(x - tamanoFicha/2 - 3, y - tamanoFicha/2 - 3, tamanoFicha + 6, tamanoFicha + 6);

                            // Añadir un efecto de brillo interno
                            gc.setGlobalAlpha(0.3 + 0.3 * factorPulso);
                            gc.setFill(Color.WHITE);
                            double tamanoInterior = tamanoFicha * 0.6 * factorPulso;
                            gc.fillOval(x - tamanoInterior/2, y - tamanoInterior/2, tamanoInterior, tamanoInterior);

                            // Restaurar opacidad normal
                            gc.setGlobalAlpha(1.0);
                        }
                    }

                    // Quitar efecto de sombra
                    gc.setEffect(null);
                }
            }

            // Dibujar ficha en animación de caída si está activa
            if (animando) {
                double x = columnaAnimacion * tamanoCelda + tamanoCelda / 2;

                // Obtener el jugador que está colocando la ficha
                Color colorFicha;
                if (esMovimientoIA) {
                    colorFicha = convertirAwtColorAJavaFX(controlador.getJugador2().getColor());
                } else {
                    colorFicha = convertirAwtColorAJavaFX(controlador.getJugadorActual().getColor());
                }

                // Aplicar efecto de sombra
                gc.setEffect(dropShadow);

                gc.setFill(colorFicha);
                gc.fillOval(x - tamanoFicha/2, yAnimacion - tamanoFicha/2, tamanoFicha, tamanoFicha);

                // Agregar brillo a la ficha
                gc.setFill(Color.color(1, 1, 1, 0.3));
                gc.fillOval(x - tamanoFicha/4, yAnimacion - tamanoFicha/3, tamanoFicha/4, tamanoFicha/4);

                // Quitar efecto de sombra
                gc.setEffect(null);
            }

            // Dibujar efecto hover (previsualización)
            if (!controlador.isJuegoTerminado() && !animando && columnaActual >= 0 && columnaActual < Tablero.COLUMNAS) {
                // Solo mostrar previsualización si la columna no está llena
                if (!tablero.columnaLlena(columnaActual)) {
                    double x = columnaActual * tamanoCelda + tamanoCelda / 2;

                    // Color semitransparente del jugador actual
                    Color colorHover = convertirAwtColorAJavaFX(controlador.getJugadorActual().getColor());
                    Color colorTransparente = new Color(
                            colorHover.getRed(),
                            colorHover.getGreen(),
                            colorHover.getBlue(),
                            0.4); // Alpha (transparencia)

                    gc.setFill(colorTransparente);
                    gc.fillOval(x - tamanoFicha/2, tamanoCelda/2 - tamanoFicha/2, tamanoFicha, tamanoFicha);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al dibujar tablero: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Convierte un color AWT a JavaFX
     * @param awtColor Color de AWT
     * @return Color equivalente en JavaFX
     */
    private Color convertirAwtColorAJavaFX(java.awt.Color awtColor) {
        return Color.rgb(
                awtColor.getRed(),
                awtColor.getGreen(),
                awtColor.getBlue(),
                awtColor.getAlpha() / 255.0);
    }

    /**
     * Fuerza un repintado completo del tablero.
     */
    public void refrescarTablero() {
        dibujarTablero();
    }
}