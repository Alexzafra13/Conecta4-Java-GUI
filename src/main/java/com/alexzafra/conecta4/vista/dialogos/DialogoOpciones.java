package com.alexzafra.conecta4.vista.dialogos;

import com.alexzafra.conecta4.util.SistemaAudio;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import java.util.Optional;

/**
 * Diálogo para configurar opciones del juego como la resolución y audio
 */
public class DialogoOpciones extends Stage {

    // Resoluciones disponibles
    public static final Dimension2D RESOLUCION_PEQUENA = new Dimension2D(700, 600);
    public static final Dimension2D RESOLUCION_MEDIA = new Dimension2D(800, 700);
    public static final Dimension2D RESOLUCION_GRANDE = new Dimension2D(1000, 850);

    // Resultado seleccionado
    private Dimension2D resolucionSeleccionada = RESOLUCION_MEDIA; // Por defecto
    private boolean cambioAceptado = false;

    // Componentes de la interfaz
    private ComboBox<String> comboResolucion;
    private Slider sliderVolumenEfectos;
    private Slider sliderVolumenMusica;
    private CheckBox checkActivarAudio;
    private Button btnAceptar;
    private Button btnCancelar;

    /**
     * Constructor del diálogo de opciones
     * @param owner Ventana padre
     */
    public DialogoOpciones(Window owner) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        setTitle("Opciones de Juego");

        // Establecer la resolución actual
        if (owner instanceof Stage) {
            Stage ventanaPadre = (Stage) owner;
            this.resolucionSeleccionada = new Dimension2D(
                    ventanaPadre.getWidth(),
                    ventanaPadre.getHeight()
            );
        }

