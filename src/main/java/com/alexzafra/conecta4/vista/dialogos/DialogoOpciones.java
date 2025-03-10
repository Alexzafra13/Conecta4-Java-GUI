package com.alexzafra.conecta4.vista.dialogos;

import com.alexzafra.conecta4.util.ConfiguracionVentana;
import com.alexzafra.conecta4.util.ResolucionesJuego;
import com.alexzafra.conecta4.util.SistemaAudio;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import java.util.Optional;

/**
 * Diálogo para configurar opciones del juego como la resolución y modo de ventana
 */
public class DialogoOpciones extends Stage {

    // Resultado seleccionado
    private Dimension2D resolucionSeleccionada;
    private boolean cambioAceptado = false;
    private boolean modoCompleta = false;

    // Componentes de la interfaz
    private ComboBox<String> comboResolucion;
    private ComboBox<String> comboModoVentana;
    private Button btnAceptar;
    private Button btnCancelar;

    /**
     * Constructor del diálogo de opciones
     * @param owner Ventana padre
     */
    public DialogoOpciones(Window owner) {
        // Configuración básica del diálogo
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        setTitle("Opciones de Juego");

        // Obtener la configuración actual
        ConfiguracionVentana config = ConfiguracionVentana.getInstancia();
        this.resolucionSeleccionada = config.getResolucion();
        this.modoCompleta = config.isPantallaCompleta();

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

        // Establecer la selección actual basada en la resolución actual
        String resolucionActual = ResolucionesJuego.obtenerNombreResolucion(resolucionSeleccionada);
        comboResolucion.setValue(resolucionActual);
        comboResolucion.setPrefWidth(200);

        gridOpciones.add(lblResolucion, 0, 0);
        gridOpciones.add(comboResolucion, 1, 0);

        // Selección de modo de ventana
        Label lblModoVentana = new Label("Modo de Ventana:");
        lblModoVentana.setStyle("-fx-text-fill: white;");

        comboModoVentana = new ComboBox<>();
        comboModoVentana.getItems().addAll("Ventana", "Pantalla Completa");

        // Establecer el modo actual
        ConfiguracionVentana config = ConfiguracionVentana.getInstancia();
        comboModoVentana.setValue(config.isPantallaCompleta() ? "Pantalla Completa" : "Ventana");
        comboModoVentana.setPrefWidth(200);

        // Añadir listener para desactivar el selector de resolución en modo pantalla completa
        comboModoVentana.valueProperty().addListener((obs, oldVal, newVal) -> {
            comboResolucion.setDisable("Pantalla Completa".equals(newVal));
        });

        // Inicializar el estado del selector de resolución según el modo actual
        comboResolucion.setDisable("Pantalla Completa".equals(comboModoVentana.getValue()));

        gridOpciones.add(lblModoVentana, 0, 1);
        gridOpciones.add(comboModoVentana, 1, 1);

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

                // Guardar modo de ventana
                modoCompleta = "Pantalla Completa".equals(comboModoVentana.getValue());

                // Actualizar configuración global
                ConfiguracionVentana configVentana = ConfiguracionVentana.getInstancia();
                configVentana.setResolucion(resolucionSeleccionada);
                configVentana.setPantallaCompleta(modoCompleta);

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
     * Obtiene el modo de pantalla completa seleccionado
     * @return true si se seleccionó pantalla completa, false para modo ventana
     */
    public boolean isPantallaCompletaSeleccionada() {
        return modoCompleta;
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