package com.alexzafra.conecta4.vista;

import com.alexzafra.conecta4.controller.ControladorJuego;
import com.alexzafra.conecta4.util.ConfiguracionVentana;
import com.alexzafra.conecta4.util.SistemaAudio;
import com.alexzafra.conecta4.vista.componentes.BarraEstado;
import com.alexzafra.conecta4.vista.componentes.PanelPuntuaciones;
import com.alexzafra.conecta4.vista.componentes.ReproductorMusica;
import com.alexzafra.conecta4.vista.dialogos.DialogoOpciones;
import com.alexzafra.conecta4.vista.dialogos.DialogoSeleccionModo;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import com.alexzafra.conecta4.vista.componentes.BarraArrastre;

import java.util.Optional;

/**
 * Ventana principal del juego Conecta 4 en JavaFX.
 * Contiene todos los componentes visuales y gestiona la interfaz.
 */
public class VentanaPrincipal extends BorderPane {
    private static final int ANCHO = 800;
    private static final int ALTO = 700;

    private ControladorJuego controlador;
    private TableroView panelTablero;
    private PanelPuntuaciones panelPuntuaciones;
    private BarraEstado barraEstado;
    private ReproductorMusica reproductorMusica;
    private boolean modoSeleccionado = false;
    private BarraArrastre barraArrastre;

    // Variable para pausar entre movimientos de la máquina
    private PauseTransition pausaMovimientoMaquina;

    /**
     * Constructor de la ventana principal.
     */
    public VentanaPrincipal() {
        // Crear el controlador del juego
        controlador = new ControladorJuego();

        // Obtener la configuración de ventana
        ConfiguracionVentana config = ConfiguracionVentana.getInstancia();
        Dimension2D resolucion = config.getResolucion();
        boolean pantallaCompleta = config.isPantallaCompleta();

        // Configuración de estilos
        setStyle("-fx-background-color: #282850;");

        // Inicializar componentes
        inicializarComponentes();

        // Actualizar estado inicial
        actualizarEstadoJuego();

        // Configurar escuchadores para redimensionamiento
        configurarEscuchadoresRedimension();

        // Inicializar barra de arrastre si es necesario
        Platform.runLater(() -> {
            actualizarBarraArrastre();
        });
    }

    /**
     * Inicializa todos los componentes de la interfaz.
     */
    private void inicializarComponentes() {
        // Panel del tablero (centro)
        panelTablero = new TableroView(controlador, this);

        // Panel de puntuaciones (arriba)
        panelPuntuaciones = new PanelPuntuaciones(controlador);

        // Crear reproductor de música (modo minimalista para ahorrar espacio)
        reproductorMusica = new ReproductorMusica(true);

        // Panel superior con puntuaciones y reproductor de música
        HBox panelSuperior = new HBox(20);
        panelSuperior.setAlignment(Pos.CENTER);
        panelSuperior.setPadding(new Insets(5));
        panelSuperior.setStyle("-fx-background-color: #1e1e46;");

        // Añadir componentes al panel superior
        panelSuperior.getChildren().addAll(panelPuntuaciones, reproductorMusica);
        HBox.setHgrow(panelPuntuaciones, Priority.ALWAYS);

        // Barra de estado (abajo)
        barraEstado = new BarraEstado();

        // Panel de botones (abajo)
        HBox panelBotones = crearPanelBotones();
        panelBotones.setPadding(new Insets(10, 0, 10, 0));

        // Panel sur (barra de estado + botones)
        VBox panelSur = new VBox(10);
        panelSur.setStyle("-fx-background-color: #282850;");
        panelSur.getChildren().addAll(barraEstado, panelBotones);

        // Configurar la pausa para el movimiento de la máquina
        pausaMovimientoMaquina = new PauseTransition(Duration.millis(800));
        pausaMovimientoMaquina.setOnFinished(event -> {
            // Realizar movimiento de la máquina
            controlador.realizarMovimientoMaquina();

            // Actualizar la interfaz después del movimiento
            panelTablero.refrescarTablero();
            barraEstado.establecerMensajeEstado(controlador.getMensajeEstado());
        });

        // Crear un contenedor que centrará el tablero
        StackPane contenedorTablero = new StackPane();
        contenedorTablero.getChildren().add(panelTablero);
        contenedorTablero.setPadding(new Insets(10));

        // Agregar componentes al panel principal
        setTop(panelSuperior);
        setCenter(contenedorTablero); // Usar el contenedor en lugar del tablero directamente
        setBottom(panelSur);
    }

