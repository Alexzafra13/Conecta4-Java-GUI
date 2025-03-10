package com.alexzafra.conecta4.vista.dialogos;

import com.alexzafra.conecta4.util.ResolucionesJuego;
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

    // Resultado seleccionado
    private Dimension2D resolucionSeleccionada;
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
        } else {
            // Valor por defecto si no hay ventana padre
            this.resolucionSeleccionada = ResolucionesJuego.RES_1024x768;
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
        comboResolucion.getItems().addAll(ResolucionesJuego.NOMBRES_RESOLUCIONES);
        comboResolucion.setPrefWidth(200);

        // Establecer la selección actual basada en la resolución actual
        String resolucionActual = ResolucionesJuego.obtenerNombreResolucion(resolucionSeleccionada);
        comboResolucion.setValue(resolucionActual);

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

        sliderVolumenEfectos = new Slider(0, 1, SistemaAudio.getInstancia().getVolumenEfectos());
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

        sliderVolumenMusica = new Slider(0, 1, SistemaAudio.getInstancia().getVolumenMusica());
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
                String resolucionSeleccionadaTexto = comboResolucion.getValue();
                resolucionSeleccionada = ResolucionesJuego.obtenerResolucionPorNombre(resolucionSeleccionadaTexto);

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