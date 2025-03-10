package com.alexzafra.conecta4.vista.dialogos;

import com.alexzafra.conecta4.controller.ControladorJuego;
import com.alexzafra.conecta4.controller.InteligenciaArtificial;
import com.alexzafra.conecta4.util.SistemaAudio;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Optional;

/**
 * Diálogo para seleccionar el modo de juego (1 jugador o 2 jugadores) y la dificultad
 */
public class DialogoSeleccionModo extends Stage {

    // Constantes para los modos de juego
    public static final int MODO_UN_JUGADOR = 1;
    public static final int MODO_DOS_JUGADORES = 2;

    // Controlador del juego
    private ControladorJuego controlador;

    // Variable para almacenar el resultado
    private boolean aceptado = false;

    // Componentes de la interfaz
    private RadioButton radioUnJugador;
    private RadioButton radioDosJugadores;
    private ComboBox<String> comboDificultad;

    /**
     * Constructor del diálogo
     *
     * @param controlador Controlador del juego
     */
    public DialogoSeleccionModo(ControladorJuego controlador) {
        this.controlador = controlador;

        // Configurar el diálogo
        setTitle("Seleccionar Modo de Juego");
        initStyle(StageStyle.UNDECORATED);
        initModality(Modality.APPLICATION_MODAL);

        // Crear el contenido del diálogo
        VBox contenido = crearContenido();

        // Crear escena
        Scene escena = new Scene(contenido, 400, 300);

        // Aplicar estilos CSS
        try {
            escena.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error al cargar CSS: " + e.getMessage());
        }

        setScene(escena);
        centerOnScreen();
    }

    /**
     * Crea el contenido del diálogo
     *
     * @return Panel con el contenido
     */
    private VBox crearContenido() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #28284A;");
        panel.getStyleClass().add("dialogo-fondo");

        // Título
        Label lblTitulo = new Label("Seleccione el modo de juego");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);
        lblTitulo.getStyleClass().add("dialogo-titulo");
        lblTitulo.setAlignment(Pos.CENTER);

        // Grupo de botones de radio
        VBox panelRadios = new VBox(10);
        panelRadios.setPadding(new Insets(10, 0, 10, 0));

        ToggleGroup grupoModo = new ToggleGroup();

        radioUnJugador = new RadioButton("Un Jugador (contra la máquina)");
        radioUnJugador.setTextFill(Color.WHITE);
        radioUnJugador.setToggleGroup(grupoModo);
        radioUnJugador.setSelected(controlador.esModoUnJugador());
        radioUnJugador.setOnAction(e -> actualizarEstadoComboDificultad());

        radioDosJugadores = new RadioButton("Dos Jugadores");
        radioDosJugadores.setTextFill(Color.WHITE);
        radioDosJugadores.setToggleGroup(grupoModo);
        radioDosJugadores.setSelected(!controlador.esModoUnJugador());
        radioDosJugadores.setOnAction(e -> actualizarEstadoComboDificultad());

        panelRadios.getChildren().addAll(radioUnJugador, radioDosJugadores);

        // Panel de configuración de dificultad
        GridPane panelDificultad = new GridPane();
        panelDificultad.setHgap(10);
        panelDificultad.setVgap(5);
        panelDificultad.setPadding(new Insets(5, 0, 5, 20));

        Label lblDificultad = new Label("Nivel de dificultad:");
        lblDificultad.setTextFill(Color.WHITE);

        comboDificultad = new ComboBox<>();
        comboDificultad.getItems().addAll(
                "Fácil",
                "Medio",
                "Difícil",
                "Demencial"
        );
        comboDificultad.setValue(getNombreNivel(InteligenciaArtificial.NIVEL_FACIL));
        comboDificultad.setPrefWidth(150);

        panelDificultad.add(lblDificultad, 0, 0);
        panelDificultad.add(comboDificultad, 1, 0);

        // Botones de acción
        HBox panelBotones = new HBox(15);
        panelBotones.setAlignment(Pos.CENTER);
        panelBotones.setPadding(new Insets(10, 0, 0, 0));

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.getStyleClass().add("boton-menu");
        btnAceptar.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");

                // Aplicar la configuración seleccionada
                if (radioUnJugador.isSelected()) {
                    int nivelDificultad = comboDificultad.getSelectionModel().getSelectedIndex() + 1;
                    controlador.configurarModoUnJugador(nivelDificultad);
                } else {
                    controlador.configurarModoDosJugadores();
                }

                // Marcar como aceptado y cerrar
                aceptado = true;
                close();
            } catch (Exception ex) {
                System.err.println("Error al aplicar modo: " + ex.getMessage());
                close();
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("boton-menu");
        btnCancelar.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");

                // Cerrar sin aceptar
                aceptado = false;
                close();
            } catch (Exception ex) {
                System.err.println("Error al cancelar: " + ex.getMessage());
                close();
            }
        });

        panelBotones.getChildren().addAll(btnAceptar, btnCancelar);

        // Añadir componentes al panel principal
        panel.getChildren().addAll(lblTitulo, panelRadios, panelDificultad, panelBotones);

        // Inicializar el estado del combo de dificultad
        actualizarEstadoComboDificultad();

        return panel;
    }

    /**
     * Actualiza el estado del combo de dificultad según el modo seleccionado
     */
    private void actualizarEstadoComboDificultad() {
        boolean modoUnJugador = radioUnJugador.isSelected();
        comboDificultad.setDisable(!modoUnJugador);
    }

    /**
     * Obtiene el nombre del nivel de dificultad según su valor numérico
     *
     * @param nivel Nivel de dificultad (1-4)
     * @return Nombre del nivel
     */
    private String getNombreNivel(int nivel) {
        switch (nivel) {
            case InteligenciaArtificial.NIVEL_FACIL:
                return "Fácil";
            case InteligenciaArtificial.NIVEL_MEDIO:
                return "Medio";
            case InteligenciaArtificial.NIVEL_DIFICIL:
                return "Difícil";
            case InteligenciaArtificial.NIVEL_DEMENCIAL:
                return "Demencial";
            default:
                return "Desconocido";
        }
    }

    /**
     * Obtiene el nivel de dificultad seleccionado
     *
     * @return Nivel de dificultad (1-4)
     */
    public int getNivelDificultad() {
        return comboDificultad.getSelectionModel().getSelectedIndex() + 1;
    }

    /**
     * Obtiene el modo de juego seleccionado
     *
     * @return MODO_UN_JUGADOR o MODO_DOS_JUGADORES
     */
    public int getModoSeleccionado() {
        return radioUnJugador.isSelected() ? MODO_UN_JUGADOR : MODO_DOS_JUGADORES;
    }

    /**
     * Indica si el usuario aceptó la configuración
     * @return true si aceptó, false si canceló
     */
    public boolean fueAceptado() {
        return aceptado;
    }

    /**
     * Muestra el diálogo y espera por una respuesta del usuario
     * @return Optional con true si se aceptó, o empty si se canceló
     */
    public Optional<Boolean> mostrarYObtenerResultado() {
        // Llamar al método original de Stage
        super.showAndWait();

        // Cuando se ejecuta esta línea, el diálogo ya se ha cerrado
        return aceptado ? Optional.of(Boolean.TRUE) : Optional.empty();
    }
}