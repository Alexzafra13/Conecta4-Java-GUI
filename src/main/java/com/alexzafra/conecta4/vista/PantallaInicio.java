package com.alexzafra.conecta4.vista;

import com.alexzafra.conecta4.vista.dialogos.DialogoOpciones;
import com.alexzafra.conecta4.vista.dialogos.DialogoCreditos;
import com.alexzafra.conecta4.vista.dialogos.DialogoOpcionesAvanzadas;
import com.alexzafra.conecta4.vista.componentes.ReproductorMusica;
import com.alexzafra.conecta4.util.SistemaAudio;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.scene.Scene;
import com.alexzafra.conecta4.util.ConfiguracionVentana;

/**
 * Pantalla inicial del juego con menú principal
 */
public class PantallaInicio extends BorderPane {

    private Stage primaryStage;
    private ReproductorMusica reproductorMusica;
    private VBox menuBotones;
    private Label lblTitulo;
    private VBox panelLogo;
    private ImageView logoView;

    /**
     * Constructor de la pantalla de inicio
     * @param primaryStage Referencia al escenario principal
     */
    public PantallaInicio(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Configuración del panel
        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom, #192044, #28284A);");

        // Iniciar música de fondo
        try {
            SistemaAudio.getInstancia().reproducirMusica();
        } catch (Exception e) {
            System.err.println("Error al reproducir música: " + e.getMessage());
        }

        // Inicializar componentes
        inicializarComponentes();

        // Configurar escuchadores para el tamaño de la ventana
        configurarEscuchadoresTamaño();
    }

