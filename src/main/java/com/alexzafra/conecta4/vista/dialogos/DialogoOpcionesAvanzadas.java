package com.alexzafra.conecta4.vista.dialogos;

import com.alexzafra.conecta4.util.ResolucionesJuego;
import com.alexzafra.conecta4.util.SistemaAudio;
import com.alexzafra.conecta4.vista.VentanaPrincipal;
import com.alexzafra.conecta4.vista.componentes.ReproductorMusica;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.alexzafra.conecta4.util.ConfiguracionVentana;

/**
 * Diálogo avanzado de opciones que permite configurar resolución, sonido, etc.
 */
public class DialogoOpcionesAvanzadas extends Dialog<Void> {

    // Valores de volumen
    private double volumenEfectos = 0.8;
    private double volumenMusica = 0.5;

    // Componentes de la interfaz
    private ComboBox<String> comboResolucion;
    private ComboBox<String> comboModoVentana;
    private Slider sliderEfectos;
    private Slider sliderMusica;
    private Stage ownerStage;
    private ReproductorMusica reproductorMusica;

    /**
     * Constructor del diálogo
     * @param ownerStage Ventana propietaria
     */
    public DialogoOpcionesAvanzadas(Stage ownerStage) {
        this.ownerStage = ownerStage;

        // Configurar el diálogo
        setTitle("Opciones Avanzadas");
        initStyle(StageStyle.UTILITY);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Obtener los valores actuales de audio
        volumenEfectos = SistemaAudio.getInstancia().getVolumenEfectos();
        volumenMusica = SistemaAudio.getInstancia().getVolumenMusica();

        // Crear el contenido del diálogo con pestañas
        TabPane tabPane = new TabPane();

        // Pestaña de pantalla
        Tab tabPantalla = new Tab("Pantalla");
        tabPantalla.setContent(crearPanelPantalla());
        tabPantalla.setClosable(false);

        // Pestaña de sonido
        Tab tabSonido = new Tab("Sonido");
        tabSonido.setContent(crearPanelSonido());
        tabSonido.setClosable(false);

        // Pestaña de controles (opcional, para futuras expansiones)
        Tab tabControles = new Tab("Controles");
        tabControles.setContent(crearPanelControles());
        tabControles.setClosable(false);

        // Pestaña de música
        Tab tabMusica = new Tab("Música");
        tabMusica.setContent(crearPanelMusica());
        tabMusica.setClosable(false);

        tabPane.getTabs().addAll(tabPantalla, tabSonido, tabMusica, tabControles);
        getDialogPane().setContent(tabPane);

        // Aplicar estilos
        getDialogPane().getStyleClass().add("dialogo-opciones");
        getDialogPane().setStyle("-fx-background-color: #28284A;");

        // Configurar botones
        Button btnOK = (Button) getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setText("Guardar");
        btnOK.setDefaultButton(true);

        Button btnCancel = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        btnCancel.setText("Cancelar");

        // Configurar resultado del diálogo
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // Aplicar cambios
                aplicarCambios();
            }
            return null;
        });

        // Detener el reproductor de música cuando se cierre el diálogo
        setOnCloseRequest(e -> {
            if (reproductorMusica != null) {
                reproductorMusica.detener();
            }
        });
    }

    /**
     * Crea el panel de opciones de pantalla
     * @return Panel configurado
     */
    private VBox crearPanelPantalla() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #28284A;");

        // Título
        Label lblTitulo = new Label("Configuración de Pantalla");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        // Panel de resolución
        GridPane panelResolucion = new GridPane();
        panelResolucion.setHgap(10);
        panelResolucion.setVgap(10);
        panelResolucion.setPadding(new Insets(10, 0, 10, 0));

        Label lblResolucion = new Label("Resolución:");
        lblResolucion.setTextFill(Color.WHITE);

        comboResolucion = new ComboBox<>();
        comboResolucion.getItems().addAll(ResolucionesJuego.NOMBRES_RESOLUCIONES);

        // Establecer resolución actual seleccionada
        String resolucionActual = ResolucionesJuego.obtenerNombreResolucion(
                new Dimension2D(ownerStage.getWidth(), ownerStage.getHeight()));
        comboResolucion.setValue(resolucionActual);
        comboResolucion.setPrefWidth(200);

        panelResolucion.add(lblResolucion, 0, 0);
        panelResolucion.add(comboResolucion, 1, 0);

        // Modo de ventana
        Label lblModoVentana = new Label("Modo de ventana:");
        lblModoVentana.setTextFill(Color.WHITE);

        comboModoVentana = new ComboBox<>();
        comboModoVentana.getItems().addAll("Ventana", "Ventana sin bordes", "Pantalla completa");

        // Determinar el modo actual
        ConfiguracionVentana config = ConfiguracionVentana.getInstancia();
        String modoActual;
        if (ownerStage.isFullScreen()) {
            modoActual = "Pantalla completa";
        } else if (config.isModoSinBordes()) {
            modoActual = "Ventana sin bordes";
        } else {
            modoActual = "Ventana";
        }

        comboModoVentana.setValue(modoActual);
        comboModoVentana.setPrefWidth(200);

        // Añadir listener para habilitar/deshabilitar comboResolucion
        comboModoVentana.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esPantallaCompleta = "Pantalla completa".equals(newVal);
            comboResolucion.setDisable(esPantallaCompleta);
        });

        // Establecer estado inicial del comboResolucion
        comboResolucion.setDisable("Pantalla completa".equals(comboModoVentana.getValue()));

        panelResolucion.add(lblModoVentana, 0, 1);
        panelResolucion.add(comboModoVentana, 1, 1);

        // Añadir componentes al panel principal
        panel.getChildren().addAll(lblTitulo, panelResolucion);

        return panel;
    }

    /**
     * Crea el panel de opciones de sonido
     * @return Panel configurado
     */
    private VBox crearPanelSonido() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #28284A;");

        // Título
        Label lblTitulo = new Label("Configuración de Sonido");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        // Panel de volumen
        GridPane panelVolumen = new GridPane();
        panelVolumen.setHgap(10);
        panelVolumen.setVgap(20);
        panelVolumen.setPadding(new Insets(10, 0, 10, 0));

        // Casilla para activar/desactivar audio
        Label lblActivarAudio = new Label("Audio:");
        lblActivarAudio.setTextFill(Color.WHITE);

        javafx.scene.control.CheckBox checkActivarAudio = new javafx.scene.control.CheckBox("Activar audio");
        checkActivarAudio.setSelected(SistemaAudio.getInstancia().isAudioActivado());
        checkActivarAudio.setTextFill(Color.WHITE);

        checkActivarAudio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            SistemaAudio.getInstancia().setAudioActivado(newVal);
        });

        panelVolumen.add(lblActivarAudio, 0, 0);
        panelVolumen.add(checkActivarAudio, 1, 0);

        // Volumen de efectos
        Label lblEfectos = new Label("Volumen de efectos:");
        lblEfectos.setTextFill(Color.WHITE);

        sliderEfectos = new Slider(0, 1, volumenEfectos);
        sliderEfectos.setShowTickMarks(true);
        sliderEfectos.setShowTickLabels(true);
        sliderEfectos.setMajorTickUnit(0.25);
        sliderEfectos.setBlockIncrement(0.1);
        sliderEfectos.setPrefWidth(250);

        Label lblValorEfectos = new Label(String.format("%d%%", (int)(volumenEfectos * 100)));
        lblValorEfectos.setTextFill(Color.WHITE);
        lblValorEfectos.setPrefWidth(50);

        sliderEfectos.valueProperty().addListener((observable, oldValue, newValue) -> {
            lblValorEfectos.setText(String.format("%d%%", (int)(newValue.doubleValue() * 100)));
            SistemaAudio.getInstancia().setVolumenEfectos(newValue.doubleValue());
            try {
                // Reproducir efecto de prueba
                SistemaAudio.getInstancia().reproducirEfecto("boton");
            } catch (Exception e) {
                System.err.println("Error al reproducir efecto de prueba: " + e.getMessage());
            }
        });

        HBox panelEfectos = new HBox(10, sliderEfectos, lblValorEfectos);

        // Volumen de música
        Label lblMusica = new Label("Volumen de música:");
        lblMusica.setTextFill(Color.WHITE);

        sliderMusica = new Slider(0, 1, volumenMusica);
        sliderMusica.setShowTickMarks(true);
        sliderMusica.setShowTickLabels(true);
        sliderMusica.setMajorTickUnit(0.25);
        sliderMusica.setBlockIncrement(0.1);
        sliderMusica.setPrefWidth(250);

        Label lblValorMusica = new Label(String.format("%d%%", (int)(volumenMusica * 100)));
        lblValorMusica.setTextFill(Color.WHITE);
        lblValorMusica.setPrefWidth(50);

        sliderMusica.valueProperty().addListener((observable, oldValue, newValue) -> {
            lblValorMusica.setText(String.format("%d%%", (int)(newValue.doubleValue() * 100)));
            SistemaAudio.getInstancia().setVolumenMusica(newValue.doubleValue());
        });

        HBox panelMusica = new HBox(10, sliderMusica, lblValorMusica);

        // Añadir a la cuadrícula
        panelVolumen.add(lblEfectos, 0, 1);
        panelVolumen.add(panelEfectos, 1, 1);
        panelVolumen.add(lblMusica, 0, 2);
        panelVolumen.add(panelMusica, 1, 2);

        // Añadir componentes al panel principal
        panel.getChildren().addAll(lblTitulo, panelVolumen);

        return panel;
    }

    /**
     * Crea el panel de opciones de música
     * @return Panel configurado
     */
    private VBox crearPanelMusica() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #28284A;");

        // Título
        Label lblTitulo = new Label("Reproductor de Música");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        // Crear reproductor de música completo
        reproductorMusica = new ReproductorMusica(false);

        // Añadir componentes al panel principal
        panel.getChildren().addAll(lblTitulo, reproductorMusica);
        panel.setAlignment(Pos.TOP_CENTER);

        return panel;
    }

    /**
     * Crea el panel de opciones de controles (para futuras expansiones)
     * @return Panel configurado
     */
    private VBox crearPanelControles() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #28284A;");

        // Título
        Label lblTitulo = new Label("Configuración de Controles");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        // Mensaje de "No disponible"
        Label lblNoDisponible = new Label("La configuración de controles no está disponible en esta versión.");
        lblNoDisponible.setTextFill(Color.LIGHTGRAY);
        lblNoDisponible.setWrapText(true);

        // Añadir componentes al panel principal
        panel.getChildren().addAll(lblTitulo, lblNoDisponible);
        panel.setAlignment(Pos.CENTER);

        return panel;
    }

    /**
     * Aplica los cambios de configuración
     */
    private void aplicarCambios() {
        // Aplicar cambios de resolución
        aplicarCambiosResolucion();

        // Guardar configuraciones de volumen
        volumenEfectos = sliderEfectos.getValue();
        volumenMusica = sliderMusica.getValue();

        // Aquí se podrían guardar los valores en un archivo de configuración
    }

    /**
     * Aplica los cambios de resolución
     */
    private void aplicarCambiosResolucion() {
        // Obtener la instancia de configuración de ventana
        ConfiguracionVentana config = ConfiguracionVentana.getInstancia();

        // Obtener la resolución seleccionada
        String resolucionSeleccionadaTexto = comboResolucion.getValue();
        Dimension2D nuevaResolucion = ResolucionesJuego.obtenerResolucionPorNombre(resolucionSeleccionadaTexto);

        // Verificar el modo de ventana seleccionado
        String modoVentana = comboModoVentana.getValue();
        boolean modoPantallaCompleta = "Pantalla completa".equals(modoVentana);

        // Guardar la configuración
        config.setResolucion(nuevaResolucion);
        config.setPantallaCompleta(modoPantallaCompleta);

        // Aplicar el modo de pantalla completa
        ownerStage.setFullScreen(modoPantallaCompleta);

        // Si no es pantalla completa, aplicar la resolución seleccionada
        if (!modoPantallaCompleta) {
            ownerStage.setWidth(nuevaResolucion.getWidth());
            ownerStage.setHeight(nuevaResolucion.getHeight());
            ownerStage.centerOnScreen();
        }
    }
}