        // Inicializar componentes
        inicializarComponentes();
    }

    /**
     * Inicializa los componentes de la interfaz
     */
    private void inicializarComponentes() {
        // Panel principal con espaciado
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("dialogo-fondo");

        // Título
        Label lblTitulo = new Label("Configuración");
        lblTitulo.getStyleClass().add("dialogo-titulo");

        // Panel para opciones de configuración
        GridPane gridOpciones = new GridPane();
        gridOpciones.setHgap(15);
        gridOpciones.setVgap(15);
        gridOpciones.setAlignment(Pos.CENTER);

        // Selección de resolución
        Label lblResolucion = new Label("Resolución:");
        lblResolucion.setStyle("-fx-text-fill: white;");

        comboResolucion = new ComboBox<>();
        comboResolucion.getItems().addAll(
                "Pequeña (700x600)",
                "Media (800x700)",
                "Grande (1000x850)"
        );
        comboResolucion.setPrefWidth(200);

        // Establecer la selección actual basada en la resolución actual
        if (Math.abs(resolucionSeleccionada.getWidth() - RESOLUCION_PEQUENA.getWidth()) < 1) {
            comboResolucion.getSelectionModel().select(0);
        } else if (Math.abs(resolucionSeleccionada.getWidth() - RESOLUCION_MEDIA.getWidth()) < 1) {
            comboResolucion.getSelectionModel().select(1);
        } else if (Math.abs(resolucionSeleccionada.getWidth() - RESOLUCION_GRANDE.getWidth()) < 1) {
            comboResolucion.getSelectionModel().select(2);
        }

        gridOpciones.add(lblResolucion, 0, 0);
        gridOpciones.add(comboResolucion, 1, 0);

        // Configuración de audio
        Label lblAudio = new Label("Audio:");
        lblAudio.setStyle("-fx-text-fill: white;");

        checkActivarAudio = new CheckBox("Activar audio");
        checkActivarAudio.setSelected(SistemaAudio.getInstancia().isAudioActivado());
        checkActivarAudio.setStyle("-fx-text-fill: white;");

        gridOpciones.add(lblAudio, 0, 1);
        gridOpciones.add(checkActivarAudio, 1, 1);

        // Volumen de efectos
        Label lblVolumenEfectos = new Label("Volumen efectos:");
        lblVolumenEfectos.setStyle("-fx-text-fill: white;");

        sliderVolumenEfectos = new Slider(0, 1, 0.7);
        sliderVolumenEfectos.setPrefWidth(200);
        sliderVolumenEfectos.setShowTickMarks(true);
        sliderVolumenEfectos.setShowTickLabels(true);
        sliderVolumenEfectos.setMajorTickUnit(0.25);
        sliderVolumenEfectos.setBlockIncrement(0.1);

        gridOpciones.add(lblVolumenEfectos, 0, 2);
        gridOpciones.add(sliderVolumenEfectos, 1, 2);

        // Volumen de música
        Label lblVolumenMusica = new Label("Volumen música:");
        lblVolumenMusica.setStyle("-fx-text-fill: white;");

        sliderVolumenMusica = new Slider(0, 1, 0.5);
        sliderVolumenMusica.setPrefWidth(200);
        sliderVolumenMusica.setShowTickMarks(true);
        sliderVolumenMusica.setShowTickLabels(true);
        sliderVolumenMusica.setMajorTickUnit(0.25);
        sliderVolumenMusica.setBlockIncrement(0.1);

        gridOpciones.add(lblVolumenMusica, 0, 3);
        gridOpciones.add(sliderVolumenMusica, 1, 3);

        // Panel de botones
        HBox panelBotones = new HBox(15);
        panelBotones.setAlignment(Pos.CENTER);

        btnAceptar = new Button("Aceptar");
        btnAceptar.getStyleClass().add("boton-menu");
        btnAceptar.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");

                // Guardar resolución seleccionada
                int indice = comboResolucion.getSelectionModel().getSelectedIndex();
                switch (indice) {
                    case 0:
                        resolucionSeleccionada = RESOLUCION_PEQUENA;
                        break;
                    case 1:
                        resolucionSeleccionada = RESOLUCION_MEDIA;
                        break;
                    case 2:
                        resolucionSeleccionada = RESOLUCION_GRANDE;
                        break;
                }

                // Aplicar configuración de audio
                SistemaAudio.getInstancia().setAudioActivado(checkActivarAudio.isSelected());
                SistemaAudio.getInstancia().setVolumenEfectos(sliderVolumenEfectos.getValue());
                SistemaAudio.getInstancia().setVolumenMusica(sliderVolumenMusica.getValue());

                cambioAceptado = true;
                close();
            } catch (Exception ex) {
                System.err.println("Error al aplicar opciones: " + ex.getMessage());
                close();
            }
        });

        btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("boton-menu");
        btnCancelar.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                close();
            } catch (Exception ex) {
                System.err.println("Error al cancelar: " + ex.getMessage());
                close();
            }
        });

        panelBotones.getChildren().addAll(btnAceptar, btnCancelar);

        // Agregar todos los elementos al panel principal
        panel.getChildren().addAll(
                lblTitulo,
                gridOpciones,
                panelBotones
        );

        // Crear escena y aplicar estilos
        Scene escena = new Scene(panel, 450, 350);

        try {
            escena.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error al cargar CSS: " + e.getMessage());
        }

        setScene(escena);
        centerOnScreen();
    }

    /**
     * Obtiene la resolución seleccionada
     * @return Resolución seleccionada como Dimension2D
     */
    public Dimension2D getResolucionSeleccionada() {
        return resolucionSeleccionada;
    }

    /**
     * Indica si se aceptó el cambio de opciones
     * @return true si se aceptó, false si se canceló
     */
    public boolean fueAceptado() {
        return cambioAceptado;
    }

    /**
     * Muestra el diálogo y espera a que el usuario seleccione una opción.
     * @return Optional con el resultado (true si se aceptó, false si se canceló)
     */
    public Optional<Boolean> mostrarYObtenerResultado() {
        // Llamar al método original de la clase Stage
        super.showAndWait();

        // Cuando se ejecuta esta línea, el diálogo ya se ha cerrado
        return cambioAceptado ? Optional.of(Boolean.TRUE) : Optional.empty();
    }
}