    /**
     * Configura escuchadores para detectar cambios de tamaño
     */
    private void configurarEscuchadoresTamaño() {
        // Escuchar cambios de tamaño en la escena
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, oldVal, newVal) -> {
                    ajustarTamaño(newVal.doubleValue(), primaryStage.getHeight());
                });

                newScene.heightProperty().addListener((o, oldVal, newVal) -> {
                    ajustarTamaño(primaryStage.getWidth(), newVal.doubleValue());
                });
            }
        });

        // Si ya hay una escena, añadir los escuchadores ahora
        if (getScene() != null) {
            getScene().widthProperty().addListener((o, oldVal, newVal) -> {
                ajustarTamaño(newVal.doubleValue(), primaryStage.getHeight());
            });

            getScene().heightProperty().addListener((o, oldVal, newVal) -> {
                ajustarTamaño(primaryStage.getWidth(), newVal.doubleValue());
            });
        }
    }

    /**
     * Inicializa los componentes de la pantalla
     */
    private void inicializarComponentes() {
        // Logo del juego (parte superior)
        panelLogo = new VBox(10);
        panelLogo.setAlignment(Pos.CENTER);

        lblTitulo = new Label("CONECTA 4");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 48));
        lblTitulo.setTextFill(Color.WHITE);

        // Añadir efecto de sombra al título
        DropShadow sombra = new DropShadow();
        sombra.setColor(Color.color(0, 0, 0, 0.5));
        sombra.setRadius(10);
        sombra.setOffsetY(5);
        lblTitulo.setEffect(sombra);

        // Intenta cargar una imagen de logo (opcional)
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/imagenes/logo.png"));
            logoView = new ImageView(logoImage);
            logoView.setFitWidth(300);
            logoView.setPreserveRatio(true);
            panelLogo.getChildren().addAll(logoView, lblTitulo);
        } catch (Exception e) {
            // Si no se encuentra la imagen, solo usar el texto
            System.err.println("Error al cargar logo: " + e.getMessage());
            panelLogo.getChildren().add(lblTitulo);
        }

        setTop(panelLogo);
        BorderPane.setAlignment(panelLogo, Pos.CENTER);
        BorderPane.setMargin(panelLogo, new Insets(20, 0, 30, 0));

        // Menú de botones (centro)
        menuBotones = new VBox(15);
        menuBotones.setAlignment(Pos.CENTER);
        menuBotones.setPadding(new Insets(20));

        // Botones del menú
        Button btnNuevaPartida = crearBotonMenu("Nueva Partida");
        Button btnOpciones = crearBotonMenu("Opciones");
        Button btnCreditos = crearBotonMenu("Créditos");
        Button btnSalir = crearBotonMenu("Salir");

        // Asignar acciones a los botones
        btnNuevaPartida.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                iniciarNuevaPartida();
            } catch (Exception ex) {
                System.err.println("Error al iniciar partida: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnOpciones.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                mostrarOpciones();
            } catch (Exception ex) {
                System.err.println("Error al mostrar opciones: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCreditos.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                mostrarCreditos();
            } catch (Exception ex) {
                System.err.println("Error al mostrar créditos: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnSalir.setOnAction(e -> {
            try {
                SistemaAudio.getInstancia().reproducirEfecto("boton");
                Platform.exit();
            } catch (Exception ex) {
                System.err.println("Error al salir: " + ex.getMessage());
                Platform.exit();
            }
        });

        menuBotones.getChildren().addAll(btnNuevaPartida, btnOpciones, btnCreditos, btnSalir);
        setCenter(menuBotones);

        // Crear reproductor de música (modo completo para la pantalla de inicio)
        reproductorMusica = new ReproductorMusica(false);
        reproductorMusica.setPadding(new Insets(15));
        reproductorMusica.setMaxWidth(350);
        reproductorMusica.setAlignment(Pos.CENTER);

        // Pie con versión e información (parte inferior)
        VBox panelInferior = new VBox(15);
        panelInferior.setAlignment(Pos.CENTER);

        Label lblVersion = new Label("Versión 1.0 - Desarrollado por Alex Zafra");
        lblVersion.setTextFill(Color.LIGHTGRAY);

        panelInferior.getChildren().addAll(reproductorMusica, lblVersion);

        setBottom(panelInferior);
        BorderPane.setAlignment(panelInferior, Pos.CENTER);
        BorderPane.setMargin(panelInferior, new Insets(10, 0, 5, 0));
    }

    /**
     * Ajusta el tamaño de los componentes en función del tamaño de la ventana
     * @param ancho Ancho disponible
     * @param alto Alto disponible
     */
    public void ajustarTamaño(double ancho, double alto) {
        try {
            // Solo realizar ajustes si las dimensiones son válidas
            if (ancho <= 0 || alto <= 0) {
                return;
            }

            // Ajustar tamaño del logo en función del ancho disponible
            if (logoView != null) {
                double nuevoAnchoLogo = Math.min(300, ancho * 0.4);
                logoView.setFitWidth(nuevoAnchoLogo);
            }

            // Ajustar tamaño del título
            double tamañoFuente = Math.min(48, ancho / 16);
            lblTitulo.setFont(Font.font("System", FontWeight.BOLD, tamañoFuente));

            // Ajustar ancho de los botones del menú
            double anchoBotones = Math.min(300, ancho * 0.4);
            for (javafx.scene.Node nodo : menuBotones.getChildren()) {
                if (nodo instanceof Button) {
                    Button boton = (Button) nodo;
                    boton.setPrefWidth(anchoBotones);

                    // Ajustar tamaño de fuente de los botones
                    double tamañoFuenteBoton = Math.min(16, ancho / 45);
                    boton.setFont(Font.font("System", FontWeight.BOLD, tamañoFuenteBoton));
                }
            }

            // Ajustar espaciado del menú
            double espaciadoMenu = Math.min(15, alto / 40);
            menuBotones.setSpacing(espaciadoMenu);

            // Ajustar márgenes
            BorderPane.setMargin(panelLogo, new Insets(20, 0, alto * 0.05, 0));

            // Ajustar el reproductor de música
            reproductorMusica.setMaxWidth(Math.min(350, ancho * 0.6));
        } catch (Exception e) {
            System.err.println("Error al ajustar tamaños: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea un botón estilizado para el menú
     * @param texto Texto del botón
     * @return Botón configurado
     */
    private Button crearBotonMenu(String texto) {
        Button boton = new Button(texto);
        boton.setPrefWidth(200);
        boton.setPrefHeight(40);
        boton.setFont(Font.font("System", FontWeight.BOLD, 16));
        boton.getStyleClass().add("boton-menu");

        // Efecto al pasar el mouse
        boton.setOnMouseEntered(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(100), boton);
            ft.setFromValue(1.0);
            ft.setToValue(0.8);
            ft.play();
        });

        boton.setOnMouseExited(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(100), boton);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        });

        return boton;
    }

    /**
     * Inicia una nueva partida
     */
    private void iniciarNuevaPartida() {
        // Obtener la configuración de ventana
        ConfiguracionVentana config = ConfiguracionVentana.getInstancia();
        Dimension2D resolucion = config.getResolucion();
        boolean pantallaCompleta = config.isPantallaCompleta();

        // Crear ventana principal del juego
        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();

        // Crear escena con las dimensiones de la configuración
        Scene scene = new Scene(ventanaPrincipal,
                resolucion.getWidth(),
                resolucion.getHeight());

        // Aplicar estilos CSS
        try {
            scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error al cargar CSS: " + e.getMessage());
        }

        // Guardar el estado actual de la ventana para poder restaurarlo si se cancela
        double oldWidth = primaryStage.getWidth();
        double oldHeight = primaryStage.getHeight();
        boolean oldFullscreen = primaryStage.isFullScreen();

        // Cambiar la escena sin aplicar resolución aún
        primaryStage.setScene(scene);

        // Mostrar diálogo de modo de juego
        ventanaPrincipal.mostrarDialogoModo();

        // Solo aplicar cambios de resolución si el diálogo no fue cancelado
        if (ventanaPrincipal.isModoSeleccionado()) {
            // Establecer pantalla completa si es necesario
            if (pantallaCompleta != primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(pantallaCompleta);
            }

            // Si no está en pantalla completa, establecer dimensiones específicas
            if (!pantallaCompleta) {
                primaryStage.setWidth(resolucion.getWidth());
                primaryStage.setHeight(resolucion.getHeight());

                // Centrar la ventana en la pantalla
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                primaryStage.setX((screenBounds.getWidth() - resolucion.getWidth()) / 2);
                primaryStage.setY((screenBounds.getHeight() - resolucion.getHeight()) / 2);
            }

            // Provocar un reajuste del tamaño del tablero después de que la escena esté activa
            Platform.runLater(() -> {
                // Forzar ajuste de tamaño del tablero
                ventanaPrincipal.ajustarTamañoTablero();
            });
        }
    }

    /**
     * Muestra el diálogo de opciones
     */
    private void mostrarOpciones() {
        DialogoOpcionesAvanzadas dialogo = new DialogoOpcionesAvanzadas(primaryStage);
        dialogo.showAndWait();
    }

    /**
     * Muestra los créditos del juego
     */
    private void mostrarCreditos() {
        DialogoCreditos dialogo = new DialogoCreditos();
        dialogo.showAndWait();
    }
}