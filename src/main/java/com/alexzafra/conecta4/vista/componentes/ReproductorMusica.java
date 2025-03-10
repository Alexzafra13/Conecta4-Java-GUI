package com.alexzafra.conecta4.vista.componentes;

import com.alexzafra.conecta4.util.SistemaAudio;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Componente que muestra un reproductor de música para controlar la música de fondo
 */
public class ReproductorMusica extends VBox implements SistemaAudio.CambioMusicaListener {

    private ComboBox<String> comboMusica;
    private Button btnReproducir;
    private Button btnPausar;
    private Button btnSiguiente;
    private Slider sliderVolumen;
    private List<String> canciones;
    private List<String> nombresLimpios;
    private boolean modoMinimalista;

    // Timer para actualizar la interfaz periódicamente
    private PauseTransition actualizadorUI;

    /**
     * Constructor del reproductor de música
     * @param modoMinimalista Si es true, muestra una versión compacta del reproductor
     */
    public ReproductorMusica(boolean modoMinimalista) {
        this.modoMinimalista = modoMinimalista;

        setSpacing(modoMinimalista ? 5 : 10);
        setPadding(new Insets(modoMinimalista ? 5 : 10));
        setStyle("-fx-background-color: rgba(20, 20, 60, 0.8); -fx-background-radius: 10;");
        setMaxWidth(modoMinimalista ? 250 : 350);

        // Cargar lista de canciones
        cargarCanciones();

        // Inicializar componentes
        inicializarComponentes();

        // Configurar el actualizador de UI
        configurarActualizadorUI();

        // Registrarse como observador del sistema de audio
        SistemaAudio.getInstancia().registrarObservador(this);
    }

    /**
     * Configura un timer para actualizar periódicamente la UI
     * para reflejar cambios en la reproducción de música
     */
    private void configurarActualizadorUI() {
        actualizadorUI = new PauseTransition(Duration.seconds(1));
        actualizadorUI.setOnFinished(event -> {
            // Actualizar estado de los botones
            actualizarEstadoBotones(SistemaAudio.getInstancia().isMusicaReproduciendo());

            // Actualizar slider de volumen
            double volumenActual = SistemaAudio.getInstancia().getVolumenMusica();
            if (Math.abs(sliderVolumen.getValue() - volumenActual) > 0.01) {
                sliderVolumen.setValue(volumenActual);
            }

            // Continuar el ciclo de actualización
            actualizadorUI.playFromStart();
        });

        // Iniciar el actualizador
        actualizadorUI.play();
    }

    /**
     * Implementación del método de la interfaz CambioMusicaListener
     * Se ejecuta cuando el sistema de audio cambia de canción
     */
    @Override
    public void onCambioMusica(String cancion) {
        // Actualizar la interfaz cuando cambia la canción
        actualizarCancionActual(cancion);
    }

    /**
     * Carga la lista de canciones disponibles
     */
    private void cargarCanciones() {
        canciones = new ArrayList<>();
        nombresLimpios = new ArrayList<>();

        // Obtener canciones del sistema de audio
        List<String> pistasMusicales = SistemaAudio.getInstancia().getPistasMusicales();

        for (String pista : pistasMusicales) {
            String nombreLimpio = SistemaAudio.getInstancia().getNombresCancionesLimpios().get(pista);
            agregarCancion(pista, nombreLimpio);
        }

        // Si no hay canciones registradas en el sistema, usar las predefinidas
        if (canciones.isEmpty()) {
            agregarCancion("musica_fondo.mp3", "Música de Fondo");
            agregarCancion("Musica-conecta-4-relax2.mp3", "Relax 2");
            agregarCancion("Musica-conecta-4-relax3.mp3", "Relax 3");
            agregarCancion("Musica-conecta-4-relax4.mp3", "Relax 4");
            agregarCancion("Musica-conecta-4-relax5.mp3", "Relax 5");
        }
    }

    /**
     * Agrega una canción a la lista
     * @param archivo Nombre del archivo
     * @param nombreLimpio Nombre para mostrar
     */
    private void agregarCancion(String archivo, String nombreLimpio) {
        canciones.add(archivo);
        nombresLimpios.add(nombreLimpio);
    }

