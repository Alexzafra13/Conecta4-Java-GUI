package com.alexzafra.conecta4.vista.componentes;

import com.alexzafra.conecta4.controller.ControladorJuego;
import com.alexzafra.conecta4.modelos.Jugador;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Panel que muestra las puntuaciones de los jugadores.
 * Muestra el título del juego y los puntos de cada jugador.
 */
public class PanelPuntuaciones extends BorderPane {
    private ControladorJuego controlador;
    private Label etiquetaTitulo;
    private Label etiquetaPuntuacionJ1;
    private Label etiquetaPuntuacionJ2;

    /**
     * Constructor del panel de puntuaciones.
     * @param controlador Controlador del juego
     */
    public PanelPuntuaciones(ControladorJuego controlador) {
        this.controlador = controlador;

        // Configuración del panel
        setStyle("-fx-background-color: #1e1e46; -fx-padding: 15 20 15 20;");
        setPadding(new Insets(15, 20, 15, 20));

        // Inicializar componentes
        inicializarComponentes();
    }

    /**
     * Inicializa los componentes del panel.
     */
    private void inicializarComponentes() {
        // Título del juego
        etiquetaTitulo = new Label("CONECTA 4");
        etiquetaTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        etiquetaTitulo.setTextFill(Color.WHITE);
        etiquetaTitulo.setEffect(new DropShadow(5, Color.BLACK));

        // Panel de puntuaciones con HBox
        HBox panelPuntos = new HBox(50);
        panelPuntos.setAlignment(Pos.CENTER);

        // Obtener referencias a los jugadores
        Jugador jugador1 = controlador.getJugador1();
        Jugador jugador2 = controlador.getJugador2();

        // Etiqueta de puntuación del jugador 1
        etiquetaPuntuacionJ1 = crearEtiquetaPuntuacion(jugador1);

        // Etiqueta de puntuación del jugador 2
        etiquetaPuntuacionJ2 = crearEtiquetaPuntuacion(jugador2);

        // Agregar etiquetas al panel de puntuaciones
        panelPuntos.getChildren().addAll(etiquetaPuntuacionJ1, etiquetaPuntuacionJ2);

        // Agregar componentes al panel principal
        setTop(etiquetaTitulo);
        setCenter(panelPuntos);

        // Alineación de componentes
        BorderPane.setAlignment(etiquetaTitulo, Pos.CENTER);
        BorderPane.setMargin(etiquetaTitulo, new Insets(0, 0, 10, 0));
    }

    /**
     * Crea una etiqueta formateada para mostrar la puntuación de un jugador.
     * @param jugador Jugador
     * @return Etiqueta formateada
     */
    private Label crearEtiquetaPuntuacion(Jugador jugador) {
        Label etiqueta = new Label(jugador.getNombre() + ": " + jugador.getPuntuacion());

        // Establecer estilo de la etiqueta
        etiqueta.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Convertir color AWT a JavaFX
        Color colorJavaFX = convertirAwtColorAJavaFX(jugador.getColor());
        etiqueta.setTextFill(colorJavaFX);

        etiqueta.setPadding(new Insets(5, 10, 5, 10));
        etiqueta.getStyleClass().add("label-puntuacion");

        // Agregar borde del color del jugador
        etiqueta.setStyle("-fx-border-color: " + toHexString(colorJavaFX) +
                "; -fx-border-width: 2; -fx-border-radius: 5;");

        return etiqueta;
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
     * Convierte un color JavaFX a su representación hexadecimal
     * @param color Color de JavaFX
     * @return Representación hexadecimal del color
     */
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Actualiza las puntuaciones de los jugadores.
     * Se llama cuando cambia la puntuación.
     */
    public void actualizarPuntuaciones() {
        try {
            Jugador jugador1 = controlador.getJugador1();
            Jugador jugador2 = controlador.getJugador2();

            // Actualizar textos
            etiquetaPuntuacionJ1.setText(jugador1.getNombre() + ": " + jugador1.getPuntuacion());
            etiquetaPuntuacionJ2.setText(jugador2.getNombre() + ": " + jugador2.getPuntuacion());

            // Actualizar colores
            Color colorJ1 = convertirAwtColorAJavaFX(jugador1.getColor());
            Color colorJ2 = convertirAwtColorAJavaFX(jugador2.getColor());

            etiquetaPuntuacionJ1.setTextFill(colorJ1);
            etiquetaPuntuacionJ2.setTextFill(colorJ2);

            // Actualizar bordes
            etiquetaPuntuacionJ1.setStyle("-fx-border-color: " + toHexString(colorJ1) +
                    "; -fx-border-width: 2; -fx-border-radius: 5;");
            etiquetaPuntuacionJ2.setStyle("-fx-border-color: " + toHexString(colorJ2) +
                    "; -fx-border-width: 2; -fx-border-radius: 5;");
        } catch (Exception e) {
            System.err.println("Error al actualizar puntuaciones: " + e.getMessage());
            e.printStackTrace();
        }
    }
}