    /**
     * Configura escuchadores para redimensionamiento de la ventana
     */
    private void configurarEscuchadoresRedimension() {
        // Obtener la escena actual (podría ser null si aún no se ha asignado)
        Scene escenaActual = getScene();
        if (escenaActual != null) {
            // Añadir escuchadores para el cambio de tamaño de la escena
            escenaActual.widthProperty().addListener((obs, oldVal, newVal) -> {
                ajustarTamañoTablero();
            });
            escenaActual.heightProperty().addListener((obs, oldVal, newVal) -> {
                ajustarTamañoTablero();
            });
        }

        // Agregar escuchador para cuando se asigne la escena si aún no está disponible
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, oldVal, newVal) -> {
                    ajustarTamañoTablero();
                });
                newScene.heightProperty().addListener((o, oldVal, newVal) -> {
                    ajustarTamañoTablero();
                });
            }
        });
    }

    /**
     * Ajusta el tablero al tamaño disponible
     */
    public void ajustarTamañoTablero() {
        if (panelTablero != null && getScene() != null) {
            try {
                // Obtener la configuración guardada
                ConfiguracionVentana config = ConfiguracionVentana.getInstancia();
                Dimension2D resolucion = config.getResolucion();
                boolean pantallaCompleta = config.isPantallaCompleta();

                // Obtener la ventana actual
                Stage ventana = (Stage) getScene().getWindow();

                // Calcular espacio disponible
                double anchoDisponible = pantallaCompleta ?
                        ventana.getWidth() : resolucion.getWidth();

                double altoDisponible = pantallaCompleta ?
                        ventana.getHeight() : resolucion.getHeight() - 200;

                if (anchoDisponible > 0 && altoDisponible > 0) {
                    // Ajustar el tamaño del tablero
                    panelTablero.ajustarTamanos(anchoDisponible, altoDisponible);
                    System.out.println("Ajustando tablero: " +
                            (pantallaCompleta ? "Pantalla Completa" : "Ventana Normal") +
                            " - " + anchoDisponible + "x" + altoDisponible);
                }
            } catch (Exception e) {
                System.err.println("Error al ajustar tamaño del tablero: " + e.getMessage());
            }
        }
    }

    /**
     * Crea el panel con los botones de acción.
     * @return Panel con botones
     */
    private HBox crearPanelBotones() {
        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER);
        panelBotones.setStyle("-fx-background-color: #282850;");

        // Botón de nueva partida
        Button btnNuevaPartida = new Button("Nueva Partida");
        btnNuevaPartida.getStyleClass().add("boton-menu");
        btnNuevaPartida.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                reiniciarJuego();
            } catch (Exception ex) {
                System.err.println("Error al reiniciar juego: " + ex.getMessage());
            }
        });

        // Botón de cambiar modo
        Button btnCambiarModo = new Button("Cambiar Modo");
        btnCambiarModo.getStyleClass().add("boton-menu");
        btnCambiarModo.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                mostrarDialogoModo();
            } catch (Exception ex) {
                System.err.println("Error al mostrar diálogo de modo: " + ex.getMessage());
            }
        });

        // Botón de opciones (para cambiar resolución)
        Button btnOpciones = new Button("Opciones");
        btnOpciones.getStyleClass().add("boton-menu");
        btnOpciones.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                mostrarDialogoOpciones();
            } catch (Exception ex) {
                System.err.println("Error al mostrar opciones: " + ex.getMessage());
            }
        });

        // Botón de reiniciar puntuaciones
        Button btnReiniciarPuntuaciones = new Button("Reiniciar Puntuaciones");
        btnReiniciarPuntuaciones.getStyleClass().add("boton-menu");
        btnReiniciarPuntuaciones.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                reiniciarPuntuaciones();
            } catch (Exception ex) {
                System.err.println("Error al reiniciar puntuaciones: " + ex.getMessage());
            }
        });

        // Botón de salir
        Button btnSalir = new Button("Salir");
        btnSalir.getStyleClass().add("boton-menu");
        btnSalir.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                Platform.exit();
            } catch (Exception ex) {
                System.err.println("Error al salir: " + ex.getMessage());
                Platform.exit();
            }
        });

        // Agregar botones al panel
        panelBotones.getChildren().addAll(
                btnNuevaPartida,
                btnCambiarModo,
                btnOpciones,
                btnReiniciarPuntuaciones,
                btnSalir
        );

        // Asegurar que los botones tengan espacio uniforme
        for (javafx.scene.Node btn : panelBotones.getChildren()) {
            HBox.setHgrow(btn, Priority.ALWAYS);
            ((Button)btn).setMaxWidth(Double.MAX_VALUE);
        }

        return panelBotones;
    }

    /**
     * Muestra el diálogo para configurar opciones del juego
     */
    private void mostrarDialogoOpciones() {
        try {
            // Crear diálogo con la resolución actual
            DialogoOpciones dialogo = new DialogoOpciones(getScene().getWindow());

            // Mostrar el diálogo y esperar resultado
            Optional<Boolean> resultado = dialogo.mostrarYObtenerResultado();

            // Si se aceptaron los cambios, aplicar la nueva resolución
            if (resultado.isPresent() && resultado.get()) {
                // Obtener la configuración de ventana
                ConfiguracionVentana config = ConfiguracionVentana.getInstancia();

                // Obtener la resolución seleccionada
                Dimension2D nuevaResolucion = dialogo.getResolucionSeleccionada();
                boolean modoCompleta = dialogo.isPantallaCompletaSeleccionada();

                // Obtener la ventana actual
                Stage ventana = (Stage) getScene().getWindow();

                // Aplicar el modo de pantalla completa
                if (modoCompleta != ventana.isFullScreen()) {
                    ventana.setFullScreen(modoCompleta);
                }

                // Si no es pantalla completa, ajustar el tamaño
                if (!modoCompleta) {
                    ventana.setWidth(nuevaResolucion.getWidth());
                    ventana.setHeight(nuevaResolucion.getHeight());

                    // Centrar la ventana en la pantalla
                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    ventana.setX((screenBounds.getWidth() - nuevaResolucion.getWidth()) / 2);
                    ventana.setY((screenBounds.getHeight() - nuevaResolucion.getHeight()) / 2);
                }

                // Ajustar el tamaño del tablero después de un breve retraso
                Platform.runLater(() -> {
                    ajustarTamañoTablero();
                });

                // Mostrar mensaje de confirmación
                barraEstado.establecerMensajeExito("Resolución cambiada a " +
                        (modoCompleta ? "Pantalla Completa" :
                                (int)nuevaResolucion.getWidth() + "x" +
                                        (int)nuevaResolucion.getHeight()));
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar diálogo de opciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra el diálogo para seleccionar el modo de juego
     */
    public void mostrarDialogoModo() {
        try {
            // Crear diálogo de selección de modo
            DialogoSeleccionModo dialogo = new DialogoSeleccionModo(controlador);

            // Mostrar el diálogo y esperar resultado
            Optional<Boolean> resultado = dialogo.mostrarYObtenerResultado();

            // Reiniciar el juego con el nuevo modo si se aceptó
            if (resultado.isPresent() && resultado.get()) {
                modoSeleccionado = true;
                reiniciarJuego();

                // Mostrar mensaje del modo seleccionado
                if (controlador.esModoUnJugador()) {
                    barraEstado.establecerMensajeExito("Modo Un Jugador");
                } else {
                    barraEstado.establecerMensajeExito("Modo Dos Jugadores");
                }
            } else {
                modoSeleccionado = false;
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar diálogo de modo: " + e.getMessage());
            e.printStackTrace();
            modoSeleccionado = false;
        }
    }

    /**
     * Verifica si se ha seleccionado un modo de juego
     * @return true si se seleccionó un modo, false si se canceló
     */
    public boolean isModoSeleccionado() {
        return modoSeleccionado;
    }

    /**
     * Reinicia las puntuaciones de los jugadores.
     */
    private void reiniciarPuntuaciones() {
        // Reiniciar puntuaciones
        controlador.getJugador1().reiniciarPuntuacion();
        controlador.getJugador2().reiniciarPuntuacion();

        // Actualizar panel de puntuaciones
        panelPuntuaciones.actualizarPuntuaciones();

        // Mostrar mensaje de confirmación
        barraEstado.establecerMensajeExito("Puntuaciones reiniciadas");
    }

    /**
     * Actualiza el estado del juego en la interfaz.
     * Se llama después de cada movimiento.
     */
    public void actualizarEstadoJuego() {
        // Actualizar mensaje de estado
        barraEstado.establecerMensajeEstado(controlador.getMensajeEstado());

        // Actualizar panel de puntuaciones
        panelPuntuaciones.actualizarPuntuaciones();

        // Si es modo un jugador y le toca a la máquina, iniciar timer para hacer su movimiento
        if (controlador.esModoUnJugador() &&
                controlador.getJugadorActual() == controlador.getJugador2() &&
                !controlador.isJuegoTerminado()) {

            // Iniciar la pausa y luego realizar el movimiento
            pausaMovimientoMaquina.playFromStart();
        }
    }
    /**
     * Reinicia el juego para una nueva partida.
     */
    private void reiniciarJuego() {
        // Detener efectos visuales
        panelTablero.detenerEfectos();

        // Detener timers si están activos
        if (pausaMovimientoMaquina != null) {
            pausaMovimientoMaquina.stop();
        }

        // Reiniciar juego en el controlador
        controlador.reiniciarJuego();

        // Actualizar interfaz
        panelTablero.refrescarTablero();
        actualizarEstadoJuego();
    }

    /**
     * Actualiza la interfaz para mostrar/ocultar la barra de arrastre según el modo de ventana
     */
    public void actualizarBarraArrastre() {
        ConfiguracionVentana config = ConfiguracionVentana.getInstancia();
        Stage ventana = (Stage) getScene().getWindow();

        if (config.isModoSinBordes() && !config.isPantallaCompleta()) {
            // Si no existe la barra de arrastre, crearla
            if (barraArrastre == null) {
                barraArrastre = new BarraArrastre(ventana, "Conecta 4");
            }

            // Verificar si ya está en el layout
            if (!getChildren().contains(barraArrastre)) {
                // Remover el contenido actual del top (panel de puntuaciones)
                Object topContent = getTop();

                // Crear un VBox para contener la barra de arrastre y el panel de puntuaciones
                VBox panelSuperior = new VBox();
                panelSuperior.getChildren().add(barraArrastre);

                if (topContent != null) {
                    panelSuperior.getChildren().add((Node)topContent);
                }

                // Establecer el nuevo panel superior
                setTop(panelSuperior);
            }
        } else {
            // Si existe la barra de arrastre y está visible, eliminarla
            if (barraArrastre != null) {
                // Recuperar el panel superior actual
                if (getTop() instanceof VBox) {
                    VBox panelSuperior = (VBox) getTop();
                    ObservableList<Node> hijos = panelSuperior.getChildren();

                    // Si hay más de un hijo (barra de arrastre + panel de puntuaciones)
                    if (hijos.size() > 1) {
                        // Recuperar el panel de puntuaciones
                        Node puntuaciones = hijos.get(1);

                        // Establecer solo el panel de puntuaciones como top
                        setTop(puntuaciones);
                    }
                }
            }
        }
    }


}