    /**
     * Inicializa los componentes del reproductor
     */
    private void inicializarComponentes() {
        if (modoMinimalista) {
            inicializarModoMinimalista();
        } else {
            inicializarModoCompleto();
        }
    }

    /**
     * Inicializa los componentes en modo minimalista (para pantalla de juego)
     */
    private void inicializarModoMinimalista() {
        // Panel superior con título y botones
        HBox panelSuperior = new HBox(5);
        panelSuperior.setAlignment(Pos.CENTER_LEFT);

        // Botones de control
        btnReproducir = new Button("▶");
        btnReproducir.getStyleClass().add("boton-musica-mini");
        btnReproducir.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                SistemaAudio.getInstancia().reproducirMusica();
                actualizarEstadoBotones(true);
            } catch (Exception ex) {
                System.err.println("Error al reproducir música: " + ex.getMessage());
            }
        });

        btnPausar = new Button("⏸");
        btnPausar.getStyleClass().add("boton-musica-mini");
        btnPausar.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                SistemaAudio.getInstancia().pausarMusica();
                actualizarEstadoBotones(false);
            } catch (Exception ex) {
                System.err.println("Error al pausar música: " + ex.getMessage());
            }
        });

        btnSiguiente = new Button("⏭");
        btnSiguiente.getStyleClass().add("boton-musica-mini");
        btnSiguiente.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                SistemaAudio.getInstancia().siguienteCancion();
            } catch (Exception ex) {
                System.err.println("Error al cambiar canción: " + ex.getMessage());
            }
        });

        // Selector de canción compacto
        comboMusica = new ComboBox<>();
        comboMusica.getItems().addAll(nombresLimpios);
        comboMusica.setValue(SistemaAudio.getInstancia().getNombreCancionActual());
        comboMusica.setMaxWidth(120);
        comboMusica.getStyleClass().add("combo-musica-mini");

        comboMusica.setOnAction(e -> {
            try {
                int indice = comboMusica.getSelectionModel().getSelectedIndex();
                if (indice >= 0 && indice < canciones.size()) {
                    String cancion = canciones.get(indice);
                    // Si la ruta no comienza con /audio/, añadirlo
                    if (!cancion.startsWith("/audio/")) {
                        cancion = "/audio/" + cancion;
                    }
                    SistemaAudio.getInstancia().cambiarMusica(cancion);
                    actualizarEstadoBotones(true);
                }
            } catch (Exception ex) {
                System.err.println("Error al cambiar canción: " + ex.getMessage());
            }
        });

        panelSuperior.getChildren().addAll(btnReproducir, btnPausar, btnSiguiente, comboMusica);

        // Control de volumen
        sliderVolumen = new Slider(0, 1, 0.5);
        sliderVolumen.setMaxWidth(Double.MAX_VALUE);
        sliderVolumen.valueProperty().addListener((obs, oldVal, newVal) -> {
            SistemaAudio.getInstancia().setVolumenMusica(newVal.doubleValue());
        });

        // Inicializar con el volumen actual
        sliderVolumen.setValue(SistemaAudio.getInstancia().getVolumenMusica());

        // Actualizar estado de los botones según si está reproduciendo o no
        actualizarEstadoBotones(SistemaAudio.getInstancia().isMusicaReproduciendo());

        // Agregar componentes al panel
        getChildren().addAll(panelSuperior, sliderVolumen);
    }

    /**
     * Inicializa los componentes en modo completo (para menú)
     */
    private void inicializarModoCompleto() {
        // Título
        Label lblTitulo = new Label("Reproductor de Música");
        lblTitulo.setTextFill(Color.WHITE);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Selector de canción
        Label lblCancion = new Label("Seleccionar canción:");
        lblCancion.setTextFill(Color.WHITE);

        comboMusica = new ComboBox<>();
        comboMusica.getItems().addAll(nombresLimpios);
        comboMusica.setValue(SistemaAudio.getInstancia().getNombreCancionActual());
        comboMusica.setMaxWidth(Double.MAX_VALUE);
        comboMusica.getStyleClass().add("combo-musica");

        comboMusica.setOnAction(e -> {
            try {
                int indice = comboMusica.getSelectionModel().getSelectedIndex();
                if (indice >= 0 && indice < canciones.size()) {
                    String cancion = canciones.get(indice);
                    // Si la ruta no comienza con /audio/, añadirlo
                    if (!cancion.startsWith("/audio/")) {
                        cancion = "/audio/" + cancion;
                    }
                    SistemaAudio.getInstancia().cambiarMusica(cancion);
                    actualizarEstadoBotones(true);
                }
            } catch (Exception ex) {
                System.err.println("Error al cambiar canción: " + ex.getMessage());
            }
        });

        // Botones de control
        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER);

        btnReproducir = new Button("▶ Reproducir");
        btnReproducir.getStyleClass().add("boton-musica");
        btnReproducir.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                SistemaAudio.getInstancia().reproducirMusica();
                actualizarEstadoBotones(true);
            } catch (Exception ex) {
                System.err.println("Error al reproducir música: " + ex.getMessage());
            }
        });

        btnPausar = new Button("⏸ Pausar");
        btnPausar.getStyleClass().add("boton-musica");
        btnPausar.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                SistemaAudio.getInstancia().pausarMusica();
                actualizarEstadoBotones(false);
            } catch (Exception ex) {
                System.err.println("Error al pausar música: " + ex.getMessage());
            }
        });

        btnSiguiente = new Button("⏭ Siguiente");
        btnSiguiente.getStyleClass().add("boton-musica");
        btnSiguiente.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                SistemaAudio.getInstancia().siguienteCancion();
            } catch (Exception ex) {
                System.err.println("Error al cambiar canción: " + ex.getMessage());
            }
        });

        panelBotones.getChildren().addAll(btnReproducir, btnPausar, btnSiguiente);

        // Control de volumen
        Label lblVolumen = new Label("Volumen:");
        lblVolumen.setTextFill(Color.WHITE);

        sliderVolumen = new Slider(0, 1, 0.5);
        sliderVolumen.setMaxWidth(Double.MAX_VALUE);
        sliderVolumen.valueProperty().addListener((obs, oldVal, newVal) -> {
            SistemaAudio.getInstancia().setVolumenMusica(newVal.doubleValue());
        });

        // Inicializar con el volumen actual
        sliderVolumen.setValue(SistemaAudio.getInstancia().getVolumenMusica());

        // Actualizar estado de los botones según si está reproduciendo o no
        actualizarEstadoBotones(SistemaAudio.getInstancia().isMusicaReproduciendo());

        // Agregar componentes al panel
        getChildren().addAll(
                lblTitulo,
                lblCancion,
                comboMusica,
                panelBotones,
                lblVolumen,
                sliderVolumen
        );
    }

    /**
     * Actualiza el estado de los botones según si está reproduciendo o no
     * @param reproduciendo true si está reproduciendo, false si está pausado
     */
    private void actualizarEstadoBotones(boolean reproduciendo) {
        btnReproducir.setDisable(reproduciendo);
        btnPausar.setDisable(!reproduciendo);
    }

    /**
     * Actualiza la interfaz cuando cambia la canción actual
     * @param nombreCancion Nombre de la canción actual
     */
    public void actualizarCancionActual(String nombreCancion) {
        // Buscar la canción en la lista
        String nombreLimpio = SistemaAudio.getInstancia().getNombreCancionActual();

        // Actualizar el ComboBox sin disparar el evento onAction
        comboMusica.valueProperty().set(nombreLimpio);

        // Si queremos seleccionar por índice, búscamos la posición en la lista
        for (int i = 0; i < canciones.size(); i++) {
            String rutaCancion = canciones.get(i);
            if (rutaCancion.equals(nombreCancion) ||
                    (rutaCancion.contains(nombreCancion.substring(nombreCancion.lastIndexOf("/") + 1)))) {
                comboMusica.getSelectionModel().select(i);
                break;
            }
        }
    }

    /**
     * Detiene el actualizador y libera recursos cuando el componente ya no está visible
     */
    public void detener() {
        if (actualizadorUI != null) {
            actualizadorUI.stop();
        }

        // Eliminarse como observador del sistema de audio
        SistemaAudio.getInstancia().eliminarObservador(this);
    }

    /**
     * Destruye recursos y se elimina como observador del sistema de audio
     */
    public void dispose() {
        detener();
    }
}